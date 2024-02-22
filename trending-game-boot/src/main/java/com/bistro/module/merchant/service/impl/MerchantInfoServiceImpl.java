package com.bistro.module.merchant.service.impl;

import com.bistro.common.utils.DateUtils;
import com.bistro.module.merchant.domain.MerchantInfo;
import com.bistro.module.merchant.mapper.MerchantInfoMapper;
import com.bistro.module.merchant.service.IMerchantInfoService;
import com.bistro.web.core.config.MerchantConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商户信息Service业务层处理
 *
 * @author jason.lin
 * @date 2021-11-23
 */
@Service
public class MerchantInfoServiceImpl implements IMerchantInfoService
{
    @Autowired
    private MerchantInfoMapper merchantInfoMapper;

    @Autowired
    private MerchantConfig merchantConfig;

    /**
     * 查询商户信息
     *
     * @param id 商户信息主键
     * @return 商户信息
     */
    @Override
    public MerchantInfo selectMerchantInfoById(Integer id)
    {
        return merchantInfoMapper.selectMerchantInfoById(id);
    }

    /**
     * 查询商户信息列表
     *
     * @param merchantInfo 商户信息
     * @return 商户信息
     */
    @Override
    public List<MerchantInfo> selectMerchantInfoList(MerchantInfo merchantInfo)
    {
        return merchantInfoMapper.selectMerchantInfoList(merchantInfo);
    }

    /**
     * 新增商户信息
     *
     * @param merchantInfo 商户信息
     * @return 结果
     */
    @Override
    public int insertMerchantInfo(MerchantInfo merchantInfo)
    {
        merchantInfo.setCreateTime(DateUtils.getNowDate());
        return merchantInfoMapper.insertMerchantInfo(merchantInfo);
    }

    /**
     * 修改商户信息
     *
     * @param merchantInfo 商户信息
     * @return 结果
     */
    @Override
    public int updateMerchantInfo(MerchantInfo merchantInfo)
    {
        merchantInfo.setUpdateTime(DateUtils.getNowDate());
        return merchantInfoMapper.updateMerchantInfo(merchantInfo);
    }

    /**
     * 批量删除商户信息
     *
     * @param ids 需要删除的商户信息主键
     * @return 结果
     */
    @Override
    public int deleteMerchantInfoByIds(Integer[] ids)
    {
        return merchantInfoMapper.deleteMerchantInfoByIds(ids);
    }

    /**
     * 删除商户信息信息
     *
     * @param id 商户信息主键
     * @return 结果
     */
    @Override
    public int deleteMerchantInfoById(Integer id)
    {
        return merchantInfoMapper.deleteMerchantInfoById(id);
    }

    @Override
    public MerchantInfo selectMerchantInfoByCode(String code)
    {
        return merchantConfig.getConfig().get(code);
    }


}