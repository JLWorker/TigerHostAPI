package tgc.plus.callservice.configs;

import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider;
import io.r2dbc.spi.*;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import reactor.netty.resources.LoopResources;

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

    @Value("${flyway.url}")
    String flywayUrl;

    @Value("${flyway.locations}")
    String flywayLocations;

    //R2DBC базируется на WebFlux, поэтому r2dbc для обработки соединений с базой, отправки запросов использует
    //под капотом EventLoop, по умолчанию он равен 1, то есть не все потоки приложения задействованы в работе
    //с базой данных.


    @Override
    @Bean
    public @NotNull ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, POOLING_DRIVER)
                .option(PROTOCOL, "postgresql")
                .option(HOST, host)
                .option(PORT, Integer.valueOf(port))
                .option(USER, username)
                .option(PASSWORD, password)
                .option(DATABASE, database)
                .option(MAX_SIZE, maxPoolSize)
                .option(VALIDATION_QUERY, "SELECT 1")
                .option(VALIDATION_DEPTH, ValidationDepth.REMOTE)
                        .option(PostgresqlConnectionFactoryProvider.LOOP_RESOURCES, LoopResources.create("pref", -1, maxPoolSize, true, false))
                .build());
    }

    @Bean
    public ReactiveTransactionManager reactiveTransactionManager(){
        return new R2dbcTransactionManager(connectionFactory());
    }

    @Bean(initMethod = "migrate")
    public Flyway configureFlyway(){
        FluentConfiguration fluentConfiguration = new FluentConfiguration();
        fluentConfiguration.dataSource(flywayUrl, username, password);
        fluentConfiguration.locations(flywayLocations);
        fluentConfiguration.baselineOnMigrate(true);
        return Flyway.configure().configuration(fluentConfiguration).load();
    }

}
