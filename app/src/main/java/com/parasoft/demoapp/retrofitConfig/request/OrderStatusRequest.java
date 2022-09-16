package com.parasoft.demoapp.retrofitConfig.request;

import lombok.Data;

@Data
public class OrderStatusRequest {
    private String status;
    private String comments;
    private boolean reviewedByPRCH;
    private boolean reviewedByAPV;
}
