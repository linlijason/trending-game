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
import com.bistro.module.game.domain.GameRecodeDetail;
import com.bistro.module.game.service.IGameRecodeDetailService;
import com.bistro.common.utils.poi.ExcelUtil;
import com.bistro.common.core.page.TableDataInfo;

/**
 * 游戏记录详情Controller
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
@RestController
@RequestMapping("/game/recorddetail")
public class GameRecodeDetailController extends BaseController
{
    @Autowired
    private IGameRecodeDetailService gameRecodeDetailService;

    /**
     * 查询游戏记录详情列表
     */
    @PreAuthorize("@ss.hasPermi('game:recorddetail:list')")
    @GetMapping("/list")
    public TableDataInfo list(GameRecodeDetail gameRecodeDetail)
    {
        startPage();
        List<GameRecodeDetail> list = gameRecodeDetailService.selectGameRecodeDetailList(gameRecodeDetail);
        return getDataTable(list);
    }

    /**
     * 导出游戏记录详情列表
     */
    @PreAuthorize("@ss.hasPermi('game:recorddetail:export')")
    @Log(title = "游戏记录详情", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(GameRecodeDetail gameRecodeDetail)
    {
        List<GameRecodeDetail> list = gameRecodeDetailService.selectGameRecodeDetailList(gameRecodeDetail);
        ExcelUtil<GameRecodeDetail> util = new ExcelUtil<GameRecodeDetail>(GameRecodeDetail.class);
        return util.exportExcel(list, "游戏记录详情数据");
    }

    /**
     * 获取游戏记录详情详细信息
     */
    @PreAuthorize("@ss.hasPermi('game:recorddetail:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gameRecodeDetailService.selectGameRecodeDetailById(id));
    }

    /**
     * 新增游戏记录详情
     */
    @PreAuthorize("@ss.hasPermi('game:recorddetail:add')")
    @Log(title = "游戏记录详情", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GameRecodeDetail gameRecodeDetail)
    {
        return toAjax(gameRecodeDetailService.insertGameRecodeDetail(gameRecodeDetail));
    }

    /**
     * 修改游戏记录详情
     */
    @PreAuthorize("@ss.hasPermi('game:recorddetail:edit')")
    @Log(title = "游戏记录详情", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GameRecodeDetail gameRecodeDetail)
    {
        return toAjax(gameRecodeDetailService.updateGameRecodeDetail(gameRecodeDetail));
    }

    /**
     * 删除游戏记录详情
     */
    @PreAuthorize("@ss.hasPermi('game:recorddetail:remove')")
    @Log(title = "游戏记录详情", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gameRecodeDetailService.deleteGameRecodeDetailByIds(ids));
    }
}
