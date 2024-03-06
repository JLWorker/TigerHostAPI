package tgc.plus.feedbackgateaway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableWebFlux
public class FeedbackGateawayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeedbackGateawayApplication.class, args);
    }

}
