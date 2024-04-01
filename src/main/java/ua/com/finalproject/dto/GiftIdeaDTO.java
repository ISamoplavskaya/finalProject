package ua.com.finalproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GiftIdeaDTO {
    @Schema(description = "Unique gift identifier", example = "1")
    private Long id;
    @Schema(description = "Gift name", example = "Flowers")
    @NotBlank(message = "Name cannot be empty")
    private String giftName;
    @Schema(description = "Gift price", example = "50.99")
    @PositiveOrZero(message = "Price cannot be negative")
    private double price;
    @Schema(description = "Gift description", example = "Example")
    @Size(max = 200, message = "Description must contain no more than 200 characters")
    private String description;
}
