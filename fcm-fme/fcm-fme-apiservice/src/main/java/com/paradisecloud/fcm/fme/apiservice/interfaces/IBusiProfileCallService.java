package com.paradisecloud.fcm.fme.apiservice.interfaces;

import java.util.List;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiProfileCall;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.CallProfile;

/**
 * call模板Service接口
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
public interface IBusiProfileCallService 
{
    
    /**
     * <pre>同步所有</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28 
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    void syncAllProfile(FmeBridge fmeBridge, CallProfileProcessor callProfileProcessor);
    
    public List<ModelBean> getAllCallProfiles(Long deptId);
    
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
     * 批量删除call模板
     * 
     * @param ids 需要删除的call模板ID
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

    /**
     * 删除call模板信息
     * 
     * @param id call模板ID
     * @return 结果
     */
    public RestResponse deleteBusiProfileCallById(BusiProfileCall busiProfileCall);
    
    public static interface CallProfileProcessor
    {
        void process(CallProfile callProfile);
    }
}
