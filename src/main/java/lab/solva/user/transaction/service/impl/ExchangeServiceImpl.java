package lab.solva.user.transaction.service.impl;

import lab.solva.user.transaction.dto.ExchangeInfoDto;
import lab.solva.user.transaction.dto.ExchangeRateDto;
import lab.solva.user.transaction.enumeration.CurrencyType;
import lab.solva.user.transaction.model.ExchangeInfoEntity;
import lab.solva.user.transaction.model.ExchangeRateEntity;
import lab.solva.user.transaction.repository.ExchangeInfoRepository;
import lab.solva.user.transaction.repository.ExchangeRateRepository;
import lab.solva.user.transaction.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeInfoRepository exchangeInfoRepository;

    private final ExchangeRateRepository exchangeRateRepository;

    @Override
    public Set<ExchangeRateEntity> gettingRates() {

        // Получение текущей даты
        LocalDate currentDate = LocalDate.now();

        // Получение данных на текущую дата из БД
        Optional<ExchangeInfoEntity> currentExchangeInfoOptional =
                exchangeInfoRepository.findCurrentExchangeInfo(currentDate);

        if (currentExchangeInfoOptional.isPresent()) {
            ExchangeInfoEntity exchangeInfoEntity = currentExchangeInfoOptional.get();

            log.debug("!Exchange Rates obtained from the Database, id={}, currentDate={}",
                    exchangeInfoEntity.getId(), currentDate);

            return exchangeRateRepository.findAllExchangeRate(exchangeInfoEntity.getId());

        } else {

            // Обработка случая, когда нет данных на текущую дату в БД
            if (!requestExchange(currentDate)) {
                log.debug("!Warning, Exchange Rates were not received for the Current Date from the Database, " +
                                "currentDate={}", currentDate);
            }
        }
        // Получение данных на последнюю имеющуюся дату из БД
        // Если данные из внешнего сервиса на текущую даты были успешно сохраннны в БД, получаем эти данные
        // Здесь предполагается, что данных позже текущей даты нет в БД
        Optional<ExchangeInfoEntity> latestExchangeInfoOptional = exchangeInfoRepository.findLatestExchangeInfo();

        if (latestExchangeInfoOptional.isPresent()) {
            ExchangeInfoEntity exchangeInfoEntity = latestExchangeInfoOptional.get();

            log.debug("!Exchange Rates obtained from the Database, id={}, requestDate={}",
                    exchangeInfoEntity.getId(), exchangeInfoEntity.getRequestDate());

            return exchangeRateRepository.findAllExchangeRate(exchangeInfoEntity.getId());

        } else {
            // Обработка случая, когда нет данных в БД
            // Дальнейшая работа приложения будет без данных о курсах валют
            log.error("!Attention, Exchange Rates were not received from the Database, " +
                    "currentDate={}", currentDate);
        }

        return null;
    }

    private boolean requestExchange(LocalDate currentDate) {

        // Преобразование в формат dd.MM.yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = currentDate.format(formatter);

        String resourceUrl = "https://nationalbank.kz/rss/get_rates.cfm?fdate=" + formattedDate;
        String xmlData = fetchXmlData(resourceUrl);

        if (xmlData == null) {
            return false;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xmlData));
            Document document = builder.parse(inputSource);

            NodeList item = document.getElementsByTagName("item");
            List<ExchangeRateDto> exchangeRateDtoList = parseExchangeRates(item);

            Element rootElement = document.getDocumentElement();
            ExchangeInfoDto exchangeInfoDto = parseExchangeInfo(rootElement, exchangeRateDtoList);

            // Сохраним объекты exchangeInfoDTO и exchangeRateDtoList в БД
            if (exchangeInfoDto != null && exchangeRateDtoList.size() > 0) {
                ExchangeInfoEntity exchangeInfoEntity = new ExchangeInfoEntity();
                exchangeInfoEntity.setResource(exchangeInfoDto.link);
                exchangeInfoEntity.setRequestDate(exchangeInfoDto.date);

                Set<ExchangeRateEntity> exchangeRateEntitySet = new HashSet<>();

                for (ExchangeRateDto exchangeRateDto : exchangeRateDtoList) {
                    ExchangeRateEntity exchangeRateEntity = new ExchangeRateEntity();
                    exchangeRateEntity.setCurrencyName(exchangeRateDto.fullname);
                    exchangeRateEntity.setCurrencyCode(exchangeRateDto.title);
                    exchangeRateEntity.setExchangeRate(exchangeRateDto.description);

                    // Сохраняем ссылку на родительскую сущность
                    exchangeRateEntity.setExchangeInfoEntity(exchangeInfoEntity);
                    exchangeRateEntitySet.add(exchangeRateEntity);
                }
                exchangeInfoEntity.setExchangeRateEntities(exchangeRateEntitySet);

                exchangeInfoRepository.save(exchangeInfoEntity);

                log.debug("!Exchange Rates save successfully, id={}, currentDate={}",
                        exchangeInfoEntity.getId(), currentDate);

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("!Attention, there is a problem with the XML parser, required Tags were not found or the XML Structure is broken");
        }

        return false;
    }

    private String fetchXmlData(String resourceUrl) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(resourceUrl, HttpMethod.GET, entity, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {

                return new String(response.getBody(), StandardCharsets.UTF_8);

            } catch (Exception e) {
                e.printStackTrace();
                log.error("!Attention, there is a problem with the XML parser, failed to convert Request result to string");
            }
        }

        return null;
    }

    private List<ExchangeRateDto> parseExchangeRates(NodeList item) {

        List<ExchangeRateDto> exchangeRateDtoList = new ArrayList<>();

        try {
            for (int i = 0; i < item.getLength(); i++) {
                Element itemElement = (Element) item.item(i);
                String title = getTextContent(itemElement, "title");

                // Выборка по необходимым валютам для хранение их в БД
                if (CurrencyType.USD.name().equals(title)||
                    CurrencyType.EUR.name().equals(title)||
                    CurrencyType.RUB.name().equals(title)) {
                    String fullname = getTextContent(itemElement, "fullname");
                    double description = Double.parseDouble(Objects.requireNonNull(getTextContent(itemElement, "description")));

                    ExchangeRateDto exchangeRateDto = new ExchangeRateDto(fullname, title, description);
                    exchangeRateDtoList.add(exchangeRateDto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("!Attention, there is a problem with the XML parser, required Tags not found");
        }

        return exchangeRateDtoList;
    }

    private ExchangeInfoDto parseExchangeInfo(Element rootElement, List<ExchangeRateDto> item) {

        try {
            String link = getTextContent(rootElement, "link");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate date = LocalDate.parse(Objects.requireNonNull(getTextContent(rootElement, "date")), formatter);

            return new ExchangeInfoDto(link, date, item);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("!Attention, there is a problem with the XML parser, the necessary Tags were not found or their Format is broken");
        }

        return null;
    }

    private String getTextContent(Element parentElement, String tagName) {

        NodeList nodeList = parentElement.getElementsByTagName(tagName);

        if (nodeList.getLength() > 0) {

            return nodeList.item(0).getTextContent();

        } else {

            return null;
        }
    }

    @Override
    public List<ExchangeRateDto> getAllExchangeRateDtoByCurrentDate() {

        List<ExchangeRateDto> exchangeRateDtoList = new ArrayList<>();
        List<ExchangeRateEntity> exchangeRateEntityList = gettingRates().stream().toList();

        for (ExchangeRateEntity exchangeRateEntity : exchangeRateEntityList) {
            ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
            exchangeRateDto.setTitle(exchangeRateEntity.getCurrencyCode());
            exchangeRateDto.setFullname(exchangeRateEntity.getCurrencyName());
            exchangeRateDto.setDescription(exchangeRateEntity.getExchangeRate());
            exchangeRateDtoList.add(exchangeRateDto);
        }

        log.debug("!Getting all Exchange Rates");

        return exchangeRateDtoList;
    }
}
