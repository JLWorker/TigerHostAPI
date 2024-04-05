package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

public interface TariffEntity {
    Boolean getActiveStatus();
    String getUniqueElement();

}
