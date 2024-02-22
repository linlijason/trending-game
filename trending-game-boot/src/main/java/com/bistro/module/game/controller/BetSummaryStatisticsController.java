package com.bistro.module.game.controller;

import com.bistro.common.core.controller.BaseController;
import com.bistro.module.game.domain.BetPeriodStatisticsQuery;
import com.bistro.module.game.domain.BetPeriodStatisticsVo;
import com.bistro.module.game.service.IBetPeriodStatisticsService;
import com.bistro.module.game.service.IGameBaseInfoService;
import com.bistro.module.merchant.service.IMerchantInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bet_summary_statistics")
public class BetSummaryStatisticsController extends BaseController {

    @Autowired
    IGameBaseInfoService gameBaseInfoService;

    @Autowired
    IMerchantInfoService merchantInfoService;

    @Autowired
    IBetPeriodStatisticsService betPeriodStatisticsService;


    @PreAuthorize("@ss.hasPermi('game:summarystatistics:list')")
    @GetMapping("/list")
    public List<BetPeriodStatisticsVo> list(BetPeriodStatisticsQuery query) {
        List<BetPeriodStatisticsVo> list = betPeriodStatisticsService.summary(query);
        return list;
    }

}
