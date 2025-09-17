package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiDialPlanRuleInbound;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * 内呼计划Mapper接口
 * 
 * @author lilinhai
 * @date 2022-05-13
 */
public interface BusiDialPlanRuleInboundMapper 
{
    /**
     * 查询内呼计划
     * 
     * @param id 内呼计划ID
     * @return 内呼计划
     */
    public BusiDialPlanRuleInbound selectBusiDialPlanRuleInboundById(Integer id);

    /**
     * 查询内呼计划列表
     * 
     * @param busiDialPlanRuleInbound 内呼计划
     * @return 内呼计划集合
     */
    public List<BusiDialPlanRuleInbound> selectBusiDialPlanRuleInboundList(BusiDialPlanRuleInbound busiDialPlanRuleInbound);

    /**
     * 新增内呼计划
     * 
     * @param busiDialPlanRuleInbound 内呼计划
     * @return 结果
     */
    public int insertBusiDialPlanRuleInbound(BusiDialPlanRuleInbound busiDialPlanRuleInbound);

    /**
     * 修改内呼计划
     * 
     * @param busiDialPlanRuleInbound 内呼计划
     * @return 结果
     */
    public int updateBusiDialPlanRuleInbound(BusiDialPlanRuleInbound busiDialPlanRuleInbound);

    /**
     * 删除内呼计划
     * 
     * @param id 内呼计划ID
     * @return 结果
     */
    public int deleteBusiDialPlanRuleInboundById(Integer id);

    /**
     * 批量删除内呼计划
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiDialPlanRuleInboundByIds(Integer[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
