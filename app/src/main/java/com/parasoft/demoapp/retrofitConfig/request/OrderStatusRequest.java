package com.parasoft.demoapp.retrofitConfig.request;

import com.parasoft.demoapp.retrofitConfig.response.OrderStatus;

import lombok.Data;

@Data
public class OrderStatusRequest {
    private OrderStatus status;
    private String comments;
    private boolean reviewedByPRCH;
    private boolean reviewedByAPV;
}
