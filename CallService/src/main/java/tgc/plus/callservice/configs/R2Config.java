package tgc.plus.callservice.configs;

import io.r2dbc.spi.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static io.r2dbc.pool.PoolingConnectionFactoryProvider.*;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;

@Configuration
@EnableTransactionManagement
@EnableR2dbcRepositories
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

    @Value("${postgresql.max-pool-size}")
    Integer maxPoolSize;

    @Override
    @Bean
    public @NotNull ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "postgresql")
                .option(HOST, host)
                .option(PORT, Integer.valueOf(port))
                .option(USER, username)
                .option(PASSWORD, password)
                .option(DATABASE, database)
                .option(MAX_SIZE, maxPoolSize)
                .option(VALIDATION_QUERY, "SELECT 1")
                .option(VALIDATION_DEPTH, ValidationDepth.REMOTE)
                .build());
    }

    @Bean
    public ReactiveTransactionManager reactiveTransactionManager(){
        return new R2dbcTransactionManager(connectionFactory());
    }

}
