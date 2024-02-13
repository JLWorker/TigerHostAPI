package tgc.plus.callservice.configs;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.connection.SingleConnectionFactory;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;

@Configuration
@EnableR2dbcRepositories
public class R2Config{
//
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
         return ConnectionFactories.get(builder()
//                .option(DRIVER, "pool")
                .option(DRIVER, "postgresql")
                .option(HOST, host)
                .option(PORT, Integer.valueOf(port))
                .option(USER, username)
                .option(PASSWORD, password)
                .option(DATABASE, database)
                .build());

//        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder()
//                .connectionFactory(factories)
//                .maxSize(17)
//                .minIdle(5)
//                .validationQuery("SELECT 1")
//                .acquireRetry(10)
//                .initialSize(10).build();
//        return new ConnectionPool(configuration);
    }


    @Bean()
    public ReactiveTransactionManager reactiveTransactionManager(){
        return new R2dbcTransactionManager(connectionFactory());
    }

    @Bean
    public TransactionalOperator transactionalOperator(){
        return TransactionalOperator.create(reactiveTransactionManager());
    }

//
//    @Value("${spring.r2dbc.url}")
//    String dataBase;

//    @Override
//    public ConnectionFactory connectionFactory() {
//        return ConnectionFactories.get(dataBase);
//    }
}
