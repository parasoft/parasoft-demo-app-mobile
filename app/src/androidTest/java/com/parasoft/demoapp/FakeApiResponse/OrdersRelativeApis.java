package com.parasoft.demoapp.FakeApiResponse;

import android.graphics.Bitmap;

import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.request.OrderStatusRequest;
import com.parasoft.demoapp.retrofitConfig.response.OrderListResponse;
import com.parasoft.demoapp.retrofitConfig.response.OrderResponse;
import com.parasoft.demoapp.retrofitConfig.response.OrderStatus;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersRelativeApis {

    public static ApiInterface allRequests_with200Response() {
        return new AllApisWith200Response();
    }

    public static ApiInterface getOrderDetails_with401Response() {
        return new GetOrderDetails_with401Response();
    }

    public static ApiInterface getOrderDetails_with404Response() {
        return new GetOrderDetails_with404Response();
    }

    public static ApiInterface getOrderDetails_with500Response() {
        return new GetOrderDetails_with500Response();
    }

    public static ApiInterface getOrderDetails_onFailure() {
        return new GetOrderDetails_onFailure();
    }

    public static ApiInterface updateOrderDetails_with400Response() {
        return new UpdateOrderDetails_with400Response();
    }

    public static ApiInterface updateOrderDetails_with401Response() {
        return new UpdateOrderDetails_with401Response();
    }

    public static ApiInterface updateOrderDetails_with403Response() {
        return new UpdateOrderDetails_with403Response();
    }

    public static ApiInterface updateOrderDetails_with404Response() {
        return new UpdateOrderDetails_with404Response();
    }

    public static ApiInterface updateOrderDetails_with500Response() {
        return new UpdateOrderDetails_with500Response();
    }

    public static ApiInterface updateOrderDetails_onFailure() {
        return new UpdateOrderDetails_onFailure();
    }

    public static ApiInterface getImage_with404Response() {
        return new GetImage_with404Response();
    }

    public static ApiInterface getImage_onFailure() {
        return new GetImage_onFailure();
    }

    public static ApiInterface localizedValue_with404Response() {
        return new LocalizedValue_with404Response();
    }

    public static ApiInterface localizedValue_onFailure() {
        return new LocalizedValue_onFailure();
    }

    public static ApiInterface getOrderList_with400Response() {
        return new GetOrderList_with400Response();
    }

    public static ApiInterface getOrderList_with401Response() {
        return new GetOrderList_with401Response();
    }

    public static ApiInterface getOrderList_with500Response() {
        return new GetOrderList_with500Response();
    }

    public static ApiInterface getOrderList_onFailure() {
        return new GetOrderList_onFailure();
    }

    private static class AllApisWith200Response extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<OrderListResponse>> getOrderList() {
            return new CallInterfaceImplForTest<ResultResponse<OrderListResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderListResponse>> callback) {
                    ResultResponse<OrderListResponse> resultResponse = new ResultResponse<>();
                    resultResponse.setData(FakeData.orderListResponse);
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
                    OrderResponse order = FakeData.getOrderByOrderNumber(orderNumber);
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
                    OrderResponse order = FakeData.getOrderByOrderNumber(orderNumber);

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
                    Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
                    byte[] bitmapData = byteArrayOutputStream.toByteArray();
                    Response<ResponseBody> response = Response.success(ResponseBody.create(MediaType.parse("image/png"), bitmapData));
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
                    resultResponse.setData("Localized location value");
                    resultResponse.setStatus(1);
                    resultResponse.setMessage("Response successfully.");

                    Response<ResultResponse<String>> response = Response.success(200, resultResponse);
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class UpdateOrderDetails_with401Response extends AllApisWith200Response {
        @Override
        public Call<ResultResponse<OrderResponse>> updateOrderDetails(String orderNumber, OrderStatusRequest orderStatusRequest) {
            return new CallInterfaceImplForTest<ResultResponse<OrderResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderResponse>> callback) {
                    Response<ResultResponse<OrderResponse>> response = Response.error(401,
                            ResponseBody.create(null, "Not authorized to update the order."));

                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class UpdateOrderDetails_with400Response extends AllApisWith200Response {
        @Override
        public Call<ResultResponse<OrderResponse>> updateOrderDetails(String orderNumber, OrderStatusRequest orderStatusRequest) {
            return new CallInterfaceImplForTest<ResultResponse<OrderResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderResponse>> callback) {
                    Response<ResultResponse<OrderResponse>> response = Response.error(400,
                            ResponseBody.create(null, "Bad request."));

                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class UpdateOrderDetails_with403Response extends AllApisWith200Response {
        @Override
        public Call<ResultResponse<OrderResponse>> updateOrderDetails(String orderNumber, OrderStatusRequest orderStatusRequest) {
            return new CallInterfaceImplForTest<ResultResponse<OrderResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderResponse>> callback) {
                    Response<ResultResponse<OrderResponse>> response = Response.error(403,
                            ResponseBody.create(null, "Not permission to update the order."));

                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class UpdateOrderDetails_with404Response extends AllApisWith200Response {
        @Override
        public Call<ResultResponse<OrderResponse>> updateOrderDetails(String orderNumber, OrderStatusRequest orderStatusRequest) {
            return new CallInterfaceImplForTest<ResultResponse<OrderResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderResponse>> callback) {
                    Response<ResultResponse<OrderResponse>> response = Response.error(404,
                            ResponseBody.create(null, "There is no order corresponding to " + orderNumber + "."));

                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class UpdateOrderDetails_with500Response extends AllApisWith200Response {
        @Override
        public Call<ResultResponse<OrderResponse>> updateOrderDetails(String orderNumber, OrderStatusRequest orderStatusRequest) {
            return new CallInterfaceImplForTest<ResultResponse<OrderResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderResponse>> callback) {
                    Response<ResultResponse<OrderResponse>> response = Response.error(500,
                            ResponseBody.create(null, "Internal error."));

                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class UpdateOrderDetails_onFailure extends AllApisWith200Response {
        @Override
        public Call<ResultResponse<OrderResponse>> updateOrderDetails(String orderNumber, OrderStatusRequest orderStatusRequest) {
            return new CallInterfaceImplForTest<ResultResponse<OrderResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderResponse>> callback) {
                    callback.onFailure(null, new RuntimeException("On failure"));
                }
            };
        }
    }

    private static class GetOrderDetails_with401Response extends AllApisWith200Response {
        @Override
        public Call<ResultResponse<OrderResponse>> getOrderDetails(String orderNumber) {
            return new CallInterfaceImplForTest<ResultResponse<OrderResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderResponse>> callback) {
                    Response<ResultResponse<OrderResponse>> response = Response.error(401,
                            ResponseBody.create(null, "Not authorized to get the order."));

                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class GetOrderDetails_with404Response extends AllApisWith200Response {
        @Override
        public Call<ResultResponse<OrderResponse>> getOrderDetails(String orderNumber) {
            return new CallInterfaceImplForTest<ResultResponse<OrderResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderResponse>> callback) {
                    Response<ResultResponse<OrderResponse>> response = Response.error(404,
                            ResponseBody.create(null, "There is no order corresponding to " + orderNumber + "."));

                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class GetOrderDetails_with500Response extends AllApisWith200Response {
        @Override
        public Call<ResultResponse<OrderResponse>> getOrderDetails(String orderNumber) {
            return new CallInterfaceImplForTest<ResultResponse<OrderResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderResponse>> callback) {
                    Response<ResultResponse<OrderResponse>> response = Response.error(500,
                            ResponseBody.create(null, "Internal error."));

                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class GetOrderDetails_onFailure extends AllApisWith200Response {
        @Override
        public Call<ResultResponse<OrderResponse>> getOrderDetails(String orderNumber) {
            return new CallInterfaceImplForTest<ResultResponse<OrderResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderResponse>> callback) {
                    callback.onFailure(null, new RuntimeException("On failure"));
                }
            };
        }
    }

    private static class LocalizedValue_with404Response extends AllApisWith200Response {
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

    private static class LocalizedValue_onFailure extends AllApisWith200Response {
        @Override
        public Call<ResultResponse<String>> localizedValue(String lang, String key) {
            return new CallInterfaceImplForTest<ResultResponse<String>>() {
                @Override
                public void enqueue(Callback<ResultResponse<String>> callback) {
                    callback.onFailure(null, new RuntimeException("On failure"));
                }
            };
        }
    }

    private static class GetImage_with404Response extends AllApisWith200Response {

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

    private static class GetImage_onFailure extends AllApisWith200Response {

        @Override
        public Call<ResponseBody> getImage(String imagePath) {
            return new CallInterfaceImplForTest<ResponseBody>() {
                @Override
                public void enqueue(Callback<ResponseBody> callback) {
                    callback.onFailure(null, new RuntimeException("On failure"));
                }
            };
        }
    }

    private static class GetOrderList_with400Response extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<OrderListResponse>> getOrderList() {
            return new CallInterfaceImplForTest<ResultResponse<OrderListResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderListResponse>> callback) {
                    ResponseBody responseBody = ResponseBody.create(null, "Invalid request parameter.");
                    Response<ResultResponse<OrderListResponse>> response = Response.error(400, responseBody);
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class GetOrderList_with401Response extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<OrderListResponse>> getOrderList() {
            return new CallInterfaceImplForTest<ResultResponse<OrderListResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderListResponse>> callback) {
                    ResponseBody responseBody = ResponseBody.create(null, "Not authorized to get all orders.");
                    Response<ResultResponse<OrderListResponse>> response = Response.error(401, responseBody);
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class GetOrderList_with500Response extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<OrderListResponse>> getOrderList() {
            return new CallInterfaceImplForTest<ResultResponse<OrderListResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderListResponse>> callback) {
                    ResponseBody responseBody = ResponseBody.create(null, "Error loading all orders.");
                    Response<ResultResponse<OrderListResponse>> response = Response.error(500, responseBody);
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class GetOrderList_onFailure extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<OrderListResponse>> getOrderList() {
            return new CallInterfaceImplForTest<ResultResponse<OrderListResponse>>() {
                @Override
                public void enqueue(Callback<ResultResponse<OrderListResponse>> callback) {
                    callback.onFailure(null, new RuntimeException("On failure"));
                }
            };
        }
    }

    public static class FakeData {
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
            order.setRegion("Location");
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

        public static void addMoreOrders(int orderQuantity) {
            String orderNumber;
            OrderStatus orderStatus = null;
            for (int i = orderQuantity; i >= 1; i--) {
                orderNumber = "23-456-0" + (i >= 10 ? "" : "0") + i;
                switch (i % 3) {
                    case 0:
                        orderStatus = OrderStatus.PROCESSED;
                        break;
                    case 1:
                        orderStatus = OrderStatus.APPROVED;
                        break;
                    case 2:
                        orderStatus = OrderStatus.DECLINED;
                        break;
                    default:
                }
                addAnOrder(orderNumber, orderStatus, 1, false);
            }
        }

        public static OrderResponse getOrderByOrderNumber(String orderNumber) {
            for(OrderResponse order : FakeData.orderList) {
                if(order.getOrderNumber().equals(orderNumber)) {
                    return order;
                }
            }
            return null;
        }

        private static List<OrderResponse.OrderItemInfo> prepareOrderItems(int number) {
            List<OrderResponse.OrderItemInfo> orderItems = new ArrayList<>();
            for(int i = 1; i <= number; i++) {
                OrderResponse.OrderItemInfo orderItemInfo =
                        new OrderResponse.OrderItemInfo("Item name " + i,  "Item description " + i,
                                "/image/path",  i);
                orderItems.add(orderItemInfo);
            }

            return orderItems;
        }
    }
}
