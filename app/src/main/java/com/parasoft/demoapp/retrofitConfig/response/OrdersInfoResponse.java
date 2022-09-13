package com.parasoft.demoapp.retrofitConfig.response;

import java.util.List;
import lombok.*;

@Data
public class OrdersInfoResponse {
    List<OrdersInfo> content;

    @Data
    public class OrdersInfo {
        private Long id;
        private String orderNumber;
    }
}