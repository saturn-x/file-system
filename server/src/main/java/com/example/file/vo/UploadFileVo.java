package com.example.file.vo;

//import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data

public class UploadFileVo {

    private String timeStampName;

    private boolean skipUpload;

    private boolean needMerge;

    private List<Integer> uploaded;

    private String data;

    @Override
    public String toString() {
        return "UploadFileVo{" +
                "timeStampName='" + timeStampName + '\'' +
                ", skipUpload=" + skipUpload +
                ", needMerge=" + needMerge +
                ", uploaded=" + uploaded +
                ", data='" + data + '\'' +
                '}';
    }
}