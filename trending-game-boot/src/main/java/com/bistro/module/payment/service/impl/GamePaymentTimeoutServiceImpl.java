package com.bistro.module.payment.service.impl;

import com.bistro.common.utils.DateUtils;
import com.bistro.module.payment.domain.GamePaymentTimeout;
import com.bistro.module.payment.mapper.GamePaymentTimeoutMapper;
import com.bistro.module.payment.service.IGamePaymentTimeoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 超时支付Service业务层处理
 * 
 * @author jason.lin
 * @date 2022-03-18
 */
@Service
public class GamePaymentTimeoutServiceImpl implements IGamePaymentTimeoutService 
{
    @Autowired
    private GamePaymentTimeoutMapper gamePaymentTimeoutMapper;

    /**
     * 查询超时支付
     * 
     * @param id 超时支付主键
     * @return 超时支付
     */
    @Override
    public GamePaymentTimeout selectGamePaymentTimeoutById(Long id)
    {
        return gamePaymentTimeoutMapper.selectGamePaymentTimeoutById(id);
    }

    /**
     * 查询超时支付列表
     * 
     * @param gamePaymentTimeout 超时支付
     * @return 超时支付
     */
    @Override
    public List<GamePaymentTimeout> selectGamePaymentTimeoutList(GamePaymentTimeout gamePaymentTimeout)
    {
        return gamePaymentTimeoutMapper.selectGamePaymentTimeoutList(gamePaymentTimeout);
    }

    /**
     * 新增超时支付
     * 
     * @param gamePaymentTimeout 超时支付
     * @return 结果
     */
    @Override
    public int insertGamePaymentTimeout(GamePaymentTimeout gamePaymentTimeout)
    {
        gamePaymentTimeout.setCreateTime(DateUtils.getNowDate());
        return gamePaymentTimeoutMapper.insertGamePaymentTimeout(gamePaymentTimeout);
    }

    /**
     * 修改超时支付
     * 
     * @param gamePaymentTimeout 超时支付
     * @return 结果
     */
    @Override
    public int updateGamePaymentTimeout(GamePaymentTimeout gamePaymentTimeout)
    {
        gamePaymentTimeout.setUpdateTime(DateUtils.getNowDate());
        return gamePaymentTimeoutMapper.updateGamePaymentTimeout(gamePaymentTimeout);
    }

    /**
     * 批量删除超时支付
     * 
     * @param ids 需要删除的超时支付主键
     * @return 结果
     */
    @Override
    public int deleteGamePaymentTimeoutByIds(Long[] ids)
    {
        return gamePaymentTimeoutMapper.deleteGamePaymentTimeoutByIds(ids);
    }

    /**
     * 删除超时支付信息
     * 
     * @param id 超时支付主键
     * @return 结果
     */
    @Override
    public int deleteGamePaymentTimeoutById(Long id)
    {
        return gamePaymentTimeoutMapper.deleteGamePaymentTimeoutById(id);
    }

    @Override
    public int batchInsert(List<GamePaymentTimeout> list) {
        return gamePaymentTimeoutMapper.batchInsert(list);
    }
}
