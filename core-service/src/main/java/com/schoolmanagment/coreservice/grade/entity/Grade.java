//package com.schoolmanagment.coreservice.grade.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.util.UUID;
//@Entity
//@Table(name = "grades",
//        uniqueConstraints = @UniqueConstraint(
//        name = "uq_school_grade_name",
//        columnNames = {"school_id", "grade_name"})
//)
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Grade {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID id;
//
//    @Column(name = "school_id", nullable = false)
//    private UUID schoolId;
//
//    @Column(name = "grade_name", nullable = false)
//    private String gradeName;
//
//    @Column(name = "level_order", nullable = false)
//    private Integer levelOrder;
//}
