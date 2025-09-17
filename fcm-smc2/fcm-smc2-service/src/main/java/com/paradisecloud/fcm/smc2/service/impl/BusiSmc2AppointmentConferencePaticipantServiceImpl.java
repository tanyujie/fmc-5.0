package com.paradisecloud.fcm.smc2.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import com.paradisecloud.fcm.dao.mapper.BusiSmc2AppointmentConferencePaticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiSmc2AppointmentConferencePaticipant;
import com.paradisecloud.fcm.smc2.service.IBusiSmc2AppointmentConferencePaticipantService;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author lilinhai
 * @date 2023-05-04
 */
@Service
public class BusiSmc2AppointmentConferencePaticipantServiceImpl implements IBusiSmc2AppointmentConferencePaticipantService 
{
    @Resource
    private BusiSmc2AppointmentConferencePaticipantMapper busiSmc2AppointmentConferencePaticipantMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiSmc2AppointmentConferencePaticipant selectBusiSmc2AppointmentConferencePaticipantById(Integer id)
    {
        return busiSmc2AppointmentConferencePaticipantMapper.selectBusiSmc2AppointmentConferencePaticipantById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiSmc2AppointmentConferencePaticipant 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiSmc2AppointmentConferencePaticipant> selectBusiSmc2AppointmentConferencePaticipantList(BusiSmc2AppointmentConferencePaticipant busiSmc2AppointmentConferencePaticipant)
    {
        return busiSmc2AppointmentConferencePaticipantMapper.selectBusiSmc2AppointmentConferencePaticipantList(busiSmc2AppointmentConferencePaticipant);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiSmc2AppointmentConferencePaticipant 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusiSmc2AppointmentConferencePaticipant(BusiSmc2AppointmentConferencePaticipant busiSmc2AppointmentConferencePaticipant)
    {
        return busiSmc2AppointmentConferencePaticipantMapper.insertBusiSmc2AppointmentConferencePaticipant(busiSmc2AppointmentConferencePaticipant);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiSmc2AppointmentConferencePaticipant 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusiSmc2AppointmentConferencePaticipant(BusiSmc2AppointmentConferencePaticipant busiSmc2AppointmentConferencePaticipant)
    {
        return busiSmc2AppointmentConferencePaticipantMapper.updateBusiSmc2AppointmentConferencePaticipant(busiSmc2AppointmentConferencePaticipant);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmc2AppointmentConferencePaticipantByIds(Integer[] ids)
    {
        return busiSmc2AppointmentConferencePaticipantMapper.deleteBusiSmc2AppointmentConferencePaticipantByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmc2AppointmentConferencePaticipantById(Integer id)
    {
        return busiSmc2AppointmentConferencePaticipantMapper.deleteBusiSmc2AppointmentConferencePaticipantById(id);
    }

    @Override
    public List<BusiSmc2AppointmentConferencePaticipant> selectBusiSmc2AppointmentConferencePaticipantListByAppointId(Integer id) {
        return busiSmc2AppointmentConferencePaticipantMapper.selectBusiSmc2AppointmentConferencePaticipantListByAppointId(id);
    }
}
