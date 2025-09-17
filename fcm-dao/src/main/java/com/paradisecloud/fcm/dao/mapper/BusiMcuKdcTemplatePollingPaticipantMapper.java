package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcTemplatePollingPaticipant;

/**
 * 轮询方案的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
public interface BusiMcuKdcTemplatePollingPaticipantMapper 
{
    /**
     * 查询轮询方案的参会者
     * 
     * @param id 轮询方案的参会者ID
     * @return 轮询方案的参会者
     */
    public BusiMcuKdcTemplatePollingPaticipant selectBusiMcuKdcTemplatePollingPaticipantById(Long id);

    /**
     * 查询轮询方案的参会者列表
     * 
     * @param busiTemplatePollingPaticipant 轮询方案的参会者
     * @return 轮询方案的参会者集合
     */
    public List<BusiMcuKdcTemplatePollingPaticipant> selectBusiMcuKdcTemplatePollingPaticipantList(BusiMcuKdcTemplatePollingPaticipant busiTemplatePollingPaticipant);

    /**
     * 新增轮询方案的参会者
     * 
     * @param busiTemplatePollingPaticipant 轮询方案的参会者
     * @return 结果
     */
    public int insertBusiMcuKdcTemplatePollingPaticipant(BusiMcuKdcTemplatePollingPaticipant busiTemplatePollingPaticipant);

    /**
     * 修改轮询方案的参会者
     * 
     * @param busiTemplatePollingPaticipant 轮询方案的参会者
     * @return 结果
     */
    public int updateBusiMcuKdcTemplatePollingPaticipant(BusiMcuKdcTemplatePollingPaticipant busiTemplatePollingPaticipant);

    /**
     * 删除轮询方案的参会者
     * 
     * @param id 轮询方案的参会者ID
     * @return 结果
     */
    public int deleteBusiMcuKdcTemplatePollingPaticipantById(Long id);
    
    /**
     * <pre>根据轮询方案ID删除参会者记录</pre>
     * @author lilinhai
     * @since 2021-02-25 13:56 
     * @param pollingSchemeId
     * @return int
     */
    public int deletePollingPaticipantByPollingSchemeId(Long pollingSchemeId);

    /**
     * 批量删除轮询方案的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuKdcTemplatePollingPaticipantByIds(Long[] ids);
    
    int deleteBusiMcuKdcTemplatePollingPaticipantByAttendeeIds(String[] attendeeIds);
}
