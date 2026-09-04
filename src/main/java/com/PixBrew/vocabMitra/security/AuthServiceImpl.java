package com.PixBrew.vocabMitra.security;

import com.PixBrew.vocabMitra.dto.LoginRequestDto;
import com.PixBrew.vocabMitra.dto.LoginResponseDto;
import com.PixBrew.vocabMitra.dto.SignupRequestDto;
import com.PixBrew.vocabMitra.dto.SignupResponseDto;
import com.PixBrew.vocabMitra.entity.Users;
import com.PixBrew.vocabMitra.repository.UsersRepository;
import com.PixBrew.vocabMitra.service.AuthService;
import com.PixBrew.vocabMitra.type.RoleType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public SignupResponseDto userSignUp(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String email = requestDto.getEmail();

        if(usersRepository.existsByUsernameAndEmail(username, email)){
            throw new IllegalArgumentException("user already present with username: " + username
            + " and email: " + email);
        }
        Users user = Users.builder()
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .email(requestDto.getEmail())
                .role(RoleType.ADMIN)
                .build();
        Users savedUser = usersRepository.save(user);
        return modelMapper.map(savedUser, SignupResponseDto.class);
    }

    @Override
    public LoginResponseDto userLogin(LoginRequestDto loginRequestDto) {
        //1 we will feed username and password to AuthenticationManager via Authentication.
        //2AuthenticationManager will pass the data from daoAutheProvider and check is the username and password valid
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );
        //3 after successful validation by daoAuthenticationProvider  we will now create the jwt token to pass to the session
        Users users = (Users) authentication.getPrincipal();
        String jwtAccessToken = authUtil.createAccessToken(users);
        return new LoginResponseDto(jwtAccessToken, users.getId(),users.getFirstName(), users.getLastName(), users.getUsername(), users.getEmail(), users.getRole());
    }


//    the password reset logic

//    public void sendPasswordResetLink(ForgotPasswordRequestDto requestDto) {
//        String email = requestDto.getEmail();
//        // 1. Find the user
//        Users user = userRepo.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        //2 . Generate the secure token and set expiry to 15 mins
//        String token = UUID.randomUUID().toString();
//        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
//                .token(token)
//                .user(user)
//                .expiryDate(LocalDateTime.now().plusMinutes(15))
//                .build();
//        PasswordResetToken SavedPasswordResetToken = tokenRepo.save(passwordResetToken);
//        //3 Build the mail lonk pointing to The frontend's Password Reset Form
//        String resetUrl = resetPageUrl + token;//add to env file
//        //4 Send the email
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(user.getEmail());
//        mailMessage.setSubject("BrewBuy - Password Reset");
//        mailMessage.setText("Click here to reset your password: " + resetUrl);
//        mailSender.send(mailMessage);
//    }
//    // --- PART 2: VERIFY TOKEN & SAVE NEW PASSWORD ---
//    public void verifyAndResetPassword(ResetPasswordRequestDto passwordRequestDto) {
//        //1 find verify that the token exists in db or not
//        String token = passwordRequestDto.getToken();
//        PasswordResetToken resetToken = tokenRepo.findByToken(token)
//                .orElseThrow(() -> new RuntimeException("Invalid Token"));
//        //2 check if token is valid or has expired
//        if(resetToken.getExpiryDate().isBefore(LocalDateTime.now())){
//            tokenRepo.delete(resetToken);//clear the dead token
//            throw new RuntimeException("Token has expired");
//        }
//        //3 update the password for user
//        Users user = resetToken.getUser();
//        user.setPassword(passwordEncoder.encode(passwordRequestDto.getNewPassword()));
//        userRepo.save(user);
//        //4 delete the token now so it cannot be reused
//        tokenRepo.delete(resetToken);
//    }
}
