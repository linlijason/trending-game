package com.bistro.module.payment.mapper;

import com.bistro.module.payment.domain.GamePaymentInfo;

import java.util.List;

/**
 * 支付信息Mapper接口
 *
 * @author gavin
 * @date 2021-11-17
 */
public interface GamePaymentInfoMapper {
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
     * 删除支付信息
     *
     * @param id 支付信息主键
     * @return 结果
     */
    int deleteGamePaymentInfoById(Long id);

    /**
     * 批量删除支付信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteGamePaymentInfoByIds(Long[] ids);

    int batchInsertGamePaymentInfo(List<GamePaymentInfo> list);

    int updateBatchResult(List<GamePaymentInfo> list);
}
