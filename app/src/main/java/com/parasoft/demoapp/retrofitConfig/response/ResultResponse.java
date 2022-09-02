package com.parasoft.demoapp.retrofitConfig.response;

import lombok.Data;

@Data
public class ResultResponse<T> {
  private Integer status;
  private String message;
  private T data;
}