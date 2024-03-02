package tgc.plus.callservice;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.*;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.dto.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
//@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://31.200.225.93:9199", "port=9199" })
public class ConsumerMessageTest {

//    @Value("${spring.kafka.bootstrap-servers}")
//    public static String server;

    public static KafkaTemplate<String, MessageTest> kafkaTemplate;
    @BeforeAll
    public static void initTestProducer(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "31.200.225.93:9189");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        DefaultKafkaProducerFactory<String, MessageTest> producerFactory = new DefaultKafkaProducerFactory<>(props);
        producerFactory.setProducerPerThread(true);
        kafkaTemplate = new KafkaTemplate<>(producerFactory);
    }

    @Test
    public void sendMessageToTopic() {

        Flux<Void> res = Flux.range(1, 30000).flatMapSequential(el->{
                String userCode = UUID.randomUUID().toString();
                MessageTest baseMessageTest = new MessageTest(userCode, new SaveUserDataTest(UUID.randomUUID().toString(), "sadasASD463"));
                ProducerRecord<String, MessageTest> record = new ProducerRecord<>("callservice", baseMessageTest);
                record.headers().add("method", "save".getBytes());
                kafkaTemplate.send(record);
                System.out.println(userCode + " - code " + el);
                return Mono.empty();
        });
        res.subscribe();
    }

        @Test
        public void sendMessageToCreateVM() {
            String userCode = UUID.randomUUID().toString();
            MessageTest messageTest = new MessageTest("f3fc21e3-4619-4a7f-aff7-b43ff419a88e",new VirtualMachineCreateDataTest(993, "root", "ghtydjJS8732", 2313, "192.168.64.33"));
            ProducerRecord<String, MessageTest> record = new ProducerRecord<>("call_service", messageTest);
            record.headers().add("method", "send_vm_cr".getBytes());

            kafkaTemplate.send(record);
            System.out.println(userCode + " - code ");
        }

    @Test
    public void sendMessageWarningVM() {
        String userCode = UUID.randomUUID().toString();
        MessageTest messageTest = new MessageTest("f3fc21e3-4619-4a7f-aff7-b43ff419a88e",new VirtualMachineExpireDataTest(3422, "2024-02-10 12:32", 150.00));
        ProducerRecord<String, MessageTest> record = new ProducerRecord<>("call_service", messageTest);
        record.headers().add("method", "send_vm_wn".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }

    @Test
    public void sendMessageErrorVM() {
        String userCode = UUID.randomUUID().toString();
        MessageTest messageTest = new MessageTest("f3fc21e3-4619-4a7f-aff7-b43ff419a88e",new VirtualMachineExpireDataTest(3422, "2024-02-10 12:32", 150.00));
        ProducerRecord<String, MessageTest> record = new ProducerRecord<>("call_service", messageTest);
        record.headers().add("method", "send_vm_ex".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }

    @Test
    public void sendMessageRestorePassword() {
        String userCode = UUID.randomUUID().toString();
        MessageTest messageTest = new MessageTest("0fa6c6be-b388-4937-9fe5-8c6e2f0c7120",new PasswordRestoreDataTest("http://192.168.90.103/restore"));
        ProducerRecord<String, MessageTest> record = new ProducerRecord<>("callservice", messageTest);
        record.headers().add("method", "send_rest".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }

    @Test
    public void sendMessageToChangePhone() {
            String userCode = UUID.randomUUID().toString();
            MessageTest baseMessageTest = new MessageTest("f3fc21e3-4619-4a7f-aff7-b43ff419a88e",new EditPhoneDataTest("89244380870"));
            ProducerRecord<String, MessageTest> record = new ProducerRecord<>("callservice", baseMessageTest);
            record.headers().add("method", "update_ph".getBytes());

            kafkaTemplate.send(record);
            System.out.println(userCode + " - code ");
        }

//
//
    @Test
    public void sendMessageToChangeEmail() {
        String userCode = UUID.randomUUID().toString();
        MessageTest baseMessageTest = new MessageTest("d1cbc561-b978-4fc7-82c2-edc8d77032bc", new EditEmailDataTest("567842bm@bk.ru"));
        ProducerRecord<String, MessageTest> record = new ProducerRecord<>("callservice", baseMessageTest);
        record.headers().add("method", "update_em".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }
}
