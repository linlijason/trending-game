package com.bistro.module.game.controller;

import com.bistro.common.annotation.Log;
import com.bistro.common.core.controller.BaseController;
import com.bistro.common.core.domain.AjaxResult;
import com.bistro.common.core.page.TableDataInfo;
import com.bistro.common.enums.BusinessType;
import com.bistro.common.utils.poi.ExcelUtil;
import com.bistro.module.game.domain.GamePeriod;
import com.bistro.module.game.service.IGamePeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 奖期管理Controller
 * 
 * @author jason.lin
 * @date 2021-12-30
 */
@RestController
@RequestMapping("/game/period")
public class GamePeriodController extends BaseController
{
    @Autowired
    private IGamePeriodService gamePeriodService;

    /**
     * 查询奖期管理列表
     */
    @PreAuthorize("@ss.hasPermi('game:period:list')")
    @GetMapping("/list")
    public TableDataInfo list(GamePeriod gamePeriod)
    {
        startPage();
        List<GamePeriod> list = gamePeriodService.selectGamePeriodList(gamePeriod);
        return getDataTable(list);
    }

    /**
     * 导出奖期管理列表
     */
    @PreAuthorize("@ss.hasPermi('game:period:export')")
    @Log(title = "奖期管理", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(GamePeriod gamePeriod)
    {
        List<GamePeriod> list = gamePeriodService.selectGamePeriodList(gamePeriod);
        ExcelUtil<GamePeriod> util = new ExcelUtil<GamePeriod>(GamePeriod.class);
        return util.exportExcel(list, "奖期管理数据");
    }

    /**
     * 获取奖期管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('game:period:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gamePeriodService.selectGamePeriodById(id));
    }

    /**
     * 新增奖期管理
     */
    @PreAuthorize("@ss.hasPermi('game:period:add')")
    @Log(title = "奖期管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GamePeriod gamePeriod)
    {
        return toAjax(gamePeriodService.insertGamePeriod(gamePeriod));
    }

    /**
     * 修改奖期管理
     */
    @PreAuthorize("@ss.hasPermi('game:period:edit')")
    @Log(title = "奖期管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GamePeriod gamePeriod)
    {
        return toAjax(gamePeriodService.updateGamePeriod(gamePeriod));
    }

    /**
     * 删除奖期管理
     */
    @PreAuthorize("@ss.hasPermi('game:period:remove')")
    @Log(title = "奖期管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gamePeriodService.deleteGamePeriodByIds(ids));
    }
}
