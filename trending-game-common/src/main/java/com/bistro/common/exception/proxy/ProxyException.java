package com.bistro.common.exception.proxy;

import com.bistro.common.exception.base.BaseException;

public class ProxyException extends BaseException {
    public ProxyException( String defaultMessage) {
        super("proxy", null, null, defaultMessage);
    }
}
