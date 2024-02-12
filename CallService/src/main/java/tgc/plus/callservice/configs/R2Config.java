package tgc.plus.callservice.configs;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_SIZE;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;

@Configuration
@EnableR2dbcRepositories
@EnableTransactionManagement
public class R2Config {

    @Value("${postgresql.username}")
    String username;

    @Value("${postgresql.password}")
    String password;

    @Value("${postgresql.host}")
    String host;

    @Value("${postgresql.port}")
    String port;

    @Value("${postgresql.database}")
    String database;

    @Bean
    public ConnectionFactory factory() {
         return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER,"pool")
                 .option(PROTOCOL, "postgresql")
                .option(HOST, host)
                .option(PORT, Integer.valueOf(port))
                .option(USER, username)
                .option(PASSWORD, password)
                .option(DATABASE, database)
                .option(MAX_SIZE, 17)
                .build());
    }

    public ConnectionPool pool() {
        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(factory())
                .acquireRetry(10)
                .backgroundEvictionInterval(Duration.ofSeconds(20))
                .name("r2dbc_call_service")
                .minIdle(5)
                .maxSize(17)
                .maxIdleTime(Duration.ofSeconds(2))
//                .validationDepth(ValidationDepth.REMOTE)
                .validationQuery("SELECT 1")
                .build();
        return new ConnectionPool(configuration);
    }


    @Bean
    public ReactiveTransactionManager reactiveTransactionManager(){
        return new R2dbcTransactionManager(factory());
    }

    @Bean
    public TransactionalOperator transactionalOperator(){
        return TransactionalOperator.create(reactiveTransactionManager());
    }

}
