package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiMqttDept;

/**
 * Mapper接口
 * 
 * @author zyz
 * @date 2021-07-21
 */
public interface BusiMqttDeptMapper 
{
    /**
             * 查询租户分配mqtt资源的信息
     * 
     * @param id 
     * @return BusiMqttDept
     */
    public BusiMqttDept selectBusiMqttDeptById(Long id);

    /**
            * 查询租户分配mqtt资源的信息列表
     * 
     * @param busiMqttDept 
     * @return List<BusiMqttDept>
     */
    public List<BusiMqttDept> selectBusiMqttDeptList(BusiMqttDept busiMqttDept);

    /**
             * 新增租户分配mqtt资源的信息
     * 
     * @param busiMqttDept 
     * @return int
     */
    public int insertBusiMqttDept(BusiMqttDept busiMqttDept);

    /**
             * 修改租户分配mqtt资源的信息
     * 
     * @param busiMqttDept 
     * @return int
     */
    public int updateBusiMqttDept(BusiMqttDept busiMqttDept);

    /**
             * 删除租户分配mqtt资源的信息
     * 
     * @param id 
     * @return int
     */
    public int deleteBusiMqttDeptById(Long id);

    /**
            * 批量删除租户分配mqtt资源的信息
     * 
     * @param ids 
     * @return int
     */
    public int deleteBusiMqttDeptByIds(Long[] ids);
}
