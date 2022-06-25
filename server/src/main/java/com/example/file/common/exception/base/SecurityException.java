package com.example.file.common.exception.base;

import com.example.file.common.resp.Status;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class SecurityException extends BaseException {
    public SecurityException(Status status) {
        super(status);
    }

    public SecurityException(Status status, Object data) {
        super(status, data);
    }

    public SecurityException(Integer code, String message) {
        super(code, message);
    }

    public SecurityException(Integer code, String message, Object data) {
        super(code, message, data);
    }
}
