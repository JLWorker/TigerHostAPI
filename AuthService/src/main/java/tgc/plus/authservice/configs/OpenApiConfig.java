package tgc.plus.authservice.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenApiConfig(){
        return new OpenAPI().info(new Info()
                        .title("AuthService API")
                        .description("This api describes all endpoints for three controllers")
                        .version("${api.version}"));
    }
}
