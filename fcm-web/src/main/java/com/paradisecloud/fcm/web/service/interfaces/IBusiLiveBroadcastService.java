package com.paradisecloud.fcm.web.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiLiveBroadcast;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.vo.BusiLiveBroadcastVo;

import java.util.List;

/**
 * 直播记录Service接口
 *
 * @author lilinhai
 * @date 2024-05-07
 */
public interface IBusiLiveBroadcastService
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
    public List<BusiLiveBroadcast> selectBusiLiveBroadcastList(BusiLiveBroadcastVo busiLiveBroadcast);

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
     * 批量删除直播记录
     *
     * @param ids 需要删除的直播记录ID
     * @return 结果
     */
    public int deleteBusiLiveBroadcastByIds(Long[] ids);

    /**
     * 删除直播记录信息
     *
     * @param id 直播记录ID
     * @return 结果
     */
    public int deleteBusiLiveBroadcastById(Long id);

    /**
     * 获取部门直播计数
     *
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();

    /**
     * 结束直播
     * @param id
     * @return
     */
    Boolean endLive(Long id, int endType);
}