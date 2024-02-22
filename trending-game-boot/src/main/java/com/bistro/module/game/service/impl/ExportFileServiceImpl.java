package com.bistro.module.game.service.impl;

import com.bistro.common.core.domain.model.LoginUser;
import com.bistro.common.utils.DateUtils;
import com.bistro.module.game.domain.ExportFile;
import com.bistro.module.game.mapper.ExportFileMapper;
import com.bistro.module.game.service.IExportFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 导出下载Service业务层处理
 * 
 * @author jason.lin
 * @date 2022-01-14
 */
@Service
public class ExportFileServiceImpl implements IExportFileService 
{
    @Autowired
    private ExportFileMapper exportFileMapper;

    /**
     * 查询导出下载
     * 
     * @param id 导出下载主键
     * @return 导出下载
     */
    @Override
    public ExportFile selectExportFileById(Integer id)
    {
        return exportFileMapper.selectExportFileById(id);
    }

    /**
     * 查询导出下载列表
     * 
     * @param exportFile 导出下载
     * @return 导出下载
     */
    @Override
    public List<ExportFile> selectExportFileList(ExportFile exportFile)
    {
        return exportFileMapper.selectExportFileList(exportFile);
    }

    /**
     * 新增导出下载
     * 
     * @param exportFile 导出下载
     * @return 结果
     */
    @Override
    public int insertExportFile(ExportFile exportFile)
    {
        exportFile.setCreateTime(DateUtils.getNowDate());
        return exportFileMapper.insertExportFile(exportFile);
    }

    /**
     * 修改导出下载
     * 
     * @param exportFile 导出下载
     * @return 结果
     */
    @Override
    public int updateExportFile(ExportFile exportFile)
    {
        exportFile.setUpdateTime(DateUtils.getNowDate());
        return exportFileMapper.updateExportFile(exportFile);
    }

    /**
     * 批量删除导出下载
     * 
     * @param ids 需要删除的导出下载主键
     * @return 结果
     */
    @Override
    public int deleteExportFileByIds(Integer[] ids)
    {
        return exportFileMapper.deleteExportFileByIds(ids);
    }

    /**
     * 删除导出下载信息
     * 
     * @param id 导出下载主键
     * @return 结果
     */
    @Override
    public int deleteExportFileById(Integer id)
    {
        return exportFileMapper.deleteExportFileById(id);
    }

    /**
     * 查询导出下载列表
     *
     * @param exportFile 导出下载
     * @return 导出下载
     */
    @Override
    public List<ExportFile> selectExportFileList(ExportFile exportFile, LoginUser loginUser)
    {
        if(!loginUser.getUser().isAdmin()){
            exportFile.setUid(loginUser.getUserId());
        }
        return exportFileMapper.selectExportFileList(exportFile);
    }
}
