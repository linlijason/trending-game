package com.bistro.module.player.controller;

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
import com.bistro.module.player.domain.GameUserInfo;
import com.bistro.module.player.service.IGameUserInfoService;
import com.bistro.common.utils.poi.ExcelUtil;
import com.bistro.common.core.page.TableDataInfo;

/**
 * 玩家信息Controller
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
@RestController
@RequestMapping("/player/user")
public class GameUserInfoController extends BaseController
{
    @Autowired
    private IGameUserInfoService gameUserInfoService;

    /**
     * 查询玩家信息列表
     */
    @PreAuthorize("@ss.hasPermi('player:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(GameUserInfo gameUserInfo)
    {
        startPage();
        List<GameUserInfo> list = gameUserInfoService.selectGameUserInfoList(gameUserInfo);
        return getDataTable(list);
    }

    /**
     * 导出玩家信息列表
     */
    @PreAuthorize("@ss.hasPermi('player:user:export')")
    @Log(title = "玩家信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(GameUserInfo gameUserInfo)
    {
        List<GameUserInfo> list = gameUserInfoService.selectGameUserInfoList(gameUserInfo);
        ExcelUtil<GameUserInfo> util = new ExcelUtil<GameUserInfo>(GameUserInfo.class);
        return util.exportExcel(list, "玩家信息数据");
    }

    /**
     * 获取玩家信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('player:user:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gameUserInfoService.selectGameUserInfoById(id));
    }

    /**
     * 新增玩家信息
     */
    @PreAuthorize("@ss.hasPermi('player:user:add')")
    @Log(title = "玩家信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GameUserInfo gameUserInfo)
    {
        return toAjax(gameUserInfoService.insertGameUserInfo(gameUserInfo));
    }

    /**
     * 修改玩家信息
     */
    @PreAuthorize("@ss.hasPermi('player:user:edit')")
    @Log(title = "玩家信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GameUserInfo gameUserInfo)
    {
        return toAjax(gameUserInfoService.updateGameUserInfo(gameUserInfo));
    }

    /**
     * 删除玩家信息
     */
    @PreAuthorize("@ss.hasPermi('player:user:remove')")
    @Log(title = "玩家信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gameUserInfoService.deleteGameUserInfoByIds(ids));
    }
}
