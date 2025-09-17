package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiFsbcServerDept;

/**
 * FSBC服务器-部门映射Mapper接口
 * 
 * @author lilinhai
 * @date 2021-04-21
 */
public interface BusiFsbcServerDeptMapper 
{
    /**
     * 查询FSBC服务器-部门映射
     * 
     * @param id FSBC服务器-部门映射ID
     * @return FSBC服务器-部门映射
     */
    public BusiFsbcServerDept selectBusiFsbcServerDeptById(Long id);

    /**
     * 查询FSBC服务器-部门映射列表
     * 
     * @param busiFsbcServerDept FSBC服务器-部门映射
     * @return FSBC服务器-部门映射集合
     */
    public List<BusiFsbcServerDept> selectBusiFsbcServerDeptList(BusiFsbcServerDept busiFsbcServerDept);

    /**
     * 新增FSBC服务器-部门映射
     * 
     * @param busiFsbcServerDept FSBC服务器-部门映射
     * @return 结果
     */
    public int insertBusiFsbcServerDept(BusiFsbcServerDept busiFsbcServerDept);

    /**
     * 修改FSBC服务器-部门映射
     * 
     * @param busiFsbcServerDept FSBC服务器-部门映射
     * @return 结果
     */
    public int updateBusiFsbcServerDept(BusiFsbcServerDept busiFsbcServerDept);

    /**
     * 删除FSBC服务器-部门映射
     * 
     * @param id FSBC服务器-部门映射ID
     * @return 结果
     */
    public int deleteBusiFsbcServerDeptById(Long id);

    /**
     * 批量删除FSBC服务器-部门映射
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiFsbcServerDeptByIds(Long[] ids);
}
