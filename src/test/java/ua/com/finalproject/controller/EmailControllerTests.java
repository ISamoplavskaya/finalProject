package ua.com.finalproject.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.com.finalproject.exeption.EmailSendingException;
import ua.com.finalproject.service.EmailService;
import ua.com.finalproject.service.JwtService;
import ua.com.finalproject.service.UserService;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class EmailControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EmailService emailService;
    @MockBean
    private UserService userService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void testSendMail_success() throws Exception {
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test email content";
        doNothing().when(emailService).sendEmail(to, subject, text);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/email/send")
                        .param("to", to)
                        .param("subject", subject)
                        .param("text", text)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Email successfully sent!"));
    }

    @Test
    public void testSendMail_invalidEmail() throws Exception {
        String invalidEmail = "invalidemail";
        String subject = "Test Subject";
        String text = "Test email content";

        doThrow(IllegalArgumentException.class).doThrow(EmailSendingException.class).when(emailService).sendEmail(invalidEmail, subject, text);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/email/send")
                        .param("to", invalidEmail)
                        .param("subject", subject)
                        .param("text", text)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}


