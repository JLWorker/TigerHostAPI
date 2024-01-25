package tgc.plus.authservice.services.utils;


import lombok.Getter;
import lombok.ToString;

@Getter
public enum RoleList {

    USER("user"),
    ADMIN("admin");

    private String name;

    RoleList(String name) {
        this.name = name;
    }
}
