package com.bistro.module.payment.controller;

import com.bistro.common.annotation.Log;
import com.bistro.common.core.controller.BaseController;
import com.bistro.common.core.domain.AjaxResult;
import com.bistro.common.core.page.TableDataInfo;
import com.bistro.common.enums.BusinessType;
import com.bistro.common.utils.poi.ExcelUtil;
import com.bistro.module.payment.domain.GamePaymentTimeout;
import com.bistro.module.payment.service.IGamePaymentTimeoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 超时支付Controller
 * 
 * @author jason.lin
 * @date 2022-03-18
 */
@RestController
@RequestMapping("/payment/paymentTimeout")
public class GamePaymentTimeoutController extends BaseController
{
    @Autowired
    private IGamePaymentTimeoutService gamePaymentTimeoutService;

    /**
     * 查询超时支付列表
     */
    @PreAuthorize("@ss.hasPermi('payment:paymentTimeout:list')")
    @GetMapping("/list")
    public TableDataInfo list(GamePaymentTimeout gamePaymentTimeout)
    {
        startPage();
        List<GamePaymentTimeout> list = gamePaymentTimeoutService.selectGamePaymentTimeoutList(gamePaymentTimeout);
        return getDataTable(list);
    }

    /**
     * 导出超时支付列表
     */
    @PreAuthorize("@ss.hasPermi('payment:paymentTimeout:export')")
    @Log(title = "超时支付", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(GamePaymentTimeout gamePaymentTimeout)
    {
        List<GamePaymentTimeout> list = gamePaymentTimeoutService.selectGamePaymentTimeoutList(gamePaymentTimeout);
        ExcelUtil<GamePaymentTimeout> util = new ExcelUtil<GamePaymentTimeout>(GamePaymentTimeout.class);
        return util.exportExcel(list, "超时支付数据");
    }

    /**
     * 获取超时支付详细信息
     */
    @PreAuthorize("@ss.hasPermi('payment:paymentTimeout:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gamePaymentTimeoutService.selectGamePaymentTimeoutById(id));
    }

    /**
     * 新增超时支付
     */
    @PreAuthorize("@ss.hasPermi('payment:paymentTimeout:add')")
    @Log(title = "超时支付", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GamePaymentTimeout gamePaymentTimeout)
    {
        return toAjax(gamePaymentTimeoutService.insertGamePaymentTimeout(gamePaymentTimeout));
    }

    /**
     * 修改超时支付
     */
    @PreAuthorize("@ss.hasPermi('payment:paymentTimeout:edit')")
    @Log(title = "超时支付", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GamePaymentTimeout gamePaymentTimeout)
    {
        return toAjax(gamePaymentTimeoutService.updateGamePaymentTimeout(gamePaymentTimeout));
    }

    /**
     * 删除超时支付
     */
    @PreAuthorize("@ss.hasPermi('payment:paymentTimeout:remove')")
    @Log(title = "超时支付", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gamePaymentTimeoutService.deleteGamePaymentTimeoutByIds(ids));
    }
}
