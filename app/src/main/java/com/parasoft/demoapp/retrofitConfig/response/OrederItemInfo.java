package com.parasoft.demoapp.retrofitConfig.response;

import lombok.Data;

@Data
public class OrederItemInfo {
    private Long id;

    private String name;

    private String description;

    private String image;

    private Long itemId;

    private Integer quantity;

    public OrederItemInfo(String name, String description, String image, Integer quantity) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.quantity = quantity;
    }
}
