package tgc.plus.callservice.listeners.utils;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.stereotype.Component;

import java.util.Collection;

//@Component
//public class ConsumerRebalanceListener implements ConsumerAwareRebalanceListener {
//
//    @Override
//    public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
//
//    }
//
//}
