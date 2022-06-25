package com.example.file.controller;

import com.example.file.common.resp.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {
//    @Cacheable(value = RedisCacheConfig.CacheNames.LIST_DATA)
//    @PostMapping("/list")
//    public List<String> list(@RequestBody MyPageParam pageParam) {
//        return fakeData(pageParam);
//    }
//
//    @CacheEvict(value = RedisCacheConfig.CacheNames.LIST_DATA, allEntries = true)
//    @PostMapping("/update")
//    public String update() {
//        return "更新成功，缓存数据已清除";
//    }
//
//    // 模拟分页数据
//    private List<String> fakeData(MyPageParam pageParam) {
//        List<String> list = new ArrayList<>();
//        for (int i = 0; i < pageParam.getPageSize(); i++) {
//            list.add(pageParam.getPageNum()+ "-" + i);
//        }
//        return list;
//    }
//
//    @CacheEvict(value = "*", allEntries = true)
//    @PostMapping("/clear")
//    public String clear() {
//        return "删除所有缓存数据成功";
//    }

    @GetMapping("/test/hello")
    public ApiResponse hello() {
        log.warn("执行hello");

        return ApiResponse.ofSuccess("hello world");
    }


}
