package de.kirillmrotzek.legalflow.model;

import de.kirillmrotzek.legalflow.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Contract contract;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private LocalDateTime assessedAt;

    @Column(nullable = false)
    private String assessedBy;
}
