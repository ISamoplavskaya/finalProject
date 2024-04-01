package ua.com.finalproject.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendDTO {
    @Schema(description = "Friend's unique ID", example = "1")
    private Long id;
    @Schema(description = "Friend's name", example = "John")
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @Schema(description = "Friend's date of birth", example = "1990-01-01")
    private LocalDate birthday;

}
