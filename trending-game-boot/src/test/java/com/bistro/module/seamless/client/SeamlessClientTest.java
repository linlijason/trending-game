package com.bistro.module.seamless.client;


import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class SeamlessClientTest {
    @Autowired
    private SeamlessClient client;

    @Test
    public void payInfo(){


        /*

        {
    "token": "N2lXVi80ZW5IL3FaM0l5b1RLc1FwUT09",
    "unique_id": "e9d63a4a-1757-43e7-96a7-46519dc4f7be",
    "merchant_code": "bistro20211123",
    "timestamp": 1626659709,
    "sign": "b719eae13c2500124b3d2ef0640e4ccb85655d92"
}
         */
        PlayerInfoRequest request=new PlayerInfoRequest();
        request.setToken("N2lXVi80ZW5IL3FaM0l5b1RLc1FwUT09");
        request.setUniqueId("e9d63a4a-1757-43e7-96a7-46519dc4f7be");
        request.setMerchantCode("bistro20211123");
        request.setTimestamp(1626659709);
        request.setSign("b719eae13c2500124b3d2ef0640e4ccb85655d92");
        request=JSONObject.parseObject("{\"merchant_code\":\"bistro20211123\",\"sign\":\"6d6fe210926b6f2e2a9bcd32109ca43cab55b064\",\"timestamp\":1641019897,\"token\":\"bERoWHQrZUJDWThwbHlLdFhPZ2JPUT09\",\"unique_id\":\"524fd94209c3414b837fd026eec0675c\"}",
                PlayerInfoRequest.class
        );
      // request = JSONObject.parseObject("{\"merchant_code\":\"bistro20211123\",\"sign\":\"d307730a5e9535a7c3ff3856b90bb05aab23a8cd\",\"timestamp\":1640339443,\"token\":\"K0h0N2ZjL0NhUjZkRjV1QnEzQkpSZz09\",\"unique_id\":\"70b62380d76b4ce7abbc3f0042e1a22a\"}",PlayerInfoRequest.class);
        client.playerInfo(request);
    }

}