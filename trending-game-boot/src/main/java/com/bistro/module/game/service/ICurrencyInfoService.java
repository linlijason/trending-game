package com.bistro.module.game.service;

import java.util.List;
import com.bistro.module.game.domain.CurrencyInfo;

/**
 * 货币配置Service接口
 * 
 * @author jason.lin
 * @date 2021-12-06
 */
public interface ICurrencyInfoService 
{
    /**
     * 查询货币配置
     * 
     * @param id 货币配置主键
     * @return 货币配置
     */
     CurrencyInfo selectCurrencyInfoById(Integer id);

    /**
     * 查询货币配置列表
     * 
     * @param currencyInfo 货币配置
     * @return 货币配置集合
     */
    List<CurrencyInfo> selectCurrencyInfoList(CurrencyInfo currencyInfo);

    /**
     * 新增货币配置
     * 
     * @param currencyInfo 货币配置
     * @return 结果
     */
    int insertCurrencyInfo(CurrencyInfo currencyInfo);

    /**
     * 修改货币配置
     * 
     * @param currencyInfo 货币配置
     * @return 结果
     */
    int updateCurrencyInfo(CurrencyInfo currencyInfo);

    /**
     * 批量删除货币配置
     * 
     * @param ids 需要删除的货币配置主键集合
     * @return 结果
     */
    int deleteCurrencyInfoByIds(Integer[] ids);

    /**
     * 删除货币配置信息
     * 
     * @param id 货币配置主键
     * @return 结果
     */
    int deleteCurrencyInfoById(Integer id);
}
