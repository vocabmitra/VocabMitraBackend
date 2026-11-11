package com.PixBrew.vocabMitra.service.impl;

import com.PixBrew.vocabMitra.dto.SubscriptionPaymentVerificationDto;
import com.PixBrew.vocabMitra.dto.SubscriptionRequestDto;
import com.PixBrew.vocabMitra.dto.SubscriptionResponseDto;
import com.PixBrew.vocabMitra.entity.SubscriptionOrder;
import com.PixBrew.vocabMitra.entity.SubscriptionPlans;
import com.PixBrew.vocabMitra.entity.Users;
import com.PixBrew.vocabMitra.exception.RecordNotFoundException;
import com.PixBrew.vocabMitra.repository.SubscriptionOrderRepo;
import com.PixBrew.vocabMitra.repository.SubscriptionPlanRepo;
import com.PixBrew.vocabMitra.repository.UsersRepository;
import com.PixBrew.vocabMitra.security.AuthUtil;
import com.PixBrew.vocabMitra.service.PaymentService;
import com.PixBrew.vocabMitra.service.SubscriptionOrderService;
import com.PixBrew.vocabMitra.service.UsersService;
import com.PixBrew.vocabMitra.type.OrderPaymentStatus;
import com.PixBrew.vocabMitra.type.RoleType;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionOrderServiceImpl implements SubscriptionOrderService {

    private final PaymentService paymentService; // Injected loosely via the interface
    private final SubscriptionOrderRepo orderRepository;
    private final UsersService usersService;
    private final UsersRepository usersRepository;
    private final AuthUtil authUtil;
    private final SubscriptionPlanRepo subscriptionPlanRepo;
    private final SubscriptionOrderRepo subscriptionOrderRepo;

    @Override
    @Transactional
    public SubscriptionResponseDto createSubscriptionOrder(Authentication auth, SubscriptionRequestDto request) throws Exception {
        String username = auth.getName();
        Users currentUser = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RecordNotFoundException("User Not Found with username: " + username));

        SubscriptionPlans selectedPlan = subscriptionPlanRepo.findById(request.getSubscriptionPlanId())
                .orElseThrow(() -> new RecordNotFoundException("Plan does not exist with id: " + request.getSubscriptionPlanId()));

        // SAFETY UPGRADE: Use findFirstBy... to prevent crashes if there are multiple abandoned clicks
        Optional<SubscriptionOrder> alreadyPendingOrder = subscriptionOrderRepo
                .findFirstByUserAndPlanAndStatusOrderByCreatedAtDesc(currentUser, selectedPlan, OrderPaymentStatus.CREATED);

        if (alreadyPendingOrder.isPresent()) {
            SubscriptionOrder pendingOrder = alreadyPendingOrder.get(); // Clean variable assignment

            return SubscriptionResponseDto.builder()
                    .razorpayOrderId(pendingOrder.getRazorpayOrderId())
                    .plan(pendingOrder.getPlan().getPlanName())
                    .amount(pendingOrder.getAmountPaid())
                    .currency("INR")
                    .build();
        }

        Double amountInRupees = selectedPlan.getPlanPrice();
        // Use Math.round to prevent weird floating-point precision drops (e.g. 129.0 * 100 becoming 12899.999...)
        int amountInPaise = (int) Math.round(amountInRupees * 100);
        int durationMonths = selectedPlan.getDurationInMonths();

        // Delegate to the PaymentService
        try{
        String generatedOrderId = paymentService.createOrder(amountInPaise);

            // Save state to your database
            SubscriptionOrder newOrder = SubscriptionOrder.builder()
                    .razorpayOrderId(generatedOrderId)
                    .status(OrderPaymentStatus.CREATED)
                    .durationMonths(durationMonths)
                    .plan(selectedPlan)
                    .amountPaid(amountInPaise / 100) // Storing Rupees
                    .user(currentUser)
                    .build();

            // (Make sure your injected repo is consistently named! You used orderRepository here but subscriptionOrderRepo above)
            subscriptionOrderRepo.save(newOrder);

            return SubscriptionResponseDto.builder()
                    .razorpayOrderId(generatedOrderId)
                    .plan(selectedPlan.getPlanName())
                    .amount(amountInPaise) // Returning Paise
                    .currency("INR")
                    .build();
        } catch (RazorpayException e) {
            throw new RazorpayException("Failed to initialize payment gateway: " + e.getMessage());
        }
    }

    @Override
    @Transactional
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
        SubscriptionPlans boughtPlan = subscriptionPlanRepo.findById(paymentVerificationDto.getSubscriptionPlanId())
                .orElseThrow(()-> new IllegalArgumentException("Plan Not found with provided id"));
        long currentOrdersCount = boughtPlan.getOrderCount();
        boughtPlan.setOrderCount(currentOrdersCount + 1);
        subscriptionPlanRepo.save(boughtPlan);
        return true;
    }
}

