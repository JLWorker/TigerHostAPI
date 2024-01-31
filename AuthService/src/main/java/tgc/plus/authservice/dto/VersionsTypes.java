package tgc.plus.authservice.dto;

import lombok.Getter;

@Getter
public enum VersionsTypes {
    USER_VERSION("user_version"),
    DEVICE_VERSION("device_version"),
    TOKEN_VERSION("token_version");

    private final String name;

    VersionsTypes(String name) {
        this.name = name;
    }
}
