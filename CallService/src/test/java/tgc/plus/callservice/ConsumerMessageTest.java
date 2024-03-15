//package tgc.plus.callservice;
//
//import org.apache.kafka.clients.producer.KafkaProducerTest;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.apache.logging.log4j.core.tools.Generate;
//import org.apache.logging.log4j.simple.SimpleLogger;
//import org.apache.logging.log4j.spi.ExtendedLogger;
//import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;
//import org.apache.logging.slf4j.Log4jLogger;
//import org.apache.logging.slf4j.Log4jMarkerFactory;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Schedulers;
//import reactor.kafka.sender.KafkaSender;
//import reactor.kafka.sender.SenderOptions;
//import reactor.kafka.sender.SenderRecord;
//import tgc.plus.callservice.dto.*;
//
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.logging.Logger;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
////@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://31.200.225.93:9199", "port=9199" })
//public class ConsumerMessageTest {
//
//    private final org.slf4j.Logger logger = LoggerFactory.getLogger(ConsumerMessageTest.class.getName());
//
//    public static KafkaSender<Long, MessageTest> kafkaSender;
//
//    @BeforeAll
//    public static void initTestProducer(){
//        Map<String, Object> props = new HashMap<>();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "31.200.225.93:9199");
////        props.put(ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
//        props.put(ProducerConfig.ACKS_CONFIG, "all");
//        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
//        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        SenderOptions<Long, MessageTest> senderOptions = SenderOptions.create(props);
//        senderOptions.scheduler(Schedulers.newBoundedElastic(3, 100000, "test-config"));
//        kafkaSender = KafkaSender.create(senderOptions);
//    }
//
//    @Test
//    public void sendMessageToTopic() {
//
//        AtomicInteger counter = new AtomicInteger();
//
//        Flux<Object> res = Flux.range(1, 15000).flatMapSequential(el->{
//                String userCode = UUID.randomUUID().toString();
//                MessageTest baseMessageTest = new MessageTest(userCode,
//                        new SaveUserDataTest(UUID.randomUUID().toString(), "sadasASD463"));
//                ProducerRecord<Long, MessageTest> producerRecord = new ProducerRecord<>("event-service",
//                         baseMessageTest);
//                producerRecord.headers().add("method", "save".getBytes());
//                SenderRecord<Long, MessageTest, String> senderRecord = SenderRecord.create(producerRecord,
//                        UUID.randomUUID().toString());
//                return kafkaSender.send(Mono.just(senderRecord))
//                        .flatMap(result -> {
//                            counter.getAndIncrement();
//                            return Mono.empty();
//                        }).doOnError(e -> logger.info(e.getMessage()));
//        }).doFinally(finish ->
//            logger.info(String.format("%s messages was send%n", counter)));
//        res.subscribe();
//    }

//        @Test
//        public void sendMessageToCreateVM() {
//            String userCode = UUID.randomUUID().toString();
//            MessageTest messageTest = new MessageTest("f3fc21e3-4619-4a7f-aff7-b43ff419a88e",new VirtualMachineCreateDataTest(993, "root", "ghtydjJS8732", 2313, "192.168.64.33"));
//            ProducerRecord<String, MessageTest> record = new ProducerRecord<>("event-service", messageTest);
//            record.headers().add("method", "send_vm_cr".getBytes());
//
//            kafkaSender.send(Mono.just(record));
//            System.out.println(userCode + " - code ");
//        }
//
//    @Test
//    public void sendMessageWarningVM() {
//        String userCode = UUID.randomUUID().toString();
//        MessageTest messageTest = new MessageTest("f3fc21e3-4619-4a7f-aff7-b43ff419a88e",new VirtualMachineExpireDataTest(3422, "2024-02-10 12:32", 150.00));
//        ProducerRecord<String, MessageTest> record = new ProducerRecord<>("call_service", messageTest);
//        record.headers().add("method", "send_vm_wn".getBytes());
//
//        kafkaSender.send(Mono.just(record));
//        System.out.println(userCode + " - code ");
//    }
//
//    @Test
//    public void sendMessageErrorVM() {
//        String userCode = UUID.randomUUID().toString();
//        MessageTest messageTest = new MessageTest("f3fc21e3-4619-4a7f-aff7-b43ff419a88e",new VirtualMachineExpireDataTest(3422, "2024-02-10 12:32", 150.00));
//        ProducerRecord<String, MessageTest> record = new ProducerRecord<>("call_service", messageTest);
//        record.headers().add("method", "send_vm_ex".getBytes());
//
//        kafkaSender.send(Mono.just(record));
//        System.out.println(userCode + " - code ");
//    }
//
//    @Test
//    public void sendMessageRestorePassword() {
//        String userCode = UUID.randomUUID().toString();
//        MessageTest messageTest = new MessageTest("0fa6c6be-b388-4937-9fe5-8c6e2f0c7120",new PasswordRestoreDataTest("http://192.168.90.103/restore"));
//        ProducerRecord<String, MessageTest> record = new ProducerRecord<>("callservice", messageTest);
//        record.headers().add("method", "send_rest".getBytes());
//
//        kafkaSender.send(Mono.just(record));
//        System.out.println(userCode + " - code ");
//    }
//
//    @Test
//    public void sendMessageToChangePhone() {
//            String userCode = UUID.randomUUID().toString();
//            MessageTest baseMessageTest = new MessageTest("f3fc21e3-4619-4a7f-aff7-b43ff419a88e",new EditPhoneDataTest("89244380870"));
//            ProducerRecord<String, MessageTest> record = new ProducerRecord<>("callservice", baseMessageTest);
//            record.headers().add("method", "update_ph".getBytes());
//
//            kafkaSender.send(Mono.just(record));
//            System.out.println(userCode + " - code ");
//        }
//
////
////
//    @Test
//    public void sendMessageToChangeEmail() {
//        String userCode = UUID.randomUUID().toString();
//        MessageTest baseMessageTest = new MessageTest("d1cbc561-b978-4fc7-82c2-edc8d77032bc", new EditEmailDataTest("567842bm@bk.ru"));
//        ProducerRecord<String, MessageTest> record = new ProducerRecord<>("callservice", baseMessageTest);
//        record.headers().add("method", "update_em".getBytes());
//
//        kafkaSender.send(Mono.just(record));
//        System.out.println(userCode + " - code ");
//    }
//}
