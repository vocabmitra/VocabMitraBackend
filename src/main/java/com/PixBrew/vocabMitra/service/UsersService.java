package com.PixBrew.vocabMitra.service;

import com.PixBrew.vocabMitra.dto.ProfileResponseDto;
import com.PixBrew.vocabMitra.entity.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface UsersService {
     ProfileResponseDto loadUserProfile(long id);

     ProfileResponseDto updateUserProfile(Authentication authentication, Map<String, Object> requestDto);


    void delete(long id);

    Users findUserByUsername(String username);

    void saveUser(Users user);
}
