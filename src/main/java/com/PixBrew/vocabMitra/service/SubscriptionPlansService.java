package com.PixBrew.vocabMitra.service;

import com.PixBrew.vocabMitra.dto.SubscriptionPlanAdminRequestDto;
import com.PixBrew.vocabMitra.dto.SubscriptionPlanAdminResponseDto;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public interface SubscriptionPlansService {
    List<SubscriptionPlanAdminResponseDto> getAllSubscriptionPlans();

    SubscriptionPlanAdminResponseDto createSubscriptionPlan(Authentication auth, @Valid SubscriptionPlanAdminRequestDto requestDto);

    SubscriptionPlanAdminResponseDto updateExistingSubscriptionPlan(Authentication auth, long subscriptionPlanId, Map<String, Object> updates);

    void deleteSubscriptionPLan(long subscriptionPlanId);
}
