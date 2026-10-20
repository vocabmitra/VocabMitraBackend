package com.PixBrew.vocabMitra.controller;

import com.PixBrew.vocabMitra.dto.SubscriptionPaymentVerificationDto;
import com.PixBrew.vocabMitra.dto.SubscriptionRequestDto;
import com.PixBrew.vocabMitra.dto.SubscriptionResponseDto;
import com.PixBrew.vocabMitra.service.SubscriptionOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionOrderController {

    private final SubscriptionOrderService subscriptionOrderService;
//    Frontend asks for an Order ID to start the Razorpay popup
    @PostMapping("/create-order")
    public ResponseEntity<SubscriptionResponseDto> createSubscriptionOrder(Authentication auth,
                                                                           @RequestBody SubscriptionRequestDto requestDto) {
        try{
            System.out.println(requestDto.getDurationMonths());
            SubscriptionResponseDto response = subscriptionOrderService.createSubscriptionOrder(auth, requestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);
        } catch (Exception e) {
            throw new RuntimeException("Error creating Razorpay order: " + e.getMessage());
        }
    }
//    Frontend sends the success details back for cryptographic verification
    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPaymentForSubscriptionOrder(Authentication authentication,
                                                               @RequestBody SubscriptionPaymentVerificationDto verificationDto) {
        try {
            boolean isSuccessful = subscriptionOrderService.verifyAndActivateSubscription(authentication, verificationDto);
            if(isSuccessful){
                return ResponseEntity.ok(Map.of("message", "Payment verified and subscription activated successfully!"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Payment verification failed. Invalid signature."));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error verifying razorpay Payment: " + e.getMessage());
        }
    }
}
