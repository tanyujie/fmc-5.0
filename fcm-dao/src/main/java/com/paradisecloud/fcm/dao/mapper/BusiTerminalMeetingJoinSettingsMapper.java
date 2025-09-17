package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiTerminalMeetingJoinSettings;

/**
 * 入会设置Mapper接口
 * 
 * @author lilinhai
 * @date 2021-07-08
 */
public interface BusiTerminalMeetingJoinSettingsMapper 
{
    /**
     * 查询入会设置
     * 
     * @param id 入会设置ID
     * @return 入会设置
     */
    public BusiTerminalMeetingJoinSettings selectBusiTerminalMeetingJoinSettingsById(Long id);

    /**
     * 查询入会设置列表
     * 
     * @param busiTerminalMeetingJoinSettings 入会设置
     * @return 入会设置集合
     */
    public List<BusiTerminalMeetingJoinSettings> selectBusiTerminalMeetingJoinSettingsList(BusiTerminalMeetingJoinSettings busiTerminalMeetingJoinSettings);

    /**
     * 新增入会设置
     * 
     * @param busiTerminalMeetingJoinSettings 入会设置
     * @return 结果
     */
    public int insertBusiTerminalMeetingJoinSettings(BusiTerminalMeetingJoinSettings busiTerminalMeetingJoinSettings);

    /**
     * 修改入会设置
     * 
     * @param busiTerminalMeetingJoinSettings 入会设置
     * @return 结果
     */
    public int updateBusiTerminalMeetingJoinSettings(BusiTerminalMeetingJoinSettings busiTerminalMeetingJoinSettings);

    /**
     * 删除入会设置
     * 
     * @param id 入会设置ID
     * @return 结果
     */
    public int deleteBusiTerminalMeetingJoinSettingsById(Long id);

    /**
     * 批量删除入会设置
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiTerminalMeetingJoinSettingsByIds(Long[] ids);
}
