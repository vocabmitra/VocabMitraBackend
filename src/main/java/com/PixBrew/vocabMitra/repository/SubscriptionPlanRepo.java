package com.PixBrew.vocabMitra.repository;

import com.PixBrew.vocabMitra.entity.SubscriptionPlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPlanRepo extends JpaRepository<SubscriptionPlans, Long> {
}
