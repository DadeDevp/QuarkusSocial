package io.github.devpdade.quarkussocial.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class CreateUserRequest {
    @NotBlank(message = "Name is required") //verfica se a string é null ou vazia ""
    private String name;
    @NotNull(message = "Age is Required") // Verifica apenas se o Integer é null
    private Integer age;

}
