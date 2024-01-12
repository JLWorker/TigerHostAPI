package tgc.plus.authservice.configs;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories
public class R2Config extends AbstractR2dbcConfiguration {

    @Value("${spring.r2dbc.url}")
    String dbUrl;

    @Override
    public ConnectionFactory connectionFactory() {
       return ConnectionFactories.get(dbUrl);
    }
}
