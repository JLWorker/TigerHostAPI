package tgc.plus.callservice;

//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.mock.mockito.SpyBean;
//import reactor.core.publisher.Mono;
//import tgc.plus.callservice.listeners.MessageListener;
//import tgc.plus.callservice.repositories.UserRepository;
//import tgc.plus.callservice.services.EmailSender;
//
//import java.time.Duration;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.temporal.TemporalAccessor;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.*;
//import static org.hamcrest.MatcherAssert.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class KafkaConsumerTest {
//
//    @MockBean
//    private EmailSender emailSender;
//
//    @SpyBean
//    private UserRepository userRepository;
//
//    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
//
//    @Test
//    public void processingSaveUserMessages(){
//        Mockito.when(emailSender.sendMessage(anyMap(), anyString(), anyString())).thenReturn(Mono.empty());
//        Mockito.verify(userRepository, Mockito.times(15000)).save(any());
//        int callCount = Mockito.mockingDetails(userRepository).getInvocations().size();
//        logger.info("Spy object invocations - " + callCount);
//        assertEquals(callCount, 15000, String.format("Counter - %s", callCount));
//    }
//
//
//}
