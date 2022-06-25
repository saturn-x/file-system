package com.example.file.dto;


import lombok.Data;
/*
* 登陆成功的返回值
* */
@Data
public class LoginUserVo {
    String username;
    String nickname;
    String token;
    String spaceSize;
}
