package com.paradisecloud.fcm.smartroom.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomDept;

import java.util.List;

/**
 * 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）Service接口
 * 
 * @author lilinhai
 * @date 2024-01-26
 */
public interface IBusiSmartRoomDeptService
{
    /**
     * 查询会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     * 
     * @param id 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）ID
     * @return 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     */
    public BusiSmartRoomDept selectBusiSmartRoomDeptById(Long id);

    /**
     * 查询会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）列表
     * 
     * @param busiSmartRoomDept 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     * @return 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）集合
     */
    public List<BusiSmartRoomDept> selectBusiSmartRoomDeptList(BusiSmartRoomDept busiSmartRoomDept);

    /**
     * 新增会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     * 
     * @param busiSmartRoomDept 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     * @return 结果
     */
    public int insertBusiSmartRoomDept(BusiSmartRoomDept busiSmartRoomDept);

    /**
     * 修改会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     * 
     * @param busiSmartRoomDept 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     * @return 结果
     */
    public int updateBusiSmartRoomDept(BusiSmartRoomDept busiSmartRoomDept);

    /**
     * 批量删除会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeptByIds(Long[] ids);

    /**
     * 删除会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）信息
     * 
     * @param id 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeptById(Long id);
}
