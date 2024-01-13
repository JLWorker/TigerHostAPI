package tgc.plus.callservice.listeners.utils;

public enum CommandsName {
    //Command Pattern
    SAVE("save"),
    EDIT_PHONE("update_ph"),
    UPDATE_EMAIL("update_em"),
    SEND_EMAIL("send_em"),
    SEND_PHONE("send_ph");

    private final String name;

    CommandsName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
