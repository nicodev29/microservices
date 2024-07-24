package com.microfull.products.DTOs;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.microfull.products.model.Product}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    private String sku;
    private String name;
    private String description;
    private Double price;
    private Boolean status;

}