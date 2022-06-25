package com.example.file.dto;

import lombok.Data;

@Data
public class UserLoginDTO {
    String username;
    String password;
    boolean isRemember;
}
