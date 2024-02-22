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
import com.bistro.module.game.domain.GameBet;
import com.bistro.module.game.service.IGameBetService;
import com.bistro.common.utils.poi.ExcelUtil;
import com.bistro.common.core.page.TableDataInfo;

/**
 * 下注信息Controller
 * 
 * @author gavin
 * @date 2021-11-17
 */
@RestController
@RequestMapping("/game/bet")
public class GameBetController extends BaseController
{
    @Autowired
    private IGameBetService gameBetService;

    /**
     * 查询下注信息列表
     */
    @PreAuthorize("@ss.hasPermi('game:bet:list')")
    @GetMapping("/list")
    public TableDataInfo list(GameBet gameBet)
    {
        startPage();
        List<GameBet> list = gameBetService.selectGameBetList(gameBet);
        return getDataTable(list);
    }

    /**
     * 导出下注信息列表
     */
    @PreAuthorize("@ss.hasPermi('game:bet:export')")
    @Log(title = "下注信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(GameBet gameBet)
    {
        List<GameBet> list = gameBetService.selectGameBetList(gameBet);
        ExcelUtil<GameBet> util = new ExcelUtil<GameBet>(GameBet.class);
        return util.exportExcel(list, "下注信息数据");
    }

    /**
     * 获取下注信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('game:bet:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gameBetService.selectGameBetById(id));
    }

    /**
     * 新增下注信息
     */
    @PreAuthorize("@ss.hasPermi('game:bet:add')")
    @Log(title = "下注信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GameBet gameBet)
    {
        return toAjax(gameBetService.insertGameBet(gameBet));
    }

    /**
     * 修改下注信息
     */
    @PreAuthorize("@ss.hasPermi('game:bet:edit')")
    @Log(title = "下注信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GameBet gameBet)
    {
        return toAjax(gameBetService.updateGameBet(gameBet));
    }

    /**
     * 删除下注信息
     */
    @PreAuthorize("@ss.hasPermi('game:bet:remove')")
    @Log(title = "下注信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gameBetService.deleteGameBetByIds(ids));
    }
}
