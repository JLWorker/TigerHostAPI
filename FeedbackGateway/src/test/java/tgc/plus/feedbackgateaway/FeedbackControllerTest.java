package tgc.plus.feedbackgateaway;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import tgc.plus.feedbackgateaway.api.FeedbackController;
import tgc.plus.feedbackgateaway.configs.SpringSecurityConfig;
import tgc.plus.feedbackgateaway.dto.EventMessage;
import tgc.plus.feedbackgateaway.facade.utils.EventTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FeedbackControllerTest {

    @Value("${kafka.topic}")
    private String topic;

    private final static String code = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2NvZGUiOiIxZDM2NzkyMy1mNjcxLTRmMWUtODcwOC1kOTM2NWVjYzQ3MWUiLCJyb2xlIjoiVVNFUiIsImV4cCI6MTcwOTc0MzA1OSwiaWF0IjoxNzA5NzM5ODU5fQ.Pv_Bj13p8PidiRmFwW8mKWi3VeXO1zZjA3Y8cTgplTM";
    public static KafkaSender<String, EventMessage> kafkaSender;
    private final String authHeader = String.format("Bearer_%s", code);
    private final WebClient webClient = WebClient.create("http://localhost:8083");

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
        SenderOptions<String, EventMessage> senderOptions = SenderOptions.create(props);
        kafkaSender = KafkaSender.create(senderOptions);
    }

    @Test
    public void getEvents(){
        ParameterizedTypeReference<ServerSentEvent<EventMessage>> event = new ParameterizedTypeReference<ServerSentEvent<EventMessage>>(){};
        Flux<ServerSentEvent<EventMessage>> events = webClient.get()
                .uri("/feedback/events")
                .header("Authorization", authHeader)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve().bodyToFlux(event);

        events.flatMapSequential(msg -> {
            System.out.printf("Id: %s, Event: %s, payload_type: %s%n", msg.id(),msg.event(),msg.data().getOperationType());
            return Mono.empty();
        }).subscribe();

    }

    @Test
    public void fastEventCreator(){
        Flux<Void> recordsFeedback = Flux.range(0, 1)
                .flatMapSequential(el -> {
                    ProducerRecord<String, EventMessage> messageRecord = new ProducerRecord<>(topic, new EventMessage("update_user_info"));
                    messageRecord.headers().add("user_code", "1d367923-f671-4f1e-8708-d9365ecc471e".getBytes());
                    SenderRecord<String, EventMessage, String> senderRecord = SenderRecord.create(messageRecord, UUID.randomUUID().toString());
                    return kafkaSender.send(Mono.just(senderRecord))
                        .doFinally(signal -> System.out.printf("El - %s, was send%n", el))
                            .then();
                });

        recordsFeedback.subscribe();
    }


}
