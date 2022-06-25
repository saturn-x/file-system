package com.example.file.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("share_file")
@Data
public class ShareFile {
    @TableId(value = "id", type = IdType.AUTO)
    int id;
    int userId;
    int userFileId; // 获取用户的id
    char hasPassword;
    String password;
    Date createTime;
    String durationTime;
    @TableLogic
    int status;


}
