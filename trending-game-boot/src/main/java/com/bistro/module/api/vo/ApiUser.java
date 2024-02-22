package com.bistro.module.api.vo;

public class ApiUser {
    private Long uid;
    private String lang;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public ApiUser(Long uid, String lang){
        this.uid = uid;
        this.lang = lang;
    }
}
