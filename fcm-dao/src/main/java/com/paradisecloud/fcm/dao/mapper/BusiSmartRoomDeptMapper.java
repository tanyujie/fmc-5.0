package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiSmartRoomDept;

import java.util.List;

/**
 * 智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）Mapper接口
 *
 * @author lilinhai
 * @date 2024-02-19
 */
public interface BusiSmartRoomDeptMapper
{
    /**
     * 查询智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）
     *
     * @param id 智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）ID
     * @return 智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）
     */
    public BusiSmartRoomDept selectBusiSmartRoomDeptById(Long id);

    /**
     * 查询智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）列表
     *
     * @param busiSmartRoomDept 智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）
     * @return 智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）集合
     */
    public List<BusiSmartRoomDept> selectBusiSmartRoomDeptList(BusiSmartRoomDept busiSmartRoomDept);

    /**
     * 新增智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）
     *
     * @param busiSmartRoomDept 智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）
     * @return 结果
     */
    public int insertBusiSmartRoomDept(BusiSmartRoomDept busiSmartRoomDept);

    /**
     * 修改智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）
     *
     * @param busiSmartRoomDept 智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）
     * @return 结果
     */
    public int updateBusiSmartRoomDept(BusiSmartRoomDept busiSmartRoomDept);

    /**
     * 删除智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）
     *
     * @param id 智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeptById(Long id);

    /**
     * 批量删除智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeptByIds(Long[] ids);
}