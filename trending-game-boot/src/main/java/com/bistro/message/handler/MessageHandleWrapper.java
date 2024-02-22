package com.bistro.message.handler;

import com.bistro.message.model.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageHandleWrapper {
    @Autowired
    private List<MessageHandle> handles;
    private Map<MessageHandle, Type> typeMap=new HashMap<>();

    public List<MessageHandle> getHandles() {
        return handles;
    }

    @PostConstruct
    public void init(){
        for (MessageHandle handle : handles) {
            Type bodyType = ((ParameterizedTypeImpl) handle.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
            typeMap.put(handle,bodyType);
        }

    }

    public Type getBodyType(MessageHandle handle){
        return typeMap.get(handle);
    }
}
