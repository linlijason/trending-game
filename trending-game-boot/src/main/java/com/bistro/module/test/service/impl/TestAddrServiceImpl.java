package com.bistro.module.test.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bistro.module.test.mapper.TestAddrMapper;
import com.bistro.module.test.domain.TestAddr;
import com.bistro.module.test.service.ITestAddrService;

/**
 * test_addrService业务层处理
 * 
 * @author jason.lin
 * @date 2021-12-17
 */
@Service
public class TestAddrServiceImpl implements ITestAddrService 
{
    @Autowired
    private TestAddrMapper testAddrMapper;

    /**
     * 查询test_addr
     * 
     * @param id test_addr主键
     * @return test_addr
     */
    @Override
    public TestAddr selectTestAddrById(Integer id)
    {
        return testAddrMapper.selectTestAddrById(id);
    }

    /**
     * 查询test_addr列表
     * 
     * @param testAddr test_addr
     * @return test_addr
     */
    @Override
    public List<TestAddr> selectTestAddrList(TestAddr testAddr)
    {
        return testAddrMapper.selectTestAddrList(testAddr);
    }

    /**
     * 新增test_addr
     * 
     * @param testAddr test_addr
     * @return 结果
     */
    @Override
    public int insertTestAddr(TestAddr testAddr)
    {
        return testAddrMapper.insertTestAddr(testAddr);
    }

    /**
     * 修改test_addr
     * 
     * @param testAddr test_addr
     * @return 结果
     */
    @Override
    public int updateTestAddr(TestAddr testAddr)
    {
        return testAddrMapper.updateTestAddr(testAddr);
    }

    /**
     * 批量删除test_addr
     * 
     * @param ids 需要删除的test_addr主键
     * @return 结果
     */
    @Override
    public int deleteTestAddrByIds(Integer[] ids)
    {
        return testAddrMapper.deleteTestAddrByIds(ids);
    }

    /**
     * 删除test_addr信息
     * 
     * @param id test_addr主键
     * @return 结果
     */
    @Override
    public int deleteTestAddrById(Integer id)
    {
        return testAddrMapper.deleteTestAddrById(id);
    }
}
