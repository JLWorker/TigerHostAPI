package tgc.plus.authservice;

import io.netty.util.internal.ThreadLocalRandom;
import org.apache.kafka.common.protocol.types.Field;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.UUID;

//@SpringBootTest()
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
        SecureRandom secureRandom = new SecureRandom();
        int all = 10;
        for (int i = 0; i < 10; i++) {
            System.out.println(Instant.now().getNano());
        }

    }

    @Test
    void generateTokenId() {
        for (int i = 0; i < 10; i++) {
            int randInt = ThreadLocalRandom.current().nextInt(1000, 10000);
            System.out.println(String.valueOf(Instant.now().getNano()) + randInt);
        }
    }
}
