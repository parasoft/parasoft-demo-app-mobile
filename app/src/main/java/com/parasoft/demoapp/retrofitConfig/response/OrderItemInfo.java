package com.parasoft.demoapp.retrofitConfig.response;

import lombok.Data;

@Data
public class OrderItemInfo {
    private Long id;

    private String name;

    private String description;

    private String image;

    private Long itemId;

    private Integer quantity;

    public OrderItemInfo(String name, String description, String image, Integer quantity) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.quantity = quantity;
    }
}
