package tgc.plus.authservice.facades.utils;

import lombok.Getter;

@Getter
public enum EventsTypesNames {

    UPDATE_ACCOUNT("update_account_info"),
    UPDATE_TOKENS("update_user_tokens");

    private final String name;

     EventsTypesNames(String name) {
        this.name = name;
    }
}
