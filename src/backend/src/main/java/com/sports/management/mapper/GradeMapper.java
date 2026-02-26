package com.sports.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.management.dto.GradeDTO;
import com.sports.management.entity.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GradeMapper extends BaseMapper<Grade> {
    Page<GradeDTO> selectGradePage(Page<GradeDTO> page, 
                                   @Param("studentId") Integer studentId, 
                                   @Param("courseId") Integer courseId, 
                                   @Param("gradeType") String gradeType);
                                   
    @Select("SELECT AVG(score) FROM grades")
    Double selectAverageScore();
}
