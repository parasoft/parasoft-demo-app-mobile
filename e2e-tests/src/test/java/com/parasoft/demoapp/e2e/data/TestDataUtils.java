package com.parasoft.demoapp.e2e.data;

import static com.parasoft.demoapp.e2e.common.TestUtils.waitForSeconds;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Random;
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

    private static final String STATUS_FIELD = "status";
    private static final int OK_STATUS = 1;

    private static final String ORDER_TEST_DATA_RESOURCE_LOCATION = "order-test-data.json";

    public static void resetDatabase() {
        given().
                auth().preemptive().basic(PURCHASER_USERNAME, PURCHASER_PASSWORD).
                when().
                put(API_BASE_URL + "/v1/demoAdmin/databaseReset").
                then().
                contentType(ContentType.JSON).
                body(STATUS_FIELD, equalTo(OK_STATUS));
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

    public static List<Order> createTestDataOrdersWithRandomPurchaser() throws IOException {
        List<Order> orders = new ArrayList<>();
        OrderTestData[] orderTestDataArray = new GsonBuilder().create().
                fromJson(getOrderTestDataReader(), OrderTestData[].class);
        Arrays.stream(orderTestDataArray).forEach(orderTestData -> {
            String purchaserUsername = getRandomPurchaserUsername();
            addItemsToCartWithGivenPurchaser(orderTestData.getOrderItems(), purchaserUsername);
            orders.add(createOrderWithGivenPurchaser(orderTestData, purchaserUsername));
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
                body(STATUS_FIELD, equalTo(OK_STATUS)).
                extract().
                body().as(OrdersResponse.class).getData().getContent();
    }

    public static String getLocalizedValue(String key) {
        return given().
                auth().preemptive().basic(APPROVER_USERNAME, APPROVER_PASSWORD).
                pathParam("key", key).
                when().
                get(API_BASE_URL + "/v1/localize/EN/{key}").
                then().
                contentType(ContentType.JSON).
                body(STATUS_FIELD, equalTo(OK_STATUS)).
                extract().path("data");
    }

    public static List<Order> getNewOrders() {
        return getAllOrders().stream().filter(Order::isNewOrder).collect(Collectors.toList());
    }

    public static List<Order> getOpenOrders() {
        return getAllOrders().stream().filter(Order::isOpenOrder).collect(Collectors.toList());
    }

    private static void addItemsToCart(Map<Long,Integer> itemIdQtyMap) {
        itemIdQtyMap.forEach(TestDataUtils::addItemToCart);
    }

    private static void addItemsToCart(List<OrderItem> orderItems) {
        orderItems.forEach(orderItem -> {
            addItemToCart(orderItem.getItemId(), orderItem.getQuantity());
        });
    }

    private static void addItemsToCartWithGivenPurchaser(List<OrderItem> orderItems,
                                                         String purchaserUsername) {
        orderItems.forEach(orderItem -> {
            addItemToCartWithGivenPurchaser(orderItem.getItemId(), orderItem.getQuantity(),
                    purchaserUsername);
        });
    }

    private static void addItemToCart(Long itemId, Integer itemQty) {
        addItemToCartWithGivenPurchaser(itemId, itemQty, PURCHASER_USERNAME);
    }

    private static void addItemToCartWithGivenPurchaser(Long itemId, Integer itemQty,
                                                   String purchaserUsername) {
        given().
                auth().preemptive().basic(purchaserUsername, PURCHASER_PASSWORD).
                body("{\"itemId\": " + itemId + ", \"itemQty\": " + itemQty + "}").
                contentType(ContentType.JSON).
                when().
                post(API_BASE_URL + "/v1/cartItems").
                then().
                contentType(ContentType.JSON).
                body(STATUS_FIELD, equalTo(OK_STATUS));
    }

    private static Order submitOrder(OrderRequest orderRequest) {
        return submitOrderWithGivenPurchaser(orderRequest, PURCHASER_USERNAME);
    }

    private static Order submitOrderWithGivenPurchaser(OrderRequest orderRequest,
                                                       String purchaserUsername) {
        return given().
                auth().preemptive().basic(purchaserUsername, PURCHASER_PASSWORD).
                body(orderRequest).contentType(ContentType.JSON).
                when().
                post(API_BASE_URL + "/v1/orders").
                then().
                contentType(ContentType.JSON).
                body(STATUS_FIELD, equalTo(OK_STATUS)).
                extract().
                body().as(OrderResponse.class).getData();
    }

    private static Order getOrder(String orderNumber) {
        return given().
                auth().preemptive().basic(APPROVER_USERNAME, APPROVER_PASSWORD).
                pathParam("orderNumber", orderNumber).
                when().
                get(API_BASE_URL + "/v1/orders/{orderNumber}").
                then().
                contentType(ContentType.JSON).
                body(STATUS_FIELD, equalTo(OK_STATUS)).
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
                body(STATUS_FIELD, equalTo(OK_STATUS)).
                extract().
                body().as(OrderResponse.class).getData();
    }

    private static Order createOrder(OrderTestData orderTestData) {
        Order submittedOrder = submitOrder(orderTestData.getOrderRequest());
        return updateOrderBasedOnTestData(orderTestData, submittedOrder);
    }

    private static Order createOrderWithGivenPurchaser(OrderTestData orderTestData,
                                                       String purchaserUsername) {
        Order submittedOrder =
                submitOrderWithGivenPurchaser(orderTestData.getOrderRequest(), purchaserUsername);
        return updateOrderBasedOnTestData(orderTestData, submittedOrder);
    }

    private static Order updateOrderBasedOnTestData(OrderTestData orderTestData,
                                                    Order submittedOrder) {
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
                orderUpdateRequest.setStatus(Order.PROCESSED_STATUS);
            }
        }
        if (StringUtils.isNotEmpty(orderTestData.getComments())) {
            orderUpdateRequest.setComments(orderTestData.getComments());
        }
        if (BooleanUtils.isTrue(orderTestData.getReviewedByAPV())) {
            orderUpdateRequest.setReviewedByAPV(true);
        }

        do {
            waitForSeconds(2);
        } while (!getOrder(submittedOrder.getOrderNumber()).isOpenOrder());

        return updateOrder(submittedOrder.getOrderNumber(), orderUpdateRequest);
    }

    private static BufferedReader getOrderTestDataReader() throws IOException {
        InputStream input = AppiumConfig.getResourceInputStream(ORDER_TEST_DATA_RESOURCE_LOCATION);
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

    private static String getRandomPurchaserUsername() {
        OptionalInt randomNumberOpt = new Random().ints(1, 51)
                .findFirst();
        if (randomNumberOpt.isPresent()) {
            int randomNumber = randomNumberOpt.getAsInt();
            if (randomNumber == 1) {
                return PURCHASER_USERNAME;
            } else {
                return PURCHASER_USERNAME + randomNumber;
            }
        } else {
            return PURCHASER_USERNAME;
        }
    }
}
