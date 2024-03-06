package tgc.plus.feedbackgateaway.facade.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum EventTypes {

    HEARTBEATS("heartbeats"),
    SIMPLE("simple");

    private String name;

    EventTypes(String name) {
        this.name = name;
    }
}
