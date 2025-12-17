package com.ecommerce.notification.consumer;

import com.ecommerce.notification.dto.EmailMessageDTO;
import com.ecommerce.notification.util.EmailUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.mail.Session;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private Session mailSession;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    private ObjectMapper objectMapper;
    private String validMessage;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        EmailMessageDTO emailDto = new EmailMessageDTO();
        emailDto.setTo("test@example.com");
        emailDto.setSubject("Test Subject");
        emailDto.setBody("Test Body");

        try {
            validMessage = objectMapper.writeValueAsString(emailDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConsumeValidMessage() {
        // Test: Valid Kafka message is consumed and email is sent
        try (MockedStatic<EmailUtil> emailUtilMock = mockStatic(EmailUtil.class)) {
            emailUtilMock.when(() -> EmailUtil.sendEmail(
                any(Session.class),
                eq("test@example.com"),
                eq("Test Subject"),
                eq("Test Body")
            )).thenAnswer(invocation -> null);

            notificationConsumer.consume(validMessage);

            emailUtilMock.verify(() -> EmailUtil.sendEmail(
                any(Session.class),
                eq("test@example.com"),
                eq("Test Subject"),
                eq("Test Body")
            ), times(1));
        }
    }

    @Test
    void testConsumeInvalidJson() {
        // Test: Invalid JSON message is handled gracefully
        String invalidMessage = "{ invalid json }";

        try (MockedStatic<EmailUtil> emailUtilMock = mockStatic(EmailUtil.class)) {
            notificationConsumer.consume(invalidMessage);

            emailUtilMock.verify(() -> EmailUtil.sendEmail(
                any(), anyString(), anyString(), anyString()
            ), never());
        }
    }

    @Test
    void testConsumeEmailSendFailure() {
        // Test: Email sending failure is handled
        try (MockedStatic<EmailUtil> emailUtilMock = mockStatic(EmailUtil.class)) {
            emailUtilMock.when(() -> EmailUtil.sendEmail(
                any(Session.class),
                anyString(),
                anyString(),
                anyString()
            )).thenThrow(new RuntimeException("Email sending failed"));

            notificationConsumer.consume(validMessage);

            emailUtilMock.verify(() -> EmailUtil.sendEmail(
                any(Session.class),
                eq("test@example.com"),
                eq("Test Subject"),
                eq("Test Body")
            ), times(1));
        }
    }
}

