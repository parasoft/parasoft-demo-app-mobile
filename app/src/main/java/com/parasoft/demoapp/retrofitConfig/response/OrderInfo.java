package com.parasoft.demoapp.retrofitConfig.response;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class OrderInfo {

    private Long id;

    private String orderNumber;

    private String requestedBy;

    private String status;

    private Boolean reviewedByAPV;

    private Boolean reviewedByPRCH;

    private String respondedBy;  // approver's username

    private List<OrederItemInfo> orderItems; // parasoft-suppress UC.AURCO "expected"

    private String region;

    private String location;

    private String orderImage;

    private String receiverId;

    private String eventId;

    private String eventNumber;

    private Date submissionDate;

    private Date approverReplyDate;

    private String comments;

}
