package com.parasoft.demoapp.e2e.data;

import lombok.Data;

@Data
public abstract class ApiResponse {
    protected Integer status;
    protected String message;
}
