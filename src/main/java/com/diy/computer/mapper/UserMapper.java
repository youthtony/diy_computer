package com.diy.computer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diy.computer.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
