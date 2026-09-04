package com.PixBrew.vocabMitra.service.impl;

import com.PixBrew.vocabMitra.dto.VocabResponse;
import com.PixBrew.vocabMitra.entity.UserVocabProgressAndBookMarks;
import com.PixBrew.vocabMitra.entity.Users;
import com.PixBrew.vocabMitra.entity.Vocabs;
import com.PixBrew.vocabMitra.repository.UserVocabProgressAndBookMarkRepo;
import com.PixBrew.vocabMitra.service.UserVocabProgressBookMarkService;
import com.PixBrew.vocabMitra.service.UsersService;
import com.PixBrew.vocabMitra.service.VocabsService;
import com.PixBrew.vocabMitra.util.StreakCountUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserVocabProgressBookMarkServiceImpl implements UserVocabProgressBookMarkService {

    private final UsersService usersService;
    private final UserVocabProgressAndBookMarkRepo userVocabProgressAndBookMarkRepo;
    private final VocabsService vocabsService;
    private final StreakCountUtil streakCountUtil;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public void bookMarkVocab(Authentication auth, Long vocabId) {
        String username = auth.getName();
        Users user = usersService.findUserByUsername(username);

        // Check if the record already exists
        UserVocabProgressAndBookMarks progress = userVocabProgressAndBookMarkRepo
                .findByUserIdAndVocabsId(user.getId(), vocabId);

        // Use this boolean to track the final state of the bookmark toggle
        boolean isNowBookmarked;

        if (progress == null) {
            // SCENARIO 1: First time interacting with this word
            UserVocabProgressAndBookMarks newProgress = new UserVocabProgressAndBookMarks();
            newProgress.setUser(user);
            newProgress.setVocabs(vocabsService.findById(vocabId));
            newProgress.setBookmarked(true);
            newProgress.setLearned(false);

            // YOU MUST EXPLICITLY SAVE NEW OBJECTS
            userVocabProgressAndBookMarkRepo.save(newProgress);

            isNowBookmarked = true; // The word is now bookmarked!

        } else {
            // SCENARIO 2: Record exists. Let's make it a TOGGLE.
            boolean currentBookmarkState = progress.isBookmarked();
            progress.setBookmarked(!currentBookmarkState);

            isNowBookmarked = !currentBookmarkState; // Tracks whatever it just changed to
        }

        //  Trigger the streak engine ONLY if they actively bookmarked the word
        if (isNowBookmarked) {
            streakCountUtil.updateUserStreak(user);
        }
    }

    @Override
    @Transactional
    public void markVocabAsLearned(Authentication auth, Long vocabId) {
        String username = auth.getName();
        Users user = usersService.findUserByUsername(username);

        // Check if the record already exists
        UserVocabProgressAndBookMarks progress = userVocabProgressAndBookMarkRepo
                .findByUserIdAndVocabsId(user.getId(), vocabId);

        // We will use this boolean to track the final state of the word
        boolean isNowLearned;

        if (progress == null) {
            // SCENARIO 1: First time interacting with this word
            UserVocabProgressAndBookMarks newProgress = new UserVocabProgressAndBookMarks();
            newProgress.setUser(user);
            newProgress.setVocabs(vocabsService.findById(vocabId));
            newProgress.setLearned(true);
            newProgress.setBookmarked(false);

            userVocabProgressAndBookMarkRepo.save(newProgress);

            isNowLearned = true; // The word is now learned!

        } else {
            // SCENARIO 2: Record exists. Toggle the Learned state.
            boolean currentLearnedState = progress.isLearned();
            progress.setLearned(!currentLearnedState);

            isNowLearned = !currentLearnedState; // Tracks whatever it just changed to
        }

        //  Trigger the streak engine ONLY if they actually learned the word
        // (This prevents them from getting a streak point for un-learning a word)
        if (isNowLearned) {
            streakCountUtil.updateUserStreak(user);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VocabResponse> getAllBookmarkedVocabs(Authentication auth, int page, int size) {
        String username = auth.getName();
        Users user = usersService.findUserByUsername(username);
        long userId = user.getId();
        // 2. Create the Pageable object
        Pageable pageable = PageRequest.of(page, size);
        // 3. Let the database fetch ONLY the 10 bookmarked records for this specific page
        Page<UserVocabProgressAndBookMarks> bookMarkedProgressePage = userVocabProgressAndBookMarkRepo
                .findByUserIdAndIsBookmarkedTrue(userId,pageable);
        // 4. Map the Page of Progress entities directly into a Page of VocabResponse DTOs
        Page<VocabResponse> responsePage = bookMarkedProgressePage.map(progress -> {
            // Extract the Vocab from the progress, and let ModelMapper turn it into the DTO
            Vocabs vocab = progress.getVocabs();
            return modelMapper.map(vocab, VocabResponse.class);
        });
        return responsePage;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VocabResponse> getAllLearnedVocabs(Authentication auth, int page, int size) {
        String username = auth.getName();
        Users user = usersService.findUserByUsername(username);
        long userId = user.getId();
        // 1. create the page oject
        Pageable pageable = PageRequest.of(page, size);
        // 3. Let the database fetch ONLY the 10 learned records for this specific page
        Page<UserVocabProgressAndBookMarks> learnedVocabProgress = userVocabProgressAndBookMarkRepo
                .findByUserIdAndIsLearnedTrue(userId,pageable);
        // 4. Map the Page of Progress entities directly into a Page of VocabResponse DTOs
        Page<VocabResponse> responsePage = learnedVocabProgress.map(progress -> {
            // Extract the Vocab from the progress, and let ModelMapper turn it into the DTO
            Vocabs vocab = progress.getVocabs();
            return modelMapper.map(vocab, VocabResponse.class);
        });
        return responsePage;
    }
}
