package com.parasoft.demoapp.retrofitConfig.response;

import java.util.List;
import lombok.*;

@Data
public class OrderListResponse {
    List<OrderResponse> content;
}