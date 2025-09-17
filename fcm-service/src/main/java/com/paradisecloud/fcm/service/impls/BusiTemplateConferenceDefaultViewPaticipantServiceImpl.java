package com.paradisecloud.fcm.service.impls;

import java.util.List;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceDefaultViewPaticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiTemplateConferenceDefaultViewPaticipant;
import com.paradisecloud.fcm.service.interfaces.IBusiTemplateConferenceDefaultViewPaticipantService;

/**
 * 默认视图的参会者Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-04-08
 */
@Service
public class BusiTemplateConferenceDefaultViewPaticipantServiceImpl implements IBusiTemplateConferenceDefaultViewPaticipantService 
{
    @Autowired
    private BusiTemplateConferenceDefaultViewPaticipantMapper busiTemplateConferenceDefaultViewPaticipantMapper;

    /**
     * 查询默认视图的参会者
     * 
     * @param id 默认视图的参会者ID
     * @return 默认视图的参会者
     */
    @Override
    public BusiTemplateConferenceDefaultViewPaticipant selectBusiTemplateConferenceDefaultViewPaticipantById(Long id)
    {
        return busiTemplateConferenceDefaultViewPaticipantMapper.selectBusiTemplateConferenceDefaultViewPaticipantById(id);
    }

    /**
     * 查询默认视图的参会者列表
     * 
     * @param busiTemplateConferenceDefaultViewPaticipant 默认视图的参会者
     * @return 默认视图的参会者
     */
    @Override
    public List<BusiTemplateConferenceDefaultViewPaticipant> selectBusiTemplateConferenceDefaultViewPaticipantList(BusiTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant)
    {
        return busiTemplateConferenceDefaultViewPaticipantMapper.selectBusiTemplateConferenceDefaultViewPaticipantList(busiTemplateConferenceDefaultViewPaticipant);
    }

    /**
     * 新增默认视图的参会者
     * 
     * @param busiTemplateConferenceDefaultViewPaticipant 默认视图的参会者
     * @return 结果
     */
    @Override
    public int insertBusiTemplateConferenceDefaultViewPaticipant(BusiTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant)
    {
        busiTemplateConferenceDefaultViewPaticipant.setCreateTime(new Date());
        return busiTemplateConferenceDefaultViewPaticipantMapper.insertBusiTemplateConferenceDefaultViewPaticipant(busiTemplateConferenceDefaultViewPaticipant);
    }

    /**
     * 修改默认视图的参会者
     * 
     * @param busiTemplateConferenceDefaultViewPaticipant 默认视图的参会者
     * @return 结果
     */
    @Override
    public int updateBusiTemplateConferenceDefaultViewPaticipant(BusiTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant)
    {
        busiTemplateConferenceDefaultViewPaticipant.setUpdateTime(new Date());
        return busiTemplateConferenceDefaultViewPaticipantMapper.updateBusiTemplateConferenceDefaultViewPaticipant(busiTemplateConferenceDefaultViewPaticipant);
    }

    /**
     * 批量删除默认视图的参会者
     * 
     * @param ids 需要删除的默认视图的参会者ID
     * @return 结果
     */
    @Override
    public int deleteBusiTemplateConferenceDefaultViewPaticipantByIds(Long[] ids)
    {
        return busiTemplateConferenceDefaultViewPaticipantMapper.deleteBusiTemplateConferenceDefaultViewPaticipantByIds(ids);
    }

    /**
     * 删除默认视图的参会者信息
     * 
     * @param id 默认视图的参会者ID
     * @return 结果
     */
    @Override
    public int deleteBusiTemplateConferenceDefaultViewPaticipantById(Long id)
    {
        return busiTemplateConferenceDefaultViewPaticipantMapper.deleteBusiTemplateConferenceDefaultViewPaticipantById(id);
    }
}
