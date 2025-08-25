package br.com.wtd.liveinsights.dto;

import org.springframework.http.HttpStatus;

public record ErrorResponse(String message, HttpStatus status) {
    
}
