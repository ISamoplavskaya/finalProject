package ua.com.finalproject.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.com.finalproject.dto.JwtAuthenticationResponse;
import ua.com.finalproject.dto.SignInRequest;
import ua.com.finalproject.dto.SignUpRequest;
import ua.com.finalproject.entity.Role;
import ua.com.finalproject.entity.User;
import ua.com.finalproject.util.ObjectUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTests {
    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    public void testSignUp() {
        SignUpRequest request = new SignUpRequest("testUser", "test@example.com", "password");
        User savedUser = ObjectUtils.getUser(request.getUsername());
        savedUser.setUsername(request.getEmail());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userService.create(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        JwtAuthenticationResponse response = authenticationService.signUp(request);

        assertEquals("jwtToken", response.getToken());
        verify(userService, times(1)).create(any(User.class));
        verify(jwtService, times(1)).generateToken(any(UserDetails.class));
    }

    @Test
    void testSignIn() {
        SignInRequest signInRequest = new SignInRequest("username", "password");
        UserDetails userDetails = User.builder()
                .username(signInRequest.getUsername())
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();
        when(userService.userDetailsService()).thenReturn((username) -> userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwtToken");

        // Act
        JwtAuthenticationResponse response = authenticationService.signIn(signInRequest);

        // Assert
        assertEquals("jwtToken", response.getToken());
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
        verify(userService).userDetailsService();
        verify(jwtService).generateToken(userDetails);
    }
}


