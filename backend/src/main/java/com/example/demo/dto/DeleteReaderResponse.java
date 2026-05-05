package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteReaderResponse {
    private Long readerId;
    private String message;
}
