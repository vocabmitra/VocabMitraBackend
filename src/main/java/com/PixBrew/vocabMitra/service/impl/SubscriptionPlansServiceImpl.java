package com.PixBrew.vocabMitra.service.impl;

import com.PixBrew.vocabMitra.dto.SubscriptionPlanAdminRequestDto;
import com.PixBrew.vocabMitra.dto.SubscriptionPlanAdminResponseDto;
import com.PixBrew.vocabMitra.entity.SubscriptionPlans;
import com.PixBrew.vocabMitra.entity.Users;
import com.PixBrew.vocabMitra.repository.SubscriptionPlanRepo;
import com.PixBrew.vocabMitra.service.SubscriptionPlansService;
import com.PixBrew.vocabMitra.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionPlansServiceImpl implements SubscriptionPlansService {
    
    private final SubscriptionPlanRepo subscriptionPlanRepo;
    private final ModelMapper modelMapper;
    private final UsersService usersService;

    @Override
    public List<SubscriptionPlanAdminResponseDto> getAllSubscriptionPlans() {
        List<SubscriptionPlans> plans = subscriptionPlanRepo.findAll();
        if(plans.isEmpty()){
            return Collections.emptyList();
        }
        return plans.stream()
                .map(plan -> modelMapper.map(plan, SubscriptionPlanAdminResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubscriptionPlanAdminResponseDto createSubscriptionPlan(Authentication auth, SubscriptionPlanAdminRequestDto requestDto) {
        String username = auth.getName();
        Users adminUser = usersService.findUserByUsername(username);
        long loggedInAdminId = adminUser.getId();

        SubscriptionPlans newSubscriptionPlan = SubscriptionPlans.builder()
                .planName(requestDto.getPlanName())
                .planPrice(requestDto.getPlanPrice())
                .durationInMonths(requestDto.getDurationInMonths())
                .createdAt(Instant.now())
                .orderCount(0)
                .build();
        SubscriptionPlans savedPlan = subscriptionPlanRepo.save(newSubscriptionPlan);
        return modelMapper.map(savedPlan, SubscriptionPlanAdminResponseDto.class);
    }

    @Override
    @Transactional
    public SubscriptionPlanAdminResponseDto updateExistingSubscriptionPlan(Authentication auth, long subscriptionPlanId, Map<String, Object> updates) {

        SubscriptionPlans thePlan = subscriptionPlanRepo.findById(subscriptionPlanId)
                .orElseThrow(()-> new IllegalArgumentException("Plan does not exist with id: " + subscriptionPlanId));
        String username = auth.getName();
        Users adminUser = usersService.findUserByUsername(username);
        long loggedInAdinId = adminUser.getId();
        long orderCount = thePlan.getOrderCount();

        updates.forEach((field, value) -> {
            switch (field) {
                case "planName" : thePlan.setPlanName((String) value);
                   break;
                case "planPrice" : thePlan.setPlanPrice((double) value);
                   break;
                case "durationInMonth" : thePlan.setDurationInMonths((int) value);
                   break;
                default:
                    throw new IllegalArgumentException("Invalid Field for updates");
            }
        });
        thePlan.setUpdatedAt(Instant.now());
        thePlan.setOrderCount(orderCount);
        thePlan.setUpdatedByAdmin(loggedInAdinId);

        SubscriptionPlans savedPlan = subscriptionPlanRepo.save(thePlan);

        return modelMapper.map(savedPlan, SubscriptionPlanAdminResponseDto.class);
    }

    @Override
    public void deleteSubscriptionPLan(long subscriptionPlanId) {
        subscriptionPlanRepo.deleteById(subscriptionPlanId);
    }
}
