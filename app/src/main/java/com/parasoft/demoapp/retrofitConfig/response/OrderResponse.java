package com.parasoft.demoapp.retrofitConfig.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class OrderResponse {
    private Long id;

    private String orderNumber;

    private String requestedBy;

    private OrderStatus status;

    private Boolean reviewedByAPV;

    private Boolean reviewedByPRCH;

    private String respondedBy;  // approver's username

    private List<OrderItemInfo> orderItems = new ArrayList<>();

    private String region;

    private String location;

    private String orderImage;

    private String receiverId;

    private String eventId;

    private String eventNumber;

    private String submissionDate;

    private Date approverReplyDate;

    private String comments;

    @Data
    public static class OrderItemInfo {
        private Long id;

        private String name;

        private String description;

        private String image;

        private Long itemId;

        private Integer quantity;

        public OrderItemInfo(String name, String description, String image, Integer quantity) {
            this.name = name;
            this.description = description;
            this.image = image;
            this.quantity = quantity;
        }
    }
}