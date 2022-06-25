package com.example.file.dto;


import com.sun.istack.internal.NotNull;
import lombok.Data;

@Data
public class UserRegisterDTO {
    @NotNull
    String username;
    @NotNull
    String nickname;
    @NotNull
    String password;
    @NotNull
    String inviteCode;

}
