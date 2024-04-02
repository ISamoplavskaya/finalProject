package ua.com.finalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ua.com.finalproject.dto.UserDto;
import ua.com.finalproject.dto.UserUpdateRequest;
import ua.com.finalproject.entity.User;
import ua.com.finalproject.service.UserService;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Retrieving current user details for user: {}", userDetails.getUsername());
        User user = userService.getByUsername(userDetails.getUsername());
        UserDto userDto = modelMapper.map(user, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/me")
    public ResponseEntity<String> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestBody @Valid UserUpdateRequest updateRequest) {
        log.info("Updating user details for user: {}", userDetails.getUsername());
        User user = userService.getByUsername(userDetails.getUsername());
        if (!updateRequest.getNewPassword().equals(updateRequest.getConfirmPassword())) {
            log.error("Password confirmation does not match for user: {}", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password confirmation does not match");
        }
        if (!updateRequest.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
        }
        user.setUsername(updateRequest.getUsername());
        user.setEmail(updateRequest.getEmail());
        userService.save(user);
        log.info("User details updated successfully for user: {}", userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body("User updated successfully.");
    }
}
