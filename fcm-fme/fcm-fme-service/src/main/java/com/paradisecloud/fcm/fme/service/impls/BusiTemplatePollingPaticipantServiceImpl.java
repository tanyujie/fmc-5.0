package com.paradisecloud.fcm.fme.service.impls;

import java.util.List;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.paradisecloud.fcm.dao.mapper.BusiTemplatePollingPaticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiTemplatePollingPaticipant;
import com.paradisecloud.fcm.fme.service.interfaces.IBusiTemplatePollingPaticipantService;

/**
 * 轮询方案的参会者Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
@Service
public class BusiTemplatePollingPaticipantServiceImpl implements IBusiTemplatePollingPaticipantService 
{
    @Autowired
    private BusiTemplatePollingPaticipantMapper busiTemplatePollingPaticipantMapper;

    /**
     * 查询轮询方案的参会者
     * 
     * @param id 轮询方案的参会者ID
     * @return 轮询方案的参会者
     */
    @Override
    public BusiTemplatePollingPaticipant selectBusiTemplatePollingPaticipantById(Long id)
    {
        return busiTemplatePollingPaticipantMapper.selectBusiTemplatePollingPaticipantById(id);
    }

    /**
     * 查询轮询方案的参会者列表
     * 
     * @param busiTemplatePollingPaticipant 轮询方案的参会者
     * @return 轮询方案的参会者
     */
    @Override
    public List<BusiTemplatePollingPaticipant> selectBusiTemplatePollingPaticipantList(BusiTemplatePollingPaticipant busiTemplatePollingPaticipant)
    {
        return busiTemplatePollingPaticipantMapper.selectBusiTemplatePollingPaticipantList(busiTemplatePollingPaticipant);
    }

    /**
     * 新增轮询方案的参会者
     * 
     * @param busiTemplatePollingPaticipant 轮询方案的参会者
     * @return 结果
     */
    @Override
    public int insertBusiTemplatePollingPaticipant(BusiTemplatePollingPaticipant busiTemplatePollingPaticipant)
    {
        busiTemplatePollingPaticipant.setCreateTime(new Date());
        return busiTemplatePollingPaticipantMapper.insertBusiTemplatePollingPaticipant(busiTemplatePollingPaticipant);
    }

    /**
     * 修改轮询方案的参会者
     * 
     * @param busiTemplatePollingPaticipant 轮询方案的参会者
     * @return 结果
     */
    @Override
    public int updateBusiTemplatePollingPaticipant(BusiTemplatePollingPaticipant busiTemplatePollingPaticipant)
    {
        busiTemplatePollingPaticipant.setUpdateTime(new Date());
        return busiTemplatePollingPaticipantMapper.updateBusiTemplatePollingPaticipant(busiTemplatePollingPaticipant);
    }

    /**
     * 批量删除轮询方案的参会者
     * 
     * @param ids 需要删除的轮询方案的参会者ID
     * @return 结果
     */
    @Override
    public int deleteBusiTemplatePollingPaticipantByIds(Long[] ids)
    {
        return busiTemplatePollingPaticipantMapper.deleteBusiTemplatePollingPaticipantByIds(ids);
    }

    /**
     * 删除轮询方案的参会者信息
     * 
     * @param id 轮询方案的参会者ID
     * @return 结果
     */
    @Override
    public int deleteBusiTemplatePollingPaticipantById(Long id)
    {
        return busiTemplatePollingPaticipantMapper.deleteBusiTemplatePollingPaticipantById(id);
    }
}
