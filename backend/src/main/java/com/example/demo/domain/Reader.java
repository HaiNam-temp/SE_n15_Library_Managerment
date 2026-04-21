package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    private Integer borrowingBookCount;

    @OneToMany(mappedBy = "reader")
    private List<BorrowReceipt> borrowReceipts = new ArrayList<>();
}