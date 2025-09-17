package com.paradisecloud.smc.service.impl;

import java.util.List;

import com.github.pagehelper.PageHelper;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.smc.dao.model.BusiSmcTemplateConference;
import com.paradisecloud.smc.dao.model.mapper.BusiSmcTemplateConferenceMapper;
import com.paradisecloud.smc.service.IBusiSmcTemplateConferenceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * smc模板会议关联Service业务层处理
 * 
 * @author nj
 * @date 2022-09-20
 */
@Service
public class BusiSmcTemplateConferenceServiceImpl implements IBusiSmcTemplateConferenceService
{
    @Resource
    private BusiSmcTemplateConferenceMapper busiSmcTemplateConferenceMapper;

    /**
     * 查询smc模板会议关联
     * 
     * @param id smc模板会议关联ID
     * @return smc模板会议关联
     */
    @Override
    public BusiSmcTemplateConference selectBusiSmcTemplateConferenceById(Integer id)
    {
        return busiSmcTemplateConferenceMapper.selectBusiSmcTemplateConferenceById(id);
    }

    /**
     * 查询smc模板会议关联列表
     * 
     * @param busiSmcTemplateConference smc模板会议关联
     * @return smc模板会议关联
     */
    @Override
    public List<BusiSmcTemplateConference> selectBusiSmcTemplateConferenceList(BusiSmcTemplateConference busiSmcTemplateConference)
    {
        return busiSmcTemplateConferenceMapper.selectBusiSmcTemplateConferenceList(busiSmcTemplateConference);
    }

    /**
     * 新增smc模板会议关联
     * 
     * @param busiSmcTemplateConference smc模板会议关联
     * @return 结果
     */
    @Override
    public int insertBusiSmcTemplateConference(BusiSmcTemplateConference busiSmcTemplateConference)
    {
        return busiSmcTemplateConferenceMapper.insertBusiSmcTemplateConference(busiSmcTemplateConference);
    }

    /**
     * 修改smc模板会议关联
     * 
     * @param busiSmcTemplateConference smc模板会议关联
     * @return 结果
     */
    @Override
    public int updateBusiSmcTemplateConference(BusiSmcTemplateConference busiSmcTemplateConference)
    {
        return busiSmcTemplateConferenceMapper.updateBusiSmcTemplateConference(busiSmcTemplateConference);
    }

    /**
     * 批量删除smc模板会议关联
     * 
     * @param ids 需要删除的smc模板会议关联ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmcTemplateConferenceByIds(Integer[] ids)
    {
        return busiSmcTemplateConferenceMapper.deleteBusiSmcTemplateConferenceByIds(ids);
    }

    /**
     * 删除smc模板会议关联信息
     * 
     * @param id smc模板会议关联ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmcTemplateConferenceById(Integer id)
    {
        return busiSmcTemplateConferenceMapper.deleteBusiSmcTemplateConferenceById(id);
    }

    @Override
    public int deleteBusiSmcTemplateConferenceByConferenceId(String conferenceId) {
        return busiSmcTemplateConferenceMapper.deleteBusiSmcTemplateConferenceByConferenceId(conferenceId);
    }

    @Override
    public List<BusiSmcTemplateConference> selectTemplateConferenceList(Long deptId,int pageIndex,int pageSize) {
        PageHelper.startPage(pageIndex,pageSize);
        List<BusiSmcTemplateConference> templateConferences = busiSmcTemplateConferenceMapper.selectTemplateConferenceList(deptId);
        return templateConferences;
    }
}
