package com.PixBrew.vocabMitra.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name ="subscription_plans")
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class SubscriptionPlans {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long subscriptionPlanId;
    @Column(nullable = false)
    private String planName;
    @Column(nullable = false)
    private int durationInMonths;
    @Column(nullable = false)
    private double planPrice;
    private long orderCount;
    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;
    @Column(nullable = false)
    private long updatedByAdmin;
}
