package com.PixBrew.vocabMitra.dto;

import com.PixBrew.vocabMitra.type.VocabType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VocabResponse {
    private long id;
    private String vocab;
    private VocabType vocabType;
    private String useCaseTag;
    private String trick;
    private String meaning;
    private String example;
    private String message;
    private LocalDateTime updatedAt;
}
