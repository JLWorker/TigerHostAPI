package tgc.plus.apigateway.filters.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Getter
public enum TokenRequestHeader {

    ACCESS("Authorization"),
    TWO_FACTOR("2FA-Token");

    private final String headerName;

}
