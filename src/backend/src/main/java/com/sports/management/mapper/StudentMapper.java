package com.sports.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sports.management.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
