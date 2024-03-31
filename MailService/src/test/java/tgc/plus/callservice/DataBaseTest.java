//package tgc.plus.callservice;
//
//import io.r2dbc.pool.ConnectionPool;
//import io.r2dbc.pool.ConnectionPoolConfiguration;
//import io.r2dbc.spi.ConnectionFactories;
//import io.r2dbc.spi.ConnectionFactory;
//import io.r2dbc.spi.ConnectionFactoryOptions;
//import io.r2dbc.spi.ValidationDepth;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Scheduler;
//import reactor.core.scheduler.Schedulers;
//import tgc.plus.callservice.dto.MessageElement;
//import tgc.plus.callservice.dto.message_payloads.SaveUserData;
//import tgc.plus.callservice.entity.User;
//import tgc.plus.callservice.repositories.UserRepository;
//
//import java.time.Duration;
//import java.util.UUID;
//
//import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_SIZE;
//import static io.r2dbc.spi.ConnectionFactoryOptions.*;
//import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
//
//@SpringBootTest
//public class DataBaseTest {
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Test
//    public void sendPosts(){
//
//        Scheduler scheduler = Schedulers.newBoundedElastic(8, 100000, "Test");
//
//        Flux<User> flux = Flux.range(1, 15000)
//                .publishOn(scheduler).flatMap(el -> {
//                    String uuid = UUID.randomUUID().toString();
//                    System.out.println("Was save - " + uuid);
//                    return userRepository.save(new User(UUID.randomUUID().toString(), "45637289@bk.ru"));
//                });
//
//        flux.subscribe();
//
//    }
//
//}
