package lab.solva.user.transaction.respond;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class SuccessMutation implements MutationResponse {
    private Boolean success;
    private String message;
}