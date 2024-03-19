package tgc.plus.proxmoxservice.exceptions.proxmox_exceptions;

public class UnexpectedJsonNodeType extends RuntimeException{
    public UnexpectedJsonNodeType(String message) {
        super(message);
    }
}
