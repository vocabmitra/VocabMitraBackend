package com.PixBrew.vocabMitra.dto;

import jakarta.persistence.Column;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
}
