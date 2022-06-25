package com.example.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.file.domain.File;
import com.example.file.domain.UploadTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper extends BaseMapper<File> {
    int insertFile(UploadTask uploadTask);
}
