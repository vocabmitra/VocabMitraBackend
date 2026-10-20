package com.PixBrew.vocabMitra.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionResponseDto {
    private String razorpayOrderId;
    private int amount; // in paise
    private String currency;
}
