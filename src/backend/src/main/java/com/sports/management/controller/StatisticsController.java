package com.sports.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sports.management.mapper.AttendanceMapper;
import com.sports.management.mapper.CourseMapper;
import com.sports.management.mapper.GradeMapper;
import com.sports.management.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private AttendanceMapper attendanceMapper;
    
    @Autowired
    private GradeMapper gradeMapper;

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        Map<String, Object> data = new HashMap<>();
        
        // Basic Counts
        data.put("totalStudents", studentMapper.selectCount(null));
        data.put("totalCourses", courseMapper.selectCount(null));
        data.put("activeCourses", courseMapper.selectCount(new QueryWrapper<com.sports.management.entity.Course>().eq("status", "active")));
        data.put("totalAttendance", attendanceMapper.selectCount(null));
        data.put("totalGrades", gradeMapper.selectCount(null));
        
        // Average Grade
        Double avgGrade = gradeMapper.selectAverageScore();
        data.put("averageGrade", avgGrade != null ? String.format("%.1f", avgGrade) : "0.0");
        
        // Weekly Activity (Last 7 days)
        List<Map<String, Object>> weeklyActivity = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        
        // Fetch raw data from DB
        List<Map<String, Object>> rawActivity = attendanceMapper.selectWeeklyAttendance(startDate);
        
        // Map raw data to date string for easy lookup
        Map<String, Long> activityMap = new HashMap<>();
        if (rawActivity != null) {
            for (Map<String, Object> record : rawActivity) {
                // Handle different date types returned by JDBC (java.sql.Date or LocalDate)
                Object dateObj = record.get("date");
                String dateStr = dateObj.toString();
                Long count = ((Number) record.get("count")).longValue();
                activityMap.put(dateStr, count);
            }
        }
        
        // Fill in last 7 days (including today)
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("E"); // Mon, Tue... or 周一, 周二 based on locale
        // For Chinese "周一", "周二", we might need specific locale or manual mapping if system locale isn't Chinese
        // Let's use simple day names for now or map them manually to be safe
        String[] weekDays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            String dateStr = date.toString(); // YYYY-MM-DD
            Long count = activityMap.getOrDefault(dateStr, 0L);
            
            Map<String, Object> dayData = new HashMap<>();
            // Use day of week from date
            int dayOfWeek = date.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
            dayData.put("day", weekDays[dayOfWeek - 1]);
            dayData.put("value", count);
            weeklyActivity.add(dayData);
        }
        
        data.put("weeklyActivity", weeklyActivity);
        
        // Student Distribution (Course Enrollment)
        List<Map<String, Object>> distribution = courseMapper.selectStudentDistribution();
        data.put("studentDistribution", distribution);
        
        // Grade Trend (Last 6 months)
        List<Map<String, Object>> gradeTrend = gradeMapper.selectMonthlyAverageGrade();
        data.put("gradeTrend", gradeTrend);
        
        return ResponseEntity.ok(data);
    }
}
