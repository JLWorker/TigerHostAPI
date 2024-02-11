package tgc.plus.callservice;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;
import tgc.plus.callservice.dto.MessageTest;
import tgc.plus.callservice.dto.SaveUserDataTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReactTest {

    Logger logger = Logger.getLogger(this.getClass().getName());

    static KafkaSender<Long, MessageTest> kafkaSender;

    @BeforeAll
    public static void producerConfigs(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "31.200.225.93:9199");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
//        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 128);
//        props.put(ProducerConfig.RETRIES_CONFIG, 3);
//        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 10000);
//        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 4);
        kafkaSender = KafkaSender.create(SenderOptions.create(props));
    }

    @Test
    public void sendTestMessage(){

        Flux<SenderRecord<Long, MessageTest, Integer>> flux = Flux.range(1, 50000)
                .flatMap(el -> {
                    String userCode = UUID.randomUUID().toString();
                    MessageTest baseMessageTest = new MessageTest(userCode, new SaveUserDataTest(String.format("%s@bk.ru", new Random().nextInt()), "sadasASD"));
                    ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("callservice", baseMessageTest);
                    record.headers().add("method", "save".getBytes());
                    record.headers().add("id", new byte[]{el.byteValue()});
                    return Mono.just(SenderRecord.create(record, el));
                });

       kafkaSender.send(flux).doOnNext(sendElem -> System.out.println(String.format("Partition - %s; offset - %s; meta - %s",
                sendElem.recordMetadata().partition(), sendElem.recordMetadata().offset(), sendElem.correlationMetadata().toString())))
                .doOnError(e -> logger.warning(e.getMessage()))
               .subscribe();

//        String userCode = UUID.randomUUID().toString();
//        MessageTest baseMessageTest = new MessageTest(userCode, new SaveUserDataTest(String.format("%s@bk.ru", new Random().nextInt()), "sadasASD"));
//                    ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("callService", baseMessageTest);
//                    record.headers().add("method", "save".getBytes());
//
//        sendOne(baseMessageTest).subscribe();

    }

    private ProducerRecord<Long, MessageTest> createProducer(){
//        return Mono.defer(()->{
        String userCode = UUID.randomUUID().toString();
        MessageTest baseMessageTest = new MessageTest(userCode, new SaveUserDataTest(String.format("%s@bk.ru", new Random().nextInt()), "sadasASD"));
        ProducerRecord<Long, MessageTest> record = new ProducerRecord<>("call_service", baseMessageTest);
        record.headers().add("method", "save".getBytes());
        return record;
//        });
    }

    public Flux<SenderResult<String>> sendOne(MessageTest messageTest){
        ProducerRecord<Long, MessageTest> producerRecord = new ProducerRecord<>("call_service", messageTest);
        producerRecord.headers().add(new RecordHeader("method", "save".getBytes()));
        Mono<SenderRecord<Long, MessageTest, String>> record = Mono.just(SenderRecord.create(producerRecord, UUID.randomUUID().toString()));
        return kafkaSender.send(record);
    }

}
