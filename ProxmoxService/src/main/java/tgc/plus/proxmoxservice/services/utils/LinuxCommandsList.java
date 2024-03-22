package tgc.plus.proxmoxservice.services.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public enum LinuxCommandsList {

    USER_ADD("useradd -m %s -p %s");

    private String command;

    LinuxCommandsList(String command) {
        this.command = command;
    }
}
