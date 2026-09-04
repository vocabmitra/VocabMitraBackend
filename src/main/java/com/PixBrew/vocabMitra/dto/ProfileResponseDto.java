package com.PixBrew.vocabMitra.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDto {

    private String firstName;
    private String lastName;
    private String username;
    private String email;

    // The lightweight statistics
    private long totalBookmarked;
    private long totalLearned;
    // The Daily Streak fields
    private int currentStreak;
    private int maxStreak;
    private boolean isStreakActiveToday;
}
