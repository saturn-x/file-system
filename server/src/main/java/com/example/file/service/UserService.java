package com.example.file.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.file.common.exception.base.SecurityException;
import com.example.file.common.resp.ApiResponse;
import com.example.file.common.resp.Consts;
import com.example.file.common.resp.Status;
import com.example.file.common.utli.JwtUtil;
import com.example.file.domain.LoginUser;
import com.example.file.domain.User;
import com.example.file.domain.UserInfo;
import com.example.file.dto.LoginUserVo;
import com.example.file.dto.UserLoginDTO;
import com.example.file.dto.UserRegisterDTO;
import com.example.file.mapper.UserInfoMapper;
import com.example.file.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import static com.example.file.common.resp.Status.USER_REGISTER_ERROR;

@Service
@Slf4j
public class UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    UserMapper userMapper;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    UserInfoMapper userInfoMapper;



    public ApiResponse register(@RequestBody UserRegisterDTO dto) {
        // 查询数据库是否有相同的
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername,dto.getUsername());
        User user = userMapper.selectOne(lambdaQueryWrapper);
        if(user != null) {
            return ApiResponse.ofStatus(USER_REGISTER_ERROR);
        }else{
            User cur = new User();
            cur.setUsername(dto.getUsername());
            cur.setNickname(dto.getNickname());
            cur.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
            int insert = userMapper.insert(cur);
            Long spaceSize = 1024*1024*1024l;
            switch (dto.getInviteCode()) {
                case Consts.INVITE_CODE1:
                    spaceSize *= 1;
                    break;
                case Consts.INVITE_CODE2:
                    spaceSize *= 2;
                    break;
                case Consts.INVITE_CODE3:
                    spaceSize *= 3;
                    break;
            }
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(cur.getId().intValue());
            userInfo.setSpaceSize(spaceSize.toString());
            userInfoMapper.insert(userInfo);


            return ApiResponse.ofSuccess("账号创建成功",null);
        }
    }


    public ApiResponse login(UserLoginDTO dto) {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,dto.getUsername());
        User findOne = userMapper.selectOne(queryWrapper);
        if(findOne == null) {
            throw new SecurityException(Status.USER_LOGIN_ERROR);
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication); // 设置到SecurityContextHolder
        String jwt = jwtUtil.createJWT(authentication, dto.isRemember());
        // 将jwt和 nick name  username 封装成VO
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setUsername(findOne.getUsername());
        loginUserVo.setNickname(findOne.getNickname());
        loginUserVo.setToken(jwt);
        UserInfo userInfo = userInfoMapper.selectOne(new QueryWrapper<UserInfo>().eq("user_id", findOne.getId()));
        loginUserVo.setSpaceSize(userInfo.getSpaceSize());
        return ApiResponse.ofSuccess(loginUserVo); // 返回jwt

    }


    public ApiResponse logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        jwtUtil.deleteJWT(loginUser.getUserPrincipal().getId());

        return ApiResponse.ofSuccess("注销成功");
    }
}
