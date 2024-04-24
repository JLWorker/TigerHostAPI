package tgc.plus.authservice;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import tgc.plus.authservice.facades.utils.utils_enums.MailServiceCommand;
import tgc.plus.authservice.utils.TestUtils;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.atomic.AtomicInteger;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SendKafkaMessageTest {
//
//    @Autowired
//    private FacadeUtils facadeUtils;
//
//    @Autowired
//    private TestUtils testUtils;
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
//
//    private final Integer messageCount = 26000;
//
//    @Test
//    public void sendMessageTest() {
//        AtomicInteger atomicInteger = new AtomicInteger();
//
//        Flux<Void> sendMessages = Flux.range(0, messageCount).flatMapSequential(el -> testUtils.generateRandomUser().flatMap(user ->
//                        facadeUtils.createMessageForSaveUser(user.get("uc"), user.get("em"), user.get("ps"))
//                                .flatMap(message -> facadeUtils.sendMessageInCallService(message, MailServiceCommand.SAVE.getName()))
//                                .doOnSuccess(res -> atomicInteger.getAndIncrement())))
//                .doOnError(e -> logger.error(e.getMessage()))
//                .doOnComplete(() -> {
//                    assertEquals((int) messageCount, atomicInteger.get(),
//                            String.format("Were send not all messages - %s!", atomicInteger.get()));
//                    logger.info("Were send - {} messages!", atomicInteger.get());
//                });
//        sendMessages.subscribe();
//    }
}
