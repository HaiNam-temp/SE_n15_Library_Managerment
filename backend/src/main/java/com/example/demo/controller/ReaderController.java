package com.example.demo.controller;

import com.example.demo.dto.ReaderDto;
import com.example.demo.service.ReaderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/readers")
public class ReaderController extends BaseController {

    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @GetMapping
    public List<ReaderDto> findAll() {
        return readerService.findAll();
    }

    @GetMapping("/{id}")
    public ReaderDto findById(@PathVariable Long id) {
        return readerService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReaderDto create(@RequestBody ReaderDto readerDto) {
        return readerService.create(readerDto);
    }

    @PutMapping("/{id}")
    public ReaderDto update(@PathVariable Long id, @RequestBody ReaderDto readerDto) {
        return readerService.update(id, readerDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        readerService.delete(id);
    }
}
