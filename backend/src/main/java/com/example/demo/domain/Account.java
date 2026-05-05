package com.example.demo.domain;

import com.example.demo.enums.AccountRole;
import com.example.demo.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private AccountRole role;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;
}
