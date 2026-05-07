package com.example.demo.controller;

import com.example.demo.dto.BorrowStatusDto;
import com.example.demo.dto.CreateLoanRequest;
import com.example.demo.dto.LoanResponse;
import com.example.demo.dto.ReturnBookRequest;
import com.example.demo.dto.ReturnBookResponse;
import com.example.demo.service.BorrowReturnService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/borrow-return")
public class BorrowReturnController extends BaseController {

    private final BorrowReturnService borrowReturnService;

    public BorrowReturnController(BorrowReturnService borrowReturnService) {
        this.borrowReturnService = borrowReturnService;
    }

    @GetMapping("/readers/{readerId}/status")
    public BorrowStatusDto checkBorrowStatus(@PathVariable Long readerId) {
        return borrowReturnService.checkBorrowStatus(readerId);
    }

    @PostMapping("/loans")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse createLoan(@RequestBody CreateLoanRequest request) {
        return borrowReturnService.createLoan(request);
    }

    @PostMapping("/returns")
    public ReturnBookResponse returnBook(@RequestBody ReturnBookRequest request) {
        return borrowReturnService.returnBook(request);
    }

    @GetMapping("/loans")
    public List<LoanResponse> findAllLoans() {
        return borrowReturnService.findAllLoans();
    }
}
