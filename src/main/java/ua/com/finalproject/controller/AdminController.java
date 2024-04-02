package ua.com.finalproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.finalproject.dto.UserDto;
import ua.com.finalproject.entity.User;
import ua.com.finalproject.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userService.getAllUsers();
        List<UserDto> usersDto = users.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(usersDto);
    }

    @PutMapping("/users/{id}/role-{role}")
    public ResponseEntity<String> changeRole(@PathVariable Long id,@PathVariable String role) {
        log.info("Change role to user with ID: {}", id);
        userService.changeRole(id, role);
        return ResponseEntity.ok("The user received a role successfully");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        User user = userService.getById(id);
        userService.delete(user);
        return ResponseEntity.ok("User deleted successfully");
    }
}
