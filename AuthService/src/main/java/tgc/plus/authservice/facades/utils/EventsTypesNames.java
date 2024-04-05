package tgc.plus.authservice.facades.utils;

import lombok.Getter;

@Getter
public enum EventsTypesNames {

    UPDATE_ACCOUNT("update_account"),
    UPDATE_TOKENS("update_tokens");

    private final String name;

     EventsTypesNames(String name) {
        this.name = name;
    }
}
