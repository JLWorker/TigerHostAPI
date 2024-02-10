package tgc.plus.authservice.facades.utils;

import lombok.Getter;

@Getter
public enum CommandsForMessage {

    SAVE("save"),

    EDIT_PHONE("update_ph"),

    UPDATE_EMAIL("update_em"),

    SEND_RECOVERY_CODE("send_rest"),

    SEND_2AUTH_CODE("send_2th_code");


    private final String name;

    CommandsForMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
