package com.ecommerce.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${mail.smtp.host:smtp.gmail.com}")
    private String smtpHost;

    @Value("${mail.smtp.port:587}")
    private String smtpPort;

    @Value("${mail.smtp.username:your_email@example.com}")
    private String username;

    @Value("${mail.smtp.password:your_password}")
    private String password;

    @Bean
    public Session mailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }
}

