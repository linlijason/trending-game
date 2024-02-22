package com.bistro.module.seamless.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "seamlessClient", url = "${urls.seamlessUrl}", configuration = {FeignErrorDecoder.class})
public interface SeamlessClient {

    @PostMapping(value = "/player_info")
    SeamlessResult playerInfo(PlayerInfoRequest request);

    @PostMapping("/bet")
    SeamlessResult bet(BetRequest request);

    @PostMapping("/payout")
    SeamlessResult payout(PayoutRequest request);

    @PostMapping("/refund")
    SeamlessResult refund(RefundRequest request);


    @PostMapping("/batch_payout")
    SeamlessBatchPayoutResult batchPayout(BatchPayoutRequest request);

}
