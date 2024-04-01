package ua.com.finalproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.finalproject.dto.JwtAuthenticationResponse;
import ua.com.finalproject.dto.SignInRequest;
import ua.com.finalproject.dto.SignUpRequest;
import ua.com.finalproject.queue.JobQueue;
import ua.com.finalproject.service.AuthenticationService;
import ua.com.finalproject.service.EmailService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
@Slf4j
public class AuthController {
    private final AuthenticationService authenticationService;
    private final EmailService emailService;
    private final JobQueue jobQueue;

    @Operation(summary = "User registration")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        log.info("User signed up: {}", request.getUsername());
        JwtAuthenticationResponse jwt = authenticationService.signUp(request);
        jobQueue.put(() -> {
            emailService.sendEmail(request.getEmail(), "Дякуємо за реєстрацію", "Ви успишно зареєстровані");
            log.info("Registration email sent to: {}", request.getEmail());
        });
        return jwt;
    }

    @Operation(summary = "User authorization")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        log.info("User signed in: {}", request.getUsername());
        return authenticationService.signIn(request);
    }

}