package com.schoolmanagment.coreservice.roomsection.mapper;

import com.schoolmanagment.coreservice.classroom.entity.ClassRoom;
import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.grade.entity.Grade;
import com.schoolmanagment.coreservice.roomsection.dto.RoomSectionDto;
import com.schoolmanagment.coreservice.roomsection.dto.RoomSectionRequest;
import com.schoolmanagment.coreservice.roomsection.entity.RoomSection;
import org.springframework.stereotype.Component;

@Component
public class RoomSectionMapper {

    public RoomSectionDto toDto(RoomSection roomSection) {
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

    public RoomSection toEntity(RoomSectionRequest request, ClassSection classSection, ClassRoom room) {
        return RoomSection.builder()
                .classSection(classSection)
                .room(room)
                .active(true)
                .build();
    }

    public void updateEntity(RoomSection roomSection, ClassSection classSection, ClassRoom room) {
        roomSection.setClassSection(classSection);
        roomSection.setRoom(room);
    }
}
