package com.example.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@TableName("file")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class File {
    @TableId(value = "id", type = IdType.AUTO)
    int id;
    String createTime;
    String identify;
    long fileSize;
    String fileUrl;
}
