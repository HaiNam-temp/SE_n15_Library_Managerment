package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> data;
    private int pageIndex;
    private int pageSize;
    private long totalCount;
    private int totalPages;
}
