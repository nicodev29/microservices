package com.microfull.orders.service;

import com.microfull.orders.DTOs.BaseResponse;
import com.microfull.orders.DTOs.OrderItemRequest;
import com.microfull.orders.DTOs.OrderRequest;
import com.microfull.orders.config.WebClientConfig;
import com.microfull.orders.model.Order;
import com.microfull.orders.model.OrderItems;
import com.microfull.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {


    private OrderRepository orderRepository;
    private WebClient.Builder webClientBuilder;


    public void placeOrder(OrderRequest orderRequest) {

        //check for stock

        BaseResponse result = this.webClientBuilder.build()
                .post()
                .uri("http://localhost:8082/api/inventory/stock")
                .bodyValue(orderRequest.getOrderItems())
                .retrieve()
                .bodyToMono(BaseResponse.class)
                .block();
        if ((result != null && !result.hasError())) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setOrderItems(orderRequest.getOrderItems().stream()
                    .map(orderItemRequest -> mapOrderItemRequestToOrderItem(orderItemRequest, order))
                    .toList());
            this.orderRepository.save(order);
        }else {
            throw new RuntimeException("Error occurred while placing order");
        }
    }

    private OrderItems mapOrderItemRequestToOrderItem(OrderItemRequest orderItemRequest, Order order) {
        return OrderItems.builder()
                .id(orderItemRequest.getId())
                .sku(orderItemRequest.getSku())
                .price(orderItemRequest.getPrice())
                .quantity(orderItemRequest.getQuantity())
                .order(order)
                .build();
    }

}
