package com.PixBrew.vocabMitra.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class SubscriptionPaymentVerificationDto {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
