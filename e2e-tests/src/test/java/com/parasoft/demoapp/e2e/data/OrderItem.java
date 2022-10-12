package com.parasoft.demoapp.e2e.data;

import lombok.Data;

@Data
public class OrderItem {

    private static final String ORDER_ITEM_QUANTITY_PREFIX = "x";

    private Long id;
    private String name;
    private String description;
    private String image;
    private Long itemId;
    private Integer quantity;

    public String getUiQuantity() {
        return ORDER_ITEM_QUANTITY_PREFIX + quantity;
    }
}
