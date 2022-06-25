package com.example.file.common.resp;


import lombok.Data;

@Data
public class ApiResponse {

    private Integer code;
    private String message;
    private Object data;
    private ApiResponse(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ApiResponse of(Integer code, String message, Object data) {
        return new ApiResponse(code, message, data);
    }
    public static ApiResponse ofSuccess() {
        return ofSuccess(null);
    }
    public static ApiResponse ofSuccess(Object data) {
        return ofStatus(Status.SUCCESS, data);
    }    public static ApiResponse ofSuccess(String message,Object data) {
        return of(200,message,data);
    }

    public static ApiResponse ofStatus(Status status) {
        return ofStatus(status, null);
    }
    public static ApiResponse ofStatus(IStatus status, Object data) {
        return of(status.getCode(), status.getMessage(), data);
    }
    public static <T extends Exception> ApiResponse ofException(T t) {
        return of(500, t.getMessage(), null);
    }


}
