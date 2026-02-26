package com.sports.management.dto;

import com.sports.management.entity.Attendance;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AttendanceDTO extends Attendance {
    private String studentName;
    private String courseName;
}
