package com.sports.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sports.management.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
