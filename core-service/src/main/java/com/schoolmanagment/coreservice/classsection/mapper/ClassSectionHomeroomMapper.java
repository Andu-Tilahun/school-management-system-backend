package com.schoolmanagment.coreservice.classsection.mapper;

import com.schoolmanagment.coreservice.classsection.dto.ClassSectionHomeroomDto;
import com.schoolmanagment.coreservice.classsection.entity.ClassSectionHomeroom;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClassSectionHomeroomMapper {

    public ClassSectionHomeroomDto toDto(ClassSectionHomeroom homeroom) {
        return ClassSectionHomeroomDto.fromEntity(homeroom);
    }

    public List<ClassSectionHomeroomDto> toDtoList(List<ClassSectionHomeroom> homerooms) {
        return homerooms.stream()
                .map(this::toDto)
                .toList();
    }
}
