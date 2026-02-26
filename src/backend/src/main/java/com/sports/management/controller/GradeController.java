package com.sports.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.management.dto.GradeDTO;
import com.sports.management.entity.Grade;
import com.sports.management.mapper.GradeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    @Autowired
    private GradeMapper gradeMapper;

    @GetMapping
    public ResponseEntity<?> getGrades(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) String gradeType) {
        
        Page<GradeDTO> gradePage = new Page<>(page, size);
        return ResponseEntity.ok(gradeMapper.selectGradePage(gradePage, studentId, courseId, gradeType));
    }

    @PostMapping
    public ResponseEntity<?> createGrade(@RequestBody Grade grade) {
        gradeMapper.insert(grade);
        return ResponseEntity.ok(grade);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGrade(@PathVariable Integer id, @RequestBody Grade grade) {
        grade.setId(id);
        gradeMapper.updateById(grade);
        return ResponseEntity.ok(grade);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGrade(@PathVariable Integer id) {
        gradeMapper.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
