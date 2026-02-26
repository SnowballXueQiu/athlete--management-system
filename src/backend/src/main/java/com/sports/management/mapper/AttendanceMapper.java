package com.sports.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.management.dto.AttendanceDTO;
import com.sports.management.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {
    Page<AttendanceDTO> selectAttendancePage(Page<AttendanceDTO> page, @Param("studentId") Integer studentId, @Param("courseId") Integer courseId, @Param("date") LocalDate date);

    @Select("SELECT DATE(attendance_date) as date, COUNT(*) as count FROM attendance WHERE attendance_date >= #{startDate} GROUP BY DATE(attendance_date)")
    List<Map<String, Object>> selectWeeklyAttendance(@Param("startDate") LocalDate startDate);
}
