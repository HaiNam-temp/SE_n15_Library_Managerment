package com.example.demo.service;

import com.example.demo.dto.ReaderRequest;
import com.example.demo.dto.ReaderResponse;
import com.example.demo.dto.ReaderPageResponse;
import com.example.demo.dto.ReaderDetailResponse;
import com.example.demo.dto.ReaderUpdateRequest;
import com.example.demo.dto.DeleteReaderResponse;

public interface ReaderService {
    ReaderResponse createReader(ReaderRequest request);
    ReaderPageResponse getReaders(int pageIndex, int pageSize);
    ReaderDetailResponse getReaderDetail(Long readerId);
    ReaderResponse updateReader(Long readerId, ReaderUpdateRequest request);
    DeleteReaderResponse deleteReader(Long readerId);
}
