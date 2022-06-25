package com.example.file.common.exception.base;

import com.example.file.common.resp.Status;

public class FileException extends BaseException{

    public FileException(Status status) {
        super(status);
    }

    public FileException(Status status, Object data) {
        super(status, data);
    }

    public FileException(Integer code, String message) {
        super(code, message);
    }

    public FileException(Integer code, String message, Object data) {
        super(code, message, data);
    }
}
