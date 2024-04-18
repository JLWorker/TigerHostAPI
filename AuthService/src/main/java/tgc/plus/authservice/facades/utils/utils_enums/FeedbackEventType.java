package tgc.plus.authservice.facades.utils.utils_enums;

import lombok.Getter;

@Getter
public enum FeedbackEventType {

    UPDATE_ACCOUNT("update_account"),
    UPDATE_TOKENS("update_tokens");

    private final String name;

     FeedbackEventType(String name) {
        this.name = name;
    }
}
