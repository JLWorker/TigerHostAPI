package tgc.plus.callservice.listeners.utils;

import tgc.plus.callservice.dto.MessageElement;

public interface Command {
     void execution(MessageElement messageElement);

     void executionForSender(String method, MessageElement messageElement);
}
