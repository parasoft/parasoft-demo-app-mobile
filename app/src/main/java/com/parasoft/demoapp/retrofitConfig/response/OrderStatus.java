package com.parasoft.demoapp.retrofitConfig.response;

public enum OrderStatus {

    PROCESSED("Processed"), APPROVED("Approved"), DECLINED("Declined");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
