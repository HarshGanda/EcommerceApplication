package com.ecommerce.notification.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.Session;

import static org.junit.jupiter.api.Assertions.*;

class EmailConfigTest {

    @Test
    void testMailSessionCreation() {
        // Test: Mail session is created with default properties
        EmailConfig emailConfig = new EmailConfig();

        ReflectionTestUtils.setField(emailConfig, "smtpHost", "smtp.test.com");
        ReflectionTestUtils.setField(emailConfig, "smtpPort", "587");
        ReflectionTestUtils.setField(emailConfig, "username", "test@example.com");
        ReflectionTestUtils.setField(emailConfig, "password", "testpassword");

        Session session = emailConfig.mailSession();

        assertNotNull(session);
        assertEquals("smtp.test.com", session.getProperty("mail.smtp.host"));
        assertEquals("587", session.getProperty("mail.smtp.port"));
        assertEquals("true", session.getProperty("mail.smtp.auth"));
        assertEquals("true", session.getProperty("mail.smtp.starttls.enable"));
    }

    @Test
    void testMailSessionWithCustomPort() {
        // Test: Mail session with custom port
        EmailConfig emailConfig = new EmailConfig();

        ReflectionTestUtils.setField(emailConfig, "smtpHost", "smtp.custom.com");
        ReflectionTestUtils.setField(emailConfig, "smtpPort", "465");
        ReflectionTestUtils.setField(emailConfig, "username", "custom@example.com");
        ReflectionTestUtils.setField(emailConfig, "password", "custompassword");

        Session session = emailConfig.mailSession();

        assertNotNull(session);
        assertEquals("smtp.custom.com", session.getProperty("mail.smtp.host"));
        assertEquals("465", session.getProperty("mail.smtp.port"));
    }

    @Test
    void testMailSessionAuthenticator() {
        // Test: Mail session has authenticator configured
        EmailConfig emailConfig = new EmailConfig();

        ReflectionTestUtils.setField(emailConfig, "smtpHost", "smtp.gmail.com");
        ReflectionTestUtils.setField(emailConfig, "smtpPort", "587");
        ReflectionTestUtils.setField(emailConfig, "username", "user@gmail.com");
        ReflectionTestUtils.setField(emailConfig, "password", "password123");

        Session session = emailConfig.mailSession();

        assertNotNull(session);
        assertNotNull(session.getProperty("mail.smtp.auth"));
        assertEquals("true", session.getProperty("mail.smtp.auth"));
    }
}

