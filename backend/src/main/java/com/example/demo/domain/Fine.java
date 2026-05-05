package com.example.demo.domain;

import com.example.demo.enums.FineStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "fine")
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fineId;

    private BigDecimal amount;
    private String reason;

    @Enumerated(EnumType.STRING)
    private FineStatus status;

    @OneToOne
    @JoinColumn(name = "loan_detail_id")
    private LoanDetail loanDetail;
}
