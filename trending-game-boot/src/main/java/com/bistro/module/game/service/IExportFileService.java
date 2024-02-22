package com.bistro.module.game.service;

import com.bistro.common.core.domain.model.LoginUser;
import com.bistro.module.game.domain.ExportFile;

import java.util.List;

/**
 * 导出下载Service接口
 * 
 * @author jason.lin
 * @date 2022-01-14
 */
public interface IExportFileService 
{
    /**
     * 查询导出下载
     * 
     * @param id 导出下载主键
     * @return 导出下载
     */
     ExportFile selectExportFileById(Integer id);

    /**
     * 查询导出下载列表
     * 
     * @param exportFile 导出下载
     * @return 导出下载集合
     */
    List<ExportFile> selectExportFileList(ExportFile exportFile);

    /**
     * 新增导出下载
     * 
     * @param exportFile 导出下载
     * @return 结果
     */
    int insertExportFile(ExportFile exportFile);

    /**
     * 修改导出下载
     * 
     * @param exportFile 导出下载
     * @return 结果
     */
    int updateExportFile(ExportFile exportFile);

    /**
     * 批量删除导出下载
     * 
     * @param ids 需要删除的导出下载主键集合
     * @return 结果
     */
    int deleteExportFileByIds(Integer[] ids);

    /**
     * 删除导出下载信息
     * 
     * @param id 导出下载主键
     * @return 结果
     */
    int deleteExportFileById(Integer id);

    List<ExportFile> selectExportFileList(ExportFile exportFile, LoginUser loginUser);
}
