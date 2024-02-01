package tgc.plus.authservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
public class ChangeResponse {

    @JsonProperty
    Long version;

    public Long getVersion() {
        return version;
    }

    @JsonSetter
    public void setVersion(Long version) {
        this.version = version;
    }

    @JsonCreator
    public ChangeResponse(Long version) {
        this.version = version;
    }
}
