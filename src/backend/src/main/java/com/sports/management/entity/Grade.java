package com.sports.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("grades")
public class Grade {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer studentId;
    private Integer courseId;
    private String gradeType;
    private BigDecimal score;
    private BigDecimal maxScore;
    private LocalDate gradeDate;
    private String comment;
    private LocalDateTime createdAt;
}
