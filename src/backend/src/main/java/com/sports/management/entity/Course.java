package com.sports.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("courses")
public class Course {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private Integer teacherId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String daysOfWeek;
    private String location;
    private Integer maxStudents;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
