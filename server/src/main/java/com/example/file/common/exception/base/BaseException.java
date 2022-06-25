package com.example.file.common.exception.base;

import com.example.file.common.resp.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseException extends RuntimeException {
    public Integer code;
    public String message;
    public Object data;
    public Status status;
    public BaseException(Status status) {
        super(status.getMessage());
        this.code = status.getCode();
        this.message = status.getMessage();
        this.status = status;
    }

    public BaseException(Status status, Object data) {
        this(status);
        this.data = data;
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(Integer code, String message, Object data) {
        this(code, message);
        this.data = data;
    }
}
