package com.example.file.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.file.domain.LoginUser;
import com.example.file.domain.User;
import com.example.file.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username)  {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        User user = userMapper.selectOne(queryWrapper);
        if(Objects.isNull(user)) {
            throw new UsernameNotFoundException("查找不到用户");
        }
        /*查询到全新 信息 并且将信息 封装到userdetails*/
        List<String> list = new ArrayList<>(Arrays.asList("admin","test"));

        return new LoginUser(user,list);
    }



}
