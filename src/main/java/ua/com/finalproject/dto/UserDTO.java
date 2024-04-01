package ua.com.finalproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.finalproject.entity.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @Schema(description = "User's unique ID", example = "1")
    private Long id;
    @Schema(description = "User name", example = "Jon123")
    @Size(min = 3, max = 50, message = "Username must contain from 3 to 50 characters")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Schema(description = "Email address", example = "jondoe@gmail.com")
    @Size(min = 5, max = 255, message = "The email address must contain between 5 and 255 characters")
    @NotBlank(message = "Email address cannot be empty")
    @Email(message = "Email address must be in the format user@example.com")
    private String email;
    @Schema(description = "User's role", example = "ROLE_USER")
    private Role role;
}
