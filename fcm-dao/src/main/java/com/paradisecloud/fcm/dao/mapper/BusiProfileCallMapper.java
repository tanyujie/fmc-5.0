package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiProfileCall;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * call模板Mapper接口
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
public interface BusiProfileCallMapper 
{
    /**
     * 查询call模板
     * 
     * @param id call模板ID
     * @return call模板
     */
    public BusiProfileCall selectBusiProfileCallById(Long id);

    /**
     * 查询call模板列表
     * 
     * @param busiProfileCall call模板
     * @return call模板集合
     */
    public List<BusiProfileCall> selectBusiProfileCallList(BusiProfileCall busiProfileCall);

    /**
     * 新增call模板
     * 
     * @param busiProfileCall call模板
     * @return 结果
     */
    public int insertBusiProfileCall(BusiProfileCall busiProfileCall);

    /**
     * 修改call模板
     * 
     * @param busiProfileCall call模板
     * @return 结果
     */
    public int updateBusiProfileCall(BusiProfileCall busiProfileCall);

    /**
     * 删除call模板
     * 
     * @param id call模板ID
     * @return 结果
     */
    public int deleteBusiProfileCallById(Long id);

    /**
     * 批量删除call模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiProfileCallByIds(Long[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
