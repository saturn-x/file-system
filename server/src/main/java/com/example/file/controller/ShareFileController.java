package com.example.file.controller;

import com.example.file.common.resp.ApiResponse;
import com.example.file.dto.ShareFileDTO;
import com.example.file.service.ShareFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;


@RestController
@RequestMapping("/share")
public class ShareFileController {
    @Autowired
    ShareFileService shareFileService;

    @PostMapping("/new")
    public ApiResponse addShareFile(@RequestBody ShareFileDTO shareFileDTO) {
        return shareFileService.addShareFile(shareFileDTO);
    }
    /*根据用户的token 返回所有未过期的分享记录*/
    @GetMapping("/all")
    public ApiResponse infoAll() {
        return shareFileService.infoAll();
    }

    @GetMapping("/info")
    public ApiResponse info(@RequestParam String key) {
        return shareFileService.getinfo(key);
    }

    @GetMapping("/vaild")
    public ApiResponse vaild(@RequestParam String key,@RequestParam String password) {
        return shareFileService.vaild(key, password);
    }

    @GetMapping("/download")
    public void download(@RequestParam String key, @RequestParam String password, HttpServletResponse resp) throws FileNotFoundException, UnsupportedEncodingException {
        shareFileService.downloadFile(key,password,resp);

    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse delete(@PathVariable("id") int id){
        return shareFileService.delete(id);
    }




}
