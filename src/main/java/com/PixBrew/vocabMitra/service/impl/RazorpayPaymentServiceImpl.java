package com.PixBrew.vocabMitra.service.impl;

import com.PixBrew.vocabMitra.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayPaymentServiceImpl implements PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;
    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Override
    public String createOrder(int amountInPaise) throws Exception {
        RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);
        JSONObject orderRequest = new JSONObject();
        // Razorpay expects the amount in paise, so multiply by 100
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn" + System.currentTimeMillis());

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        // Return the generated ID (e.g., "order_xyz123")
        return razorpayOrder.get("id");
    }

    @Override
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            // We combine the order ID and payment ID, and check it against the signature using your Secret Key
            String payload = orderId + "|" + paymentId;
            return Utils.verifySignature(payload, signature, keySecret);
        } catch (Exception e) {
            System.err.println("Signature verification failed: " + e.getMessage());
            return false;
        }
    }
}
