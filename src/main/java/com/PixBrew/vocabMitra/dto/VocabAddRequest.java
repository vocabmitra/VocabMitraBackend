package com.PixBrew.vocabMitra.dto;

import com.PixBrew.vocabMitra.type.VocabType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VocabAddRequest {
    @NotBlank
    private String vocab;
    private VocabType vocabType;
    @NotBlank
    private String useCaseTag;
    @NotBlank
    private String trick;
    @NotBlank
    private String meaning;
    @NotBlank
    private String example;
    private String message;
    private Boolean isPremium;
}
