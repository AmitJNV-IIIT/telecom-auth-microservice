package com.excitel.serviceimpl;//NOSONAR

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Service class for sending emails.
 */
@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.password}")
    private String emailPassword;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    private JavaMailSender emailSender;
    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    /**
     * Constructor injection for JavaMailSender.
     *
     * @param emailSender The JavaMailSender instance to be used for sending emails.
     */
    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * Creates and configures a JavaMailSender instance.
     *
     * @return A configured JavaMailSender instance.
     */
    public JavaMailSender createMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(emailUsername);
        mailSender.setPassword(emailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    /**
     * Sends a confirmation email to the specified recipient.
     *
     * @param recipientEmail The email address of the recipient.
     * @param subject        The subject of the email.
     * @param content        The content of the email.
     */
    public void sendConfirmationEmail(String recipientEmail, String subject, String content) {
        logger.info("Sending Mail");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailUsername);
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(content);
        emailSender.send(message);
    }
}