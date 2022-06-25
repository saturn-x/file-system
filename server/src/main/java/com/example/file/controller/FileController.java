package com.example.file.controller;


import com.example.file.common.resp.ApiResponse;
import com.example.file.dto.UploadFileDTO;
import com.example.file.mapper.UserFileMapper;
import com.example.file.service.FileService;
import com.example.file.vo.UploadFileVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequestMapping("/file")
@RestController
@Slf4j
public class FileController {


    /*
        实现一个最简单的文件上传
     */
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserFileMapper userFileMapper;

    @Autowired
    FileService fileService;

    @RequestMapping(value = "/uploadfile",method = RequestMethod.POST)
    public ApiResponse uploadfile(HttpServletRequest request, UploadFileDTO uploadFileDto) throws IOException {
        Set<String> resultSet =redisTemplate.opsForSet().members(uploadFileDto.getIdentifier());
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        List<Integer> list = new ArrayList<>();
        for(String i:resultSet) {
            list.add(Integer.parseInt(i));
        }
        if(!isMultipart) {
            UploadFileVo uploadFileVo = new UploadFileVo();
            uploadFileVo.setSkipUpload(false);
            return ApiResponse.ofSuccess("不是文件，请选择正确的文件上传",uploadFileVo);
        }
        if(resultSet.contains(uploadFileDto.getChunkNumber())) {
            UploadFileVo vo = new UploadFileVo();
            vo.setSkipUpload(false);
            vo.setUploaded(list);
            return ApiResponse.ofSuccess("该文件分块已经存在",vo);

        }
        return fileService.uploadFile(request,uploadFileDto);
    }
    @GetMapping(value = "/uploadfile")
    public ApiResponse uploadfileFast(HttpServletRequest request,  UploadFileDTO uploadFileDto) {
        boolean isfast = fileService.fastTransfer(uploadFileDto);
        UploadFileVo uploadFileVo = new UploadFileVo();
        uploadFileVo.setSkipUpload(isfast); // 是否符合快传
        return ApiResponse.ofSuccess(uploadFileVo);
    }

    @GetMapping(value = "/info")
    public ApiResponse getInfo() {
        return fileService.getinfo();
    }


    @DeleteMapping(value = "/delete/{id}")
    public ApiResponse deleteById(@PathVariable("id") Integer id) {
        return fileService.deleteById(id);
    }

    @GetMapping(value = "/download")
    public void downloadfile(@RequestParam Integer fileId, HttpServletResponse response) throws UnsupportedEncodingException, FileNotFoundException {
         fileService.downloadfile(fileId,response);
    }


}
