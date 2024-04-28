package tgc.plus.authservice.configs.utils;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.InvalidRecordException;
import org.apache.kafka.common.utils.Utils;
import tgc.plus.authservice.facades.utils.utils_enums.PartitioningStrategy;

import java.util.Map;

public class CustomPartitioner implements Partitioner {

    private final Integer CHANGE_MESSAGE_PARTITION = 0;
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {

        if (key.toString().isBlank() || !(key instanceof String))
            throw new InvalidRecordException("Message must have a key");

        int partitionCounts = cluster.partitionCountForTopic(topic);

        if (key.toString().equals(PartitioningStrategy.MESSAGES_MAKING_CHANGES.getStrategy()))
            return CHANGE_MESSAGE_PARTITION;
        else {
            int msgPartition = Math.abs(Utils.murmur2(valueBytes)) % partitionCounts;
            return msgPartition== CHANGE_MESSAGE_PARTITION ? msgPartition+1 : msgPartition;
        }
    }

    @Override
    public void close() {

    }
    @Override
    public void configure(Map<String, ?> configs) {

    }
}
