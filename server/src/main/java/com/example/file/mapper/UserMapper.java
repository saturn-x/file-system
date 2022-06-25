package com.example.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.file.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {



}
