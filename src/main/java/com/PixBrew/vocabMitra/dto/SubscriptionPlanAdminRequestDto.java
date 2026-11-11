package com.PixBrew.vocabMitra.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
public class SubscriptionPlanAdminRequestDto {
    @NotBlank(message = "Plan name cannot be blank.")
    private String planName;
    @NotBlank(message = "Plan Duration cannot be blank.")
    @Max(value = 6, message = "Plan duration cannot be more than 6 Months.")
    private int durationInMonths;
    @NotBlank(message = "Subscription Plan Price cannot be blank.")
    @Min(value = 100, message = "Subscription Plan Price cannot be less than 100(INR).")
    private double planPrice;
}
