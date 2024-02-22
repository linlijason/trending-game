package com.bistro.module.seamless.client;

import com.alibaba.fastjson.JSONObject;
import com.bistro.common.exception.api.ApiException;
import com.bistro.common.exception.api.ApiExceptionMsgEnum;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class FeignErrorDecoder implements ErrorDecoder {

    private static final Logger log = LoggerFactory.getLogger(FeignErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        ApiException baseException = null;
        String errorContent = null;
        int status = response.status();
        String reason = response.reason();
        try {
            errorContent = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
            SeamlessResult result = JSONObject.parseObject(errorContent, SeamlessResult.class);
            baseException = new ApiException(ApiExceptionMsgEnum.COM_SEAMLESS_ERROR.getCode(), result.getErrMsg());
            log.error("处理FeignClient 异常错误:{},{},{},{}", methodKey, status, reason, errorContent);
        } catch (Exception e) {
            log.error("处理FeignClient 异常错误:{},{},{},{}", methodKey, status, reason, errorContent, e);
            return new ApiException(ApiExceptionMsgEnum.COM_UNKNOWN_ERROR.getCode(), errorContent);
        }
        return baseException;
    }
}
