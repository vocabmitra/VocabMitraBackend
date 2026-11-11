package com.PixBrew.vocabMitra.dto;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class ValidationExceptionResponseDto {

    private Instant timestamp;
    private int statusCode;
    private String error;
    private String message;
    private String path;
    private Map<String, String> fieldErrors;
}
