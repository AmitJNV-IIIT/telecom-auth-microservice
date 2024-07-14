package com.excitel.serviceimpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class EmailServiceImplTest {

    @InjectMocks
    EmailService emailService;

    @Mock
    JavaMailSender emailSender;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testCreateMailSender() {

        JavaMailSender result = emailService.createMailSender();

        assertTrue(result instanceof JavaMailSenderImpl);
    }

    @Test
    public void testSendConfirmationEmail() {
        String recipientEmail = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(content);

        emailService.sendConfirmationEmail(recipientEmail, subject, content);

        verify(emailSender, times(1)).send(message);
    }



}