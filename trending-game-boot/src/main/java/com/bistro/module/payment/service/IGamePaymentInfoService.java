package com.bistro.module.payment.service;

import java.util.List;
import com.bistro.module.payment.domain.GamePaymentInfo;

/**
 * 支付信息Service接口
 * 
 * @author gavin
 * @date 2021-11-17
 */
public interface IGamePaymentInfoService 
{
    /**
     * 查询支付信息
     * 
     * @param id 支付信息主键
     * @return 支付信息
     */
     GamePaymentInfo selectGamePaymentInfoById(Long id);

    /**
     * 查询支付信息列表
     * 
     * @param gamePaymentInfo 支付信息
     * @return 支付信息集合
     */
    List<GamePaymentInfo> selectGamePaymentInfoList(GamePaymentInfo gamePaymentInfo);

    /**
     * 新增支付信息
     * 
     * @param gamePaymentInfo 支付信息
     * @return 结果
     */
    int insertGamePaymentInfo(GamePaymentInfo gamePaymentInfo);

    /**
     * 修改支付信息
     * 
     * @param gamePaymentInfo 支付信息
     * @return 结果
     */
    int updateGamePaymentInfo(GamePaymentInfo gamePaymentInfo);

    /**
     * 批量删除支付信息
     * 
     * @param ids 需要删除的支付信息主键集合
     * @return 结果
     */
    int deleteGamePaymentInfoByIds(Long[] ids);

    /**
     * 删除支付信息信息
     * 
     * @param id 支付信息主键
     * @return 结果
     */
    int deleteGamePaymentInfoById(Long id);
}
