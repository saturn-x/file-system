package com.example.file;


import com.example.file.domain.UploadTask;
import com.example.file.mapper.FileMapper;
import com.example.file.mapper.ShareFileMapper;
import com.example.file.mapper.UploadTaskMapper;
import com.example.file.mapper.UserFileMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestSQL {

    @Autowired
    FileMapper fileMapper;

    @Autowired
    UploadTaskMapper uploadTaskMapper;

    @Autowired
    ShareFileMapper shareFileMapper;

    @Autowired
    UserFileMapper userFileMapper;

    @Test
    void test() {
        UploadTask uploadTask = new UploadTask();
        uploadTask.setExtend("ext");
        uploadTask.setFileSize(123);
        uploadTask.setIdentify("1231231asdhasdkhasd23");
        uploadTask.setFileName("秘密咨询");
        fileMapper.insertFile(uploadTask);
        System.out.println(uploadTask.getId());
//        userFileMapper.insertUserFile(uploadTask);
    }

    @Test
    void test2(){
        // 插入一条记录 根据记录
    }



}
