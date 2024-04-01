package ua.com.finalproject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTests {
    private EmailService emailService;
    @Mock
    private JavaMailSenderImpl javaMailSender;
    @Mock
    private Environment environment;

    @BeforeEach
    public void setUp() {
        emailService = new EmailService(javaMailSender, environment);
    }

    @Test
    public void sendEmail_Successfully() {
        String to = "recipient@example.com";
        String subject = "subject";
        String text = "text";
        String senderEmail = "test@example.com";
        String password = "password";

        lenient().when(environment.getProperty("SPRING_MAIL_USERNAME")).thenReturn(senderEmail);
        when(environment.getProperty("SPRING_MAIL_PASSWORD")).thenReturn(password);

        emailService.sendEmail(to, subject, text);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
