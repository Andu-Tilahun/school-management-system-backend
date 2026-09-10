package com.schoolmanagment.coreservice.roomsection.dto;

import com.schoolmanagment.coreservice.classroom.entity.ClassRoom;
import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.grade.entity.Grade;
import com.schoolmanagment.coreservice.roomsection.entity.RoomSection;
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
public class RoomSectionDto {

    private UUID id;
    private UUID schoolId;
    private UUID classSectionId;
    private UUID gradeId;
    private String gradeName;
    private UUID roomId;
    private String roomNumber;
    private Integer roomSize;
    private LocalDateTime createdAt;

    public static RoomSectionDto fromEntity(RoomSection roomSection) {
        ClassSection classSection = roomSection.getClassSection();
        Grade grade = classSection != null ? classSection.getGrade() : null;
        ClassRoom room = roomSection.getRoom();
        return RoomSectionDto.builder()
                .id(roomSection.getId())
                .schoolId(roomSection.getSchoolId())
                .classSectionId(classSection != null ? classSection.getId() : null)
                .gradeId(grade != null ? grade.getId() : null)
                .gradeName(grade != null ? grade.getName() : null)
                .roomId(room != null ? room.getId() : null)
                .roomNumber(room != null ? room.getRoomNumber() : null)
                .roomSize(room != null ? room.getRoomSize() : null)
                .createdAt(roomSection.getCreatedAt())
                .build();
    }
}
