package com.bistro.module.game.controller;

import com.bistro.common.annotation.Log;
import com.bistro.common.core.controller.BaseController;
import com.bistro.common.core.domain.AjaxResult;
import com.bistro.common.core.page.TableDataInfo;
import com.bistro.common.enums.BusinessType;
import com.bistro.common.utils.poi.ExcelUtil;
import com.bistro.module.game.domain.ExportFile;
import com.bistro.module.game.service.IExportFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 导出下载Controller
 *
 * @author jason.lin
 * @date 2022-01-14
 */
@RestController
@RequestMapping("/game/export")
public class ExportFileController extends BaseController {
    @Autowired
    private IExportFileService exportFileService;

    /**
     * 查询导出下载列表
     */
    @PreAuthorize("@ss.hasPermi('game:export:list')")
    @GetMapping("/list")
    public TableDataInfo list(ExportFile exportFile) {
        startPage();
        List<ExportFile> list = exportFileService.selectExportFileList(exportFile, getLoginUser());
        return getDataTable(list);
    }

    /**
     * 导出导出下载列表
     */
    @PreAuthorize("@ss.hasPermi('game:export:export')")
    @Log(title = "导出下载", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ExportFile exportFile) {
        List<ExportFile> list = exportFileService.selectExportFileList(exportFile);
        ExcelUtil<ExportFile> util = new ExcelUtil<ExportFile>(ExportFile.class);
        return util.exportExcel(list, "导出下载数据");
    }

    /**
     * 获取导出下载详细信息
     */
    @PreAuthorize("@ss.hasPermi('game:export:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id) {
        return AjaxResult.success(exportFileService.selectExportFileById(id));
    }

    /**
     * 新增导出下载
     */
    @PreAuthorize("@ss.hasPermi('game:export:add')")
    @Log(title = "导出下载", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ExportFile exportFile) {
        return toAjax(exportFileService.insertExportFile(exportFile));
    }

    /**
     * 修改导出下载
     */
    @PreAuthorize("@ss.hasPermi('game:export:edit')")
    @Log(title = "导出下载", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ExportFile exportFile) {
        return toAjax(exportFileService.updateExportFile(exportFile));
    }

    /**
     * 删除导出下载
     */
    @PreAuthorize("@ss.hasPermi('game:export:remove')")
    @Log(title = "导出下载", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids) {
        return toAjax(exportFileService.deleteExportFileByIds(ids));
    }
}
