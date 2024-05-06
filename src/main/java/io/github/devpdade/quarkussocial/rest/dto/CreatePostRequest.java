package io.github.devpdade.quarkussocial.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePostRequest {
    @NotBlank(message = "Text is required")
    private String text;
}
