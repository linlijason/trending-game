package com.bistro.module.payment.service.impl;

import java.util.List;
import com.bistro.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bistro.module.payment.mapper.GamePaymentInfoMapper;
import com.bistro.module.payment.domain.GamePaymentInfo;
import com.bistro.module.payment.service.IGamePaymentInfoService;

/**
 * 支付信息Service业务层处理
 * 
 * @author gavin
 * @date 2021-11-17
 */
@Service
public class GamePaymentInfoServiceImpl implements IGamePaymentInfoService 
{
    @Autowired
    private GamePaymentInfoMapper gamePaymentInfoMapper;

    /**
     * 查询支付信息
     * 
     * @param id 支付信息主键
     * @return 支付信息
     */
    @Override
    public GamePaymentInfo selectGamePaymentInfoById(Long id)
    {
        return gamePaymentInfoMapper.selectGamePaymentInfoById(id);
    }

    /**
     * 查询支付信息列表
     * 
     * @param gamePaymentInfo 支付信息
     * @return 支付信息
     */
    @Override
    public List<GamePaymentInfo> selectGamePaymentInfoList(GamePaymentInfo gamePaymentInfo)
    {
        return gamePaymentInfoMapper.selectGamePaymentInfoList(gamePaymentInfo);
    }

    /**
     * 新增支付信息
     * 
     * @param gamePaymentInfo 支付信息
     * @return 结果
     */
    @Override
    public int insertGamePaymentInfo(GamePaymentInfo gamePaymentInfo)
    {
        gamePaymentInfo.setCreateTime(DateUtils.getNowDate());
        return gamePaymentInfoMapper.insertGamePaymentInfo(gamePaymentInfo);
    }

    /**
     * 修改支付信息
     * 
     * @param gamePaymentInfo 支付信息
     * @return 结果
     */
    @Override
    public int updateGamePaymentInfo(GamePaymentInfo gamePaymentInfo)
    {
        gamePaymentInfo.setUpdateTime(DateUtils.getNowDate());
        return gamePaymentInfoMapper.updateGamePaymentInfo(gamePaymentInfo);
    }

    /**
     * 批量删除支付信息
     * 
     * @param ids 需要删除的支付信息主键
     * @return 结果
     */
    @Override
    public int deleteGamePaymentInfoByIds(Long[] ids)
    {
        return gamePaymentInfoMapper.deleteGamePaymentInfoByIds(ids);
    }

    /**
     * 删除支付信息信息
     * 
     * @param id 支付信息主键
     * @return 结果
     */
    @Override
    public int deleteGamePaymentInfoById(Long id)
    {
        return gamePaymentInfoMapper.deleteGamePaymentInfoById(id);
    }
}
