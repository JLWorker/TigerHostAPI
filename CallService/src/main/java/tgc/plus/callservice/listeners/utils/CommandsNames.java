package tgc.plus.callservice.listeners.utils;

public enum CommandsNames {
    //Command Pattern
    SAVE("save"),
    UPDATE_PHONE("update_ph"),
    UPDATE_EMAIL("update_em"),
    SEND_EMAIL("send_em");

    private final String name;

    CommandsNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
