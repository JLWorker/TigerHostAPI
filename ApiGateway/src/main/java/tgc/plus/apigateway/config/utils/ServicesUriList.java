package tgc.plus.apigateway.config.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public enum ServicesUriList {

    AUTH_SERVICE("http://localhost:8081"),
    PROXMOX_SERVICE("http://localhost:8085"),
    PROVIDED_SERVICE("http://localhost:8087"),
    FEEDBACK_SERVICE("http://localhost:8083");

    private final String url;

    ServicesUriList(String url) {
        this.url = url;
    }
}
