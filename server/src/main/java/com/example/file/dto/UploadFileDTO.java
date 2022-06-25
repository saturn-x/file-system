package com.example.file.dto;

//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

//import javax.persistence.*;

@Data

public class UploadFileDTO {
/*
filename: 屏幕截图 2022-02-22 154159.png
identifier: 7acb7394948859e799d9cd9ef7e5efa1
totalChunks: 1
chunkNumber: 1
totalSize: 66550
 */

    @Override
    public String toString() {
        return "UploadFileDTO{" +
                "filename='" + filename + '\'' +
                ", chunkNumber=" + chunkNumber +
                ", chunkSize=" + chunkSize +
                ", totalChunks=" + totalChunks +
                ", totalSize=" + totalSize +
                ", identifier='" + identifier + '\'' +
                '}';
    }

    private String filename;

    private int chunkNumber;

    private long chunkSize;


    private int totalChunks;

    private long totalSize;

    private String identifier;

}
