package de.kirillmrotzek.legalflow.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ContractRenewalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Contract contract;

    @Column(nullable = false)
    private LocalDate previousEndDate;

    @Column(nullable = false)
    private LocalDate newEndDate;

    @Column(nullable = false)
    private LocalDateTime renewedAt;

    @Column(nullable = false)
    private String renewedBy;
}
