package tgc.plus.callservice.configs;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;

@Configuration
@EnableR2dbcRepositories
public class R2Config extends AbstractR2dbcConfiguration{
//
//    @Value("${postgresql.username}")
//    String username;
//
//    @Value("${postgresql.password}")
//    String password;
//
//    @Value("${postgresql.host}")
//    String host;
//
//    @Value("${postgresql.port}")
//    String port;
//
//    @Value("${postgresql.database}")
//    String database;
//
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
//                .option(DRIVER, "postgresql")
//                .option(HOST, host)
//                .option(PORT, Integer.valueOf(port))
//                .option(USER, username)
//                .option(PASSWORD, password)
//                .option(DATABASE, database)
//                .build());
//    }
//
//
//    @Bean("reactiveTransactionManager")
//    public ReactiveTransactionManager reactiveTransactionManager(){
//        return new R2dbcTransactionManager(connectionFactory());
//    }

    @Value("${spring.r2dbc.url}")
    String dataBase;

    @Override
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(dataBase);
    }
}
