package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class UserChangeContactResponse {

    private Map<String, Object> data;

    public UserChangeContactResponse(Map<String, Object> data) {
        this.data = data;
    }
}

