package com.PixBrew.vocabMitra.service;

public interface PaymentService {

    String createOrder(int amountInPaise) throws Exception;
    boolean verifySignature(String orderId, String paymentId, String signature);
}
