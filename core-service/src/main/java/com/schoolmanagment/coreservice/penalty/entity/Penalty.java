package com.schoolmanagment.coreservice.penalty.entity;

import com.schoolmanagment.coreservice.auditable.SchoolAuditable;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tbl_penalties")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Penalty extends SchoolAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "penalty_rule_id", nullable = false)
    private PenaltyRule penaltyRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Column(name = "occurrence_count_at_trigger", nullable = false)
    private Integer occurrenceCountAtTrigger;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
