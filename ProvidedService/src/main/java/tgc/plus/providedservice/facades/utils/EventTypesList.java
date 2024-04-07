package tgc.plus.providedservice.facades.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public enum EventTypesList {

    UPDATE_VDS_TARIFFS("update_tariffs"),
    UPDATE_CPU_TYPES("update_cpu"),
    UPDATE_RAM_TYPES("update_ram"),
    UPDATE_MEMORY_TYPES("update_memory"),
    UPDATE_OC("update_oc"),
    UPDATE_PERIODS("update_periods"),
    UPDATE_HYPERVISORS("update_hypervisors");

    private final String command;

    EventTypesList(String command) {
        this.command = command;
    }
}
