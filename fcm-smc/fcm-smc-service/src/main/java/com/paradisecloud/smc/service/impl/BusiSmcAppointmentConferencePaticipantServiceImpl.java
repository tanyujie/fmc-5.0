package com.paradisecloud.smc.service.impl;

import java.util.List;

import com.paradisecloud.smc.dao.model.BusiSmcAppointmentConferencePaticipant;
import com.paradisecloud.smc.dao.model.mapper.BusiSmcAppointmentConferencePaticipantMapper;
import com.paradisecloud.smc.service.IBusiSmcAppointmentConferencePaticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author lilinhai
 * @date 2023-03-16
 */
@Service
public class BusiSmcAppointmentConferencePaticipantServiceImpl implements IBusiSmcAppointmentConferencePaticipantService
{
    @Autowired
    private BusiSmcAppointmentConferencePaticipantMapper busiSmcAppointmentConferencePaticipantMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiSmcAppointmentConferencePaticipant selectBusiSmcAppointmentConferencePaticipantById(Integer id)
    {
        return busiSmcAppointmentConferencePaticipantMapper.selectBusiSmcAppointmentConferencePaticipantById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiSmcAppointmentConferencePaticipant 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiSmcAppointmentConferencePaticipant> selectBusiSmcAppointmentConferencePaticipantList(BusiSmcAppointmentConferencePaticipant busiSmcAppointmentConferencePaticipant)
    {
        return busiSmcAppointmentConferencePaticipantMapper.selectBusiSmcAppointmentConferencePaticipantList(busiSmcAppointmentConferencePaticipant);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiSmcAppointmentConferencePaticipant 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusiSmcAppointmentConferencePaticipant(BusiSmcAppointmentConferencePaticipant busiSmcAppointmentConferencePaticipant)
    {
        return busiSmcAppointmentConferencePaticipantMapper.insertBusiSmcAppointmentConferencePaticipant(busiSmcAppointmentConferencePaticipant);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiSmcAppointmentConferencePaticipant 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusiSmcAppointmentConferencePaticipant(BusiSmcAppointmentConferencePaticipant busiSmcAppointmentConferencePaticipant)
    {
        return busiSmcAppointmentConferencePaticipantMapper.updateBusiSmcAppointmentConferencePaticipant(busiSmcAppointmentConferencePaticipant);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmcAppointmentConferencePaticipantByIds(Integer[] ids)
    {
        return busiSmcAppointmentConferencePaticipantMapper.deleteBusiSmcAppointmentConferencePaticipantByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmcAppointmentConferencePaticipantById(Integer id)
    {
        return busiSmcAppointmentConferencePaticipantMapper.deleteBusiSmcAppointmentConferencePaticipantById(id);
    }

    @Override
    public int deleteBusiSmcAppointmentConferencePaticipantByConferenceId(String conferenceId) {
        return busiSmcAppointmentConferencePaticipantMapper.deleteBusiSmcAppointmentConferencePaticipantByConferenceId(conferenceId);
    }

    @Override
    public List<BusiSmcAppointmentConferencePaticipant> selectBusiSmcAppointmentConferencePaticipantListByAppointId(Integer id) {
        return busiSmcAppointmentConferencePaticipantMapper.selectBusiSmcAppointmentConferencePaticipantListByAppointId(id);
    }
}
