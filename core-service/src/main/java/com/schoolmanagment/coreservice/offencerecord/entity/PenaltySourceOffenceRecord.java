package com.schoolmanagment.coreservice.offencerecord.entity;

import com.schoolmanagment.coreservice.attendance.entity.Attendance;
import com.schoolmanagment.coreservice.auditable.SchoolAuditable;
import com.schoolmanagment.coreservice.offencerecord.entity.OffenceRecord;
import com.schoolmanagment.coreservice.penalty.entity.Penalty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tbl_penalty_source_offence_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenaltySourceOffenceRecord extends SchoolAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "penalty_id", nullable = false)
    private Penalty penalty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offence_record_id", nullable = false)
    private OffenceRecord offenceRecord;
}
