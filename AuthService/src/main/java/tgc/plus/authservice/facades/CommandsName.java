package tgc.plus.authservice.facades;

import lombok.Getter;

@Getter
public enum CommandsName {

    SAVE("save"),
    UPDATE_PHONE("update_ph"),
    UPDATE_EMAIL("update_em"),
    SEND_RECOVERY_CODE("send_rest"),
    SEND_2AUTH_CODE("send_2th_code");


    private final String name;

    CommandsName(String name) {
        this.name = name;
    }

}
