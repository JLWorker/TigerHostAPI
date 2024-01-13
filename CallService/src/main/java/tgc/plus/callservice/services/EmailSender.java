package tgc.plus.callservice.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Service
@Slf4j
public class EmailSender {

    @Value("${spring.mail.username}")
    String senderEmail;

    private final JavaMailSender javaMailSender;

    public EmailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMessage(HashMap<String, String> data, String email){
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            messageHelper.setFrom(senderEmail);
            messageHelper.setTo(email);
            messageHelper.setSubject(data.get("subject"));
            messageHelper.setText(data.get("text"));
            javaMailSender.send(message);
            log.info("Mail was Send to - " + email);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }

}
