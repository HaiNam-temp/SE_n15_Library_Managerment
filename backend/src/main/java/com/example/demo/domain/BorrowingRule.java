package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "borrowing_rule")
public class BorrowingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    private Integer maxBooks;
    private Integer maxBorrowDays;
    private BigDecimal finePerDay;
}
