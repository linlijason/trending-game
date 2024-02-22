package com.bistro.module.test.controller;

import com.bistro.common.annotation.Log;
import com.bistro.common.core.controller.BaseController;
import com.bistro.common.core.domain.AjaxResult;
import com.bistro.common.core.page.TableDataInfo;
import com.bistro.common.enums.BusinessType;
import com.bistro.common.utils.poi.ExcelUtil;
import com.bistro.module.test.domain.TestAddr;
import com.bistro.module.test.service.ITestAddrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * test_addrController
 * 
 * @author jason.lin
 * @date 2021-12-17
 */
@RestController
@RequestMapping("/api/test/addr")
public class TestAddrController extends BaseController
{
    @Autowired
    private ITestAddrService testAddrService;

    /**
     * 查询test_addr列表
     */
    @GetMapping("/list")
    public TableDataInfo list(TestAddr testAddr)
    {
        startPage();
        List<TestAddr> list = testAddrService.selectTestAddrList(testAddr);
        return getDataTable(list);
    }

    /**
     * 导出test_addr列表
     */
    @PreAuthorize("@ss.hasPermi('test:addr:export')")
    @Log(title = "test_addr", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(TestAddr testAddr)
    {
        List<TestAddr> list = testAddrService.selectTestAddrList(testAddr);
        ExcelUtil<TestAddr> util = new ExcelUtil<TestAddr>(TestAddr.class);
        return util.exportExcel(list, "test_addr数据");
    }

    /**
     * 获取test_addr详细信息
     */
    @PreAuthorize("@ss.hasPermi('test:addr:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return AjaxResult.success(testAddrService.selectTestAddrById(id));
    }

    /**
     * 新增test_addr
     */
    //@PreAuthorize("@ss.hasPermi('test:addr:add')")
//    @Log(title = "test_addr", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TestAddr testAddr)
    {
        return toAjax(testAddrService.insertTestAddr(testAddr));
    }

    /**
     * 修改test_addr
     */
    @PreAuthorize("@ss.hasPermi('test:addr:edit')")
    @Log(title = "test_addr", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TestAddr testAddr)
    {
        return toAjax(testAddrService.updateTestAddr(testAddr));
    }

    /**
     * 删除test_addr
     */
    @PreAuthorize("@ss.hasPermi('test:addr:remove')")
    @Log(title = "test_addr", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(testAddrService.deleteTestAddrByIds(ids));
    }
}
