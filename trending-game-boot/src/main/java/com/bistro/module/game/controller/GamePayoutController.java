package com.bistro.module.game.controller;

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
import com.bistro.module.game.domain.GamePayout;
import com.bistro.module.game.service.IGamePayoutService;
import com.bistro.common.utils.poi.ExcelUtil;
import com.bistro.common.core.page.TableDataInfo;

/**
 * 提现Controller
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
@RestController
@RequestMapping("/game/payout")
public class GamePayoutController extends BaseController
{
    @Autowired
    private IGamePayoutService gamePayoutService;

    /**
     * 查询提现列表
     */
    @PreAuthorize("@ss.hasPermi('game:payout:list')")
    @GetMapping("/list")
    public TableDataInfo list(GamePayout gamePayout)
    {
        startPage();
        List<GamePayout> list = gamePayoutService.selectGamePayoutList(gamePayout);
        return getDataTable(list);
    }

    /**
     * 导出提现列表
     */
    @PreAuthorize("@ss.hasPermi('game:payout:export')")
    @Log(title = "提现", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(GamePayout gamePayout)
    {
        List<GamePayout> list = gamePayoutService.selectGamePayoutList(gamePayout);
        ExcelUtil<GamePayout> util = new ExcelUtil<GamePayout>(GamePayout.class);
        return util.exportExcel(list, "提现数据");
    }

    /**
     * 获取提现详细信息
     */
    @PreAuthorize("@ss.hasPermi('game:payout:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gamePayoutService.selectGamePayoutById(id));
    }

    /**
     * 新增提现
     */
    @PreAuthorize("@ss.hasPermi('game:payout:add')")
    @Log(title = "提现", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GamePayout gamePayout)
    {
        return toAjax(gamePayoutService.insertGamePayout(gamePayout));
    }

    /**
     * 修改提现
     */
    @PreAuthorize("@ss.hasPermi('game:payout:edit')")
    @Log(title = "提现", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GamePayout gamePayout)
    {
        return toAjax(gamePayoutService.updateGamePayout(gamePayout));
    }

    /**
     * 删除提现
     */
    @PreAuthorize("@ss.hasPermi('game:payout:remove')")
    @Log(title = "提现", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gamePayoutService.deleteGamePayoutByIds(ids));
    }
}
