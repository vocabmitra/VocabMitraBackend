package com.PixBrew.vocabMitra.service;

import com.PixBrew.vocabMitra.dto.VocabAddRequest;
import com.PixBrew.vocabMitra.dto.VocabCardResponseDto;
import com.PixBrew.vocabMitra.dto.VocabResponse;
import com.PixBrew.vocabMitra.entity.Vocabs;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public interface VocabsService {
     VocabResponse addVocablary(VocabAddRequest vocabAddRequest);

     Page<VocabResponse> getAllVocabs(int page, int size, String sortBy);

     VocabResponse updateVocabDetails(long id, Map<String, Object> updates);

     Page<VocabCardResponseDto> getAllVocabsForuser(Authentication authentication, int page, int size, String sortBy);

    Vocabs findById(Long vocabId);

    List<VocabResponse> getVocabsByQuery(String qVocab);

    Page<VocabCardResponseDto> getVocabsByUseCaseTag(Authentication authentication, String useCaseTag, int page, int size, String sortBy);
}
