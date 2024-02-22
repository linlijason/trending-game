package com.bistro.module.game.controller;

import com.bistro.common.annotation.Log;
import com.bistro.common.core.controller.BaseController;
import com.bistro.common.core.domain.AjaxResult;
import com.bistro.common.enums.BusinessType;
import com.bistro.module.game.domain.ManualTransactionRequest;
import com.bistro.module.game.service.IManualTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manual")
public class ManualTransactionController extends BaseController {

    @Autowired
    IManualTransactionService manualTransactionService;

    @PreAuthorize("@ss.hasPermi('manual:transaction:payoutfailed')")
    @Log(title = "手动派奖", businessType = BusinessType.MANUAL_TRANSACTION, isSaveDb = true)
    @PostMapping("/payoutfailed")
    public AjaxResult payoutFailed(@RequestBody ManualTransactionRequest request) {
        return AjaxResult.success(manualTransactionService.payoutFailed(request.getBetId()));
    }

    @PreAuthorize("@ss.hasPermi('manual:transaction:refund')")
    @Log(title = "退回投注", businessType = BusinessType.MANUAL_TRANSACTION, isSaveDb = true)
    @PostMapping("/refund")
    public AjaxResult refund(@RequestBody ManualTransactionRequest request) {
        return AjaxResult.success(manualTransactionService.refund(request.getBetId()));
    }
}
