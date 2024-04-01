package ua.com.finalproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.finalproject.dto.JwtAuthenticationResponse;
import ua.com.finalproject.dto.SignInRequest;
import ua.com.finalproject.dto.SignUpRequest;
import ua.com.finalproject.queue.JobQueue;
import ua.com.finalproject.service.AuthenticationService;
import ua.com.finalproject.service.EmailService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTests {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private EmailService emailService;

    @Mock
    private JobQueue jobQueue;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @Test
    void signUp_ValidRequest_ReturnsJwtAuthenticationResponse() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testUser");
        signUpRequest.setPassword("password");
        signUpRequest.setEmail("test@example.com");

        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse("token");
        when(authenticationService.signUp(any(SignUpRequest.class))).thenReturn(jwtAuthenticationResponse);

        // Имитируем поведение jobQueue.put, чтобы захватить переданное лямбда-выражение
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            // Выполняем переданное лямбда-выражение
            runnable.run();
            return null;
        }).when(jobQueue).put(any(Runnable.class));

        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signUpRequest)))
                .andExpect(status().isOk());

        verify(authenticationService).signUp(any(SignUpRequest.class));
        verify(emailService).sendEmail("test@example.com", "Дякуємо за реєстрацію", "Ви успишно зареєстровані");
    }

    @Test
    void signIn_ValidRequest_ReturnsJwtAuthenticationResponse() throws Exception {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setUsername("testUser");
        signInRequest.setPassword("password");

        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse("token");
        when(authenticationService.signIn(any(SignInRequest.class))).thenReturn(jwtAuthenticationResponse);

        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signInRequest)))
                .andExpect(status().isOk());

        verify(authenticationService).signIn(any(SignInRequest.class));
    }
}
