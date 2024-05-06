package io.github.devpdade.quarkussocial.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FollowerRequest {
    @NotNull(message = "followerId is required")
    private Long followerId;
}
