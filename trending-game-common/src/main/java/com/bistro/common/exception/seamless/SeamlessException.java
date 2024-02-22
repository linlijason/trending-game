package com.bistro.common.exception.seamless;

import com.bistro.common.exception.base.BaseException;

public class SeamlessException extends BaseException {

    private int errorCode;

    public SeamlessException(int errorCode,String defaultMessage) {
        super("seamless", defaultMessage);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
