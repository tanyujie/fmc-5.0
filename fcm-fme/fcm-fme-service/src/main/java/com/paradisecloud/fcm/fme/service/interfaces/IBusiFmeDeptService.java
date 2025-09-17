package com.paradisecloud.fcm.fme.service.interfaces;

import java.util.List;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiFmeDept;

/**
 * FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）Service接口
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
public interface IBusiFmeDeptService 
{
    
    /**
     * 查询FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param id FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）ID
     * @return FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     */
    public ModelBean selectBusiFmeDeptById(Long id);
    
    ModelBean selectBusiFmeDeptByDeptId(Long deptId);

    /**
     * 查询FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）列表
     * 
     * @param busiFmeDept FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * @return FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）集合
     */
    public List<ModelBean> selectBusiFmeDeptList(BusiFmeDept busiFmeDept);

    /**
     * 新增FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param busiFmeDept FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int insertBusiFmeDept(BusiFmeDept busiFmeDept);

    /**
     * 修改FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param busiFmeDept FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * @return 结果
     */
    public int updateBusiFmeDept(BusiFmeDept busiFmeDept);

    /**
     * 批量删除FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiFmeDeptByIds(Long[] ids);

    /**
     * 删除FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）信息
     * 
     * @param id FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiFmeDeptById(Long id);
    
    ModelBean toModelBean(BusiFmeDept busiFmeDept);
}
