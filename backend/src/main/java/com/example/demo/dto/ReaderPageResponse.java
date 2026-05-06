package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReaderPageResponse {
    private List<ReaderResponse> content;
    private long totalElements;
    private int totalPages;
    private int pageIndex;
    private int pageSize;
}
