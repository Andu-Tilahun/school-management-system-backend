package com.schoolmanagment.coreservice.offencerecord.mapper;

import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordDto;
import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordRequest;
import com.schoolmanagment.coreservice.offencerecord.entity.OffenceRecord;
import com.schoolmanagment.coreservice.offencerecord.enums.OffenceRecordStatus;
import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import org.springframework.stereotype.Component;

@Component
public class OffenceRecordMapper {

    public OffenceRecordDto toDto(OffenceRecord offenceRecord) {
        return OffenceRecordDto.fromEntity(offenceRecord);
    }

    public OffenceRecord toEntity(OffenceRecordRequest request, EnrollmentTerm enrollmentTerm) {
        return OffenceRecord.builder()
                .enrollmentTerm(enrollmentTerm)
                .penaltyTrigger(request.getPenaltyTrigger())
                .dateOccurred(request.getDateOccurred())
                .status(request.getStatus() != null ? request.getStatus() : OffenceRecordStatus.CONFIRMED)
                .remark(request.getRemark())
                .active(true)
                .build();
    }

    public void updateEntity(OffenceRecord offenceRecord, OffenceRecordRequest request, EnrollmentTerm enrollmentTerm) {
        offenceRecord.setEnrollmentTerm(enrollmentTerm);
        offenceRecord.setPenaltyTrigger(request.getPenaltyTrigger());
        offenceRecord.setDateOccurred(request.getDateOccurred());
        if (request.getStatus() != null) {
            offenceRecord.setStatus(request.getStatus());
        }
        offenceRecord.setRemark(request.getRemark());
    }
}
