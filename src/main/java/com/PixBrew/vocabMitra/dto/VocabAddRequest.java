package com.PixBrew.vocabMitra.dto;

import com.PixBrew.vocabMitra.type.VocabType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VocabAddRequest {
    @NotBlank(message = "Vocabulary cannot be blank.")
    private String vocab;
    private VocabType vocabType;
    @NotBlank(message = "There Must Exist at least 1 Use case.")
    private String useCaseTag;
    @NotBlank(message = "Trick cannot be blank.")
    private String trick;
    @NotBlank(message = "Meaning cannot be blank.")
    private String meaning;
    @NotBlank(message = "There must be at least one example.")
    private String example;
    private String message;
    private Boolean isPremium;
}
