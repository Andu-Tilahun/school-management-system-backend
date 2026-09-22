package com.schoolmanagment.coreservice.attendance.mapper;

import com.schoolmanagment.coreservice.attendance.dto.AttendanceDto;
import com.schoolmanagment.coreservice.attendance.dto.AttendanceRequest;
import com.schoolmanagment.coreservice.attendance.entity.Attendance;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public AttendanceDto toDto(Attendance attendance) {
        return AttendanceDto.fromEntity(attendance);
    }

    public Attendance toEntity(AttendanceRequest request, Enrollment enrollment) {
        return Attendance.builder()
                .enrollment(enrollment)
                .penaltyTrigger(request.getPenaltyTrigger())
                .dateOccurred(request.getDateOccurred())
                .status(request.getStatus())
                .remark(request.getRemark())
                .active(true)
                .build();
    }

    public void updateEntity(Attendance attendance, AttendanceRequest request, Enrollment enrollment) {
        attendance.setEnrollment(enrollment);
        attendance.setPenaltyTrigger(request.getPenaltyTrigger());
        attendance.setDateOccurred(request.getDateOccurred());
        attendance.setStatus(request.getStatus());
        attendance.setRemark(request.getRemark());
    }
}
