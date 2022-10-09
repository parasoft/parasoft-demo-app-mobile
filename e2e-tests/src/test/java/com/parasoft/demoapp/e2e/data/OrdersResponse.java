package com.parasoft.demoapp.e2e.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrdersResponse extends ApiResponse {
    private OrdersData data;
}
