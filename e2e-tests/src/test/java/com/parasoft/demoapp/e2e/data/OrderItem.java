package com.parasoft.demoapp.e2e.data;

import lombok.Data;

@Data
public class OrderItem {
    private Long id;
    private String name;
    private String description;
    private String image;
    private Long itemId;
    private Integer quantity;
}
