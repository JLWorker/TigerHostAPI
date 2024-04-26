package tgc.plus.authservice.services.utils.factories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.exceptions.exceptions_elements.CommandNotFoundException;
import tgc.plus.authservice.services.utils.utils_enums.TokenType;

@Component
public class TokenDateFactory {

    @Value("${jwt.security.access.expired.ms}")
    private Long accessTokenExpiredDate;

    @Value("${jwt.2fa.access.expired.ms}")
    private Long twoFaTokenExpiredDate;

    public Mono<Long> getTokenDateByType(TokenType type){
        return switch (type){
            case SECURITY -> Mono.just(accessTokenExpiredDate);
            case TWO_FACTOR -> Mono.just(twoFaTokenExpiredDate);
            default -> Mono.error(new CommandNotFoundException(String.format("Elem with name %s not found", type)));
        };
    }


}
