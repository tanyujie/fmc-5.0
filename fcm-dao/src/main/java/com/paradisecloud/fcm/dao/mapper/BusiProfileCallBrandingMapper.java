package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiProfileCallBranding;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * callBranding模板Mapper接口
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
public interface BusiProfileCallBrandingMapper 
{
    /**
     * 查询callBranding模板
     * 
     * @param id callBranding模板ID
     * @return callBranding模板
     */
    public BusiProfileCallBranding selectBusiProfileCallBrandingById(Long id);

    /**
     * 查询callBranding模板列表
     * 
     * @param busiProfileCallBranding callBranding模板
     * @return callBranding模板集合
     */
    public List<BusiProfileCallBranding> selectBusiProfileCallBrandingList(BusiProfileCallBranding busiProfileCallBranding);

    /**
     * 新增callBranding模板
     * 
     * @param busiProfileCallBranding callBranding模板
     * @return 结果
     */
    public int insertBusiProfileCallBranding(BusiProfileCallBranding busiProfileCallBranding);

    /**
     * 修改callBranding模板
     * 
     * @param busiProfileCallBranding callBranding模板
     * @return 结果
     */
    public int updateBusiProfileCallBranding(BusiProfileCallBranding busiProfileCallBranding);

    /**
     * 删除callBranding模板
     * 
     * @param id callBranding模板ID
     * @return 结果
     */
    public int deleteBusiProfileCallBrandingById(Long id);

    /**
     * 批量删除callBranding模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiProfileCallBrandingByIds(Long[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
