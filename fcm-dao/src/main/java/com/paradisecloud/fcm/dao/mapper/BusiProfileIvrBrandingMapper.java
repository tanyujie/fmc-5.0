package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiProfileIvrBranding;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * ivrBranding模板Mapper接口
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
public interface BusiProfileIvrBrandingMapper 
{
    /**
     * 查询ivrBranding模板
     * 
     * @param id ivrBranding模板ID
     * @return ivrBranding模板
     */
    public BusiProfileIvrBranding selectBusiProfileIvrBrandingById(Long id);

    /**
     * 查询ivrBranding模板列表
     * 
     * @param busiProfileIvrBranding ivrBranding模板
     * @return ivrBranding模板集合
     */
    public List<BusiProfileIvrBranding> selectBusiProfileIvrBrandingList(BusiProfileIvrBranding busiProfileIvrBranding);

    /**
     * 新增ivrBranding模板
     * 
     * @param busiProfileIvrBranding ivrBranding模板
     * @return 结果
     */
    public int insertBusiProfileIvrBranding(BusiProfileIvrBranding busiProfileIvrBranding);

    /**
     * 修改ivrBranding模板
     * 
     * @param busiProfileIvrBranding ivrBranding模板
     * @return 结果
     */
    public int updateBusiProfileIvrBranding(BusiProfileIvrBranding busiProfileIvrBranding);

    /**
     * 删除ivrBranding模板
     * 
     * @param id ivrBranding模板ID
     * @return 结果
     */
    public int deleteBusiProfileIvrBrandingById(Long id);

    /**
     * 批量删除ivrBranding模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiProfileIvrBrandingByIds(Long[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
