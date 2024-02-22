package com.bistro.module.game.service.impl;

import com.bistro.common.utils.DateUtils;
import com.bistro.module.game.domain.CurrencyInfo;
import com.bistro.module.game.mapper.CurrencyInfoMapper;
import com.bistro.module.game.service.ICurrencyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 货币配置Service业务层处理
 * 
 * @author jason.lin
 * @date 2021-12-06
 */
@Service
public class CurrencyInfoServiceImpl implements ICurrencyInfoService 
{
    @Autowired
    private CurrencyInfoMapper currencyInfoMapper;

    /**
     * 查询货币配置
     * 
     * @param id 货币配置主键
     * @return 货币配置
     */
    @Override
    public CurrencyInfo selectCurrencyInfoById(Integer id)
    {
        return currencyInfoMapper.selectCurrencyInfoById(id);
    }

    /**
     * 查询货币配置列表
     * 
     * @param currencyInfo 货币配置
     * @return 货币配置
     */
    @Override
    public List<CurrencyInfo> selectCurrencyInfoList(CurrencyInfo currencyInfo)
    {
        return currencyInfoMapper.selectCurrencyInfoList(currencyInfo);
    }

    /**
     * 新增货币配置
     * 
     * @param currencyInfo 货币配置
     * @return 结果
     */
    @Override
    public int insertCurrencyInfo(CurrencyInfo currencyInfo)
    {
        currencyInfo.setCreateTime(DateUtils.getNowDate());
        currencyInfo.setUpdateTime(currencyInfo.getCreateTime());
        return currencyInfoMapper.insertCurrencyInfo(currencyInfo);
    }

    /**
     * 修改货币配置
     * 
     * @param currencyInfo 货币配置
     * @return 结果
     */
    @Override
    public int updateCurrencyInfo(CurrencyInfo currencyInfo)
    {
        currencyInfo.setUpdateTime(DateUtils.getNowDate());
        return currencyInfoMapper.updateCurrencyInfo(currencyInfo);
    }

    /**
     * 批量删除货币配置
     * 
     * @param ids 需要删除的货币配置主键
     * @return 结果
     */
    @Override
    public int deleteCurrencyInfoByIds(Integer[] ids)
    {
        return currencyInfoMapper.deleteCurrencyInfoByIds(ids);
    }

    /**
     * 删除货币配置信息
     * 
     * @param id 货币配置主键
     * @return 结果
     */
    @Override
    public int deleteCurrencyInfoById(Integer id)
    {
        return currencyInfoMapper.deleteCurrencyInfoById(id);
    }
}
