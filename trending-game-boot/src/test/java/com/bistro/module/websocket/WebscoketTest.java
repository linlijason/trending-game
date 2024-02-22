package com.bistro.module.websocket;

import okhttp3.*;
import okio.ByteString;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class WebscoketTest {

    @Test
    public void test_1000_client() throws Exception {
       List<String> lines=IOUtils.readLines( getClass().getClassLoader()
                .getResourceAsStream("clients.txt"), StandardCharsets.UTF_8);
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(1000);
        dispatcher.setMaxRequestsPerHost(1000);
        OkHttpClient client = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(10,60,TimeUnit.SECONDS))
                .build();
        List<WebSocket> wsList=new ArrayList<>();
        AtomicInteger cnt=new AtomicInteger();
        int i=0;
        for (String s1 : lines) {
//            String[] clients=s1.split("\\?sid=");
//            System.out.println(clients[0]+" "+clients[1]);
//            FileUtils.write(new File("/Users/linli/Downloads/clients.csv"),
//                    clients[0]+","+clients[1]+"\n",true);
            Request request =new  Request.Builder()
                    .url("ws://47.108.189.93:8021/ws/"+s1)
                    .build();
           client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    super.onOpen(webSocket, response);
                   // System.out.println("onOpen");
                    cnt.incrementAndGet();
                    wsList.add(webSocket);
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    super.onMessage(webSocket, text);
                   // System.out.println("onMessage"+text);
                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    super.onMessage(webSocket, bytes);
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    super.onClosing(webSocket, code, reason);
                    System.out.println("onClosing:"+code+reason);

                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    super.onClosed(webSocket, code, reason);
                    System.out.println("onclosed:"+code+reason);
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    super.onFailure(webSocket, t, response);
                    System.out.println("onFailure:"+t.getMessage());
                }
            });
           i++;
           if(i%30==0){
               Thread.sleep(3000);
           }
           if(i==50){
               break;
           }


        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(1000*20L);
                        System.out.println(cnt.get());
                        for (WebSocket webSocket : wsList) {
                            if (webSocket.send("{\"type\":\"HB\",\"body\":{\"ping\":\"ping\"}}")) ;
                        }
                    }
                }catch (Exception e){

                }
            }
        }).start();
       Thread.sleep(1000000000L);
    }
}
