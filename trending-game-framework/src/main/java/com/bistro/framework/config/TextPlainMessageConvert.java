package com.bistro.framework.config;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

public class TextPlainMessageConvert  extends MappingJackson2HttpMessageConverter {
    public TextPlainMessageConvert(){
        List<MediaType> list=new ArrayList<>();
        list.add(MediaType.IMAGE_GIF);
        setSupportedMediaTypes(list);
    }
}
