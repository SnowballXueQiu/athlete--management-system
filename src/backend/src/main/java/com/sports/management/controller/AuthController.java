package com.sports.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sports.management.dto.LoginRequest;
import com.sports.management.dto.LoginResponse;
import com.sports.management.entity.User;
import com.sports.management.mapper.UserMapper;
import com.sports.management.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", request.getUsername()));
        
        if (user == null || !user.getPassword().equals(request.getPassword())) {
             return ResponseEntity.badRequest().body("用户名或密码错误");
        }

        String token = jwtUtils.generateToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getRole(), user.getName()));
    }
}
