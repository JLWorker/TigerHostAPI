package tgc.plus.callservice;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.dto.*;

import java.util.*;

@SpringBootTest
//@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://31.200.225.93:9199", "port=9199" })
public class ConsumerMessageTest {

//    @Value("${spring.kafka.bootstrap-servers}")
//    public static String server;

    public static KafkaTemplate<Long, MessageTest> kafkaTemplate;

    @BeforeAll
    public static void initTestProducer(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "31.200.225.93:9199");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        DefaultKafkaProducerFactory<Long, MessageTest> producerFactory = new DefaultKafkaProducerFactory<>(props);
        producerFactory.setProducerPerThread(true);
        kafkaTemplate = new KafkaTemplate<>(producerFactory);
    }

    @Test
    public void sendMessageToTopic() {

//        int i = 10;
//        while (i-- > 9)
//            boolean result = kafkaTemplate.send("call_service",
//                    new TestJson(UUID.randomUUID().toString(), "https://auth.tgc.plus/reset/i9810ska1")).isDone();
//            Assert.isTrue(!result, "failed!");
        Flux<Void> res = Flux.range(1, 15000).flatMap(el->{
                String userCode = UUID.randomUUID().toString();
                MessageTest baseMessageTest = new MessageTest(userCode, new SaveUserDataTest(String.format("%s@bk.ru", new Random().nextInt()), "sadasASD463"));
                ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("callservice", baseMessageTest);
                record.headers().add("method", "save".getBytes());
                kafkaTemplate.send(record);
                System.out.println(userCode + " - code ");
                return Mono.empty();
        });
        res.subscribe();
    }

        @Test
        public void sendMessageToCreateVM() {
            String userCode = UUID.randomUUID().toString();
            MessageTest messageTest = new MessageTest("f3fc21e3-4619-4a7f-aff7-b43ff419a88e",new VirtualMachineCreateDataTest(993, "root", "ghtydjJS8732", 2313, "192.168.64.33"));
            ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("call_service", messageTest);
            record.headers().add("method", "send_vm_cr".getBytes());

            kafkaTemplate.send(record);
            System.out.println(userCode + " - code ");
        }

    @Test
    public void sendMessageWarningVM() {
        String userCode = UUID.randomUUID().toString();
        MessageTest messageTest = new MessageTest("f3fc21e3-4619-4a7f-aff7-b43ff419a88e",new VirtualMachineExpireDataTest(3422, "2024-02-10 12:32", 150.00));
        ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("call_service", messageTest);
        record.headers().add("method", "send_vm_wn".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }

    @Test
    public void sendMessageErrorVM() {
        String userCode = UUID.randomUUID().toString();
        MessageTest messageTest = new MessageTest("f3fc21e3-4619-4a7f-aff7-b43ff419a88e",new VirtualMachineExpireDataTest(3422, "2024-02-10 12:32", 150.00));
        ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("call_service", messageTest);
        record.headers().add("method", "send_vm_ex".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }

    @Test
    public void sendMessageRestorePassword() {
        String userCode = UUID.randomUUID().toString();
        MessageTest messageTest = new MessageTest("f3fc21e3-4619-4a7f-aff7-b43ff419a88e",new PasswordRestoreDataTest("http://192.168.90.103/restore"));
        ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("call_service", messageTest);
        record.headers().add("method", "send_rest".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }

    @Test
    public void sendMessageTwoAuthCode() {
        String userCode = UUID.randomUUID().toString();
        MessageTest messageTest = new MessageTest("f3fc21e3-4619-4a7f-aff7-b43ff419a88e",new TwoAuthDataTest(987775));
        ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("call_service", messageTest);
        record.headers().add("method", "send_2th_code".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }


    @Test
    public void sendMessageToChangePhone() {
            String userCode = UUID.randomUUID().toString();
            MessageTest baseMessageTest = new MessageTest("f3fc21e3-4619-4a7f-aff7-b43ff419a88e",new EditPhoneDataTest("89244380870"));
            ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("call_service", baseMessageTest);
            record.headers().add("method", "update_ph".getBytes());

            kafkaTemplate.send(record);
            System.out.println(userCode + " - code ");
        }

//
//
    @Test
    public void sendMessageToChangeEmail() {
        String userCode = UUID.randomUUID().toString();
        MessageTest baseMessageTest = new MessageTest("27ccff25-d955-4b97-9d7a-1b7b0f15c896", new EditEmailDataTest("53422346@bk.ru"));
        ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("call_service", baseMessageTest);
        record.headers().add("method", "update_em".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }
}
