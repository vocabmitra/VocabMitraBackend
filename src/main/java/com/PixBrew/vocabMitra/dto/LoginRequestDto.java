package com.PixBrew.vocabMitra.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank(message = "username cannot be blank.")
    private String username;
    @NotBlank(message = "Password cannot be blank.")
    private String password;
}
