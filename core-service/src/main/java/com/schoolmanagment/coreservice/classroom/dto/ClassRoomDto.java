package com.schoolmanagment.coreservice.classroom.dto;

import com.schoolmanagment.coreservice.classroom.entity.ClassRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassRoomDto {

    private UUID id;
    private UUID schoolId;
    private String roomNumber;
    private Integer roomSize;
    private LocalDateTime createdAt;

    public static ClassRoomDto fromEntity(ClassRoom classRoom) {
        return ClassRoomDto.builder()
                .id(classRoom.getId())
                .schoolId(classRoom.getSchoolId())
                .roomNumber(classRoom.getRoomNumber())
                .roomSize(classRoom.getRoomSize())
                .createdAt(classRoom.getCreatedAt())
                .build();
    }
}
