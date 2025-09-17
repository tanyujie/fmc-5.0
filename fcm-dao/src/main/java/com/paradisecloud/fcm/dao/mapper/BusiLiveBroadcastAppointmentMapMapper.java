package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiLiveBroadcastAppointmentMap;

import java.util.List;

/**
 * 直播会议对应Mapper接口
 *
 * @author lilinhai
 * @date 2024-05-07
 */
public interface BusiLiveBroadcastAppointmentMapMapper
{
    /**
     * 查询直播会议对应
     *
     * @param id 直播会议对应ID
     * @return 直播会议对应
     */
    public BusiLiveBroadcastAppointmentMap selectBusiLiveBroadcastAppointmentMapById(Long id);

    /**
     * 查询直播会议对应列表
     *
     * @param busiLiveBroadcastAppointmentMap 直播会议对应
     * @return 直播会议对应集合
     */
    public List<BusiLiveBroadcastAppointmentMap> selectBusiLiveBroadcastAppointmentMapList(BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMap);

    /**
     * 新增直播会议对应
     *
     * @param busiLiveBroadcastAppointmentMap 直播会议对应
     * @return 结果
     */
    public int insertBusiLiveBroadcastAppointmentMap(BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMap);

    /**
     * 修改直播会议对应
     *
     * @param busiLiveBroadcastAppointmentMap 直播会议对应
     * @return 结果
     */
    public int updateBusiLiveBroadcastAppointmentMap(BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMap);

    /**
     * 删除直播会议对应
     *
     * @param id 直播会议对应ID
     * @return 结果
     */
    public int deleteBusiLiveBroadcastAppointmentMapById(Long id);

    /**
     * 批量删除直播会议对应
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiLiveBroadcastAppointmentMapByIds(Long[] ids);
}
