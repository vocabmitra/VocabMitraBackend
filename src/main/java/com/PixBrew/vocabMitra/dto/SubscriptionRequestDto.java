package com.PixBrew.vocabMitra.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SubscriptionRequestDto {
    private int durationMonths; // ex:- 1,2,3.
}
