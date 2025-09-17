package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiOpsInfo;

import java.util.List;

/**
 * ops配置信息Mapper接口
 * 
 * @author lilinhai
 * @date 2024-05-27
 */
public interface BusiOpsInfoMapper 
{
    /**
     * 查询ops配置信息
     * 
     * @param id ops配置信息ID
     * @return ops配置信息
     */
    public BusiOpsInfo selectBusiOpsInfoById(Integer id);

    /**
     * 查询ops配置信息列表
     * 
     * @param busiOpsInfo ops配置信息
     * @return ops配置信息集合
     */
    public List<BusiOpsInfo> selectBusiOpsInfoList(BusiOpsInfo busiOpsInfo);

    /**
     * 新增ops配置信息
     * 
     * @param busiOpsInfo ops配置信息
     * @return 结果
     */
    public int insertBusiOpsInfo(BusiOpsInfo busiOpsInfo);

    /**
     * 修改ops配置信息
     * 
     * @param busiOpsInfo ops配置信息
     * @return 结果
     */
    public int updateBusiOpsInfo(BusiOpsInfo busiOpsInfo);

    /**
     * 删除ops配置信息
     * 
     * @param id ops配置信息ID
     * @return 结果
     */
    public int deleteBusiOpsInfoById(Integer id);

    /**
     * 批量删除ops配置信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiOpsInfoByIds(Integer[] ids);
}
