package com.PixBrew.vocabMitra.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "First Name cannot be blank.")
    private String firstName;
    @NotBlank(message = "Last Name cannot be blank.")
    private String lastName;
    @NotBlank(message = "We require username for authentication so it cannot be blank.")
    private String username;
    @Email(message = "Please enter a valid email.")
    private String email;
    @NotBlank(message = "Password cannot be blank.")
    @Min(value = 8, message = "Password must contain minimum 8 characters.")
    private String password;
}
