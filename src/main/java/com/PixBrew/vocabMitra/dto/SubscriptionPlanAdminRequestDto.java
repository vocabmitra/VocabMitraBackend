package com.PixBrew.vocabMitra.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
public class SubscriptionPlanAdminRequestDto {
    @NotBlank
    private String planName;
    @NotBlank
    @Max(6)
    private int durationInMonths;
    @NotBlank
    @Min(100)
    private double planPrice;
}
