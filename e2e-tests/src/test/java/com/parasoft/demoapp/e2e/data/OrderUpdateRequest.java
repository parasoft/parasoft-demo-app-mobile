package com.parasoft.demoapp.e2e.data;

import lombok.Data;

@Data
public class OrderUpdateRequest {
    private String status;
    private String comments;
    private Boolean reviewedByAPV;
}
