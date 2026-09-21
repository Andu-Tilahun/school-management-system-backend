package com.schoolmanagment.coreservice.offencerecord.service;

import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordDto;
import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordFilterRequest;
import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface OffenceRecordService {

    Page<OffenceRecordDto> list(int page, int size);

    Page<OffenceRecordDto> filter(OffenceRecordFilterRequest request);

    OffenceRecordDto getById(UUID id);

    OffenceRecordDto create(OffenceRecordRequest request);

    OffenceRecordDto update(UUID id, OffenceRecordRequest request);

    void delete(UUID id);

    OffenceRecordDto deactivate(UUID offenceRecordId);
}
