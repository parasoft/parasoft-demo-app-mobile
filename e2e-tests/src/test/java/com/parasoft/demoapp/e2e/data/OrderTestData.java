package com.parasoft.demoapp.e2e.data;

import java.util.List;

import lombok.Data;

@Data
public class OrderTestData {
    private String status;
    private String comments;
    private Boolean reviewedByAPV;
    private List<OrderItem> orderItems;
    private OrderRequest orderRequest;
}
