package com.sports.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.management.entity.Student;
import com.sports.management.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentMapper studentMapper;

    @GetMapping
    public ResponseEntity<?> getStudents(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "asc") String order) {
        
        Page<Student> studentPage = new Page<>(page, size);
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            queryWrapper.like("name", name);
        }
        
        if ("desc".equalsIgnoreCase(order)) {
            queryWrapper.orderByDesc("id");
        } else {
            queryWrapper.orderByAsc("id");
        }
        
        return ResponseEntity.ok(studentMapper.selectPage(studentPage, queryWrapper));
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Student student) {
        studentMapper.insert(student);
        return ResponseEntity.ok(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Integer id, @RequestBody Student student) {
        student.setId(id);
        studentMapper.updateById(student);
        return ResponseEntity.ok(student);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Integer id) {
        studentMapper.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudent(@PathVariable Integer id) {
        return ResponseEntity.ok(studentMapper.selectById(id));
    }
}
