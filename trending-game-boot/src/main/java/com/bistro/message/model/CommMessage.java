package com.bistro.message.model;


import com.alibaba.fastjson.JSONObject;

public class CommMessage {
    private MessageType type;
    private  String lan;
    private JSONObject body;


    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public JSONObject getBody() {
        return body;
    }

    public void setBody(JSONObject body) {
        this.body = body;
    }

    public static CommMessage create(MessageType type,Object o){
        CommMessage cm=new CommMessage();
        cm.setBody((JSONObject)JSONObject.toJSON(o));
        cm.setType(type);
        return cm;
    }

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }
}
