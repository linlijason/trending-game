package com.bistro.module.game.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bistro.common.annotation.Excel;
import com.bistro.common.core.domain.BaseEntity;
import com.bistro.common.utils.spring.SpringUtils;
import com.bistro.module.api.vo.GameInfo;
import com.bistro.utils.MultiplierUtils;
import com.bistro.utils.OddsUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 游戏基本信息对象 game_base_info
 *
 * @author gavin
 * @date 2021-11-17
 */
public class GameBaseInfo extends BaseEntity {
    public static final String COIN_GUESS_CODE = "61";
    public static final String MIMES_CODE = "60";
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * game code
     */
    @Excel(name = "game code")
    private String code;

    /**
     * game name
     */
    @Excel(name = "game name")
    private String name;

    /**
     * 最小下注金额
     */
    @Excel(name = "最小下注金额")
    private BigDecimal minBetAmount;

    /**
     * 最大下注金额
     */
    @Excel(name = "最大下注金额")
    private BigDecimal maxBetAmount;

    /**
     * 最大奖励金额
     */
    @Excel(name = "最大奖励金额")
    private BigDecimal maxPayoutAmount;

    /**
     * 抽成百分比
     */
    @Excel(name = "抽成百分比")
    private BigDecimal commissionRate;

    /**
     * 是否开放 1 -开放 0 关闭
     */
    @Excel(name = "是否开放 1 -开放 0 关闭")
    private Integer isOpen;

    private String extConfig;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMinBetAmount(BigDecimal minBetAmount) {
        this.minBetAmount = minBetAmount;
    }

    public BigDecimal getMinBetAmount() {
        return minBetAmount;
    }

    public void setMaxBetAmount(BigDecimal maxBetAmount) {
        this.maxBetAmount = maxBetAmount;
    }

    public BigDecimal getMaxBetAmount() {
        return maxBetAmount;
    }

    public void setMaxPayoutAmount(BigDecimal maxPayoutAmount) {
        this.maxPayoutAmount = maxPayoutAmount;
    }

    public BigDecimal getMaxPayoutAmount() {
        return maxPayoutAmount;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
    }

    public Integer getIsOpen() {
        return isOpen;
    }

    public String getExtConfig() {
        return extConfig;
    }

    public void setExtConfig(String extConfig) {
        this.extConfig = extConfig;
    }

    public List<CoinGuessOptionConfig> getCoinGuessOptions() {
        if (COIN_GUESS_CODE.equals(code)) {
            if (StringUtils.isEmpty(extConfig)) {
                //填充默认值
                return CoinGuessOption.names().stream().map(s -> {
                    CoinGuessOptionConfig c = new CoinGuessOptionConfig();
                    CoinGuessOption cgp = CoinGuessOption.valueOf(s);
                    c.setOption(cgp);
                    c.setOriginOdds(cgp.originOdds());
                    c.setOdds(OddsUtils.calcOdds(c.getOriginOdds(),
                            commissionRate));
                    return c;
                }).collect(Collectors.toList()).stream()
                        .filter((i -> !(i.getOption().equals(CoinGuessOption.Big) || i.getOption().equals(CoinGuessOption.Small))))
                        .collect(Collectors.toList());
            } else {
                return SpringUtils.toObject(extConfig,
                        new TypeReference<List<CoinGuessOptionConfig>>() {
                        }).stream().filter((i -> !(i.getOption().equals(CoinGuessOption.Big) || i.getOption().equals(CoinGuessOption.Small))))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    public List<GameInfo.MultiplierConfigItem> getMimesConfigs() {
        if (MIMES_CODE.equals(code)) {
            if (StringUtils.isEmpty(extConfig)) {
                return MultiplierUtils.getAllMinesMultipliers(commissionRate);
            } else {
                Map<String, Map<String, BigDecimal>> map = new HashMap<>();
                JSONObject json = JSON.parseObject(extConfig);
                for (String mineCount : json.keySet()) {
                    JSONObject hits = json.getJSONObject(mineCount);
                    Map<String, BigDecimal> hitMap = new HashMap<>();
                    for (String hitKey : hits.keySet()) {
                        hitMap.put(hitKey, hits.getBigDecimal(hitKey));
                    }
                    map.put(mineCount, hitMap);
                }

                return MultiplierUtils.getAllMinesMultipliers(commissionRate, map);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("code", getCode())
                .append("name", getName())
                .append("minBetAmount", getMinBetAmount())
                .append("maxBetAmount", getMaxBetAmount())
                .append("maxPayoutAmount", getMaxPayoutAmount())
                .append("commissionRate", getCommissionRate())
                .append("isOpen", getIsOpen())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
