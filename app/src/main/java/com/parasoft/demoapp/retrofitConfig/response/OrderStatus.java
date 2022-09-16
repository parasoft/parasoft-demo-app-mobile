package com.parasoft.demoapp.retrofitConfig.response;

public enum OrderStatus {

    SUBMITTED("Submitted"), APPROVED("Approved"), DECLINED("Declined");

    private String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
