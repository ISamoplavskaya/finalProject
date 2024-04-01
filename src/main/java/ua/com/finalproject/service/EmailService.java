package ua.com.finalproject.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import ua.com.finalproject.exeption.EmailSendingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSenderImpl javaMailSender;
    private final Environment env;
    private String senderEmail;

    @PostConstruct
    public void init() {
        senderEmail = env.getProperty("SPRING_MAIL_USERNAME");
        String password = env.getProperty("SPRING_MAIL_PASSWORD");
        javaMailSender.setUsername(senderEmail);
        javaMailSender.setPassword(password);
    }

    public void sendEmail(String to, String subject, String text) {
        log.info("Sending email to={}", to);
        log.info("Sending email from={}", senderEmail);
        log.info("Password={}", env.getProperty("SPRING_MAIL_PASSWORD"));

        if (!isValidEmailAddress(to)) {
            log.error("Invalid email address provided: {}", to);
            throw new IllegalArgumentException("Invalid email address provided");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(senderEmail);
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
            log.info("Email successfully sent to: {}", to);
        } catch (MailException e) {
            log.error("Mail authentication failed for sender: {}", senderEmail);
            throw new EmailSendingException("Failed to send email: Mail authentication failed", e);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new EmailSendingException("Failed to send email", e);
        }
    }

    private boolean isValidEmailAddress(String email) {
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }
}
