package tgc.plus.providedservice.facades.utils;

import lombok.Getter;

@Getter
public enum MessageAvailableList {

    PUBLIC("public");

    private final String key;

    MessageAvailableList(String key) {
        this.key = key;
    }
}
