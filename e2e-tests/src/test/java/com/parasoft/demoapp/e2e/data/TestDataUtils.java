package com.parasoft.demoapp.e2e.data;

import static org.hamcrest.Matchers.equalTo;
import static io.restassured.RestAssured.given;

import com.google.gson.GsonBuilder;
import com.parasoft.demoapp.e2e.common.AppiumConfig;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.restassured.http.ContentType;

public final class TestDataUtils {

    public static final String PURCHASER_USERNAME = "purchaser";
    public static final String PURCHASER_PASSWORD = "password";
    public static final String APPROVER_USERNAME = "approver";
    public static final String APPROVER_PASSWORD = "password";

    private static final String ANDROID_LOCALHOST_URL_PREFIX = "http://10.0.2.2";
    private static final String LOCALHOST_URL_PREFIX = "http://localhost";
    private static final String API_BASE_URL = getApiBaseUrl();

    public static void resetDatabase() {
        given().
                auth().preemptive().basic(PURCHASER_USERNAME, PURCHASER_PASSWORD).
                when().
                put(API_BASE_URL + "/v1/demoAdmin/databaseReset").
                then().
                contentType(ContentType.JSON).
                body("status", equalTo(1));
    }

    public static List<Order> createTestDataOrders() throws IOException {
        List<Order> orders = new ArrayList<>();
        OrderTestData[] orderTestDataArray = new GsonBuilder().create().
                fromJson(getOrderTestDataReader(), OrderTestData[].class);
        Arrays.stream(orderTestDataArray).forEach(orderTestData -> {
            addItemsToCart(orderTestData.getOrderItems());
            orders.add(createOrder(orderTestData));
        });
        return orders;
    }

    public static List<Order> getAllOrders() {
        return given().
                auth().preemptive().basic(APPROVER_USERNAME, APPROVER_PASSWORD).
                when().
                get(API_BASE_URL + "/v1/orders").
                then().
                contentType(ContentType.JSON).
                body("status", equalTo(1)).
                extract().
                body().as(OrdersResponse.class).getData().getContent();
    }

    public static List<Order> getNewOrders() {
        return getAllOrders().stream().
                filter(order -> BooleanUtils.isFalse(order.getReviewedByAPV())).
                collect(Collectors.toList());
    }

    public static String dateToLocalDateString(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(),
                ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String dateToLocalTimeString(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(),
                ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private static void addItemsToCart(Map<Long,Integer> itemIdQtyMap) {
        itemIdQtyMap.forEach(TestDataUtils::addItemToCart);
    }

    private static void addItemsToCart(List<OrderItem> orderItems) {
        orderItems.forEach(orderItem -> {
            addItemToCart(orderItem.getItemId(), orderItem.getQuantity());
        });
    }

    private static void addItemToCart(Long itemId, Integer itemQty) {
        given().
                auth().preemptive().basic(PURCHASER_USERNAME, PURCHASER_PASSWORD).
                param("itemId", itemId).
                param("itemQty", itemQty).
                when().
                post(API_BASE_URL + "/v1/cartItems").
                then().
                contentType(ContentType.JSON).
                body("status", equalTo(1));
    }

    private static Order submitOrder(OrderRequest orderRequest) {
        return given().
                auth().preemptive().basic(PURCHASER_USERNAME, PURCHASER_PASSWORD).
                body(orderRequest).contentType(ContentType.JSON).
                when().
                post(API_BASE_URL + "/v1/orders").
                then().
                contentType(ContentType.JSON).
                body("status", equalTo(1)).
                extract().
                body().as(OrderResponse.class).getData();
    }

    private static Order updateOrder(String orderNumber, OrderUpdateRequest orderUpdateRequest) {
        return given().
                auth().preemptive().basic(APPROVER_USERNAME, APPROVER_PASSWORD).
                pathParam("orderNumber", orderNumber).
                body(orderUpdateRequest).contentType(ContentType.JSON).
                when().
                put(API_BASE_URL + "/v1/orders/{orderNumber}").
                then().
                contentType(ContentType.JSON).
                body("status", equalTo(1)).
                extract().
                body().as(OrderResponse.class).getData();
    }

    private static Order createOrder(OrderTestData orderTestData) {
        Order submittedOrder = submitOrder(orderTestData.getOrderRequest());

        OrderUpdateRequest orderUpdateRequest = new OrderUpdateRequest();
        switch (orderTestData.getStatus()) {
            case Order.APPROVED_STATUS: {
                orderUpdateRequest.setStatus(Order.APPROVED_STATUS);
                break;
            }
            case Order.DECLINED_STATUS: {
                orderUpdateRequest.setStatus(Order.DECLINED_STATUS);
                break;
            }
            default: {
                orderUpdateRequest.setStatus(Order.SUBMITTED_STATUS);
            }
        }
        if (StringUtils.isNotEmpty(orderTestData.getComments())) {
            orderUpdateRequest.setComments(orderTestData.getComments());
        }
        if (BooleanUtils.isTrue(orderTestData.getReviewedByAPV())) {
            orderUpdateRequest.setReviewedByAPV(true);
        }

        return updateOrder(submittedOrder.getOrderNumber(), orderUpdateRequest);
    }

    private static BufferedReader getOrderTestDataReader() throws IOException {
        final String orderTestDataResource = "order-test-data.json";
        InputStream input = AppiumConfig.getResourceInputStream(orderTestDataResource);
        if (input == null) {
            throw new IOException("Fail to load order test data.");
        }
        return new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
    }

    private static String getApiBaseUrl() {
        String pdaServerUrl = AppiumConfig.pdaServerUrl();
        if (StringUtils.startsWith(pdaServerUrl, ANDROID_LOCALHOST_URL_PREFIX)) {
            return StringUtils.replaceOnce(pdaServerUrl,
                    ANDROID_LOCALHOST_URL_PREFIX, LOCALHOST_URL_PREFIX);
        }
        return pdaServerUrl;
    }
}
