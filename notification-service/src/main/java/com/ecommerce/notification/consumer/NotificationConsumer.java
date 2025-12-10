package com.ecommerce.notification.consumer;

import com.ecommerce.notification.dto.EmailMessageDTO;
import com.ecommerce.notification.util.EmailUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.mail.Session;

@Component
public class NotificationConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private Session mailSession;

    @KafkaListener(topics = "notification-events", groupId = "notification-group")
    public void consume(String message) {
        try {
            EmailMessageDTO emailMessage = objectMapper.readValue(message, EmailMessageDTO.class);
            EmailUtil.sendEmail(mailSession, emailMessage.getTo(), emailMessage.getSubject(), emailMessage.getBody());
            System.out.println("Email sent successfully to: " + emailMessage.getTo());
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}