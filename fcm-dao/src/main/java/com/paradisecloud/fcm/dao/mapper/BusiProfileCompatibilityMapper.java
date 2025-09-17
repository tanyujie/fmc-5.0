package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiProfileCompatibility;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * 兼容性参数模板Mapper接口
 * 
 * @author lilinhai
 * @date 2021-07-27
 */
public interface BusiProfileCompatibilityMapper 
{
    /**
     * 查询兼容性参数模板
     * 
     * @param id 兼容性参数模板ID
     * @return 兼容性参数模板
     */
    public BusiProfileCompatibility selectBusiProfileCompatibilityById(Long id);

    /**
     * 查询兼容性参数模板列表
     * 
     * @param busiProfileCompatibility 兼容性参数模板
     * @return 兼容性参数模板集合
     */
    public List<BusiProfileCompatibility> selectBusiProfileCompatibilityList(BusiProfileCompatibility busiProfileCompatibility);

    /**
     * 新增兼容性参数模板
     * 
     * @param busiProfileCompatibility 兼容性参数模板
     * @return 结果
     */
    public int insertBusiProfileCompatibility(BusiProfileCompatibility busiProfileCompatibility);

    /**
     * 修改兼容性参数模板
     * 
     * @param busiProfileCompatibility 兼容性参数模板
     * @return 结果
     */
    public int updateBusiProfileCompatibility(BusiProfileCompatibility busiProfileCompatibility);

    /**
     * 删除兼容性参数模板
     * 
     * @param id 兼容性参数模板ID
     * @return 结果
     */
    public int deleteBusiProfileCompatibilityById(Long id);

    /**
     * 批量删除兼容性参数模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiProfileCompatibilityByIds(Long[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
