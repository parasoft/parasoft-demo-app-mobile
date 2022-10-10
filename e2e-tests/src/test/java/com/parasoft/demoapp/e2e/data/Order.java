package com.parasoft.demoapp.e2e.data;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Order {
    public static final String HASH_SIGN = "#";
    private static  final String TIME_PATTERN = "HH:mm:ss";

    public static final String SUBMITTED_STATUS = "SUBMITTED";
    public static final String DECLINED_STATUS = "DECLINED";
    public static final String APPROVED_STATUS = "APPROVED";

    private static final String OPEN_STATUS = "OPEN";
    public static final String DENIED_STATUS = "DENIED";

    private Long id;
    private String orderNumber;
    private String requestedBy;
    private String status;
    private Boolean reviewedByAPV;
    private Boolean reviewedByPRCH;
    private String respondedBy;
    private List<OrderItem> orderItems;
    private String region;
    private String location;
    private String orderImage;
    private String receiverId;
    private String eventId;
    private String eventNumber;
    private Date submissionDate;
    private Date approverReplyDate;
    private String comments;

    public String getUiStatus() {
        if (StringUtils.equals(status, SUBMITTED_STATUS)) {
            return OPEN_STATUS;
        } else if (StringUtils.equals(status, DECLINED_STATUS)) {
            return DENIED_STATUS;
        }
        return status;
    }

    public String getUiOrderNumber() {
        return HASH_SIGN + orderNumber;
    }

    public String getOrderDetailDate() {
        return LocalDateTime.ofInstant(submissionDate.toInstant(), ZoneId.systemDefault()).
                format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public String getOrderDetailTime() {
        return LocalDateTime.ofInstant(submissionDate.toInstant(), ZoneId.systemDefault()).
                format(DateTimeFormatter.ofPattern(TIME_PATTERN));
    }

    public boolean isOpenOrder() {
        return StringUtils.equals(status, SUBMITTED_STATUS);
    }
}
