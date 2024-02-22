package com.bistro;

import feign.Logger;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.ObjectFactory;
import feign.codec.Decoder;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocket
public class AppConfig {
    @Bean
    public Decoder feignDecoder(){
        ObjectFactory<HttpMessageConverters> messageConverters = new ObjectFactory<HttpMessageConverters>() {
            @Override
            public HttpMessageConverters getObject() throws BeansException {
                return new HttpMessageConverters(new MappingJackson2HttpMessageConverter(){
                    @Override
                    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
                        List<MediaType> myType = new ArrayList<>(supportedMediaTypes);
                        myType.add(MediaType.TEXT_PLAIN);
                        super.setSupportedMediaTypes(myType);
                    }
                });
            }
        };
        return new SpringDecoder(messageConverters);
    }
    @Bean
    Logger.Level feignLoggerLeave(){
        return Logger.Level.FULL;
    }


    @Bean
    @ConditionalOnProperty(value = "websocket.enable",havingValue = "true")
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }

}
