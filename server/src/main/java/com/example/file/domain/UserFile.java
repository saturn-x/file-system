package com.example.file.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@TableName("userfile")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFile {
    int id;
    String createTime;
    long fileSize;
    String extend;
    String fileName;
    int fileId;
    int userId;
    @TableLogic
    int status;
}
