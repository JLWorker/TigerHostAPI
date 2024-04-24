package tgc.plus.authservice.configs.utils;

import lombok.Getter;

@Getter
public enum TokenResponseHeader {

    LOGOUT("Logout"),
    EXPIRED("Expired"),
    TWO_FACTOR("2FA-Token");


    private final String name;

    TokenResponseHeader(String name) {
        this.name = name;
    }
}
