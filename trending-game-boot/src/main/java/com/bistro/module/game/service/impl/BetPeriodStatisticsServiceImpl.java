package com.bistro.module.game.service.impl;

import com.bistro.common.utils.StringUtils;
import com.bistro.module.game.domain.BetPeriodStatisticsQuery;
import com.bistro.module.game.domain.BetPeriodStatisticsVo;
import com.bistro.module.game.mapper.GameBetMapper;
import com.bistro.module.game.service.IBetPeriodStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BetPeriodStatisticsServiceImpl implements IBetPeriodStatisticsService {

    @Autowired
    GameBetMapper gameBetMapper;


    @Override
    public List<BetPeriodStatisticsVo> search(BetPeriodStatisticsQuery query) {
        List<BetPeriodStatisticsVo> list = gameBetMapper.selectBetPeriodStatistics(query);
        if (list == null || list.isEmpty()) {
            return initList();
        } else {
            List<BetPeriodStatisticsVo> result = new ArrayList<>();
            int betCountSum = 0;
            int payoutCountSum = 0;
            BigDecimal betAmountTotalSum = BigDecimal.ZERO;
            BigDecimal payoutAmountTotalSum = BigDecimal.ZERO;
            BigDecimal grossSum = BigDecimal.ZERO;
            for (int i = 0; i < 24; i++) {
                String period = StringUtils.leftPad(String.valueOf(i), 2, "0") + "时";
                boolean periodExist = false;
                for (BetPeriodStatisticsVo item : list) {
                    if (item.getPeriod().equals(period)) {
                        periodExist = true;
                        item.setGross(item.getBetAmountTotal().subtract(item.getPayoutAmountTotal()));
                        if (item.getBetAmountTotal().compareTo(BigDecimal.ZERO) <= 0) {
                            item.setGrossProfit(BigDecimal.ZERO);
                        } else {
                            item.setGrossProfit(item.getGross().divide(item.getBetAmountTotal(), 4, BigDecimal.ROUND_DOWN));
                        }

                        result.add(item);
                        betCountSum = betCountSum + item.getBetCount();
                        payoutCountSum = payoutCountSum + item.getPayoutCount();
                        betAmountTotalSum = betAmountTotalSum.add(item.getBetAmountTotal());
                        payoutAmountTotalSum = payoutAmountTotalSum.add(item.getPayoutAmountTotal());
                        grossSum = grossSum.add(item.getGross());
                        break;
                    }
                }
                if (!periodExist) {
                    result.add(initItem(period));
                }
            }
            BetPeriodStatisticsVo sum = new BetPeriodStatisticsVo();
            sum.setPeriod("合计");
            sum.setPayoutAmountTotal(payoutAmountTotalSum);
            sum.setPayoutCount(payoutCountSum);
            sum.setBetAmountTotal(betAmountTotalSum);
            sum.setBetCount(betCountSum);
            sum.setGross(grossSum);
            if (sum.getBetAmountTotal().compareTo(BigDecimal.ZERO) <= 0) {
                sum.setGrossProfit(BigDecimal.ZERO);
            } else {
                sum.setGrossProfit(sum.getGross().divide(sum.getBetAmountTotal(), 4, BigDecimal.ROUND_DOWN));
            }

            result.add(sum);
            return result;
        }
    }

    private List<BetPeriodStatisticsVo> initList() {
        List<BetPeriodStatisticsVo> list = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            BetPeriodStatisticsVo vo = new BetPeriodStatisticsVo();
            String period = null;
            if (i == 24) {//合计
                period = "合计";
            } else {
                period = StringUtils.leftPad(String.valueOf(i), 2, "0") + "时";
            }
            vo.setPeriod(period);
            vo.setBetCount(0);
            vo.setBetAmountTotal(BigDecimal.ZERO);
            vo.setPayoutCount(0);
            vo.setPayoutAmountTotal(BigDecimal.ZERO);
            vo.setGross(BigDecimal.ZERO);
            vo.setGrossProfit(BigDecimal.ZERO);
            list.add(vo);
        }
        return list;
    }

    private BetPeriodStatisticsVo initItem(String period) {
        BetPeriodStatisticsVo vo = new BetPeriodStatisticsVo();
        vo.setPeriod(period);
        vo.setBetCount(0);
        vo.setBetAmountTotal(BigDecimal.ZERO);
        vo.setPayoutCount(0);
        vo.setPayoutAmountTotal(BigDecimal.ZERO);
        vo.setGross(BigDecimal.ZERO);
        vo.setGrossProfit(BigDecimal.ZERO);
        return vo;
    }

    @Override
    public List<BetPeriodStatisticsVo> summary(BetPeriodStatisticsQuery query) {
        BetPeriodStatisticsVo betPeriodStatisticsVo = gameBetMapper.selectBetSummaryStatistics(query);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String period = dateTimeFormatter.format(query.getBeginCreateTime()) + " - " + dateTimeFormatter.format(query.getEndCreateTime());
        if (betPeriodStatisticsVo == null || betPeriodStatisticsVo.getBetCount() == 0) {
            betPeriodStatisticsVo = initItem(period);
        } else {
            betPeriodStatisticsVo.setPeriod(period);
            betPeriodStatisticsVo.setGross(betPeriodStatisticsVo.getBetAmountTotal().subtract(betPeriodStatisticsVo.getPayoutAmountTotal()));
            if (betPeriodStatisticsVo.getBetAmountTotal().compareTo(BigDecimal.ZERO) <= 0) {
                betPeriodStatisticsVo.setGrossProfit(BigDecimal.ZERO);
            } else {
                betPeriodStatisticsVo.setGrossProfit(betPeriodStatisticsVo.getGross().divide(betPeriodStatisticsVo.getBetAmountTotal(), 4, BigDecimal.ROUND_DOWN));
            }
        }
        return Arrays.asList(betPeriodStatisticsVo);
    }
}
