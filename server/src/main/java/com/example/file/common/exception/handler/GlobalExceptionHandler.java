package com.example.file.common.exception.handler;

import com.example.file.common.exception.base.BaseException;
import com.example.file.common.resp.ApiResponse;
import com.example.file.common.resp.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.file.common.exception.base.SecurityException;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = SecurityException.class)
    @ResponseBody
    public ApiResponse handlerException(HttpServletRequest request , SecurityException e) {
        log.warn("安全异常···");
        if(e.getStatus() == Status.TOKEN_ERROR) {
            return ApiResponse.ofStatus(Status.TOKEN_ERROR);
        }else if (e.getStatus() == Status.TOKEN_BLANK) {
            return ApiResponse.ofStatus(Status.TOKEN_BLANK);
        }else if (e.getStatus() == Status.USER_LOGIN_ERROR) {
            return ApiResponse.ofStatus(Status.USER_LOGIN_ERROR);
        }else if (e.getStatus() == Status.USER_REGISTER_ERROR) {
            return ApiResponse.ofStatus(Status.USER_REGISTER_ERROR);
        }

        return ApiResponse.ofStatus(Status.UNKNOWN_ERROR);
    }

    @ExceptionHandler(value =  SQLException.class)
    @ResponseBody
    public ApiResponse handlerSQLException(HttpServletRequest request,SQLException e) {
        log.warn(e.getMessage());
        return ApiResponse.ofStatus(Status.UNKNOWN_ERROR);
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiResponse handlerMethodException(HttpServletRequest request,MethodArgumentNotValidException e) {
        log.warn("请求参数异常···");
        return ApiResponse.ofStatus(Status.METHODARGUMENT_ERROR);
    }
    @ExceptionHandler(value = IllegalStateException.class)
    @ResponseBody
    public ApiResponse handlerIllegalArgumentException(HttpServletRequest request,IllegalStateException e) {
        log.warn("非法参数访问···");
        return ApiResponse.ofException(e);
    }

    @ExceptionHandler(value = BaseException.class)
    @ResponseBody
    public  ApiResponse handlerAll(HttpServletRequest request,BaseException e) {
        log.warn("其他自定义异常···");
        return ApiResponse.ofStatus(e.getStatus());
    }
}