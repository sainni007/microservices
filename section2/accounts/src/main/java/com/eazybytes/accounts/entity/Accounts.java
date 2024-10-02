package com.eazybytes.accounts.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Accounts extends BaseEntity {

    private Long customerId;
    @Id
    private Long accountNumber;
    @Column
    private String accountType;
    @Column
    private String branchAddress;
}
