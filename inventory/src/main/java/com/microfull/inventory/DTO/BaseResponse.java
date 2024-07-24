package com.microfull.inventory.DTO;

public record BaseResponse(String[] errorMessages) {

    public boolean hasError() {
        return errorMessages != null && errorMessages.length > 0;
    }
}
