package com.bistro.module.game.controller;

import com.bistro.common.annotation.Log;
import com.bistro.common.core.controller.BaseController;
import com.bistro.common.core.domain.AjaxResult;
import com.bistro.common.core.page.TableDataInfo;
import com.bistro.common.enums.BusinessType;
import com.bistro.module.game.domain.BetGameUserQuery;
import com.bistro.module.game.domain.BetGameUserVo;
import com.bistro.module.game.service.BetGameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/game-bet-user")
public class BetGameUserController extends BaseController {


    @Autowired
    private BetGameUserService betGameUserService;

    @GetMapping("/list")
    public TableDataInfo list(BetGameUserQuery query) {
        startPage();
        List<BetGameUserVo> list = betGameUserService.search(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('game:report:export')")
    @Log(title = "游戏记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(BetGameUserQuery query) {
        if (query.getBeginCreateTime() == null || query.getEndCreateTime() == null) {
            throw new SecurityException("请选择时间范围");
        }
        if (query.getBeginCreateTime().plusDays(3).compareTo(query.getEndCreateTime()) < 0) {
            throw new SecurityException("导出数据时间范围不能超过三天");
        }
        betGameUserService.exportReport(query, getLoginUser());
        return AjaxResult.success("处理中,稍后去导出下载菜单下载文件");
    }

}
