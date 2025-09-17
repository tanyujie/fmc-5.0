package com.paradisecloud.fcm.fme.apiservice.interfaces;

import java.util.List;

import org.apache.http.NameValuePair;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiCallLegProfile;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;

/**
 * 入会方案配置，控制参会者进入会议的方案Service接口
 * 
 * @author lilinhai
 * @date 2021-01-26
 */
public interface IBusiCallLegProfileService 
{
    /**
     * 查询入会方案配置，控制参会者进入会议的方案
     * 
     * @param id 入会方案配置，控制参会者进入会议的方案ID
     * @return 入会方案配置，控制参会者进入会议的方案
     */
    public BusiCallLegProfile selectBusiCallLegProfileById(Long id);

    /**
     * 查询入会方案配置，控制参会者进入会议的方案列表
     * 
     * @param busiCallLegProfile 入会方案配置，控制参会者进入会议的方案
     * @return 入会方案配置，控制参会者进入会议的方案集合
     */
    public List<BusiCallLegProfile> selectBusiCallLegProfileList(BusiCallLegProfile busiCallLegProfile);
    
    /**
     * <pre>创建默认入会方案</pre>
     * @author lilinhai
     * @since 2021-03-08 15:36 
     * @param fmeBridge
     * @return String
     */
    String createDefaultCalllegProfile(FmeBridge fmeBridge, long deptId);

    String createDefaultCalllegProfile(FmeBridge fmeBridge, long deptId,String quality);

    String createDefaultCalllegProfileNotInDb(FmeBridge fmeBridge, long deptId,boolean isMute,String quality);
    /**
     * <pre>创建默认入会方案</pre>
     * @param fmeBridge
     * @param deptId
     * @param rxAudioMute
     * @return
     */
    String createDefaultCalllegProfileIsMute(FmeBridge fmeBridge, long deptId,Boolean rxAudioMute);

    public String createDefaultCalllegProfileIsMuteQuality(FmeBridge fmeBridge, long deptId, Boolean rxAudioMute,String qualityMain);
    
    /**
     * <pre>获取当前登录用户所属部门的主用的FME的入会方案列表</pre>
     * @author lilinhai
     * @since 2021-01-26 15:28 
     * @return List<ModelBean>
     */
    List<ModelBean> getAllCallLegProfiles(Long deptId);
    
    /**
     * <pre>同步处理入会方案</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28 
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    void syncCallLegProfile(FmeBridge fmeBridge, CallLegProfileProcessor callLegProfileProcessor);

    /**
     * 新增入会方案配置，控制参会者进入会议的方案
     * 
     * @param callLegProfile 入会方案配置，控制参会者进入会议的方案
     * @return 结果
     */
    int insertBusiCallLegProfile(BusiCallLegProfile busiCallLegProfile);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
    
    void updateCallLegProfile(FmeBridge fmeBridge, String id, List<NameValuePair> nameValuePairs);

    /**
     * 修改入会方案配置，控制参会者进入会议的方案
     * 
     * @param busiCallLegProfile 入会方案配置，控制参会者进入会议的方案
     * @return 结果
     */
    int updateBusiCallLegProfile(BusiCallLegProfile busiCallLegProfile);

    /**
     * 删除入会方案配置，控制参会者进入会议的方案信息
     * 
     * @param busiCallLegProfile 入会方案配置，控制参会者进入会议的方案ID
     * @return 结果
     */
    RestResponse deleteBusiCallLegProfileById(BusiCallLegProfile busiCallLegProfile);
    
    public static interface CallLegProfileProcessor
    {
        void process(CallLegProfile callLegProfile);
    }
}
