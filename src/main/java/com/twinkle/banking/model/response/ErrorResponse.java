package com.twinkle.banking.model.response;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String responseMessage;

    public ErrorResponse(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}