package com.example.file.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("tempchunkfile")
public class TempChunkFile {
    @TableId(value = "id", type = IdType.AUTO)
    int id;
    String createTime;
    String identify;
    String fileUrl;
    int chunkNum;
    long chunkSize;
    @TableLogic
    int status;
    int userId;

}
