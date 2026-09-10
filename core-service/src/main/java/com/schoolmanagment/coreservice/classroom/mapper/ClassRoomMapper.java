package com.schoolmanagment.coreservice.classroom.mapper;

import com.schoolmanagment.coreservice.classroom.dto.ClassRoomDto;
import com.schoolmanagment.coreservice.classroom.dto.ClassRoomRequest;
import com.schoolmanagment.coreservice.classroom.entity.ClassRoom;
import org.springframework.stereotype.Component;

@Component
public class ClassRoomMapper {

    public ClassRoomDto toDto(ClassRoom classRoom) {
        return ClassRoomDto.builder()
                .id(classRoom.getId())
                .schoolId(classRoom.getSchoolId())
                .roomNumber(classRoom.getRoomNumber())
                .roomSize(classRoom.getRoomSize())
                .createdAt(classRoom.getCreatedAt())
                .build();
    }

    public ClassRoom toEntity(ClassRoomRequest request) {
        return ClassRoom.builder()
                .roomNumber(request.getRoomNumber())
                .roomSize(request.getRoomSize())
                .active(true)
                .build();
    }

    public void updateEntity(ClassRoom classRoom, ClassRoomRequest request) {
        classRoom.setRoomNumber(request.getRoomNumber());
        classRoom.setRoomSize(request.getRoomSize());
    }
}
