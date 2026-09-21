package com.schoolmanagment.coreservice.penalty.entity;

import com.schoolmanagment.coreservice.auditable.SchoolAuditable;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "tbl_penalty_rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PenaltyRule extends SchoolAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "penalty_trigger", nullable = false, length = 40)
    private PenaltyTrigger penaltyTrigger;

    @Enumerated(EnumType.STRING)
    @Column(name = "penalty_type", nullable = false, length = 40)
    private PenaltyType penaltyType;

    @Column(name = "occurrence_number", nullable = false)
    private Integer occurrenceNumber;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
