package com.ecommerce.notification.consumer;

import com.ecommerce.notification.dto.EmailMessageDTO;
import com.ecommerce.notification.util.EmailUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.mail.Session;
import java.util.Properties;

@Component
public class NotificationConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "notification-events", groupId = "notification-group")
    public void consume(String message) {
        try {
            EmailMessageDTO emailMessage = objectMapper.readValue(message, EmailMessageDTO.class);

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.example.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication("your_email@example.com", "your_password");
                }
            });

            EmailUtil.sendEmail(session, emailMessage.getTo(), emailMessage.getSubject(), emailMessage.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}