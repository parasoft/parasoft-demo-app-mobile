package com.parasoft.demoapp.e2e.data;

import lombok.Data;

@Data
public class OrderRequest {
    private String region;
    private String location;
    private String receiverId;
    private String eventId;
    private String eventNumber;
}
