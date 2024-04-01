package ua.com.finalproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Authentication request")
public class SignInRequest {
    @Schema(description = "User name", example = "Jon")
    @Size(min = 3, max = 50, message = "Username must contain from 3 to 50 characters")
    @NotBlank(message = "Username cannot be empty")
    private String username;
    @Schema(description = "Password", example = "my_1secret1_password")
    @Size(min = 8, max = 255, message = "Password must contain from 8 to 255 characters")
    @NotBlank(message = "Password cannot be empty")
    private String password;

}
