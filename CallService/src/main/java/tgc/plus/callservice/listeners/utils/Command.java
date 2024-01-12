package tgc.plus.callservice.listeners.utils;

import tgc.plus.callservice.dto.MessageData;

public interface Command {
     void execution(MessageData messageData);
}
