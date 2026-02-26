package com.sports.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.management.entity.Course;
import com.sports.management.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseMapper courseMapper;

    @GetMapping
    public ResponseEntity<?> getCourses(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        
        Page<Course> coursePage = new Page<>(page, size);
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("created_at");
        
        return ResponseEntity.ok(courseMapper.selectPage(coursePage, queryWrapper));
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        courseMapper.insert(course);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Integer id, @RequestBody Course course) {
        course.setId(id);
        courseMapper.updateById(course);
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Integer id) {
        courseMapper.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourse(@PathVariable Integer id) {
        return ResponseEntity.ok(courseMapper.selectById(id));
    }
}
