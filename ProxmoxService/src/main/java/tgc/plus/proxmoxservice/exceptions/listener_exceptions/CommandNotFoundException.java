package tgc.plus.proxmoxservice.exceptions.listener_exceptions;

public class CommandNotFoundException extends RuntimeException {

    public CommandNotFoundException(String message) {
        super(message);
    }
}
