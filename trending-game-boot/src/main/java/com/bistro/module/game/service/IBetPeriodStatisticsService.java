package com.bistro.module.game.service;

import com.bistro.module.game.domain.BetPeriodStatisticsQuery;
import com.bistro.module.game.domain.BetPeriodStatisticsVo;

import java.util.List;

public interface IBetPeriodStatisticsService {

    List<BetPeriodStatisticsVo> search(BetPeriodStatisticsQuery query);

    List<BetPeriodStatisticsVo> summary(BetPeriodStatisticsQuery query);


}
