package com.PixBrew.vocabMitra.repository;

import com.PixBrew.vocabMitra.entity.SubscriptionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionOrderRepo extends JpaRepository<SubscriptionOrder, Long> {

    Optional<SubscriptionOrder> findByRazorpayOrderId(String razorpayOrderId);
}
