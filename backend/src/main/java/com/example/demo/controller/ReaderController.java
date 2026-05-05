package com.example.demo.controller;

import com.example.demo.dto.ReaderRequest;
import com.example.demo.dto.ReaderResponse;
import com.example.demo.dto.ReaderDetailResponse;
import com.example.demo.dto.ReaderUpdateRequest;
import com.example.demo.dto.DeleteReaderResponse;
import com.example.demo.service.ReaderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
@Slf4j
public class ReaderController {

    private final ReaderService readerService;

    @PostMapping
    public ResponseEntity<ReaderResponse> createReader(@Valid @RequestBody ReaderRequest request) {
        log.info("start ReaderController.createReader - studentCodeOrCitizenId={} email={}", request.getStudentCodeOrCitizenId(), request.getEmail());
        ReaderResponse resp = readerService.createReader(request);
        log.info("end ReaderController.createReader - readerId={}", resp.getId());
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<com.example.demo.dto.ReaderPageResponse> getReaders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("start ReaderController.getReaders - page={} size={}", page, size);
        com.example.demo.dto.ReaderPageResponse resp = readerService.getReaders(page, size);
        log.info("end ReaderController.getReaders - returned={} total={}", resp.getContent().size(), resp.getTotalElements());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{readerId}")
    public ResponseEntity<ReaderDetailResponse> getReaderDetail(@PathVariable Long readerId) {
        log.info("start ReaderController.getReaderDetail - readerId={}", readerId);
        ReaderDetailResponse resp = readerService.getReaderDetail(readerId);
        log.info("end ReaderController.getReaderDetail - readerId={}", readerId);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{readerId}")
    public ResponseEntity<ReaderResponse> updateReader(@PathVariable Long readerId, @Valid @RequestBody ReaderUpdateRequest request) {
        log.info("start ReaderController.updateReader - readerId={} email={}", readerId, request.getEmail());
        ReaderResponse resp = readerService.updateReader(readerId, request);
        log.info("end ReaderController.updateReader - readerId={}", readerId);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{readerId}")
    public ResponseEntity<DeleteReaderResponse> deleteReader(@PathVariable Long readerId) {
        log.info("start ReaderController.deleteReader - readerId={}", readerId);
        DeleteReaderResponse resp = readerService.deleteReader(readerId);
        log.info("end ReaderController.deleteReader - readerId={}", readerId);
        return ResponseEntity.ok(resp);
    }
}
