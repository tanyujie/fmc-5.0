package com.paradisecloud.fcm.fme.apiservice.interfaces;

import java.util.List;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiProfileDtmf;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.DtmfProfile;

/**
 * DTMF模板Service接口
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
public interface IBusiProfileDtmfService 
{
    
    /**
     * <pre>同步所有</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28 
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    void syncAllProfile(FmeBridge fmeBridge, DtmfProfileProcessor dtmfProfileProcessor);
    
    /**
     * <pre>获取当前登录用户所属部门的主用的FME的DTMF列表</pre>
     * @author lilinhai
     * @since 2021-01-26 15:28 
     * @return List<ModelBean>
     */
    List<ModelBean> getAllDtmfProfiles(Long deptId);
    
    /**
     * 查询DTMF模板
     * 
     * @param id DTMF模板ID
     * @return DTMF模板
     */
    public BusiProfileDtmf selectBusiProfileDtmfById(Long id);

    /**
     * 查询DTMF模板列表
     * 
     * @param busiProfileDtmf DTMF模板
     * @return DTMF模板集合
     */
    public List<BusiProfileDtmf> selectBusiProfileDtmfList(BusiProfileDtmf busiProfileDtmf);

    /**
     * 新增DTMF模板
     * 
     * @param busiProfileDtmf DTMF模板
     * @return 结果
     */
    public int insertBusiProfileDtmf(BusiProfileDtmf busiProfileDtmf);

    /**
     * 修改DTMF模板
     * 
     * @param busiProfileDtmf DTMF模板
     * @return 结果
     */
    public int updateBusiProfileDtmf(BusiProfileDtmf busiProfileDtmf);

    /**
     * 批量删除DTMF模板
     * 
     * @param ids 需要删除的DTMF模板ID
     * @return 结果
     */
    public int deleteBusiProfileDtmfByIds(Long[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();

    /**
     * 删除DTMF模板信息
     * 
     * @param id DTMF模板ID
     * @return 结果
     */
    public RestResponse deleteBusiProfileDtmfById(BusiProfileDtmf busiProfileDtmf);
    
    public static interface DtmfProfileProcessor
    {
        void process(DtmfProfile dtmfProfile);
    }
}
