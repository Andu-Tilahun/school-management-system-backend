package com.schoolmanagment.coreservice.offencerecord.mapper;

import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordDto;
import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordRequest;
import com.schoolmanagment.coreservice.offencerecord.entity.OffenceRecord;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import org.springframework.stereotype.Component;

@Component
public class OffenceRecordMapper {

    public OffenceRecordDto toDto(OffenceRecord offenceRecord) {
        return OffenceRecordDto.fromEntity(offenceRecord);
    }

    public OffenceRecord toEntity(OffenceRecordRequest request, Enrollment enrollment) {
        return OffenceRecord.builder()
                .enrollment(enrollment)
                .penaltyTrigger(request.getPenaltyTrigger())
                .dateOccurred(request.getDateOccurred())
                .status(request.getStatus())
                .remark(request.getRemark())
                .active(true)
                .build();
    }

    public void updateEntity(OffenceRecord offenceRecord, OffenceRecordRequest request, Enrollment enrollment) {
        offenceRecord.setEnrollment(enrollment);
        offenceRecord.setPenaltyTrigger(request.getPenaltyTrigger());
        offenceRecord.setDateOccurred(request.getDateOccurred());
        offenceRecord.setStatus(request.getStatus());
        offenceRecord.setRemark(request.getRemark());
    }
}
