package com.sports.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.management.dto.AttendanceDTO;
import com.sports.management.entity.Attendance;
import com.sports.management.mapper.AttendanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @GetMapping
    public ResponseEntity<?> getAttendance(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        Page<AttendanceDTO> attendancePage = new Page<>(page, size);
        return ResponseEntity.ok(attendanceMapper.selectAttendancePage(attendancePage, studentId, courseId, date));
    }

    @PostMapping
    public ResponseEntity<?> createAttendance(@RequestBody Attendance attendance) {
        // Simple duplicate check could be here, but DB has unique constraint
        try {
            attendanceMapper.insert(attendance);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
             return ResponseEntity.badRequest().body("该学员在此课程当日已有考勤记录");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAttendance(@PathVariable Integer id, @RequestBody Attendance attendance) {
        attendance.setId(id);
        attendanceMapper.updateById(attendance);
        return ResponseEntity.ok(attendance);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAttendance(@PathVariable Integer id) {
        attendanceMapper.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
