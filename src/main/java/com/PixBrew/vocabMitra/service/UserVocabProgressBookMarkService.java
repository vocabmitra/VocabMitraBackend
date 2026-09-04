package com.PixBrew.vocabMitra.service;

import com.PixBrew.vocabMitra.dto.VocabResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface UserVocabProgressBookMarkService {
    void bookMarkVocab(Authentication auth, Long vocabId);
    void markVocabAsLearned(Authentication auth, Long vocabId);

   Page<VocabResponse> getAllBookmarkedVocabs(Authentication auth, int page, int size);

   Page<VocabResponse> getAllLearnedVocabs(Authentication auth, int page, int size);
}
