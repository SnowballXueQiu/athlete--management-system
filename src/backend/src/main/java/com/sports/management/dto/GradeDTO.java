package com.sports.management.dto;

import com.sports.management.entity.Grade;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GradeDTO extends Grade {
    private String studentName;
    private String courseName;
}
