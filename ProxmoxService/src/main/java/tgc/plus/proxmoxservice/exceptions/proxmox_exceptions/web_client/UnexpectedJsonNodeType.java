package tgc.plus.proxmoxservice.exceptions.proxmox_exceptions.web_client;

public class UnexpectedJsonNodeType extends Exception{
    public UnexpectedJsonNodeType(String message) {
        super(message);
    }
}
