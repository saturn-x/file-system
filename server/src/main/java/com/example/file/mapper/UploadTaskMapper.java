package com.example.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.file.domain.UploadTask;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UploadTaskMapper extends BaseMapper<UploadTask> {
}
