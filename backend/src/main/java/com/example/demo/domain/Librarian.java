package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "librarian")
public class Librarian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long librarianId;

    private String fullName;
    private String contactInfo;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
