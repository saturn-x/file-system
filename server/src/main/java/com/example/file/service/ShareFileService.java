package com.example.file.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.file.common.exception.base.FileException;
import com.example.file.common.resp.ApiResponse;
import com.example.file.common.resp.Consts;
import com.example.file.common.resp.Status;
import com.example.file.domain.File;
import com.example.file.domain.*;
import com.example.file.dto.ShareFileDTO;
import com.example.file.dto.ShareFileVo;
import com.example.file.mapper.FileMapper;
import com.example.file.mapper.ShareFileMapper;
import com.example.file.mapper.UserFileMapper;
import com.example.file.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ShareFileService {

    @Autowired
    ShareFileMapper shareFileMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserFileMapper userFileMapper;

    @Autowired
    FileMapper fileMapper;
    public ApiResponse addShareFile(ShareFileDTO shareFileDTO) {
        // 1.判断该文件是否为该用户的
        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFile::getUserId,getUserId());
        queryWrapper.eq(UserFile::getId,shareFileDTO.getUserFileId());
        UserFile userFile = userFileMapper.selectOne(queryWrapper);
        if(userFile == null || !NumberUtil.isLong(shareFileDTO.getShareTime()) || (shareFileDTO.isHasPassword()&&shareFileDTO.getPassword().length()!=6)) {
            throw new FileException(Status.SHARE_ERROR);
        }
        // 2.添加到数据库中
        ShareFile shareFile = new ShareFile();
        shareFile.setHasPassword(shareFileDTO.isHasPassword()? '1':'0');
        if(shareFileDTO.isHasPassword()) {
            shareFile.setPassword(shareFileDTO.getPassword());
        }
        shareFile.setUserFileId(userFile.getId());
        shareFile.setUserId(userFile.getUserId());
        shareFile.setDurationTime(shareFileDTO.getShareTime()); // 分享时间应该为 时间戳
        shareFileMapper.insert(shareFile);
        // 3.将序号加密
        int id = shareFile.getId();
        String encode = encode(id);
        String shareHref = Consts.SHARE_WEB + encode;
        return ApiResponse.ofSuccess("添加分享文件成功",shareHref);
    }



    public int getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long id = loginUser.getUserPrincipal().getId();
        return id.intValue();
    }


    public ApiResponse getinfo(String key) {
        String id = decode(key);
        if(!NumberUtil.isInteger(id)) {
            throw new FileException(Status.SHARE_ERROR);
        }
        // 1. 获取到shareFile
        ShareFile shareFile = shareFileMapper.selectById(id);
        if(shareFile == null) {
            throw new FileException(Status.SHARE_ERROR);
        }
        // 获取userFile
        UserFile userFile = userFileMapper.selectById(shareFile.getUserFileId());
        User user = userMapper.selectById(shareFile.getUserId());
        ShareFileVo shareFileVo = new ShareFileVo();
        shareFileVo.setNickName(user.getNickname()); // 1.设置昵称
        shareFileVo.setFileName(userFile.getFileName()); // 2.设置用户名
        shareFileVo.setHasPassword(shareFile.getHasPassword() == '1'); // 3. 设置是否有密码
        shareFileVo.setExtend(userFile.getExtend());
        //4.设置是否过期
        shareFileVo.setExpireTime(isExpire(shareFile));
        shareFileVo.setExpireTimes( DateUtil.formatDateTime(retExpireTime(shareFile)));
        return ApiResponse.ofSuccess("获取文件成功",shareFileVo);
    }



    public String encode(int id) {
        AES aes = SecureUtil.aes(Consts.SHARE_KEY.getBytes());
        return aes.encryptHex(""+id);
    }

    public String decode(String content) {
        AES aes = SecureUtil.aes(Consts.SHARE_KEY.getBytes());
        return aes.decryptStr(content);
    }

    public ApiResponse vaild(String key, String password) {
        // 1.获取key值
        String id = decode(key);
        if(!NumberUtil.isInteger(id) || password.length()!=6) {
            throw new FileException(Status.SHARE_ERROR);
        }
        // 2.获取password
        ShareFile shareFile = shareFileMapper.selectById(id);
        if(shareFile.getPassword().equals(password)){
            return ApiResponse.ofSuccess("口令正确",true);
        }else{
            return ApiResponse.ofSuccess("口令错误",false);
        }

    }

    public void downloadFile(String key, String password, HttpServletResponse response) throws UnsupportedEncodingException, FileNotFoundException {
        // 1.获取key值
        String id = decode(key);
        if(!NumberUtil.isInteger(id)) {
            throw new FileException(Status.SHARE_ERROR);
        }
        // 2.获取password
        ShareFile shareFile = shareFileMapper.selectById(id);
        if(shareFile == null || (shareFile.getHasPassword()=='1'&&!shareFile.getPassword().equals(password))){
            throw new FileException(Status.SHARE_ERROR);
        }
        if(isExpire(shareFile))
            throw new FileException(Status.SHARE_FILE_EXPIRE);
        // 3.获取文件视图
        UserFile userFile = userFileMapper.selectById(shareFile.getUserFileId());
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


    // 判断文件是否过期
    public boolean isExpire(ShareFile shareFile) {
        Date createTime = shareFile.getCreateTime();
        String durationTime = shareFile.getDurationTime();
        long day = Long.parseLong(durationTime) / (3600 * 24 * 1000);
        Date expireTime = DateUtil.offsetDay(createTime, (int) day);
        Date currentTime = new Date();
        return expireTime.getTime()-currentTime.getTime() < 0;
    }

    public Date retExpireTime(ShareFile shareFile) {
        Date createTime = shareFile.getCreateTime();
        String durationTime = shareFile.getDurationTime();
        long day = Long.parseLong(durationTime) / (3600 * 24 * 1000);
        Date expireTime = DateUtil.offsetDay(createTime, (int) day);
        return expireTime;
    }
    // 返回所有未过期的 记录
    public ApiResponse infoAll() {
        int userId = getUserId();
        List<ShareFile> list = shareFileMapper.selectList(new LambdaQueryWrapper<ShareFile>().eq(
                ShareFile::getUserId,userId
        ));
        // 遍历所有的list 判断是否过期
        List<ShareFileVo> res = new ArrayList<>();
        for(ShareFile shareFile : list) {
            if(shareFile != null &&!isExpire(shareFile)) {
                // 没过期
                ShareFileVo shareFileVo = new ShareFileVo();
                shareFileVo.setExpireTimes(DateUtil.formatDateTime(retExpireTime(shareFile))); // 1.设置过期时间
                UserFile userFile = userFileMapper.selectById(shareFile.getUserFileId());
                log.warn("userfileId{}",shareFile.getUserFileId());
                shareFileVo.setFileName(userFile.getFileName()); // 2.设置文件名
                shareFileVo.setShareFileId(shareFile.getId());
                res.add(shareFileVo);
            }else{
                // 过期了 删除该记录
                assert shareFile != null;
                shareFileMapper.deleteById(shareFile.getId());
            }
        }

        return ApiResponse.ofSuccess("获取分享信息",res);
    }

    public ApiResponse delete(int id) {
        // 1. 删除掉id
        int userId = getUserId();
        LambdaQueryWrapper<ShareFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShareFile::getId,id).eq(ShareFile::getUserId,userId);
        shareFileMapper.delete(queryWrapper);
        return ApiResponse.ofSuccess("删除成功");
    }
}
