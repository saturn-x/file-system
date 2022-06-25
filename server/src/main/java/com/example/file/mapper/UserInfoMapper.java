package com.example.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.file.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
