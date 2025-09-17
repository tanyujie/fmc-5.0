package com.paradisecloud.smc3.service.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateConference;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateDept;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateParticipant;

import java.util.List;

/**
 * 会议模板Service接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface IBusiMcuSmc3TemplateConferenceService
{
    /**
     * 查询会议模板
     * 
     * @param id 会议模板ID
     * @return 会议模板
     */
    ModelBean selectBusiTemplateConferenceById(Long id);
    
    ModelBean getTemplateConferenceDetails(BusiMcuSmc3TemplateConference tc);
    
    /**
     * <pre>查询会议封面</pre>
     * @author sinhy
     * @since 2021-10-25 11:53 
     * @param id
     * @return BusiTemplateConference
     */
    String selectBusiTemplateConferenceCoverById(Long id);
    
    /**
     * <pre>修改默认视图配置信息</pre>
     * @author lilinhai
     * @since 2021-04-08 15:29 
     * @param jsonObj
     * @param id void
     */
    void updateDefaultViewConfigInfo(JSONObject jsonObj, Long id);
    
    /**
     * 模板计数
     * @author lilinhai
     * @since 2021-05-26 12:41 
     * @return Map<Long,Integer>
     */
    List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType);

    /**
     * 查询会议模板列表
     * 
     * @param busiTemplateConference 会议模板
     * @return 会议模板集合
     */
    List<BusiMcuSmc3TemplateConference> selectBusiTemplateConferenceList(BusiMcuSmc3TemplateConference busiTemplateConference);

    /**
     * 查询会议模板列表
     *
     * @param key 会议名称或号码
     * @return 会议模板集合
     */
    Page<BusiMcuSmc3TemplateConference> selectBusiTemplateConferenceList(String key, Long deptId);

    /**
     * 查询会议模板列表
     *
     * @param key 会议名称或号码
     * @return 会议模板集合
     */
    List<BusiMcuSmc3TemplateConference> selectAllBusiTemplateConferenceList(String key, Long deptId);
    
    /**
     * 查询会议模板列表
     * 
     * @param busiTemplateConference 会议模板
     * @return 会议模板
     */
    List<BusiMcuSmc3TemplateConference> selectAllBusiTemplateConferenceList(BusiMcuSmc3TemplateConference busiTemplateConference);
    
    List<BusiMcuSmc3TemplateConference> selectBusiTemplateConferenceListWithoutBusinessFieldType(BusiMcuSmc3TemplateConference busiTemplateConference);
    List<BusiMcuSmc3TemplateConference> selectAllBusiTemplateConferenceListWithoutBusinessFieldType(BusiMcuSmc3TemplateConference busiTemplateConference);
    
    /**
     * <pre>模板集合转换成modelBean</pre>
     * @author lilinhai
     * @since 2021-01-30 14:04 
     * @param tcs
     * @return List<ModelBean>
     */
    List<ModelBean> toModelBean(List<BusiMcuSmc3TemplateConference> tcs);

    /**
     * 新增会议模板
     * 
     * @param busiTemplateConference 会议模板
     * @param masterTerminalId 
     * @param busiTemplateParticipants 参会者列表
     * @param templateDepts 
     * @return 结果
     */
    int insertBusiTemplateConference(BusiMcuSmc3TemplateConference busiTemplateConference, Long masterTerminalId, List<BusiMcuSmc3TemplateParticipant> busiTemplateParticipants, List<BusiMcuSmc3TemplateDept> templateDepts);

    /**
     * 修改会议模板
     * 
     * @param busiTemplateConference 会议模板
     * @param masterTerminalId 
     * @param busiTemplateParticipants 参会者列表
     * @param templateDepts 
     * @return 结果
     */
    int updateBusiTemplateConference(BusiMcuSmc3TemplateConference busiTemplateConference, Long masterTerminalId, List<BusiMcuSmc3TemplateParticipant> busiTemplateParticipants, List<BusiMcuSmc3TemplateDept> templateDepts);

    /**
     * 删除会议模板信息
     * 
     * @param id 会议模板ID
     * @return 结果
     */
    int deleteBusiTemplateConferenceById(Long id);
    /**
     * 删除会议模板信息
     *
     * @param id 会议模板ID
     * @return 结果
     */
    int deleteMobileBusiTemplateConferenceById(Long id);
    /**
     *  修改会议模板名称
     * @param id
     * @param name
     * @return
     */
    int updateBusiTemplateConferenceName(Long id,String name);

    List<BusiMcuSmc3TemplateConference> selectCascadeConferenceList(Long deptId);
}
