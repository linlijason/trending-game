package com.bistro.common.exception.api;

public class ApiException extends RuntimeException{
    private Integer code;
    private String message;

    public ApiException(int code,String message){
        this.message = message;
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
