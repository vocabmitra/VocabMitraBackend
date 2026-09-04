package com.PixBrew.vocabMitra.dto;

import com.PixBrew.vocabMitra.type.VocabType;
import lombok.Data;

@Data
public class VocabAddRequest {
    private String vocab;
    private VocabType vocabType;
    private String useCaseTag;
    private String trick;
    private String meaning;
    private String example;
    private String message;
}
