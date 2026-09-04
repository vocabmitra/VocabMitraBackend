package com.PixBrew.vocabMitra.controller;

import com.PixBrew.vocabMitra.dto.LoginRequestDto;
import com.PixBrew.vocabMitra.dto.LoginResponseDto;
import com.PixBrew.vocabMitra.dto.SignupRequestDto;
import com.PixBrew.vocabMitra.dto.SignupResponseDto;
import com.PixBrew.vocabMitra.service.AuthService;
import com.PixBrew.vocabMitra.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<SignupResponseDto> signUp(@RequestBody SignupRequestDto requestDto) {
        try{
            return ResponseEntity.status((HttpStatus.CREATED)).body(authService.userSignUp(requestDto));
        } catch (Exception e) {
            throw new RuntimeException("exception at signUp" + e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.userLogin(loginRequestDto));
        } catch (Exception e) {
            throw new RuntimeException("exception in login" + e.getMessage());
        }
    }
}
