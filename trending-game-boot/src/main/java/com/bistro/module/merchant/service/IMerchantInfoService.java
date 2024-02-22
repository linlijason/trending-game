package com.bistro.module.merchant.service;

import com.bistro.module.merchant.domain.MerchantInfo;

import java.util.List;

/**
 * 商户信息Service接口
 *
 * @author jason.lin
 * @date 2021-11-23
 */
public interface IMerchantInfoService
{
    /**
     * 查询商户信息
     *
     * @param id 商户信息主键
     * @return 商户信息
     */
    MerchantInfo selectMerchantInfoById(Integer id);

    /**
     * 查询商户信息列表
     *
     * @param merchantInfo 商户信息
     * @return 商户信息集合
     */
    List<MerchantInfo> selectMerchantInfoList(MerchantInfo merchantInfo);

    /**
     * 新增商户信息
     *
     * @param merchantInfo 商户信息
     * @return 结果
     */
    int insertMerchantInfo(MerchantInfo merchantInfo);

    /**
     * 修改商户信息
     *
     * @param merchantInfo 商户信息
     * @return 结果
     */
    int updateMerchantInfo(MerchantInfo merchantInfo);

    /**
     * 批量删除商户信息
     *
     * @param ids 需要删除的商户信息主键集合
     * @return 结果
     */
    int deleteMerchantInfoByIds(Integer[] ids);

    /**
     * 删除商户信息信息
     *
     * @param id 商户信息主键
     * @return 结果
     */
    int deleteMerchantInfoById(Integer id);

    MerchantInfo selectMerchantInfoByCode(String code);
}