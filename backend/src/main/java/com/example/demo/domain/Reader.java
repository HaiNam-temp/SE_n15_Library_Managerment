package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "reader")
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String email;
    private LocalDate cardCreatedDate;
    private LocalDate cardExpiredDate;
    private String cardStatus;
    private Integer currentBorrowedCount;
    private String phone;
    private String studentCodeOrCitizenId;

    @OneToOne
    @JoinColumn(name = "account_id")
    private com.example.demo.domain.Account account;

    @ManyToOne
    @JoinColumn(name = "borrowing_rule_id")
    private com.example.demo.domain.BorrowingRule borrowingRule;

    @OneToMany(mappedBy = "reader")
    private List<BorrowReceipt> borrowReceipts = new ArrayList<>();
}

