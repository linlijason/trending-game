package com.bistro.module.game.controller;

import com.bistro.common.annotation.Log;
import com.bistro.common.core.controller.BaseController;
import com.bistro.common.core.domain.AjaxResult;
import com.bistro.common.core.page.TableDataInfo;
import com.bistro.common.enums.BusinessType;
import com.bistro.common.utils.poi.ExcelUtil;
import com.bistro.module.game.domain.GameBaseInfo;
import com.bistro.module.game.service.IGameBaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 游戏基本信息Controller
 * 
 * @author gavin
 * @date 2021-11-17
 */
@RestController
@RequestMapping("/game/baseinfo")
public class GameBaseInfoController extends BaseController
{
    @Autowired
    private IGameBaseInfoService gameBaseInfoService;

    /**
     * 查询游戏基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('game:baseinfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(GameBaseInfo gameBaseInfo)
    {
        startPage();
        List<GameBaseInfo> list = gameBaseInfoService.selectGameBaseInfoList(gameBaseInfo);
        return getDataTable(list);
    }

    /**
     * 导出游戏基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('game:baseinfo:export')")
    @Log(title = "游戏基本信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(GameBaseInfo gameBaseInfo)
    {
        List<GameBaseInfo> list = gameBaseInfoService.selectGameBaseInfoList(gameBaseInfo);
        ExcelUtil<GameBaseInfo> util = new ExcelUtil<GameBaseInfo>(GameBaseInfo.class);
        return util.exportExcel(list, "游戏基本信息数据");
    }

    /**
     * 获取游戏基本信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('game:baseinfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return AjaxResult.success(gameBaseInfoService.selectGameBaseInfoById(id));
    }

    /**
     * 新增游戏基本信息
     */
    @PreAuthorize("@ss.hasPermi('game:baseinfo:add')")
    @Log(title = "游戏基本信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GameBaseInfo gameBaseInfo)
    {
        return toAjax(gameBaseInfoService.insertGameBaseInfo(gameBaseInfo));
    }

    /**
     * 修改游戏基本信息
     */
    @PreAuthorize("@ss.hasPermi('game:baseinfo:edit')")
    @Log(title = "游戏基本信息", businessType = BusinessType.UPDATE, isSaveDb = true)
    @PutMapping
    public AjaxResult edit(@RequestBody GameBaseInfo gameBaseInfo)
    {
        return toAjax(gameBaseInfoService.updateGameBaseInfo(gameBaseInfo));
    }

    /**
     * 删除游戏基本信息
     */
    @PreAuthorize("@ss.hasPermi('game:baseinfo:remove')")
    @Log(title = "游戏基本信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(gameBaseInfoService.deleteGameBaseInfoByIds(ids));
    }
}
