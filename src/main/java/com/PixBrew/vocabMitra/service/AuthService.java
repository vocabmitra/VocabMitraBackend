package com.PixBrew.vocabMitra.service;

import com.PixBrew.vocabMitra.dto.LoginRequestDto;
import com.PixBrew.vocabMitra.dto.LoginResponseDto;
import com.PixBrew.vocabMitra.dto.SignupRequestDto;
import com.PixBrew.vocabMitra.dto.SignupResponseDto;
import org.jspecify.annotations.Nullable;

public interface AuthService {
     SignupResponseDto userSignUp(SignupRequestDto requestDto);

     LoginResponseDto userLogin(LoginRequestDto loginRequestDto);
}
