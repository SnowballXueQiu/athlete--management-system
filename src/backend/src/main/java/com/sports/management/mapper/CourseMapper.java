package com.sports.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sports.management.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    @Select("SELECT c.name as name, COUNT(DISTINCT a.student_id) as count FROM courses c LEFT JOIN attendance a ON c.id = a.course_id GROUP BY c.id, c.name")
    List<Map<String, Object>> selectStudentDistribution();
}
