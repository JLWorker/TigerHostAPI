package tgc.plus.authservice.facades.utils;

import lombok.Getter;

@Getter
public enum CommandsNames {

    SAVE("save"),
    UPDATE_PHONE("update_ph"),
    UPDATE_EMAIL("update_em"),
    SEND_RECOVERY_CODE("send_rest"),
    SEND_2AUTH_CODE("send_2th_code");

    private final String name;

    CommandsNames(String name) {
        this.name = name;
    }

}
