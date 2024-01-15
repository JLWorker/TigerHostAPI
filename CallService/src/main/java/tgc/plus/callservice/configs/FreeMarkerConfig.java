package tgc.plus.callservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import tgc.plus.callservice.services.utils.EmailSenderCommands;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class FreeMarkerConfig{

    @Bean
    public FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactory(){
        FreeMarkerConfigurationFactoryBean factory = new FreeMarkerConfigurationFactoryBean();
        factory.setTemplateLoaderPath("classpath:/templates/freemarker/");
        return factory;
    }


}
