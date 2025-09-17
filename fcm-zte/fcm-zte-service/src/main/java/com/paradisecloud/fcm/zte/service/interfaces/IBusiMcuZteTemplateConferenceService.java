package com.paradisecloud.fcm.zte.service.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateConference;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateDept;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateParticipant;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

import java.util.List;

/**
 * 会议模板Service接口
 *
 * @author lilinhai
 * @date 2021-01-20
 */
public interface IBusiMcuZteTemplateConferenceService
{
    /**
     * 查询会议模板
     *
     * @param id 会议模板ID
     * @return 会议模板
     */
    ModelBean selectBusiMcuZteTemplateConferenceById(Long id);

    ModelBean getTemplateConferenceDetails(BusiMcuZteTemplateConference tc);

    /**
     * <pre>查询会议封面</pre>
     * @author sinhy
     * @since 2021-10-25 11:53
     * @param id
     * @return BusiMcuZteTemplateConference
     */
    String selectBusiMcuZteTemplateConferenceCoverById(Long id);

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
    List<BusiMcuZteTemplateConference> selectBusiMcuZteTemplateConferenceList(BusiMcuZteTemplateConference busiTemplateConference);

    /**
     * 查询会议模板列表
     *
     * @param busiTemplateConference 会议模板
     * @return 会议模板
     */
    List<BusiMcuZteTemplateConference> selectAllBusiMcuZteTemplateConferenceList(BusiMcuZteTemplateConference busiTemplateConference);

    List<BusiMcuZteTemplateConference> selectBusiMcuZteTemplateConferenceListWithoutBusinessFieldType(BusiMcuZteTemplateConference busiTemplateConference);
    List<BusiMcuZteTemplateConference> selectAllBusiMcuZteTemplateConferenceListWithoutBusinessFieldType(BusiMcuZteTemplateConference busiTemplateConference);

    /**
     * <pre>模板集合转换成modelBean</pre>
     * @author lilinhai
     * @since 2021-01-30 14:04
     * @param tcs
     * @return List<ModelBean>
     */
    List<ModelBean> toModelBean(List<BusiMcuZteTemplateConference> tcs);

    /**
     * 新增会议模板
     *
     * @param busiTemplateConference 会议模板
     * @param masterTerminalId
     * @param busiTemplateParticipants 参会者列表
     * @param templateDepts
     * @return 结果
     */
    int insertBusiMcuZteTemplateConference(BusiMcuZteTemplateConference busiTemplateConference, Long masterTerminalId, List<BusiMcuZteTemplateParticipant> busiTemplateParticipants, List<BusiMcuZteTemplateDept> templateDepts);

    /**
     * 修改会议模板
     *
     * @param busiTemplateConference 会议模板
     * @param masterTerminalId
     * @param busiTemplateParticipants 参会者列表
     * @param templateDepts
     * @return 结果
     */
    int updateBusiMcuZteTemplateConference(BusiMcuZteTemplateConference busiTemplateConference, Long masterTerminalId, List<BusiMcuZteTemplateParticipant> busiTemplateParticipants, List<BusiMcuZteTemplateDept> templateDepts);

    /**
     * 删除会议模板信息
     *
     * @param id 会议模板ID
     * @return 结果
     */
    int deleteBusiMcuZteTemplateConferenceById(Long id);

    /**
     * 查询会议模板列表
     *
     * @param key 会议名称或号码
     * @return 会议模板集合
     */
    Page<BusiMcuZteTemplateConference> selectBusiMcuZteTemplateConferenceList(String key, Long deptId);

    /**
     * 查询会议模板列表
     *
     * @param key 会议名称或号码
     * @return 会议模板集合
     */
    List<BusiMcuZteTemplateConference> selectAllBusiMcuZteTemplateConferenceList(String key, Long deptId);
}
