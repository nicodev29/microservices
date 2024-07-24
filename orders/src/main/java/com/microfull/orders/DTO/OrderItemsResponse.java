package com.microfull.orders.DTO;

public record OrderItemsResponse(Long id, String sku, Double price, Long quantity) {
}
