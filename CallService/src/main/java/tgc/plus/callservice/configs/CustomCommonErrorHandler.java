package tgc.plus.callservice.configs;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomCommonErrorHandler implements CommonErrorHandler {

    @Override
    public void handleBatch(Exception thrownException, ConsumerRecords<?, ?> data, Consumer<?, ?> consumer, MessageListenerContainer container, Runnable invokeListener) {
        log.error(String.format("Exception for %s, exception: %s, \n listener id: %s", data.count(), thrownException.getMessage(), container.getListenerId()));
    }
}
