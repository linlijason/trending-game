package com.bistro.module.test.mapper;

import java.util.List;
import com.bistro.module.test.domain.TestAddr;

/**
 * test_addrMapper接口
 * 
 * @author jason.lin
 * @date 2021-12-17
 */
public interface TestAddrMapper 
{
    /**
     * 查询test_addr
     * 
     * @param id test_addr主键
     * @return test_addr
     */
     TestAddr selectTestAddrById(Integer id);

    /**
     * 查询test_addr列表
     * 
     * @param testAddr test_addr
     * @return test_addr集合
     */
    List<TestAddr> selectTestAddrList(TestAddr testAddr);

    /**
     * 新增test_addr
     * 
     * @param testAddr test_addr
     * @return 结果
     */
    int insertTestAddr(TestAddr testAddr);

    /**
     * 修改test_addr
     * 
     * @param testAddr test_addr
     * @return 结果
     */
    int updateTestAddr(TestAddr testAddr);

    /**
     * 删除test_addr
     * 
     * @param id test_addr主键
     * @return 结果
     */
    int deleteTestAddrById(Integer id);

    /**
     * 批量删除test_addr
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteTestAddrByIds(Integer[] ids);
}
