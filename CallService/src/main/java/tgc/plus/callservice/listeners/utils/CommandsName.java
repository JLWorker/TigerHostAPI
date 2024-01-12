package tgc.plus.callservice.listeners.utils;

public enum CommandsName {
    //Command Pattern
    SAVE("save"),
    SAVE_PHONE("save_ph"),
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
