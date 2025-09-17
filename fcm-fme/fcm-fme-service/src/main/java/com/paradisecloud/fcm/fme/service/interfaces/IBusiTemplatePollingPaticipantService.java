package com.paradisecloud.fcm.fme.service.interfaces;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiTemplatePollingPaticipant;

/**
 * 轮询方案的参会者Service接口
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
public interface IBusiTemplatePollingPaticipantService 
{
    /**
     * 查询轮询方案的参会者
     * 
     * @param id 轮询方案的参会者ID
     * @return 轮询方案的参会者
     */
    public BusiTemplatePollingPaticipant selectBusiTemplatePollingPaticipantById(Long id);

    /**
     * 查询轮询方案的参会者列表
     * 
     * @param busiTemplatePollingPaticipant 轮询方案的参会者
     * @return 轮询方案的参会者集合
     */
    public List<BusiTemplatePollingPaticipant> selectBusiTemplatePollingPaticipantList(BusiTemplatePollingPaticipant busiTemplatePollingPaticipant);

    /**
     * 新增轮询方案的参会者
     * 
     * @param busiTemplatePollingPaticipant 轮询方案的参会者
     * @return 结果
     */
    public int insertBusiTemplatePollingPaticipant(BusiTemplatePollingPaticipant busiTemplatePollingPaticipant);

    /**
     * 修改轮询方案的参会者
     * 
     * @param busiTemplatePollingPaticipant 轮询方案的参会者
     * @return 结果
     */
    public int updateBusiTemplatePollingPaticipant(BusiTemplatePollingPaticipant busiTemplatePollingPaticipant);

    /**
     * 批量删除轮询方案的参会者
     * 
     * @param ids 需要删除的轮询方案的参会者ID
     * @return 结果
     */
    public int deleteBusiTemplatePollingPaticipantByIds(Long[] ids);

    /**
     * 删除轮询方案的参会者信息
     * 
     * @param id 轮询方案的参会者ID
     * @return 结果
     */
    public int deleteBusiTemplatePollingPaticipantById(Long id);
}
