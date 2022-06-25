package com.example.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("user_info")
@Data
public class UserInfo {
    @TableId(value = "id", type = IdType.AUTO)
    int id;
    int userId;
    String spaceSize;
}
