package com.PixBrew.vocabMitra.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class SubscriptionPlanAdminResponseDto {
    private long subscriptionPlanId;
    private String planName;
    private int durationInMonths;
    private double planPrice;
    private long orderCount;
    private Instant createdAt;
    private Instant updatedAt;
    private long updatedByAdmin;
}
