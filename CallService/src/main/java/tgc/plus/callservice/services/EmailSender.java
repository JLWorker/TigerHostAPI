package tgc.plus.callservice.services;

import freemarker.template.Template;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.configs.FreeMarkerConfig;
import tgc.plus.callservice.exceptions.CommandNotFound;
import tgc.plus.callservice.exceptions.EmailSenderException;
import tgc.plus.callservice.services.utils.EmailSenderCommands;

import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EmailSender {

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private JavaMailSender javaMailSender;

    private final HashMap<String, EmailSenderCommands> senderCommands = new HashMap<>();

    @PostConstruct
    void init(){
        for (EmailSenderCommands command: EmailSenderCommands.values()){
            senderCommands.put(command.getName(), command);
        }
    }

    public Mono<Void> sendMessage(Map<String, String> data, String email, String method){
    return Mono.defer(()->{
        if(senderCommands.containsKey(method)){

            EmailSenderCommands emailSenderCommands = senderCommands.get(method);

            try {
                Template template = freeMarkerConfig.freeMarkerConfigurationFactory().createConfiguration().getTemplate(emailSenderCommands.getTemplate());
                String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(template, data);

                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

                messageHelper.setFrom(senderEmail);
                messageHelper.setTo(email);
                messageHelper.setSubject(emailSenderCommands.getSubject());
                messageHelper.setText(htmlText, true);

                messageHelper.addInline("tiger_logo", new ClassPathResource("/templates/freemarker/images/tiger_logo_one.png"));

                javaMailSender.send(message);
                return Mono.empty();

            }
            catch (Exception e) {
                return Mono.error(e);
            }
        }
        else
            return Mono.error(new CommandNotFound(String.format("Command with name %s not found!", method)));
        });
    }
}
