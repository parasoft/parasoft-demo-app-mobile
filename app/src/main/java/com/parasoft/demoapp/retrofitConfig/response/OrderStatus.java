package com.parasoft.demoapp.retrofitConfig.response;

import lombok.Data;

@Data
public class OrderStatus {

    private String status;
    private String comments;
    private boolean reviewedByPRCH;
    private boolean reviewedByAPV;
}
