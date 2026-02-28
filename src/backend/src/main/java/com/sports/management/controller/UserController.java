package com.sports.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.management.entity.User;
import com.sports.management.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String role) {
        
        Page<User> userPage = new Page<>(page, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            queryWrapper.like("name", name);
        }
        
        if (StringUtils.hasText(role)) {
            queryWrapper.eq("role", role);
        }
        
        // Exclude password from result is handled by frontend not displaying it, 
        // but for security in production we should use DTO. For this demo, we return entity.
        // Also exclude admin self from deletion usually, but for list it's fine.
        
        return ResponseEntity.ok(userMapper.selectPage(userPage, queryWrapper));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userMapper.selectCount(new QueryWrapper<User>().eq("username", user.getUsername())) > 0) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }
        // Default password if not provided
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword("123456");
        }
        userMapper.insert(user);
        return ResponseEntity.ok("创建成功");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        // If password is empty, do not update it
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword(null);
        }
        userMapper.updateById(user);
        return ResponseEntity.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        userMapper.deleteById(id);
        return ResponseEntity.ok("删除成功");
    }
    
    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        // Return available roles for selection
        return ResponseEntity.ok(Arrays.asList(
            new RoleOption("coach", "教练"),
            new RoleOption("staff", "行政员"),
            new RoleOption("admin", "管理员")
        ));
    }
    
    static class RoleOption {
        public String value;
        public String label;
        
        public RoleOption(String value, String label) {
            this.value = value;
            this.label = label;
        }
    }
}