package tgc.plus.providedservice.configs.utils;

import lombok.Getter;

@Getter
public enum TokenResponseHeader {

    LOGOUT("Logout"),
    EXPIRED("Expired");


    private final String name;

    TokenResponseHeader(String name) {
        this.name = name;
    }
}
