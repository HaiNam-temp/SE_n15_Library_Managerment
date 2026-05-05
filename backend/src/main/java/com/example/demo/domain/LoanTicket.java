package com.example.demo.domain;

import com.example.demo.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "loan_ticket")
public class LoanTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    private LocalDate borrowDate;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @ManyToOne
    @JoinColumn(name = "reader_id")
    private Reader reader;

    @OneToMany(mappedBy = "loanTicket")
    private List<LoanDetail> loanDetails = new ArrayList<>();
}
