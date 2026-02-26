package com.sports.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("students")
public class Student {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String gender;
    private Integer age;
    private String phone;
    private String email;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
