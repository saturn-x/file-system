package com.example.file.common.resp;

public interface Consts {
    /**
     * JWT 在 Redis 中保存的key前缀
     */
    String REDIS_JWT_KEY_PREFIX = "security:jwt:";

    String FILE_PATH = "D:\\filetest\\";

    String SHARE_KEY = "0123456789ABHAEQ";

    String SHARE_WEB = "http://127.0.0.1:9000/share/info?key=";

    String INVITE_CODE2 = "200000";
    String  INVITE_CODE1= "100000";
    String INVITE_CODE3 = "300000";
}
