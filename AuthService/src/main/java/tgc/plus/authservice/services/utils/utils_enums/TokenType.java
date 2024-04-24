package tgc.plus.authservice.services.utils.utils_enums;

import lombok.Getter;

@Getter
public enum TokenType {


    SECURITY("secure"),
    TWO_FACTOR("2fa");

    private final String name;

    TokenType(String name) {
        this.name = name;
    }
}
