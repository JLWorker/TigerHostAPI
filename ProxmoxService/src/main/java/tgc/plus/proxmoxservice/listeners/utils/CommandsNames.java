package tgc.plus.proxmoxservice.listeners.utils;

public enum CommandsNames {
    //Command Pattern
    CREATE_NEW_VM("create_vm");

    private final String name;

    CommandsNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
