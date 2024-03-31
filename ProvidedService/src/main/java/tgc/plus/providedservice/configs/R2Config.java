package tgc.plus.providedservice.configs;

import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.ValidationDepth;
import jakarta.validation.constraints.NotNull;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

@Configuration
@EnableR2dbcRepositories
@EnableTransactionManagement
@EnableConfigurationProperties(FlywayProperties.class)
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
                .option(PostgresqlConnectionFactoryProvider.LOOP_RESOURCES, LoopResources.create("pref", -1, maxPoolSize, true, false))
                .build());
    }

    @Bean
    public ReactiveTransactionManager reactiveTransactionManager(){
       return new R2dbcTransactionManager(connectionFactory());
    }


    //можно применить endpoints для мониторинга и управления базой
    @Bean(initMethod = "migrate")
    public Flyway configureFlyway(){
        FluentConfiguration fluentConfiguration = new FluentConfiguration();
        fluentConfiguration.dataSource(flywayUrl, username, password);
        fluentConfiguration.locations(flywayLocations);
        fluentConfiguration.baselineOnMigrate(true);
        return Flyway.configure().configuration(fluentConfiguration).load();
    }

}
