package com.example.file.controller;


import com.example.file.common.resp.ApiResponse;
import com.example.file.dto.UserLoginDTO;
import com.example.file.dto.UserRegisterDTO;
import com.example.file.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ApiResponse register(  @RequestBody UserRegisterDTO registerDTO) {
        //注册
        return userService.register(registerDTO);
    }
    @PostMapping("/login")
    public ApiResponse login(@RequestBody UserLoginDTO dto) {
        return userService.login(dto);
    }

    @GetMapping("/logout")
    public ApiResponse logout() {

        return userService.logout();
    }


}
