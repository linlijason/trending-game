package com.bistro.module.payment.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bistro.common.annotation.Log;
import com.bistro.common.core.controller.BaseController;
import com.bistro.common.core.domain.AjaxResult;
import com.bistro.common.enums.BusinessType;
import com.bistro.module.payment.domain.GamePaymentInfo;
import com.bistro.module.payment.service.IGamePaymentInfoService;
import com.bistro.common.utils.poi.ExcelUtil;
import com.bistro.common.core.page.TableDataInfo;

/**
 * 支付信息Controller
 * 
 * @author gavin
 * @date 2021-11-17
 */
@RestController
@RequestMapping("/payment/payment")
public class GamePaymentInfoController extends BaseController
{
    @Autowired
    private IGamePaymentInfoService gamePaymentInfoService;

    /**
     * 查询支付信息列表
     */
    @PreAuthorize("@ss.hasPermi('payment:payment:list')")
    @GetMapping("/list")
    public TableDataInfo list(GamePaymentInfo gamePaymentInfo)
    {
        startPage();
        List<GamePaymentInfo> list = gamePaymentInfoService.selectGamePaymentInfoList(gamePaymentInfo);
        return getDataTable(list);
    }

    /**
     * 导出支付信息列表
     */
    @PreAuthorize("@ss.hasPermi('payment:payment:export')")
    @Log(title = "支付信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(GamePaymentInfo gamePaymentInfo)
    {
        List<GamePaymentInfo> list = gamePaymentInfoService.selectGamePaymentInfoList(gamePaymentInfo);
        ExcelUtil<GamePaymentInfo> util = new ExcelUtil<GamePaymentInfo>(GamePaymentInfo.class);
        return util.exportExcel(list, "支付信息数据");
    }

    /**
     * 获取支付信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('payment:payment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gamePaymentInfoService.selectGamePaymentInfoById(id));
    }

    /**
     * 新增支付信息
     */
    @PreAuthorize("@ss.hasPermi('payment:payment:add')")
    @Log(title = "支付信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GamePaymentInfo gamePaymentInfo)
    {
        return toAjax(gamePaymentInfoService.insertGamePaymentInfo(gamePaymentInfo));
    }

    /**
     * 修改支付信息
     */
    @PreAuthorize("@ss.hasPermi('payment:payment:edit')")
    @Log(title = "支付信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GamePaymentInfo gamePaymentInfo)
    {
        return toAjax(gamePaymentInfoService.updateGamePaymentInfo(gamePaymentInfo));
    }

    /**
     * 删除支付信息
     */
    @PreAuthorize("@ss.hasPermi('payment:payment:remove')")
    @Log(title = "支付信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gamePaymentInfoService.deleteGamePaymentInfoByIds(ids));
    }
}
