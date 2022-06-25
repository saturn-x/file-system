package com.example.file.dto;

import lombok.Data;

@Data
public class ShareFileDTO {
    boolean hasPassword;
    String password;
    int userFileId;
    String shareTime;
}
