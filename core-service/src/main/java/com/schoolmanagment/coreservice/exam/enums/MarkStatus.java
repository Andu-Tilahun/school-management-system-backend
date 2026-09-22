package com.schoolmanagment.coreservice.exam.enums;

public enum MarkStatus {
    REGISTERED, // registered for the exam, not yet sat/graded
    GRADED,     // studMark is populated
    ABSENT,     // didn't sit the exam
    WITHDRAWN
}