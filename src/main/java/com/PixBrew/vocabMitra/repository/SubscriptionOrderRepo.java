package com.PixBrew.vocabMitra.repository;

import com.PixBrew.vocabMitra.entity.SubscriptionOrder;
import com.PixBrew.vocabMitra.entity.SubscriptionPlans;
import com.PixBrew.vocabMitra.entity.Users;
import com.PixBrew.vocabMitra.type.OrderPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionOrderRepo extends JpaRepository<SubscriptionOrder, Long> {

    Optional<SubscriptionOrder> findByRazorpayOrderId(String razorpayOrderId);
    Optional<SubscriptionOrder> findByUserAndPlanAndStatus(Users user,
                                                           SubscriptionPlans plan,
                                                           OrderPaymentStatus status);
    Optional<SubscriptionOrder> findFirstByUserAndPlanAndStatusOrderByCreatedAtDesc(
            Users user, SubscriptionPlans plan, OrderPaymentStatus status);
}
