package com.example.file.common.resp;

import lombok.Getter;

@Getter
public enum Status implements IStatus {
    /**
     * 操作成功！
     */
    SUCCESS(200, "操作成功！"),
    METHODARGUMENT_ERROR(4000,"请求参数异常"),
    USER_LOGIN_ERROR(5000,"账号登陆失败，请检查后重新登陆"),
    TOKEN_ERROR(5001, "Token无效异常"),
    TOKEN_BLANK(5003, "未登陆异常"),
    AUTHORITY_ERROR(5005,"无权限异常"),
    USER_REGISTER_ERROR(3000,"注册账号失败"),
    UNKNOWN_ERROR(10000,"其他未知异常"),
    SHARE_ERROR(9990,"文件设置分享异常"),
    SHARE_FILE_EXPIRE(9991,"文件分享过期"),
    FILE_ERROR(9999,"文件下载异常");

    Integer code;
    String message;

    Status(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Status fromCode(Integer code) {
        Status[] statuses = Status.values();
        for (Status status : statuses) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return SUCCESS;
    }

    @Override
    public String toString() {
        return String.format(" Status:{code=%s, message=%s} ", getCode(), getMessage());
    }


}
