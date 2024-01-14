package tgc.plus.callservice.services;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import tgc.plus.callservice.configs.FreeMarkerConfig;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

@Service
@Slf4j
public class EmailSender {

    @Value("${spring.mail.username}")
    String senderEmail;

    @Autowired
    FreeMarkerConfig freeMarkerConfig;

    private final JavaMailSender javaMailSender;

    public EmailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMessage(HashMap<String, String> data, String email){
        try {
            Template template = freeMarkerConfig.freeMarkerConfigurationFactory().createConfiguration().getTemplate("tiger_template.ftl");
            String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(template, data);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            messageHelper.setFrom(senderEmail);
            messageHelper.setTo(email);
            messageHelper.setSubject(data.get("subject"));
            messageHelper.setText(htmlText, true);
            messageHelper.addInline("tiger_logo", new File("/home/makar/IdeaProjects/TigerHost/TigerHostApi/CallService/src/main/resources/templates/freemarker/images/tiger_logo.jpg"));
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

}
