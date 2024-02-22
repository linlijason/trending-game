package com.bistro.module.game.controller;

import com.bistro.common.annotation.Log;
import com.bistro.common.core.controller.BaseController;
import com.bistro.common.core.domain.AjaxResult;
import com.bistro.common.core.page.TableDataInfo;
import com.bistro.common.enums.BusinessType;
import com.bistro.common.utils.poi.ExcelUtil;
import com.bistro.module.game.domain.CurrencyInfo;
import com.bistro.module.game.service.ICurrencyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 货币配置Controller
 * 
 * @author jason.lin
 * @date 2021-12-06
 */
@RestController
@RequestMapping("/game/currencyinfo")
public class CurrencyInfoController extends BaseController
{
    @Autowired
    private ICurrencyInfoService currencyInfoService;

    /**
     * 查询货币配置列表
     */
    @PreAuthorize("@ss.hasPermi('game:currencyinfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(CurrencyInfo currencyInfo)
    {
        startPage();
        List<CurrencyInfo> list = currencyInfoService.selectCurrencyInfoList(currencyInfo);
        return getDataTable(list);
    }

    /**
     * 导出货币配置列表
     */
    @PreAuthorize("@ss.hasPermi('game:currencyinfo:export')")
    @Log(title = "货币配置", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(CurrencyInfo currencyInfo)
    {
        List<CurrencyInfo> list = currencyInfoService.selectCurrencyInfoList(currencyInfo);
        ExcelUtil<CurrencyInfo> util = new ExcelUtil<CurrencyInfo>(CurrencyInfo.class);
        return util.exportExcel(list, "货币配置数据");
    }

    /**
     * 获取货币配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('game:currencyinfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return AjaxResult.success(currencyInfoService.selectCurrencyInfoById(id));
    }

    /**
     * 新增货币配置
     */
    @PreAuthorize("@ss.hasPermi('game:currencyinfo:add')")
    @Log(title = "货币配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CurrencyInfo currencyInfo)
    {
        return toAjax(currencyInfoService.insertCurrencyInfo(currencyInfo));
    }

    /**
     * 修改货币配置
     */
    @PreAuthorize("@ss.hasPermi('game:currencyinfo:edit')")
    @Log(title = "货币配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CurrencyInfo currencyInfo)
    {
        return toAjax(currencyInfoService.updateCurrencyInfo(currencyInfo));
    }

    /**
     * 删除货币配置
     */
    @PreAuthorize("@ss.hasPermi('game:currencyinfo:remove')")
    @Log(title = "货币配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(currencyInfoService.deleteCurrencyInfoByIds(ids));
    }
}
