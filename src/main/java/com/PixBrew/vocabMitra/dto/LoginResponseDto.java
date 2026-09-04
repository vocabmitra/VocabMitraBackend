package com.PixBrew.vocabMitra.dto;

import com.PixBrew.vocabMitra.type.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    private String jwt;
    private long userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;;
    private RoleType role;
}
