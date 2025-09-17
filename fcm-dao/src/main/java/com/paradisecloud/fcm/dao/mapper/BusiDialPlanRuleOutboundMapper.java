package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiDialPlanRuleOutbound;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * 外呼计划Mapper接口
 * 
 * @author lilinhai
 * @date 2022-05-13
 */
public interface BusiDialPlanRuleOutboundMapper 
{
    /**
     * 查询外呼计划
     * 
     * @param id 外呼计划ID
     * @return 外呼计划
     */
    public BusiDialPlanRuleOutbound selectBusiDialPlanRuleOutboundById(Integer id);

    /**
     * 查询外呼计划列表
     * 
     * @param busiDialPlanRuleOutbound 外呼计划
     * @return 外呼计划集合
     */
    public List<BusiDialPlanRuleOutbound> selectBusiDialPlanRuleOutboundList(BusiDialPlanRuleOutbound busiDialPlanRuleOutbound);

    /**
     * 新增外呼计划
     * 
     * @param busiDialPlanRuleOutbound 外呼计划
     * @return 结果
     */
    public int insertBusiDialPlanRuleOutbound(BusiDialPlanRuleOutbound busiDialPlanRuleOutbound);

    /**
     * 修改外呼计划
     * 
     * @param busiDialPlanRuleOutbound 外呼计划
     * @return 结果
     */
    public int updateBusiDialPlanRuleOutbound(BusiDialPlanRuleOutbound busiDialPlanRuleOutbound);

    /**
     * 删除外呼计划
     * 
     * @param id 外呼计划ID
     * @return 结果
     */
    public int deleteBusiDialPlanRuleOutboundById(Integer id);

    /**
     * 批量删除外呼计划
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiDialPlanRuleOutboundByIds(Integer[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
