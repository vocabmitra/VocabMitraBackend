package com.PixBrew.vocabMitra.service;

import com.PixBrew.vocabMitra.dto.SubscriptionPaymentVerificationDto;
import com.PixBrew.vocabMitra.dto.SubscriptionRequestDto;
import com.PixBrew.vocabMitra.dto.SubscriptionResponseDto;
import org.springframework.security.core.Authentication;

public interface SubscriptionOrderService {

    SubscriptionResponseDto createSubscriptionOrder(Authentication auth, SubscriptionRequestDto request) throws Exception;
    boolean verifyAndActivateSubscription(Authentication auth, SubscriptionPaymentVerificationDto paymentVerificationDto);
}
