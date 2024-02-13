package tgc.plus.callservice.configs;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.repositories.UserRepository;

import java.time.Duration;

import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_SIZE;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;

@Configuration
public class R2Config extends AbstractR2dbcConfiguration {

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

    @Override
    @Bean
    public @NotNull ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
//                .option(DRIVER,"r2dbc")
                .option(DRIVER, "postgresql")
                .option(HOST, host)
                .option(PORT, Integer.valueOf(port))
                .option(USER, username)
                .option(PASSWORD, password)
                .option(DATABASE, database)
                .option(MAX_SIZE, 17)
                .build());
    }

    @Bean
    public ReactiveTransactionManager reactiveTransactionManager(){
        return new R2dbcTransactionManager(connectionFactory());
    }

    @Bean
    public TransactionalOperator transactionalOperator(){
        return TransactionalOperator.create(reactiveTransactionManager());
    }

    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(){
        return new R2dbcEntityTemplate(connectionFactory());
    }

}
