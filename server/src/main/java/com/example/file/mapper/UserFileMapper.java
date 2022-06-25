package com.example.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.file.domain.UploadTask;
import com.example.file.domain.UserFile;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserFileMapper extends BaseMapper<UserFile> {
    int insertUserFile(UploadTask up);
}
