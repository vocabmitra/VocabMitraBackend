package com.PixBrew.vocabMitra.controller;

import com.PixBrew.vocabMitra.dto.VocabCardResponseDto;
import com.PixBrew.vocabMitra.dto.VocabResponse;
import com.PixBrew.vocabMitra.service.VocabsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/vocabs")
@RequiredArgsConstructor
public class VocabController {

    private final VocabsService vocabsService;

//    page of vocabularies
    @GetMapping("/public/all")
    public ResponseEntity<Page<VocabResponse>> allVocabs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id", required = false) String sortBy
    ){
        try {
            return ResponseEntity.ok(vocabsService.getAllVocabs(page, size, sortBy));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    //Vocabs Page for users(tailored for them
    @GetMapping("/private/all/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<VocabCardResponseDto>> allVocabs(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ){
        try {
            return ResponseEntity.ok(vocabsService.getAllVocabsForuser(authentication, page, size, sortBy));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//    public search feature to search any vocab
    @GetMapping("/public/search")
    public ResponseEntity<List<VocabResponse>> searchVocabularies(
            @RequestParam("query") String query
    ) {
        // Prevent empty searches from hitting the database
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<VocabResponse> searchResults = vocabsService.getVocabsByQuery(query);
        return ResponseEntity.ok(searchResults);
    }
}
