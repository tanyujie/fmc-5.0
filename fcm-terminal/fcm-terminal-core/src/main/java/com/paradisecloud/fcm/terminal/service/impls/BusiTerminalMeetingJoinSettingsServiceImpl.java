package com.paradisecloud.fcm.terminal.service.impls;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paradisecloud.fcm.dao.mapper.BusiTerminalMeetingJoinSettingsMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminalMeetingJoinSettings;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalMeetingJoinSettingsService;

/**
 * 入会设置Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-07-08
 */
@Service
public class BusiTerminalMeetingJoinSettingsServiceImpl implements IBusiTerminalMeetingJoinSettingsService 
{
    @Autowired
    private BusiTerminalMeetingJoinSettingsMapper busiTerminalMeetingJoinSettingsMapper;

    /**
     * 查询入会设置
     * 
     * @param id 入会设置ID
     * @return 入会设置
     */
    @Override
    public BusiTerminalMeetingJoinSettings selectBusiTerminalMeetingJoinSettingsById(Long id)
    {
        return TerminalCache.getInstance().getById(id);
    }

    /**
     * 查询入会设置列表
     * 
     * @param busiTerminalMeetingJoinSettings 入会设置
     * @return 入会设置
     */
    @Override
    public List<BusiTerminalMeetingJoinSettings> selectBusiTerminalMeetingJoinSettingsList(BusiTerminalMeetingJoinSettings busiTerminalMeetingJoinSettings)
    {
        return busiTerminalMeetingJoinSettingsMapper.selectBusiTerminalMeetingJoinSettingsList(busiTerminalMeetingJoinSettings);
    }

    /**
     * 新增入会设置
     * 
     * @param busiTerminalMeetingJoinSettings 入会设置
     * @return 结果
     */
    @Override
    public int insertBusiTerminalMeetingJoinSettings(BusiTerminalMeetingJoinSettings busiTerminalMeetingJoinSettings)
    {
        busiTerminalMeetingJoinSettings.setCreateTime(new Date());
        
        int c = busiTerminalMeetingJoinSettingsMapper.insertBusiTerminalMeetingJoinSettings(busiTerminalMeetingJoinSettings);
        if (c > 0)
        {
            TerminalCache.getInstance().update(busiTerminalMeetingJoinSettings);
        }
        return c;
    }

    /**
     * 修改入会设置
     * 
     * @param busiTerminalMeetingJoinSettings 入会设置
     * @return 结果
     */
    @Override
    public int updateBusiTerminalMeetingJoinSettings(BusiTerminalMeetingJoinSettings busiTerminalMeetingJoinSettings)
    {
        if (TerminalCache.getInstance().getById(busiTerminalMeetingJoinSettings.getId()) == null)
        {
            return insertBusiTerminalMeetingJoinSettings(busiTerminalMeetingJoinSettings);
        }
        
        busiTerminalMeetingJoinSettings.setUpdateTime(new Date());
        
        int c = busiTerminalMeetingJoinSettingsMapper.updateBusiTerminalMeetingJoinSettings(busiTerminalMeetingJoinSettings);
        if (c > 0)
        {
            TerminalCache.getInstance().update(busiTerminalMeetingJoinSettings);
        }
        return c;
    }

    /**
     * 批量删除入会设置
     * 
     * @param ids 需要删除的入会设置ID
     * @return 结果
     */
    @Override
    public int deleteBusiTerminalMeetingJoinSettingsByIds(Long[] ids)
    {
        return busiTerminalMeetingJoinSettingsMapper.deleteBusiTerminalMeetingJoinSettingsByIds(ids);
    }

    /**
     * 删除入会设置信息
     * 
     * @param id 入会设置ID
     * @return 结果
     */
    @Override
    public int deleteBusiTerminalMeetingJoinSettingsById(Long id)
    {
        int c = busiTerminalMeetingJoinSettingsMapper.deleteBusiTerminalMeetingJoinSettingsById(id);
        if (c > 0)
        {
            TerminalCache.getInstance().removeTerminalMeetingJoinSettings(id);
        }
        return c;
    }
}
