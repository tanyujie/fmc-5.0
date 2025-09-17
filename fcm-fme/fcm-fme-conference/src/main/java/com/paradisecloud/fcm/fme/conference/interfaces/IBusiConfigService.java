package com.paradisecloud.fcm.fme.conference.interfaces;

import java.util.List;
import java.util.Map;

import com.paradisecloud.fcm.dao.model.BusiConfig;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author lilinhai
 * @date 2022-09-10
 */
public interface IBusiConfigService 
{
    /**
     * 查询【请填写功能名称】
     *
     * @param configId 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiConfig selectBusiConfigById(Integer configId);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param busiConfig 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiConfig> selectBusiConfigList(BusiConfig busiConfig);

    /**
     * 获取录制空间最大容量
     */
    Map<String,Object> getRecordingFilesStorageSpaceMax();

    /**
     * 新增【请填写功能名称】
     *
     * @param busiConfig 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiConfig(BusiConfig busiConfig);

    /**
     * 设置录制空间最大容量
     * @param recordingFilesStorageSpaceMax
     * @return
     */
    int updateRecordingFilesStorageSpaceMax(Double recordingFilesStorageSpaceMax);

    /**
     * 修改【请填写功能名称】
     *
     * @param busiConfig 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiConfig(BusiConfig busiConfig);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param configIds 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiConfigByIds(Integer[] configIds);

    /**
     * 删除【请填写功能名称】信息
     *
     * @param configId 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiConfigById(Integer configId);
}