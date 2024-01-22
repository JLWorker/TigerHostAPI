package tgc.plus.callservice;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;
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
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "testProducer");
        props.put(ProducerConfig.ACKS_CONFIG, "1");
//        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 10240);
//        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }

    @Test
    public void sendMessageToTopic() {

        int i = 10;
        while (i-- > 9) {
//            boolean result = kafkaTemplate.send("call_service",
//                    new TestJson(UUID.randomUUID().toString(), "https://auth.tgc.plus/reset/i9810ska1")).isDone();
//            Assert.isTrue(!result, "failed!");

            String userCode = UUID.randomUUID().toString();
            MessageTest baseMessageTest = new MessageTest(userCode, new SaveUserDataTest("4357480@bk.ru", "sadasASD"));
            ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("call_service", baseMessageTest);
            record.headers().add("method", "save".getBytes());
            kafkaTemplate.send(record);
            System.out.println(userCode + " - code ");
        }
    }

        @Test
        public void sendMessageToCreateVM() {
            String userCode = UUID.randomUUID().toString();
            MessageTest messageTest = new MessageTest("d5546e4d-06dd-4611-8430-ff70284f7d75",new VirtualMachineCreateDataTest(993, "root", "ghtydjJS8732", 2313, "192.168.64.33"));
            ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("call_service", messageTest);
            record.headers().add("method", "send_vm_cr".getBytes());

            kafkaTemplate.send(record);
            System.out.println(userCode + " - code ");
        }

    @Test
    public void sendMessageWarningVM() {
        String userCode = UUID.randomUUID().toString();
        MessageTest messageTest = new MessageTest("d5546e4d-06dd-4611-8430-ff70284f7d75",new VirtualMachineExpireDataTest(3422, "2024-02-10 12:32", 150.00));
        ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("call_service", messageTest);
        record.headers().add("method", "send_vm_wn".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }

    @Test
    public void sendMessageErrorVM() {
        String userCode = UUID.randomUUID().toString();
        MessageTest messageTest = new MessageTest("d5546e4d-06dd-4611-8430-ff70284f7d75",new VirtualMachineExpireDataTest(3422, "2024-02-10 12:32", 150.00));
        ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("call_service", messageTest);
        record.headers().add("method", "send_vm_ex".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }

    @Test
    public void sendMessageRestorePassword() {
        String userCode = UUID.randomUUID().toString();
        MessageTest messageTest = new MessageTest("d5546e4d-06dd-4611-8430-ff70284f7d75",new PasswordRestoreDataTest("http://192.168.90.103/restore"));
        ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("call_service", messageTest);
        record.headers().add("method", "send_rest".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }

    @Test
    public void sendMessageTwoAuthCode() {
        String userCode = UUID.randomUUID().toString();
        MessageTest messageTest = new MessageTest("d5546e4d-06dd-4611-8430-ff70284f7d75",new TwoAuthDataTest(987775));
        ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("call_service", messageTest);
        record.headers().add("method", "send_2th_code".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }


    @Test
    public void sendMessageToChangePhone() {
            String userCode = UUID.randomUUID().toString();
            MessageTest baseMessageTest = new MessageTest("d5546e4d-06dd-4611-8430-ff70284f7d75",new EditPhoneDataTest("89244380870"));
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
        MessageTest baseMessageTest = new MessageTest("d5546e4d-06dd-4611-8430-ff70284f7d75", new EditEmailDataTest("567842@bk.ru"));
        ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("call_service", baseMessageTest);
        record.headers().add("method", "update_em".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }
}
