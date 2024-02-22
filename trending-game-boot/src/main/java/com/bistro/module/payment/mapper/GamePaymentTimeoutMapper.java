package com.bistro.module.payment.mapper;

import com.bistro.module.payment.domain.GamePaymentTimeout;

import java.util.List;

/**
 * 超时支付Mapper接口
 * 
 * @author jason.lin
 * @date 2022-03-18
 */
public interface GamePaymentTimeoutMapper 
{
    /**
     * 查询超时支付
     * 
     * @param id 超时支付主键
     * @return 超时支付
     */
     GamePaymentTimeout selectGamePaymentTimeoutById(Long id);

    /**
     * 查询超时支付列表
     * 
     * @param gamePaymentTimeout 超时支付
     * @return 超时支付集合
     */
    List<GamePaymentTimeout> selectGamePaymentTimeoutList(GamePaymentTimeout gamePaymentTimeout);

    /**
     * 新增超时支付
     * 
     * @param gamePaymentTimeout 超时支付
     * @return 结果
     */
    int insertGamePaymentTimeout(GamePaymentTimeout gamePaymentTimeout);

    /**
     * 修改超时支付
     * 
     * @param gamePaymentTimeout 超时支付
     * @return 结果
     */
    int updateGamePaymentTimeout(GamePaymentTimeout gamePaymentTimeout);

    /**
     * 删除超时支付
     * 
     * @param id 超时支付主键
     * @return 结果
     */
    int deleteGamePaymentTimeoutById(Long id);

    /**
     * 批量删除超时支付
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteGamePaymentTimeoutByIds(Long[] ids);

    int batchInsert(List<GamePaymentTimeout> list);
}
