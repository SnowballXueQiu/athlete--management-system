package com.sports.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.management.dto.GradeDTO;
import com.sports.management.entity.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface GradeMapper extends BaseMapper<Grade> {
    Page<GradeDTO> selectGradePage(Page<GradeDTO> page, 
                                   @Param("studentId") Integer studentId, 
                                   @Param("courseId") Integer courseId, 
                                   @Param("gradeType") String gradeType);
                                   
    @Select("SELECT AVG(score) FROM grades")
    Double selectAverageScore();
    @Select("SELECT DATE_FORMAT(grade_date, '%Y-%m') as month, AVG(score) as avgScore FROM grades WHERE grade_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) GROUP BY DATE_FORMAT(grade_date, '%Y-%m') ORDER BY month ASC")
    List<Map<String, Object>> selectMonthlyAverageGrade();
}
