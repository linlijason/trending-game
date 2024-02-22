package com.bistro.utils;

import com.bistro.constants.Constants;
import com.bistro.module.api.vo.GameInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultiplierUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiplierUtils.class);

    /**
     * 计算赔率
     *
     * @param totalBlocks    总的数量块 25
     * @param mines          选择雷的数量 25
     * @param hits           //在雷数量选择一定情况下命中砖石的数量
     * @param commissionRate
     * @return
     */
    public static BigDecimal calculate(int totalBlocks, int mines, int hits, BigDecimal commissionRate) {
        if (mines <= 0 || mines >= totalBlocks ||
                totalBlocks <= 0 || hits <= 0 || hits > totalBlocks - mines) {
            return BigDecimal.ZERO;
        }
        BigDecimal result = BigDecimal.ZERO;
        int divisorStart = totalBlocks - mines;
        for (int i = 1; i <= hits; i++) {
            if (i == 1) {
                result = BigDecimal.valueOf(totalBlocks--)
                        .divide(BigDecimal.valueOf(divisorStart--), 4, BigDecimal.ROUND_DOWN);
            } else {
                result = result.multiply(BigDecimal.valueOf(totalBlocks--)
                        .divide(BigDecimal.valueOf(divisorStart--), 4, BigDecimal.ROUND_DOWN));
            }
        }
        return result.subtract(BigDecimal.ONE)
                .multiply(BigDecimal.ONE.subtract(commissionRate))
                .add(BigDecimal.ONE)
                .setScale(2, BigDecimal.ROUND_DOWN);
    }

    /**
     * 选择雷数量之后的赔率列表
     *
     * @param bombsCount     选择雷的数量
     * @param commissionRate 抽水比例
     * @return
     */

    public static List<GameInfo.MultiplierHits> getMultiplierList(int bombsCount, BigDecimal commissionRate) {
        List<GameInfo.MultiplierHits> hits = new ArrayList<>();
        for (int j = 1; j <= Constants.MINES_BLOCKS - bombsCount; j++) {//钻石数量
            GameInfo.MultiplierHits hit = new GameInfo.MultiplierHits();
            hit.setHits(j);
            hit.setMultiplier(MultiplierUtils.calculate(Constants.MINES_BLOCKS, bombsCount, j, commissionRate));
            hits.add(hit);
        }
        return hits;
    }

    /**
     * 选择雷数量之后的赔率列表
     *
     * @param bombsCount 选择雷的数量
     * @param config
     * @return
     */

    public static List<GameInfo.MultiplierHits> getMultiplierList(int bombsCount, List<GameInfo.MultiplierConfigItem> config) {
        return config.stream().filter(i -> i.getMinesCount() == bombsCount).findFirst().get().getMultiplierList();
    }

    /**
     * 选择雷数量之后的赔率列表 map形式
     *
     * @param bombsCount
     * @param commissionRate
     * @return
     */
    public static Map<String, String> getMultiplierMap(int bombsCount, BigDecimal commissionRate) {
        Map<String, String> map = new LinkedHashMap<>();
        List<GameInfo.MultiplierHits> list = getMultiplierList(bombsCount, commissionRate);
        for (GameInfo.MultiplierHits item : list) {
            map.put(String.valueOf(item.getHits()), item.getMultiplier().stripTrailingZeros().toPlainString());
        }
        return map;
    }

    /**
     * 选择雷数量之后的赔率列表 map形式
     *
     * @param bombsCount
     * @param config
     * @return
     */
    public static Map<String, String> getMultiplierMap(int bombsCount, List<GameInfo.MultiplierConfigItem> config) {
        Map<String, String> map = new LinkedHashMap<>();
        List<GameInfo.MultiplierHits> list = config.stream().filter(i -> i.getMinesCount() == bombsCount).findFirst().get().getMultiplierList();
        for (GameInfo.MultiplierHits item : list) {
            map.put(String.valueOf(item.getHits()), item.getMultiplier().stripTrailingZeros().toPlainString());
        }
        return map;
    }

    /**
     * 计算payout
     *
     * @param multiplier
     * @param betAmount
     * @param maxPayoutAmount
     * @return
     */
    public static BigDecimal calPayout(BigDecimal multiplier, BigDecimal betAmount, BigDecimal maxPayoutAmount) {
        BigDecimal payoutAmount = betAmount.multiply(multiplier)
                .setScale(Constants.PAYOUT_AMOUNT_DECIMAL_SCALE, BigDecimal.ROUND_DOWN)
                .stripTrailingZeros();
        if (payoutAmount.compareTo(maxPayoutAmount) > 0) {
            LOGGER.info("payoutAmount:{},maxPayoutAmount:{}", payoutAmount, maxPayoutAmount);
            payoutAmount = maxPayoutAmount;
        }
        return payoutAmount;
    }


    public static List<GameInfo.MultiplierConfigItem> getAllMinesMultipliers(BigDecimal commissionRate) {
        return getAllMinesMultipliers(commissionRate, Map.of());
    }

    public static List<GameInfo.MultiplierConfigItem> getAllMinesMultipliers(BigDecimal commissionRate,
                                                                             Map<String, Map<String, BigDecimal>> dbConfigs) {

        List<GameInfo.MultiplierConfigItem> items = new ArrayList<>();

        for (int i = Constants.SELECT_MINES_BLOCKS_MIN; i < Constants.MINES_BLOCKS; i++) { //雷的数量
            GameInfo.MultiplierConfigItem item = new GameInfo.MultiplierConfigItem();
            item.setMinesCount(i);
            item.setMultiplierList(MultiplierUtils.getMultiplierList(i, commissionRate));
            for (GameInfo.MultiplierHits mh : item.getMultiplierList()) {
                mh.setDefaultMultiplier(mh.getMultiplier());
                String lei = i + "";
                if (dbConfigs.containsKey(lei)) {
                    String hit = mh.getHits() + "";
                    mh.setMultiplier2(dbConfigs.get(lei).get(hit));
                }
            }
            items.add(item);

        }
        return items;
    }


    public static void main(String[] args) {
        System.out.println(calculate(25, 2, 2, BigDecimal.valueOf(0.05)));
    }
}
