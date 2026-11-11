package com.PixBrew.vocabMitra.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class GlobalExceptionResponseDto {

    private Instant timestamp;
    private int statusCode;
    private String error;
    private String message;
    private String path;
}
