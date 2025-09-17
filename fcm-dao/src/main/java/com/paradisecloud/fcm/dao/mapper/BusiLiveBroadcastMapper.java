package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiLiveBroadcast;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

import java.util.List;

/**
 * 直播记录Mapper接口
 *
 * @author lilinhai
 * @date 2024-05-07
 */
public interface BusiLiveBroadcastMapper
{
    /**
     * 查询直播记录
     *
     * @param id 直播记录ID
     * @return 直播记录
     */
    public BusiLiveBroadcast selectBusiLiveBroadcastById(Long id);

    /**
     * 查询直播记录列表
     *
     * @param busiLiveBroadcast 直播记录
     * @return 直播记录集合
     */
    public List<BusiLiveBroadcast> selectBusiLiveBroadcastList(BusiLiveBroadcast busiLiveBroadcast);

    /**
     * 新增直播记录
     *
     * @param busiLiveBroadcast 直播记录
     * @return 结果
     */
    public int insertBusiLiveBroadcast(BusiLiveBroadcast busiLiveBroadcast);

    /**
     * 修改直播记录
     *
     * @param busiLiveBroadcast 直播记录
     * @return 结果
     */
    public int updateBusiLiveBroadcast(BusiLiveBroadcast busiLiveBroadcast);

    /**
     * 删除直播记录
     *
     * @param id 直播记录ID
     * @return 结果
     */
    public int deleteBusiLiveBroadcastById(Long id);

    /**
     * 批量删除直播记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiLiveBroadcastByIds(Long[] ids);

    /**
     * 获取部门直播计数
     *
     * @author sinhy
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
