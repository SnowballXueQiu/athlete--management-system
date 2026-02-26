package com.sports.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("attendance")
public class Attendance {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer studentId;
    private Integer courseId;
    private LocalDate attendanceDate;
    private String status;
    private String note;
    private LocalDateTime createdAt;
}
