package com.PixBrew.vocabMitra.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SubscriptionRequestDto {
    @NotBlank(message = "Subscription Plan Id cannot be blank.")
    private long subscriptionPlanId;
}
