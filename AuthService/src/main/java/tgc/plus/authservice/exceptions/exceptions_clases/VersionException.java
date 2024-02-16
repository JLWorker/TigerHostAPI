package tgc.plus.authservice.exceptions.exceptions_clases;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.annotation.Documented;

@Getter
@NoArgsConstructor
public class VersionException extends RuntimeException{

    private Long newVersion;

    private String message;

    public VersionException(String message) {
        this.message = message;
    }

    public VersionException(Long newVersion, String message) {
        this.newVersion = newVersion;
        this.message = message;
    }
}
