package com.PixBrew.vocabMitra.service.impl;

import com.PixBrew.vocabMitra.dto.ProfileResponseDto;
import com.PixBrew.vocabMitra.entity.Users;
import com.PixBrew.vocabMitra.repository.UserVocabProgressAndBookMarkRepo;
import com.PixBrew.vocabMitra.repository.UsersRepository;
import com.PixBrew.vocabMitra.service.UsersService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final UserVocabProgressAndBookMarkRepo progressAndBookMarkRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ProfileResponseDto loadUserProfile(long id) {
        Users signedInUser = usersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist with id: " + id));

        ProfileResponseDto profileResponseDto = modelMapper.map(signedInUser, ProfileResponseDto.class);

        long totalBookMarked = progressAndBookMarkRepo.countByUserAndIsBookmarkedTrue(signedInUser);
        long totalLearned = progressAndBookMarkRepo.countByUserAndIsLearnedTrue(signedInUser);

        profileResponseDto.setTotalBookmarked(totalBookMarked);
        profileResponseDto.setTotalLearned(totalLearned);

        // Inside loadUserProfile(...)

        profileResponseDto.setCurrentStreak(signedInUser.getCurrentStreak());
        profileResponseDto.setMaxStreak(signedInUser.getMaxStreak());

        // Tell frontend if they have completed today's requirement
        boolean activeToday = signedInUser.getLastActiveDate() != null &&
                signedInUser.getLastActiveDate().equals(LocalDate.now());

        profileResponseDto.setStreakActiveToday(activeToday);

        return profileResponseDto;
    }

    @Override
    public ProfileResponseDto updateUserProfile(Authentication authentication, Map<String, Object> updates) {
        try {
            String username = authentication.getName();
            Users user = usersRepository.findByUsername(username);
            long userId = user.getId();
            Users users = usersRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User does not exist with id:  " + userId));
            //now updating the values
            updates.forEach((field, value) -> {
                // Adding a null check here is a good safety net in case the frontend sends a key with a null value
                if (value == null) {
                    return;
                }
                switch (field) {
                    case "firstName":
                        users.setFirstName((String) value);
                        break;

                    case "lastName":
                        users.setLastName((String) value);
                        break;

                    case "email":
                        users.setEmail((String) value);
                        break;

                    default:
                        throw new IllegalArgumentException("Invalid Field for updates: " + field);
                }
            });
            Users updatedUser = usersRepository.save(users);
            ProfileResponseDto profileResponseDto =  modelMapper.map(updatedUser, ProfileResponseDto.class);

            long totalBookMarked = progressAndBookMarkRepo.countByUserAndIsBookmarkedTrue(updatedUser);
            long totalLearned = progressAndBookMarkRepo.countByUserAndIsLearnedTrue(updatedUser);

            profileResponseDto.setTotalBookmarked(totalBookMarked);
            profileResponseDto.setTotalLearned(totalLearned);
            return profileResponseDto;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void delete(long id) {
        Users existingUser = usersRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("User not present with id: " + id));
        usersRepository.delete(existingUser);
    }

    @Override
    public Users findUserByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    @Override
    public void saveUser(Users user) {
        usersRepository.save(user);
    }
}
