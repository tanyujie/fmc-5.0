package com.paradisecloud.fcm.mqtt.interfaces;

import java.util.List;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMqttDept;

/**
   * 租户分配mqtt资源接口
 * 
 * @author zyz
 * @date 2021-07-21
 */
public interface IBusiMqttDeptService 
{
    /**
             * 查询租户分配mqtt资源
     * 
     * @param id 
     * @return BusiMqttDept
     */
    public BusiMqttDept selectBusiMqttDeptById(Long id);

    /**
             * 查询租户分配mqtt资源列表
     * 
     * @param busiMqttDept
     * @return List<BusiMqttDept>
     */
    public List<ModelBean> selectBusiMqttDeptList(BusiMqttDept busiMqttDept);

    /**
             * 新增租户分配mqtt资源
     * 
     * @param busiMqttDept 
     * @return int
     */
    public int insertBusiMqttDept(BusiMqttDept busiMqttDept);

    /**
             * 修改租户分配mqtt资源
     * 
     * @param busiMqttDept 
     * @return int
     */
    public int updateBusiMqttDept(BusiMqttDept busiMqttDept);

    /**
             * 批量删除租户分配mqtt资源
     * 
     * @param ids 
     * @return int
     */
    public int deleteBusiMqttDeptByIds(Long[] ids);

    /**
             * 删除租户分配mqtt资源
     * 
     * @param id
     * @return int
     */
    public int deleteBusiMqttDeptById(Long id);
}
