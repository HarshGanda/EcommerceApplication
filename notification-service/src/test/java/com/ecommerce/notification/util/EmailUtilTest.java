package com.ecommerce.notification.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.mail.*;
import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailUtilTest {

    @Mock
    private Session session;

    @Mock
    private Transport transport;

    @Test
    void testSendEmailSuccess() throws Exception {
        // Test: Email is sent successfully with valid parameters
        MimeMessage mockMessage = mock(MimeMessage.class);

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {
            transportMock.when(() -> Transport.send(any(MimeMessage.class)))
                    .thenAnswer(invocation -> null);

            when(session.getProperties()).thenReturn(System.getProperties());

            EmailUtil.sendEmail(session, "test@example.com", "Test Subject", "Test Body");

            transportMock.verify(() -> Transport.send(any(MimeMessage.class)), times(1));
        }
    }

    @Test
    void testSendEmailWithInvalidEmail() {
        // Test: Invalid email address throws RuntimeException
        when(session.getProperties()).thenReturn(System.getProperties());

        assertThrows(RuntimeException.class, () ->
            EmailUtil.sendEmail(session, "invalid-email", "Subject", "Body")
        );
    }

    @Test
    void testSendEmailWithNullRecipient() {
        // Test: Null recipient throws exception
        when(session.getProperties()).thenReturn(System.getProperties());

        assertThrows(RuntimeException.class, () ->
            EmailUtil.sendEmail(session, null, "Subject", "Body")
        );
    }

    @Test
    void testSendEmailWithEmptyRecipient() {
        // Test: Empty recipient throws exception
        when(session.getProperties()).thenReturn(System.getProperties());

        assertThrows(RuntimeException.class, () ->
            EmailUtil.sendEmail(session, "", "Subject", "Body")
        );
    }
}

