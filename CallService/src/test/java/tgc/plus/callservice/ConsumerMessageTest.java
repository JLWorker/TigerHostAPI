package tgc.plus.callservice;

import tgc.plus.callservice.dto.MessageData;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
//@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://31.200.225.93:9199", "port=9199" })
public class ConsumerMessageTest {

//    @Value("${spring.kafka.bootstrap-servers}")
//    public static String server;

    public static KafkaTemplate<Long, MessageData> kafkaTemplate;

    @BeforeAll
    public static void initTestProducer(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "31.200.225.93:9199");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "testProducer");
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 10240);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }

    @Test
    public void sendMessageToTopic() {

        int i = 10;
        while (i-- > 8) {
//            boolean result = kafkaTemplate.send("call_service",
//                    new TestJson(UUID.randomUUID().toString(), "https://auth.tgc.plus/reset/i9810ska1")).isDone();
//            Assert.isTrue(!result, "failed!");

            MessageData baseMessageData = new MessageData(UUID.randomUUID().toString(), "4357480@bk.ru", null, null);
            ProducerRecord<Long, MessageData> record = new ProducerRecord<>("call_service", baseMessageData);
            String userCode = UUID.randomUUID().toString();
            record.headers().add("method", "save".getBytes());

            kafkaTemplate.send(record);
            System.out.println(userCode + " - code ");
        }
    }


    @Test
    public void sendMessageToChangePhone() {


            MessageData baseMessageData = new MessageData("a08d47f0-837e-450c-9ae8-968d50d4d6fc", null, "89244273168", null);
            ProducerRecord<Long, MessageData> record = new ProducerRecord<>("call_service", baseMessageData);
            String userCode = UUID.randomUUID().toString();
            record.headers().add("method", "save_ph".getBytes());

            kafkaTemplate.send(record);
            System.out.println(userCode + " - code ");
        }
}
