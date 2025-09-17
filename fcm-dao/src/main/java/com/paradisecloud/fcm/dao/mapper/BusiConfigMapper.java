package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiConfig;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-10
 */
public interface BusiConfigMapper 
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
     * 新增【请填写功能名称】
     * 
     * @param busiConfig 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiConfig(BusiConfig busiConfig);

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiConfig 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiConfig(BusiConfig busiConfig);

    /**
     * 删除【请填写功能名称】
     * 
     * @param configId 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiConfigById(Integer configId);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param configIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConfigByIds(Integer[] configIds);
}
