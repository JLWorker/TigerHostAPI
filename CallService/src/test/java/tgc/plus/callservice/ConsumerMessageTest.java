package tgc.plus.callservice;

import org.springframework.scheduling.annotation.Async;
import tgc.plus.callservice.dto.MessageElement;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;
import tgc.plus.callservice.dto.message_payloads.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootTest
//@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://31.200.225.93:9199", "port=9199" })
public class ConsumerMessageTest {

//    @Value("${spring.kafka.bootstrap-servers}")
//    public static String server;

    public static KafkaTemplate<Long, MessageElement> kafkaTemplate;

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
            MessageElement baseMessageElement = new MessageElement(userCode, new SaveUserData("4357480@bk.ru", "asdsadkKS"));
            ProducerRecord<Long, MessageElement> record = new ProducerRecord<>("call_service", baseMessageElement);
            record.headers().add("method", "save".getBytes());

            kafkaTemplate.send(record);
            System.out.println(userCode + " - code ");
        }
    }

        @Test
        public void sendMessageToCreateVM() {
            String userCode = UUID.randomUUID().toString();
            MessageElement messageElement = new MessageElement("0d19ec91-ae22-4859-8b8d-8895eecee312",new VirtualMachineCreateData(993, "root", "ghtydjJS8732", 2313, "192.168.64.33"));
            ProducerRecord<Long, MessageElement> record = new ProducerRecord<>("call_service", messageElement);
            record.headers().add("method", "send_vm_cr".getBytes());

            kafkaTemplate.send(record);
            System.out.println(userCode + " - code ");
        }

    @Test
    public void sendMessageWarningVM() {
        String userCode = UUID.randomUUID().toString();
        MessageElement messageElement = new MessageElement("0d19ec91-ae22-4859-8b8d-8895eecee312",new VirtualMachineExpireData(3422, "2024-02-10 12:32", 150.00));
        ProducerRecord<Long, MessageElement> record = new ProducerRecord<>("call_service", messageElement);
        record.headers().add("method", "send_vm_wn".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }

    @Test
    public void sendMessageErrorVM() {
        String userCode = UUID.randomUUID().toString();
        MessageElement messageElement = new MessageElement("0d19ec91-ae22-4859-8b8d-8895eecee312",new VirtualMachineExpireData(3422, "2024-02-10 12:32", 150.00));
        ProducerRecord<Long, MessageElement> record = new ProducerRecord<>("call_service", messageElement);
        record.headers().add("method", "send_vm_ex".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }

    @Test
    public void sendMessageRestorePassword() {
        String userCode = UUID.randomUUID().toString();
        MessageElement messageElement = new MessageElement("0d19ec91-ae22-4859-8b8d-8895eecee312",new PasswordRestoreData("http://192.168.90.103/restore"));
        ProducerRecord<Long, MessageElement> record = new ProducerRecord<>("call_service", messageElement);
        record.headers().add("method", "send_rest".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }

    @Test
    public void sendMessageTwoAuthCode() {
        String userCode = UUID.randomUUID().toString();
        MessageElement messageElement = new MessageElement("0d19ec91-ae22-4859-8b8d-8895eecee312",new TwoAuthCode(987775));
        ProducerRecord<Long, MessageElement> record = new ProducerRecord<>("call_service", messageElement);
        record.headers().add("method", "send_2th_code".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }


    @Test
    public void sendMessageToChangePhone() {
            String userCode = UUID.randomUUID().toString();
            MessageElement baseMessageElement = new MessageElement("0d19ec91-ae22-4859-8b8d-8895eecee312",new EditPhoneData("89244273168"));
            ProducerRecord<Long, MessageElement> record = new ProducerRecord<>("call_service", baseMessageElement);
            record.headers().add("method", "update_ph".getBytes());

            kafkaTemplate.send(record);
            System.out.println(userCode + " - code ");
        }

//
//
    @Test
    public void sendMessageToChangeEmail() {
        String userCode = UUID.randomUUID().toString();
        MessageElement baseMessageElement = new MessageElement("0d19ec91-ae22-4859-8b8d-8895eecee312", new EditEmailData("4357480@bk.ru"));
        ProducerRecord<Long, MessageElement> record = new ProducerRecord<>("call_service", baseMessageElement);
        record.headers().add("method", "update_em".getBytes());

        kafkaTemplate.send(record);
        System.out.println(userCode + " - code ");
    }
//
//
//    @Test
//    public void sendMessageToEmail() {
//        HashMap<String, String> testMap = new HashMap<>();
//        testMap.put("subject", "Привет, это Тайгерхост!");
//        testMap.put("text", "Это тестовое сообщение!");
//
//        MessageElement baseMessageElement = new MessageElement("04e4c7b5-9e62-4df6-b10f-a88249742e3f", testMap);
//        ProducerRecord<Long, MessageElement> record = new ProducerRecord<>("call_service", baseMessageElement);
//        record.headers().add("method", "send_em".getBytes());
//
//        kafkaTemplate.send(record);
////        System.out.println(userCode + " - code ");
//    }
}
