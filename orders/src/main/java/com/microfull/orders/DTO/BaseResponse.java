package com.microfull.orders.DTO;

public record BaseResponse(String[] errorMessages) {

    public boolean hasError() {
        return errorMessages != null && errorMessages.length > 0;
    }
}
