package com.example.file.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("uploadtask")
public class UploadTask {
    @TableId(value="id",type = IdType.AUTO)
    int id;
    String createTime;
    long fileSize;
    String extend;
    String fileName;
    String identify;
    int totalChunks;
    @TableLogic
    int status;
    int userId;
}
