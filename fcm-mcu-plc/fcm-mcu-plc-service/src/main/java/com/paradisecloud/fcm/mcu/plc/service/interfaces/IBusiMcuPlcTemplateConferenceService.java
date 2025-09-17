package com.paradisecloud.fcm.mcu.plc.service.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.*;

import java.util.List;

/**
 * 会议模板Service接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface IBusiMcuPlcTemplateConferenceService 
{
    /**
     * 查询会议模板
     * 
     * @param id 会议模板ID
     * @return 会议模板
     */
    ModelBean selectBusiMcuPlcTemplateConferenceById(Long id);
    
    ModelBean getTemplateConferenceDetails(BusiMcuPlcTemplateConference tc);
    
    /**
     * <pre>查询会议封面</pre>
     * @author sinhy
     * @since 2021-10-25 11:53 
     * @param id
     * @return BusiMcuPlcTemplateConference
     */
    String selectBusiMcuPlcTemplateConferenceCoverById(Long id);
    
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
    List<BusiMcuPlcTemplateConference> selectBusiMcuPlcTemplateConferenceList(BusiMcuPlcTemplateConference busiTemplateConference);
    
    /**
     * 查询会议模板列表
     * 
     * @param busiTemplateConference 会议模板
     * @return 会议模板
     */
    List<BusiMcuPlcTemplateConference> selectAllBusiMcuPlcTemplateConferenceList(BusiMcuPlcTemplateConference busiTemplateConference);
    
    List<BusiMcuPlcTemplateConference> selectBusiMcuPlcTemplateConferenceListWithoutBusinessFieldType(BusiMcuPlcTemplateConference busiTemplateConference);
    List<BusiMcuPlcTemplateConference> selectAllBusiMcuPlcTemplateConferenceListWithoutBusinessFieldType(BusiMcuPlcTemplateConference busiTemplateConference);
    
    /**
     * <pre>模板集合转换成modelBean</pre>
     * @author lilinhai
     * @since 2021-01-30 14:04 
     * @param tcs
     * @return List<ModelBean>
     */
    List<ModelBean> toModelBean(List<BusiMcuPlcTemplateConference> tcs);

    /**
     * 新增会议模板
     * 
     * @param busiTemplateConference 会议模板
     * @param masterTerminalId 
     * @param busiTemplateParticipants 参会者列表
     * @param templateDepts 
     * @return 结果
     */
    int insertBusiMcuPlcTemplateConference(BusiMcuPlcTemplateConference busiTemplateConference, Long masterTerminalId, List<BusiMcuPlcTemplateParticipant> busiTemplateParticipants, List<BusiMcuPlcTemplateDept> templateDepts);

    /**
     * 修改会议模板
     * 
     * @param busiTemplateConference 会议模板
     * @param masterTerminalId 
     * @param busiTemplateParticipants 参会者列表
     * @param templateDepts 
     * @return 结果
     */
    int updateBusiMcuPlcTemplateConference(BusiMcuPlcTemplateConference busiTemplateConference, Long masterTerminalId, List<BusiMcuPlcTemplateParticipant> busiTemplateParticipants, List<BusiMcuPlcTemplateDept> templateDepts);

    /**
     * 删除会议模板信息
     * 
     * @param id 会议模板ID
     * @return 结果
     */
    int deleteBusiMcuPlcTemplateConferenceById(Long id);

    /**
     * 查询会议模板列表
     *
     * @param key 会议名称或号码
     * @return 会议模板集合
     */
    Page<BusiMcuPlcTemplateConference> selectBusiMcuPlcTemplateConferenceList(String key, Long deptId);

    /**
     * 查询会议模板列表
     *
     * @param key 会议名称或号码
     * @return 会议模板集合
     */
    List<BusiMcuPlcTemplateConference> selectAllBusiMcuPlcTemplateConferenceList(String key, Long deptId);
}
