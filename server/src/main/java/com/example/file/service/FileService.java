package com.example.file.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.file.common.exception.base.FileException;
import com.example.file.common.resp.ApiResponse;
import com.example.file.common.resp.Consts;
import com.example.file.domain.File;
import com.example.file.domain.*;
import com.example.file.dto.UploadFileDTO;
import com.example.file.mapper.*;
import com.example.file.vo.UploadFileVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.example.file.common.resp.Status.FILE_ERROR;


@Service
@Slf4j
public class FileService {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    FileMapper fileMapper;
    @Autowired
    UserFileMapper userFileMapper;
    @Autowired
    UploadTaskMapper uploadTaskMapper;
    @Autowired
    TempChunkFileMapper tempChunkFileMapper;

    @Autowired
    ShareFileMapper shareFileMapper;


    // 判断一个文件是否符合快传 符合快传插入到文件中
    public boolean fastTransfer(UploadFileDTO dto) {
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(File::getIdentify,dto.getIdentifier());
        File file  = fileMapper.selectOne(queryWrapper);
        if(file != null) {
            //符合上传 直接插入 file -> userfile
            addUserFile(dto,file.getId());
            return true;
        }else {
            LambdaQueryWrapper<UploadTask> qw = new LambdaQueryWrapper<>();
            qw.eq(UploadTask::getIdentify,dto.getIdentifier());
            List<UploadTask> up = uploadTaskMapper.selectList(qw);
            if( up.size() == 0 )
                addUploadTask(dto);
            return  false;
        }
    }
    // 上传文件 如果上传文件未完毕 return false  上传完毕 return true
    public ApiResponse uploadFile(HttpServletRequest request, UploadFileDTO dto) throws IOException {

        //1.将md5：chunkNumber添加到redis 中
        String file_md5 = dto.getIdentifier();
        SetOperations<String, String> set = redisTemplate.opsForSet();
        //2.将文件写入硬盘
        StandardMultipartHttpServletRequest requests = (StandardMultipartHttpServletRequest)request;
        Iterator iter = requests.getFileNames();
        String tmpFileName = Consts.FILE_PATH+dto.getIdentifier()+" "+dto.getChunkNumber();
        String allFileName = Consts.FILE_PATH +dto.getIdentifier();
        long size = 0;
        while(iter.hasNext()) {
            MultipartFile multipartFile = requests.getFile((String)iter.next());
            RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFileName, "rw");
            accessTmpFile.write(multipartFile.getBytes());
            size = multipartFile.getSize();
            accessTmpFile.close();
        }
        //3.将块记录写入数据库
        TempChunkFile res = new TempChunkFile();
        res.setFileUrl(tmpFileName);
        res.setIdentify(file_md5);
        res.setChunkSize(size);
        res.setChunkNum(dto.getChunkNumber());
        res.setStatus(0);
        res.setUserId(getUserId());
        tempChunkFileMapper.insert(res);
        // 添加代表文件块已经上传
        set.add(file_md5,dto.getChunkNumber()+"");
        //4.判断文件是否上传完毕
        Set<String> resultSet =redisTemplate.opsForSet().members(file_md5);
        UploadFileVo uploadFileVo = new UploadFileVo();
        assert resultSet != null;
        if(resultSet.size() == dto.getTotalChunks()) {
            // 块数相同  上传完毕 将文件合并
            uploadFileVo.setSkipUpload(true);
            uploadFileVo.setData("上传完毕");
            mergeFile(allFileName,file_md5);
            redisTemplate.delete(file_md5);
            return ApiResponse.ofSuccess("上传完毕",uploadFileVo);
        }else {
            // 没有上传完毕 return false
            List<Integer> list = new ArrayList<>();
            for(String i:resultSet) {
                list.add(Integer.parseInt(i));
            }
            uploadFileVo.setSkipUpload(false);
            uploadFileVo.setData("请继续上传！！");
            uploadFileVo.setUploaded(list);
            return ApiResponse.ofSuccess("文件上传未完毕",uploadFileVo);
        }
    }


    // 如果文件块满足了 合并文件
    /*
        根据md5 从数据库中查到所有的记录
        并将记录
     */
    public void mergeFile(String file_url,String md5) throws IOException {
        // 0.根据md5 查出所有已经上传的分片
        LambdaQueryWrapper<TempChunkFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TempChunkFile::getIdentify,md5)
                .eq(TempChunkFile::getStatus,0);
        List<TempChunkFile> list = tempChunkFileMapper.selectList(queryWrapper);
        LambdaQueryWrapper<UploadTask> uploadTaskWrapper = new LambdaQueryWrapper<>();
        uploadTaskWrapper.eq(UploadTask::getIdentify,md5)
                .eq(UploadTask::getStatus,0)
                .eq(UploadTask::getUserId,getUserId());// 1 代表删除 0 代表没删除
        UploadTask uploadTask = uploadTaskMapper.selectOne(uploadTaskWrapper);
        if(uploadTask == null) // 已经合并成功
            return;
        // 1.逻辑删除
        uploadTaskMapper.deleteById(uploadTask.getId());
        // 2.将所有的记录 写入到file_url：identify+extend;
        RandomAccessFile to = new RandomAccessFile(file_url+"."+uploadTask.getExtend(), "rw");
        FileChannel tochannel = to.getChannel();
        FileChannel from = null;
        for(TempChunkFile i : list) {
            from = new RandomAccessFile(i.getFileUrl(),"r").getChannel();
            from.transferTo(0,from.size(),tochannel);
            from.close();

        }
        long allsize = tochannel.size();
        tochannel.close();
        to.close();


        tempChunkFileMapper.delete(queryWrapper);
        fileMapper.insertFile(uploadTask);
        userFileMapper.insertUserFile(uploadTask);

    }





    // 插入 userfile记录
    public void addUserFile(UploadFileDTO dto,int fileId) {
        String filename = dto.getFilename();
        int dot = filename.lastIndexOf('.');
        String extend = "";
        String name = filename;
        if(dot != -1 ){
            // 没有后缀名
            extend = filename.substring(dot + 1);
            name =filename.substring(0,dot);
        }

        UserFile userFile = new UserFile();
        userFile.setFileSize(dto.getTotalSize());
        userFile.setExtend(extend);
        userFile.setFileName(name);
        userFile.setFileId(fileId);
        userFile.setUserId(getUserId());
        userFile.setStatus(0);
        userFileMapper.insert(userFile);
    }

    public void addUploadTask(UploadFileDTO dto) {
        String filename = dto.getFilename();
        int dot = filename.lastIndexOf('.');
        String extend = filename.substring(dot + 1);
        String name =filename.substring(0,dot);
        UploadTask task = new UploadTask();
        task.setFileSize(dto.getTotalSize());
        task.setExtend(extend);
        task.setFileName(name);
        task.setIdentify(dto.getIdentifier());
        task.setTotalChunks(dto.getTotalChunks());
        task.setUserId(getUserId());
        uploadTaskMapper.insert(task);
    }


    public int getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long id = loginUser.getUserPrincipal().getId();
        return id.intValue();
    }

    public ApiResponse getinfo() {
        // 根据id 返回所有的文件
        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFile::getUserId,getUserId());
        List<UserFile> list = userFileMapper.selectList(queryWrapper);
        return ApiResponse.ofSuccess(list);
    }

    public ApiResponse deleteById(Integer id) {
        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFile::getUserId,getUserId());
        queryWrapper.eq(UserFile::getId,id);
        LambdaQueryWrapper<ShareFile> eq = new LambdaQueryWrapper<ShareFile>().eq(ShareFile::getUserFileId, id).eq(ShareFile::getUserId, getUserId());
        shareFileMapper.delete(eq);
        int delete = userFileMapper.delete(queryWrapper);
        return ApiResponse.ofSuccess("删除成功");
    }

    public void downloadfile(Integer fileId, HttpServletResponse response) throws UnsupportedEncodingException, FileNotFoundException {
        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFile::getUserId,getUserId());
        queryWrapper.eq(UserFile::getId,fileId);
        UserFile userFile = userFileMapper.selectOne(queryWrapper);
        if(userFile == null) {
            throw new FileException(FILE_ERROR);
        }
        // 根据userfile
        File file = fileMapper.selectById(userFile.getFileId());
        String filePath = Consts.FILE_PATH + file.getFileUrl();
        String fileName = userFile.getFileName();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Expose-Headers","Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName,"utf-8"));
        InputStream inputStream = new FileInputStream(filePath);
        OutputStream outputStream = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            outputStream = response.getOutputStream();

            bos = new BufferedOutputStream(outputStream);
            bis = new BufferedInputStream(inputStream);

            byte[] b = new byte[1024];
            int length = 0;
            while ((length = bis.read(b)) != -1){
                bos.write(b,0,length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
