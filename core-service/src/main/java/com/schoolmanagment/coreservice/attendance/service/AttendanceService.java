package com.schoolmanagment.coreservice.attendance.service;

import com.schoolmanagment.coreservice.attendance.dto.AttendanceDto;
import com.schoolmanagment.coreservice.attendance.dto.AttendanceFilterRequest;
import com.schoolmanagment.coreservice.attendance.dto.AttendanceRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface AttendanceService {

    Page<AttendanceDto> list(int page, int size);

    Page<AttendanceDto> filter(AttendanceFilterRequest request);

    AttendanceDto getById(UUID id);

    AttendanceDto create(AttendanceRequest request);

    AttendanceDto update(UUID id, AttendanceRequest request);

    void delete(UUID id);
}
