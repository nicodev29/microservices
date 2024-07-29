package com.microfull.orders.service;

import com.microfull.orders.DTO.*;
import com.microfull.orders.model.Order;
import com.microfull.orders.model.OrderItems;
import com.microfull.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {


    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;


    public void placeOrder(OrderRequest orderRequest) {
        BaseResponse result;
        try {
            log.info("Checking stock for order: {}", orderRequest);
            result = this.webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8080/api/inventory/in-stock")
                    .bodyValue(orderRequest.getOrderItems())
                    .retrieve()
                    .bodyToMono(BaseResponse.class)
                    .block();
            log.info("Stock check result: {}", result);
        } catch (WebClientResponseException e) {
            log.error("Error occurred while checking stock: {}", e.getMessage(), e);
            throw new RuntimeException("Error occurred while checking stock: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred: " + e.getMessage());
        }

        if (result != null && !result.hasErrors()) {
            log.info("Creating order");
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setOrderItems(orderRequest.getOrderItems().stream()
                    .map(orderItemRequest -> mapOrderItemRequestToOrderItem(orderItemRequest, order))
                    .toList());
            this.orderRepository.save(order);
            log.info("Order created successfully: {}", order.getOrderNumber());
        } else {
            log.error("Error occurred while placing order. Result: {}", result);
            throw new RuntimeException("Error occurred while placing order");
        }
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = this.orderRepository.findAll();
        return orders.stream().map(this::mapToOrderResponse).toList();

    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(order.getId(),
                        order.getOrderNumber(),
                        order.getOrderItems().
                        stream().
                        map(this::mapToOrderItemRequest).
                        toList());
    }

    private OrderItemsResponse mapToOrderItemRequest(OrderItems orderItems) {
        return new OrderItemsResponse(orderItems.getId(),
                orderItems.getSku(),
                orderItems.getPrice(),
                orderItems.getQuantity());
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
