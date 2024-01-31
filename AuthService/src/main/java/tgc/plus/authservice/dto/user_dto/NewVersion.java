package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@Getter
public class NewVersion {

    private Map<String, Long> versions;

    public NewVersion(Map<String, Long> versions) {
        this.versions = versions;
    }
}

