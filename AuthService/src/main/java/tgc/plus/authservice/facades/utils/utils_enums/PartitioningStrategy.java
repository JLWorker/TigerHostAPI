package tgc.plus.authservice.facades.utils.utils_enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum PartitioningStrategy {

    MESSAGES_MAKING_CHANGES("change"),

    BASIC_MESSAGES("other");

    private String strategy;

     PartitioningStrategy(String strategy) {
        this.strategy = strategy;
    }
}
