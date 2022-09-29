package com.parasoft.demoapp.FakeApiResponse;

import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.request.OrderStatusRequest;
import com.parasoft.demoapp.retrofitConfig.response.OrderListResponse;
import com.parasoft.demoapp.retrofitConfig.response.OrderResponse;
import com.parasoft.demoapp.retrofitConfig.response.OrderStatus;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersRelativeApis {

    public static ApiInterface return200Response_allRequest() {
        return new AllApisWith200Response();
    }

    private static class AllApisWith200Response extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<OrderListResponse>> getOrderList() {
            return new CallInterfaceImplForTest<ResultResponse<OrderListResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderListResponse>> callback) {
                    ResultResponse<OrderListResponse> resultResponse = new ResultResponse<>();
                    resultResponse.setData(FakeDate.orderListResponse);
                    resultResponse.setStatus(1);
                    resultResponse.setMessage("Response orders successfully.");

                    Response<ResultResponse<OrderListResponse>> response = Response.success(200, resultResponse);
                    callback.onResponse(null, response);
                }
            };
        }

        @Override
        public Call<ResultResponse<OrderResponse>> getOrderDetails(String orderNumber) {
            return new CallInterfaceImplForTest<ResultResponse<OrderResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderResponse>> callback) {
                    ResultResponse<OrderResponse> resultResponse = new ResultResponse<>();
                    OrderResponse order = FakeDate.getOrderByOrderNumber(orderNumber);
                    resultResponse.setData(order);
                    Response<ResultResponse<OrderResponse>> response;
                    if(order != null) {
                        resultResponse.setStatus(1);
                        resultResponse.setMessage("Response successfully with one order.");
                        response = Response.success(200, resultResponse);
                    } else {
                        response = Response.error(404,
                                ResponseBody.create(null, "There is no order corresponding to " + orderNumber + "."));
                    }

                    callback.onResponse(null, response);
                }
            };
        }

        @Override
        public Call<ResultResponse<OrderResponse>> updateOrderDetails(String orderNumber, OrderStatusRequest orderStatusRequest) {
            return new CallInterfaceImplForTest<ResultResponse<OrderResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderResponse>> callback) {
                    ResultResponse<OrderResponse> resultResponse = new ResultResponse<>();
                    OrderResponse order = FakeDate.getOrderByOrderNumber(orderNumber);

                    Response<ResultResponse<OrderResponse>> response;
                    if(order == null) {
                        response = Response.error(404,
                                ResponseBody.create(null, "There is no order corresponding to " + orderNumber + "."));
                    } else {
                        if(orderStatusRequest.isReviewedByAPV()) {
                            order.setReviewedByAPV(orderStatusRequest.isReviewedByAPV());
                        }
                        order.setStatus(orderStatusRequest.getStatus());
                        order.setComments(orderStatusRequest.getComments());
                        resultResponse.setData(order);
                        resultResponse.setStatus(1);
                        resultResponse.setMessage("Order updated successfully.");
                        response = Response.success(200, resultResponse);
                    }

                    callback.onResponse(null, response);
                }
            };
        }

        @Override
        public Call<ResponseBody> getImage(String imagePath) {
            return new CallInterfaceImplForTest<ResponseBody>() {
                @Override
                public void enqueue(Callback<ResponseBody> callback) {
                    Response<ResponseBody> response = Response.success(ResponseBody.create(MediaType.parse("image/png"), ""));
                    callback.onResponse(null, response);
                }
            };
        }

        @Override
        public Call<ResultResponse<String>> localizedValue(String lang, String key) {
            return new CallInterfaceImplForTest<ResultResponse<String>>() {
                @Override
                public void enqueue(Callback<ResultResponse<String>> callback) {
                    ResultResponse<String> resultResponse = new ResultResponse<>();
                    resultResponse.setData("Localized value");
                    resultResponse.setStatus(1);
                    resultResponse.setMessage("Response successfully.");

                    Response<ResultResponse<String>> response = Response.success(200, resultResponse);
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class localizedValue_with404Response extends AllApisWith200Response {
        @Override
        public Call<ResultResponse<String>> localizedValue(String lang, String key) {
            return new CallInterfaceImplForTest<ResultResponse<String>>() {
                @Override
                public void enqueue(Callback<ResultResponse<String>> callback) {
                    Response<ResultResponse<String>> response = Response.error(404,
                            ResponseBody.create(null, "Not found the value of key."));
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class getImage_with404Response extends AllApisWith200Response {

        @Override
        public Call<ResponseBody> getImage(String imagePath) {
            return new CallInterfaceImplForTest<ResponseBody>() {
                @Override
                public void enqueue(Callback<ResponseBody> callback) {
                    Response<ResponseBody> response = Response.error(404,
                            ResponseBody.create(null, "Not found the image."));
                    callback.onResponse(null, response);
                }
            };
        }
    }

    public static class FakeDate {
        private static final OrderListResponse orderListResponse = new OrderListResponse();
        private static final List<OrderResponse> orderList = new ArrayList<>();

        static {
            orderListResponse.setContent(orderList);
        }

        public static void resetOrderListResponse() {
            orderList.clear();
        }

        public static OrderResponse addAnOrder(String orderNumber, OrderStatus status, int orderItemsNumber, boolean reviewed) {
            OrderResponse order = new OrderResponse();
            order.setOrderNumber(orderNumber);
            order.setLocation(orderNumber + " - 29.90° E, 54.41° N");
            order.setReceiverId(orderNumber + " - receiver name");
            order.setEventId(orderNumber + " - receiver number");
            order.setEventNumber(orderNumber + " - purchaser order");
            order.setStatus(status);
            order.setReviewedByAPV(reviewed);
            order.setOrderItems(prepareOrderItems(orderItemsNumber));
            order.setRequestedBy("purchaser");
            order.setSubmissionDate("2022-09-26T08:00:00.000+00:00");
            orderList.add(order);
            return order;
        }

        public static OrderResponse getOrderByOrderNumber(String orderNumber) {
            for(OrderResponse order : FakeDate.orderList) {
                if(order.getOrderNumber().equals(orderNumber)) {
                    return order;
                }
            }
            return null;
        }

        private static List<OrderResponse.OrderItemInfo> prepareOrderItems(int number) {
            List<OrderResponse.OrderItemInfo> orderItems = new ArrayList<>();
            for(int i = 0; i < number; i++) {
                OrderResponse.OrderItemInfo orderItemInfo =
                        new OrderResponse.OrderItemInfo("Item name" + 1,  "Item description" + 1,
                                "/image/path",  1);
                orderItems.add(orderItemInfo);
            }

            return orderItems;
        }
    }
}
