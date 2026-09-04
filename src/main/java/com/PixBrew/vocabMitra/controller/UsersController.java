package com.PixBrew.vocabMitra.controller;

import com.PixBrew.vocabMitra.dto.ProfileResponseDto;
import com.PixBrew.vocabMitra.dto.VocabCardResponseDto;
import com.PixBrew.vocabMitra.dto.VocabResponse;
import com.PixBrew.vocabMitra.service.UserVocabProgressBookMarkService;
import com.PixBrew.vocabMitra.service.UsersService;
import com.PixBrew.vocabMitra.service.VocabsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;
    private final VocabsService vocabsService;
    private final UserVocabProgressBookMarkService userVocabProgressBookMarkService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDto> userProfile(@RequestParam(name = "userId")long id){
        try{
            return ResponseEntity.ok(usersService.loadUserProfile(id));
        } catch (Exception e) {
            throw new RuntimeException("exception in userProfile" + e.getMessage());
        }
    }
    @PatchMapping("/update")
    public ResponseEntity<ProfileResponseDto> userUpdate(Authentication authentication,
                                                         @RequestBody Map<String, Object> updates) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(usersService.updateUserProfile(authentication, updates));
        } catch (Exception e) {
            throw new RuntimeException("exception in updateUser" + e.getMessage());
        }
    }
    @DeleteMapping("/delete{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        try {
            usersService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new RuntimeException("Exception at DeleteUser" + e.getMessage());
        }
    }
    @PostMapping("/bookmarkVocab")
    public ResponseEntity<String> bookmarkVocab(Authentication auth,
                                                @RequestParam(name = "VocabId") Long vocabId){
        try {
            userVocabProgressBookMarkService.bookMarkVocab(auth, vocabId);
            return ResponseEntity.ok("Bookmarked Successfully");
        } catch (Exception e) {
            throw new RuntimeException("Exception in BookMakVocab Method" + e.getMessage());
        }
    }

    @PostMapping("/markVocabIsLearned")
    public ResponseEntity<String> markVocabIsLearned(Authentication auth,
                                                @RequestParam(name = "VocabId") Long vocabId){
        try {
            userVocabProgressBookMarkService.markVocabAsLearned(auth, vocabId);
            return ResponseEntity.ok("vocab Successfully marked as is learned");
        } catch (Exception e) {
            throw new RuntimeException("Exception in markVocabIsLearned Method" + e.getMessage());
        }
    }
//    page of All the vocabularies  bookmarked by the user
    @GetMapping("/private/bookmarkedVocabs")
    public ResponseEntity<Page<VocabResponse>> allBookmarkedVocabsByUser(Authentication auth,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        try{
            return ResponseEntity.ok(userVocabProgressBookMarkService.getAllBookmarkedVocabs(auth, page, size));
        } catch (Exception e) {
            throw new RuntimeException("exception in allBookmarkedVocabByUser" + e.getMessage());
        }
    }
//     page of all the vocabularies learned by user
    @GetMapping("/private/learnedVocabs")
    public ResponseEntity<Page<VocabResponse>> allLearnedVocabsByUser(Authentication auth,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(userVocabProgressBookMarkService.getAllLearnedVocabs(auth, page,size));
        } catch (Exception e) {
            throw new RuntimeException("exception in allLearnedVocabsByUser()" + e.getMessage());
        }
    }
    @GetMapping("/private/filterVocab")
    public ResponseEntity<Page<VocabCardResponseDto>> filterVocabByUseCaseTag(
            Authentication authentication,
            @RequestParam("useCaseTag") String useCaseTag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy)
    {
        // Prevent empty searches from hitting the database
        if (useCaseTag == null || useCaseTag.trim().isEmpty()) {
            return ResponseEntity.ok(Page.empty());
        };
        return ResponseEntity.ok(vocabsService.getVocabsByUseCaseTag(authentication, useCaseTag, page, size, sortBy));
    }
}
