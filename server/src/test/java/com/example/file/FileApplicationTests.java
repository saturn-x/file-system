package com.example.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.file.domain.File;
import com.example.file.mapper.FileMapper;
import com.example.file.mapper.UserFileMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class FileApplicationTests {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    FileMapper fileMapper;

    @Autowired
    UserFileMapper userFileMapper;

    @Test
    void contextLoads() {

            System.out.println("\n\nredis缓存字符串示例：");
            redisTemplate.opsForValue().setIfAbsent("string", "字符串1234123123123123123");
            // 设置过期时间
//            redisTemplate.expire("string", 1, TimeUnit.MINUTES);
            String string = redisTemplate.opsForValue().get("string").toString();
            System.out.println("redisTemplate.opsForValue().get(\"string\"): " + string);
    }

    @Test
    void TestFile() {
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        int id = 1;
        queryWrapper.eq(File::getId,id);
        File file = fileMapper.selectOne(queryWrapper);
        System.out.println(file.toString());


    }


}
