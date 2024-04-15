package tgc.plus.authservice.services.utils;

import lombok.Getter;

@Getter
public enum TokenExpiredDate {

    SECURITY("secure"),
    TWO_FACTOR("2fa");

    private final String name;

    TokenExpiredDate(String name) {
        this.name = name;
    }
}
