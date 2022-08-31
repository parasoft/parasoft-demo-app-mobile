package com.parasoft.demoapp.retrofitConfig.response;

import java.util.List;

import lombok.Data;

@Data
public class PageInfoResponse<T>  {
    private Long totalElements;
    private int totalPages;
    private int size;
    private int number;
    private int numberOfElements;
    private String sort;
    private List<T> content;
}