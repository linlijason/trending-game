package com.bistro.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class SignUtils {

    public static String concatSignString(Object data,String signKey) {
        Map paramterMap = null;

        if(data instanceof Map){
            paramterMap=(Map) data;
        }else{
            paramterMap=(Map) JSON.toJSON(data);
        }


        Set<String> keySet = paramterMap.keySet();
        TreeSet<String> keyArray =new TreeSet<>(keySet);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            Object value = paramterMap.get(k);
            if (k.equals("sign") ||  value==null || value instanceof Map || value instanceof List
                    || value.toString().trim().length() == 0) {
                continue;
            }
            sb.append(value.toString().trim());
        }
        sb.append(signKey);
        return sb.toString();
    }

    public static String sha1Sign(String origin ){
        return DigestUtils.sha1Hex(origin.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        JSONObject  parm = JSONObject.parseObject("{\"amount\":2000000,\"bet_id\":4437,\"currency\":\"BRL\",\"game_code\":\"60\",\"merchant_code\":\"bistro20211123\",\"number\":\"\",\"round_id\":\"\",\"sign\":\"5ab4d9ec2b62b86cce93256982d3a690cf56934b\",\"timestamp\":1639470616,\"unique_id\":\"ebf3bec0a1574879b08c954e7efe3c4d\",\"username\":\"smbstgtestbistro2\"}");
        parm.put("list", Arrays.asList("1-","2-"));
        String concat = concatSignString(parm,"kvUjqtzXVHqk");
        String sign = sha1Sign(concat);

        System.out.println("concated string:"+concat);
        System.out.println("sign:"+sign);
        System.out.println("------------------------------------");
        JSONObject  parm1 = JSONObject.parseObject("{\"amount\":2000000.00,\"bet_id\":4437,\"currency\":\"BRL\",\"game_code\":\"60\",\"merchant_code\":\"bistro20211123\",\"number\":\"\",\"round_id\":\"\",\"sign\":\"5ab4d9ec2b62b86cce93256982d3a690cf56934b\",\"timestamp\":1639470616,\"unique_id\":\"ebf3bec0a1574879b08c954e7efe3c4d\",\"username\":\"smbstgtestbistro2\"}");
        String concat1 = concatSignString(parm1,"kvUjqtzXVHqk");
        String sign1 = sha1Sign(concat1);

        System.out.println("concated string:"+concat1);
        System.out.println("sign:"+sign1);

    }

}
