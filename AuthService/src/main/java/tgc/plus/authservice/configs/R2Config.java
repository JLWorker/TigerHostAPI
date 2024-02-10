package tgc.plus.authservice.configs;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.CompletableFuture;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
@EnableR2dbcRepositories
public class R2Config{

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
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "postgresql")
                .option(HOST, host)
                .option(PORT, Integer.valueOf(port))
                .option(USER, username)
                .option(PASSWORD, password)
                .option(DATABASE, database)
                .build());
    }

    @Bean("reactiveTransactionManager")
    public ReactiveTransactionManager reactiveTransactionManager(){
       return new R2dbcTransactionManager(connectionFactory());
    }

}
