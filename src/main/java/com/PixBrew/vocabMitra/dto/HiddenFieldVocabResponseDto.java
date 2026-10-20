package com.PixBrew.vocabMitra.dto;

import com.PixBrew.vocabMitra.type.VocabType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HiddenFieldVocabResponseDto {

    private long id;
    private String vocab;
    private VocabType vocabType;
    private String useCaseTag;
    private LocalDateTime updatedAt;
    private boolean isPremium;
}
