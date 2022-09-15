package com.parasoft.demoapp.retrofitConfig.response;

import lombok.Data;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private String requestedBy;
    private OrderStatus status;
    private Boolean reviewedByAPV;
    private String respondedBy;
    private String submissionDate;
}