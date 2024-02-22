package tgc.plus.authservice.services.utils;

import lombok.Getter;

@Getter
public enum TokensList {

    SECURITY("secure"),
    TWO_FACTOR("2fa");

    private final String name;

    TokensList(String name) {
        this.name = name;
    }
}
