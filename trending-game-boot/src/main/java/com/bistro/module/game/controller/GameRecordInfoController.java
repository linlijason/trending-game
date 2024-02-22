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
import com.bistro.module.game.domain.GameRecordInfo;
import com.bistro.module.game.service.IGameRecordInfoService;
import com.bistro.common.utils.poi.ExcelUtil;
import com.bistro.common.core.page.TableDataInfo;

/**
 * 游戏记录Controller
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
@RestController
@RequestMapping("/game/record")
public class GameRecordInfoController extends BaseController
{
    @Autowired
    private IGameRecordInfoService gameRecordInfoService;

    /**
     * 查询游戏记录列表
     */
    @PreAuthorize("@ss.hasPermi('game:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(GameRecordInfo gameRecordInfo)
    {
        startPage();
        List<GameRecordInfo> list = gameRecordInfoService.selectGameRecordInfoList(gameRecordInfo);
        return getDataTable(list);
    }

    /**
     * 导出游戏记录列表
     */
    @PreAuthorize("@ss.hasPermi('game:record:export')")
    @Log(title = "游戏记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(GameRecordInfo gameRecordInfo)
    {
        List<GameRecordInfo> list = gameRecordInfoService.selectGameRecordInfoList(gameRecordInfo);
        ExcelUtil<GameRecordInfo> util = new ExcelUtil<GameRecordInfo>(GameRecordInfo.class);
        return util.exportExcel(list, "游戏记录数据");
    }

    /**
     * 获取游戏记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('game:record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gameRecordInfoService.selectGameRecordInfoById(id));
    }

    /**
     * 新增游戏记录
     */
    @PreAuthorize("@ss.hasPermi('game:record:add')")
    @Log(title = "游戏记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GameRecordInfo gameRecordInfo)
    {
        return toAjax(gameRecordInfoService.insertGameRecordInfo(gameRecordInfo));
    }

    /**
     * 修改游戏记录
     */
    @PreAuthorize("@ss.hasPermi('game:record:edit')")
    @Log(title = "游戏记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GameRecordInfo gameRecordInfo)
    {
        return toAjax(gameRecordInfoService.updateGameRecordInfo(gameRecordInfo));
    }

    /**
     * 删除游戏记录
     */
    @PreAuthorize("@ss.hasPermi('game:record:remove')")
    @Log(title = "游戏记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gameRecordInfoService.deleteGameRecordInfoByIds(ids));
    }
}
