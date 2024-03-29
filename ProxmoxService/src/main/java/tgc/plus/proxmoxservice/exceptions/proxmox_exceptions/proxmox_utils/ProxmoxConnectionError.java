package tgc.plus.proxmoxservice.exceptions.proxmox_exceptions.proxmox_utils;

import org.apache.kafka.common.internals.FatalExitError;

public class ProxmoxConnectionError extends Error {
    public ProxmoxConnectionError(String message) {
        super(message);
    }
}
