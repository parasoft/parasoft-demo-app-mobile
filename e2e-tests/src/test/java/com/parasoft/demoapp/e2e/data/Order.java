package com.parasoft.demoapp.e2e.data;

import org.apache.commons.lang3.BooleanUtils;
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

    public static final String PROCESSED_STATUS = "PROCESSED";
    public static final String DECLINED_STATUS = "DECLINED";
    public static final String APPROVED_STATUS = "APPROVED";

    public static final String OPEN_STATUS = "OPEN";
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
        if (StringUtils.equals(status, PROCESSED_STATUS)) {
            return OPEN_STATUS;
        } else if (StringUtils.equals(status, DECLINED_STATUS)) {
            return DENIED_STATUS;
        }
        return status;
    }

    public String getUiOrderNumber() {
        return HASH_SIGN + orderNumber;
    }

    public String getOrderDetailDate(String timeZone) {
        return LocalDateTime.ofInstant(submissionDate.toInstant(), ZoneId.of(timeZone)).
                format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public String getOrderDetailTime(String timeZone) {
        return LocalDateTime.ofInstant(submissionDate.toInstant(), ZoneId.of(timeZone)).
                format(DateTimeFormatter.ofPattern(TIME_PATTERN));
    }

    public boolean isOpenOrder() {
        return StringUtils.equals(status, PROCESSED_STATUS);
    }

    public boolean isNewOrder() {
        return BooleanUtils.isFalse(reviewedByAPV);
    }

    public String getOrderItemTotalQuantity() {
        return Integer.toString(orderItems.stream().mapToInt(OrderItem::getQuantity).sum());
    }

    public boolean hasComments() {
        return StringUtils.isNotEmpty(comments);
    }
}
