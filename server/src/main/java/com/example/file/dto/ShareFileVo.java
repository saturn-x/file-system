package com.example.file.dto;

import lombok.Data;

/*
*   1、分享文件视图
*   - 用户名
*   - 过期时间
*   -
* */
@Data
public class ShareFileVo {
    int shareFileId;
    String nickName;
    String fileName;
    String extend;
    boolean hasPassword;
    // 是否过期
    boolean isExpireTime;
    // 过期时间
    String expireTimes;
}
