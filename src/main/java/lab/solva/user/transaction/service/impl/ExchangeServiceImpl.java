package lab.solva.user.transaction.service.impl;

import io.grpc.stub.StreamObserver;
import lab.solva.user.transaction.ExchangeInfoProto;
import lab.solva.user.transaction.ExchangeInfoRequest;
import lab.solva.user.transaction.ExchangeInfoResponse;
import lab.solva.user.transaction.ExchangeRateProto;
import lab.solva.user.transaction.enumeration.CurrencyType;
import lab.solva.user.transaction.model.ExchangeInfoRateEntity;
import lab.solva.user.transaction.repository.ExchangeInfoRateRepository;
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

    private final ExchangeInfoRateRepository exchangeInfoRateRepository;

    @Override
    public void gettingRates(ExchangeInfoRequest request, StreamObserver<ExchangeInfoResponse> responseObserver) {

        // Using to construct Responses
        ExchangeInfoResponse.Builder responseBuilder = ExchangeInfoResponse.newBuilder();

        // Getting the current date
        LocalDate currentDate = LocalDate.now();

        // Retrieving data for the current date from the database
        Optional<ExchangeInfoRateEntity> currentExchangeInfoOptional =
                exchangeInfoRateRepository.findCurrentExchangeInfoRate(currentDate);

        if (currentExchangeInfoOptional.isPresent()) {

            ExchangeInfoRateEntity exchangeInfoEntity = currentExchangeInfoOptional.get();

            log.debug("!Exchange Rates obtained from the Database, currentDate={}", currentDate);

            // Adding an exchange info to a response
            ExchangeInfoProto exchangeInfoProto = convertToExchangeInfoProto(exchangeInfoEntity);
            responseBuilder.setExchangeInfo(exchangeInfoProto);

        } else {

            // Handling the case when there is no data for the current date in the database
            if (requestExchangeFailed(currentDate)) {
                log.debug("!Warning, Exchange Rates were not received for the Current Date from the Database, " +
                        "currentDate={}", currentDate);
            }

            // Receiving data for the last available date from the database
            // If data from an external service for the current date was successfully saved in the database, we obtain this data
            // Here it is assumed that there is no data in the database after the current date
            // Before that calculate the latest Request Date
            LocalDate latestRequestDate = exchangeInfoRateRepository.findLatestRequestDate();
            Optional<ExchangeInfoRateEntity> latestExchangeInfoRateOptional =
                    exchangeInfoRateRepository.findLatestExchangeInfoRate(latestRequestDate);

            if (latestExchangeInfoRateOptional.isPresent()) {

                ExchangeInfoRateEntity exchangeInfoEntity = latestExchangeInfoRateOptional.get();

                log.debug("!Exchange Rates obtained from the Database, requestDate={}",
                        exchangeInfoEntity.getRequestDate());

                // Adding an exchange info to a response
                ExchangeInfoProto exchangeInfoProto = convertToExchangeInfoProto(exchangeInfoEntity);
                responseBuilder.setExchangeInfo(exchangeInfoProto);

            } else {

                // Handling the case when there is no data in the database
                // Further operation of the application will be without data on exchange rates
                log.error("!Attention, Exchange Rates were not received from the Database, " +
                        "currentDate={}", currentDate);
            }
        }

        // Build the ExchangeRatesResponse
        ExchangeInfoResponse exchangeInfoResponse = responseBuilder.build();

        // Send Response
        responseObserver.onNext(exchangeInfoResponse);
        responseObserver.onCompleted();
    }

    @Override
    public Map<String, Double> gettingRates() {

        // Getting the current date
        LocalDate currentDate = LocalDate.now();

        // Retrieving data for the current date from the database
        Optional<ExchangeInfoRateEntity> currentExchangeInfoRateOptional =
                exchangeInfoRateRepository.findCurrentExchangeInfoRate(currentDate);

        if (currentExchangeInfoRateOptional.isPresent()) {
            ExchangeInfoRateEntity exchangeInfoRateEntity = currentExchangeInfoRateOptional.get();

            log.debug("!Exchange Rates obtained from the Database, currentDate={}", currentDate);

            return exchangeInfoRateEntity.getExchangeRates();

        } else {

            // Handling the case when there is no data for the current date in the database
            if (requestExchangeFailed(currentDate)) {
                log.debug("!Warning, Exchange Rates were not received for the Current Date from the Database, " +
                                "currentDate={}", currentDate);
            }
        }

        // Receiving data for the last available date from the database
        // If data from an external service for the current date was successfully saved in the database, we obtain this data
        // Here it is assumed that there is no data in the database after the current date
        // Before that calculate the latest Request Date
        LocalDate latestRequestDate = exchangeInfoRateRepository.findLatestRequestDate();
        Optional<ExchangeInfoRateEntity> latestExchangeInfoRateOptional =
                exchangeInfoRateRepository.findLatestExchangeInfoRate(latestRequestDate);

        if (latestExchangeInfoRateOptional.isPresent()) {
            ExchangeInfoRateEntity exchangeInfoRateEntity = latestExchangeInfoRateOptional.get();

            log.debug("!Exchange Rates obtained from the Database, requestDate={}",
                    exchangeInfoRateEntity.getRequestDate());

            return exchangeInfoRateEntity.getExchangeRates();

        } else {

            // Handling the case when there is no data in the database
            // Further operation of the application will be without data on exchange rates
            log.error("!Attention, Exchange Rates were not received from the Database, " +
                    "currentDate={}", currentDate);
        }

        return null;
    }

    private boolean requestExchangeFailed(LocalDate currentDate) {

        // Convert to dd.MM.yyyy format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = currentDate.format(formatter);

        String resourceUrl = "https://nationalbank.kz/rss/get_rates.cfm?fdate=" + formattedDate;
        String xmlData = fetchXmlData(resourceUrl);

        if (xmlData == null) {
            return true;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xmlData));
            Document document = builder.parse(inputSource);
            Element rootElement = document.getDocumentElement();

            NodeList item = document.getElementsByTagName("item");
            Map<String, Double> exchangeRateMap = parseExchangeRates(item);

            // Saving data from an external service in the database
            try {
                String resource = getTextContent(rootElement, "link");
                LocalDate date = LocalDate.parse(Objects.requireNonNull(
                        getTextContent(rootElement, "date")), formatter);

                ExchangeInfoRateEntity exchangeInfoRateEntity = new ExchangeInfoRateEntity();
                exchangeInfoRateEntity.setRequestDate(date);
                exchangeInfoRateEntity.setResource(resource);
                exchangeInfoRateEntity.setExchangeRates(exchangeRateMap);

                if (exchangeRateMap.isEmpty()) {
                    exchangeInfoRateEntity.setLatest(false);
                    exchangeInfoRateRepository.save(exchangeInfoRateEntity);

                    log.debug("!Exchange Rates are not available on currentDate={}", currentDate);

                    return true;

                } else {

                    exchangeInfoRateEntity.setLatest(true);
                    exchangeInfoRateRepository.save(exchangeInfoRateEntity);

                    log.debug("!Exchange Rates save successfully, currentDate={}", currentDate);

                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();

                log.error("!Attention, there is a problem with the XML parser, the necessary Tags were not found or their Format is broken");
            }

        } catch (Exception e) {
            e.printStackTrace();

            log.error("!Attention, there is a problem with the XML parser, required Tags were not found or the XML Structure is broken");
        }

        return true;
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

    private Map<String, Double> parseExchangeRates(NodeList item) {

        Map<String, Double> exchangeRateMap = new HashMap<>();

        try {
            for (int i = 0; i < item.getLength(); i++) {
                Element itemElement = (Element) item.item(i);
                String title = getTextContent(itemElement, "title");

                // Selection of required currencies for storing them in the database
                if (CurrencyType.USD.name().equals(title)||
                    CurrencyType.EUR.name().equals(title)||
                    CurrencyType.RUB.name().equals(title)) {
                    double description = Double.parseDouble(Objects.requireNonNull(
                            getTextContent(itemElement, "description")));

                    exchangeRateMap.put(title, description);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            log.error("!Attention, there is a problem with the XML parser, required Tags not found");
        }

        return exchangeRateMap;
    }

    private String getTextContent(Element parentElement, String tagName) {

        NodeList nodeList = parentElement.getElementsByTagName(tagName);

        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();

        } else {
            return null;
        }
    }

    private ExchangeInfoProto convertToExchangeInfoProto(ExchangeInfoRateEntity exchangeInfoRateEntity) {

        Map<String, Double> exchangeRates = exchangeInfoRateEntity.getExchangeRates();

        ExchangeInfoProto.Builder builder = ExchangeInfoProto.newBuilder()
                .setResource(exchangeInfoRateEntity.getResource())
                // Set date format yyyy-MM-dd
                .setRequestDate(exchangeInfoRateEntity.getRequestDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Create list of object ExchangeRateProto foreach key-value in Map
        for (Map.Entry<String, Double> entry : exchangeRates.entrySet()) {
            String currencyCode = entry.getKey();
            Double exchangeRate = entry.getValue();
            ExchangeRateProto exchangeRateProto = ExchangeRateProto.newBuilder()
                    .setCurrencyCode(currencyCode)
                    .setExchangeRate(exchangeRate)
                    .build();

            builder.addExchangeRates(exchangeRateProto);
        }

        return builder.build();
    }
}
