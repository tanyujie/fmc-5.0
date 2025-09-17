package com.paradisecloud.fcm.fme.service.impls;

import java.util.List;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateParticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiTemplateParticipant;
import com.paradisecloud.fcm.fme.service.interfaces.IBusiTemplateParticipantService;

/**
 * 会议模板的参会者Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@Service
public class BusiTemplateParticipantServiceImpl implements IBusiTemplateParticipantService 
{
    @Autowired
    private BusiTemplateParticipantMapper busiTemplateParticipantMapper;

    /**
     * 查询会议模板的参会者
     * 
     * @param id 会议模板的参会者ID
     * @return 会议模板的参会者
     */
    @Override
    public BusiTemplateParticipant selectBusiTemplateParticipantById(Long id)
    {
        return busiTemplateParticipantMapper.selectBusiTemplateParticipantById(id);
    }

    /**
     * 查询会议模板的参会者列表
     * 
     * @param busiTemplateParticipant 会议模板的参会者
     * @return 会议模板的参会者
     */
    @Override
    public List<BusiTemplateParticipant> selectBusiTemplateParticipantList(BusiTemplateParticipant busiTemplateParticipant)
    {
        return busiTemplateParticipantMapper.selectBusiTemplateParticipantList(busiTemplateParticipant);
    }

    /**
     * 新增会议模板的参会者
     * 
     * @param busiTemplateParticipant 会议模板的参会者
     * @return 结果
     */
    @Override
    public int insertBusiTemplateParticipant(BusiTemplateParticipant busiTemplateParticipant)
    {
        busiTemplateParticipant.setCreateTime(new Date());
        return busiTemplateParticipantMapper.insertBusiTemplateParticipant(busiTemplateParticipant);
    }

    /**
     * 修改会议模板的参会者
     * 
     * @param busiTemplateParticipant 会议模板的参会者
     * @return 结果
     */
    @Override
    public int updateBusiTemplateParticipant(BusiTemplateParticipant busiTemplateParticipant)
    {
        busiTemplateParticipant.setUpdateTime(new Date());
        return busiTemplateParticipantMapper.updateBusiTemplateParticipant(busiTemplateParticipant);
    }

    /**
     * 批量删除会议模板的参会者
     * 
     * @param ids 需要删除的会议模板的参会者ID
     * @return 结果
     */
    @Override
    public int deleteBusiTemplateParticipantByIds(Long[] ids)
    {
        return busiTemplateParticipantMapper.deleteBusiTemplateParticipantByIds(ids);
    }

    /**
     * 删除会议模板的参会者信息
     * 
     * @param id 会议模板的参会者ID
     * @return 结果
     */
    @Override
    public int deleteBusiTemplateParticipantById(Long id)
    {
        return busiTemplateParticipantMapper.deleteBusiTemplateParticipantById(id);
    }
}
