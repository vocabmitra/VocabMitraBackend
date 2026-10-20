package com.PixBrew.vocabMitra.service.impl;

import com.PixBrew.vocabMitra.dto.SubscriptionPaymentVerificationDto;
import com.PixBrew.vocabMitra.dto.SubscriptionRequestDto;
import com.PixBrew.vocabMitra.dto.SubscriptionResponseDto;
import com.PixBrew.vocabMitra.entity.SubscriptionOrder;
import com.PixBrew.vocabMitra.entity.Users;
import com.PixBrew.vocabMitra.repository.SubscriptionOrderRepo;
import com.PixBrew.vocabMitra.repository.UsersRepository;
import com.PixBrew.vocabMitra.security.AuthUtil;
import com.PixBrew.vocabMitra.service.PaymentService;
import com.PixBrew.vocabMitra.service.SubscriptionOrderService;
import com.PixBrew.vocabMitra.service.UsersService;
import com.PixBrew.vocabMitra.type.OrderPaymentStatus;
import com.PixBrew.vocabMitra.type.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class SubscriptionOrderServiceImpl implements SubscriptionOrderService {

    private final PaymentService paymentService; // Injected loosely via the interface
    private final SubscriptionOrderRepo orderRepository;
    private final UsersService usersService;
    private final UsersRepository usersRepository;
    private final AuthUtil authUtil;

    @Override
    public SubscriptionResponseDto createSubscriptionOrder(Authentication auth, SubscriptionRequestDto request) throws Exception {
        String username = auth.getName();
        Users currentUser = usersRepository.findByUsername(username);

        int amountInRupees = calculatePrice(request.getDurationMonths());
        int amountInPaise = amountInRupees * 100;

        // Delegate to the PaymentService
        String generatedOrderId = paymentService.createOrder(amountInPaise);

        // Save state to your database
        SubscriptionOrder newOrder = SubscriptionOrder.builder()
                .razorpayOrderId(generatedOrderId)
                .status(OrderPaymentStatus.CREATED)
                .durationMonths(request.getDurationMonths())
                .amountPaid(amountInPaise)
                .user(currentUser)
                .build();
        orderRepository.save(newOrder);

        return SubscriptionResponseDto.builder()
                .razorpayOrderId(generatedOrderId)
                .amount(amountInPaise)
                .currency("INR")
                .build();
    }

    @Override
    public boolean verifyAndActivateSubscription(Authentication auth, SubscriptionPaymentVerificationDto paymentVerificationDto) {
        // 1. Verify cryptographic signature via PaymentService
        String orderId = paymentVerificationDto.getRazorpayOrderId();
        String paymentId = paymentVerificationDto.getRazorpayPaymentId();
        String signature = paymentVerificationDto.getRazorpaySignature();
        boolean isValid = paymentService.verifySignature(orderId, paymentId, signature);

        SubscriptionOrder order = orderRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!isValid) {
            order.setStatus(OrderPaymentStatus.FAILED);
            orderRepository.save(order);
            return false; // Tell frontend it failed
        }

        // 2. Signature is valid! Update the Order Ledger
        order.setRazorpayPaymentId(paymentId);
        order.setRazorpaySignature(signature);
        order.setStatus(OrderPaymentStatus.SUCCESS);
        orderRepository.save(order);

        // 3. Upgrade the User!
        Users user = order.getUser();
        Instant now = Instant.now();

        if (user.getSubscriptionEndDate() == null || user.getSubscriptionEndDate().isBefore(now)) {
            user.setSubscriptionStartDate(now);
            Instant newEndDate = now.atZone(ZoneOffset.UTC)
                    .plusMonths(order.getDurationMonths())
                    .toInstant();
            user.setSubscriptionEndDate(newEndDate);
        } else {
            // Stack the new months on top of their existing remaining time
            Instant newEndDate = user.getSubscriptionEndDate()
                    .atZone(ZoneOffset.UTC)
                    .plusMonths(order.getDurationMonths())
                    .toInstant();
            user.setSubscriptionEndDate(newEndDate);
        }

        user.setRole(RoleType.SUBSCRIBED_USER);
        usersRepository.save(user);

        return true;
    }

//    Helper method to count price as per month
private int calculatePrice(int months) {
    System.out.println(months);
    return switch (months) {
        case 1 -> 129;
        case 2 -> 248;
        case 3 -> 357;
        default -> throw new IllegalArgumentException("Invalid subscription duration");
    };
}
}

