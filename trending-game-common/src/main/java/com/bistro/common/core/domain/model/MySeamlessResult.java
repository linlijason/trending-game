package com.bistro.common.core.domain.model;


import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashMap;

public class MySeamlessResult extends HashMap<String, Object> {


    /** 错误码 */
    public static final String CODE_TAG = "code";

    /** 返回内容 */
    public static final String MSG_TAG = "message";

    /** 数据对象 */
    public static final String DATA_TAG = "detail";

    /** 数据对象 */
    public static final String SUCCESS_TAG = "success";

    public MySeamlessResult(){

    }


    public MySeamlessResult(boolean success, int code, String message, Object detail) {


        super.put(CODE_TAG, code);
        super.put(MSG_TAG, message);
        super.put(DATA_TAG, detail);
        super.put(SUCCESS_TAG, success);
    }
    @JSONField(serialize = false)
    public int getHttpCode() {
        int code = (int) get(CODE_TAG);
            if(code==500){
                return 500;
            }
        return 200;
    }

    public static MySeamlessResult success(Object detail) {
        return success(0, "", detail);
    }

    public static MySeamlessResult success(int code, String message, Object detail) {
        return new MySeamlessResult(true, code, message, detail);
    }


    public static MySeamlessResult error(int code, String message) {
        return new MySeamlessResult(false, code, message, null);
    }


}
