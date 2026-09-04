package com.PixBrew.vocabMitra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VocabCardResponseDto {

    private VocabResponse vocabResponse;
    private boolean isLearned;
    private boolean isBookmarked;
}
