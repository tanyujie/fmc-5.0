package com.paradisecloud.smc3.service.interfaces;

import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.dao.model.CdrCall;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;


import java.util.List;
import java.util.Map;

/**
 * 历史会议，每次挂断会保存该历史记录Service接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface IBusiMcuSmc3HistoryConferenceService
{
    /**
     * 查询历史会议，每次挂断会保存该历史记录
     * 
     * @param id 历史会议，每次挂断会保存该历史记录ID
     * @return 历史会议，每次挂断会保存该历史记录
     */
    public BusiHistoryConference selectBusiHistoryConferenceById(Long id);

    /**
     * 查询历史会议，每次挂断会保存该历史记录列表
     * 
     * @param busiHistoryConference 历史会议，每次挂断会保存该历史记录
     * @return 历史会议，每次挂断会保存该历史记录集合
     */
    public List<BusiHistoryConference> selectBusiHistoryConferenceList(BusiHistoryConference busiHistoryConference);

    /**
     * 新增历史会议，每次挂断会保存该历史记录
     * 
     * @param busiHistoryConference 历史会议，每次挂断会保存该历史记录
     * @return 结果
     */
    public int insertBusiHistoryConference(BusiHistoryConference busiHistoryConference);
    
    BusiHistoryConference saveHistory(CoSpace cosapce, CdrCall cdrCall, Long deptId);
    
    /**
     * 保存历史记录
     * @author sinhy
     * @since 2021-12-14 17:24 
     * @param cosapce
     * @param conferenceContext void
     */
    BusiHistoryConference saveHistory(CoSpace cosapce, Call call, Smc3ConferenceContext conferenceContext);
    
    BusiHistoryConference saveHistory(BusiHistoryConference busiHistoryConference, Smc3ConferenceContext conferenceContext);

    /**
     * 修改历史会议，每次挂断会保存该历史记录
     * 
     * @param busiHistoryConference 历史会议，每次挂断会保存该历史记录
     * @return 结果
     */
    public int updateBusiHistoryConference(BusiHistoryConference busiHistoryConference);

    /**
     * 批量删除历史会议，每次挂断会保存该历史记录
     * 
     * @param ids 需要删除的历史会议，每次挂断会保存该历史记录ID
     * @return 结果
     */
    public int deleteBusiHistoryConferenceByIds(Long[] ids);

    /**
     * 删除历史会议，每次挂断会保存该历史记录信息
     * 
     * @param id 历史会议，每次挂断会保存该历史记录ID
     * @return 结果
     */
    public int deleteBusiHistoryConferenceById(Long id);

    /**
     * 会议时长统计（每个模板使用次数，时间） 双柱子（x模板名，y次数和累计时长）
     * @param deptId 部门Id、会议类型、会议开始时间、会议结束时间
     * @return
     */
    List<BusiHistoryConference> reportByDept(Long deptId);
    
    /**
     * 统计
     * @author sinhy
     * @since 2021-12-14 15:58 
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();

    /**
     * 查询会议历史记录页面
     * @param reportSearchVo
     * @return
     */
    PaginationData<Map<String,Object>> selectHistoryPage(ReportSearchVo reportSearchVo);

    /**
     * 统计每天发起会议的数量
     * @param searchVo
     * @return
     */
    List<Map<String, Map<String, Object>>> reportNumOfDay(ReportSearchVo searchVo);

    /**
     * 每天发起会议的时长总和
     * @param searchVo
     * @return
     */
    List<Map<String, Map<String, Object>>> reportDurationOfDay(ReportSearchVo searchVo);

    /**
     * 不同会议时长类型数量统计
     * @param searchVo
     * @return
     */
    List<Map<String, Object>> reportDurationType(ReportSearchVo searchVo);

    /**
     * 通过
     * @param callId
     * @return
    List findRecordByCall(String callId);*/

    /**
     * 会议结束原因处理
     * @param busiHistoryConference`
     * @param endReasonsType
     * @return
     */
    int updateBusiHistoryConferenceEndReasonsType(BusiHistoryConference busiHistoryConference, int endReasonsType);

    BusiHistoryConference saveHistory(Smc3ConferenceContext conferenceContext);

    /**
     * 更新会议参会者
     *
     * @param conferenceContext
     * @param attendee
     */
    void updateBusiHistoryParticipant(Smc3ConferenceContext conferenceContext, AttendeeSmc3 attendee, boolean updateMediaInfo);

    /**
     * 更新参会者（结束会议时）
     *
     * @param busiHistoryParticipant
     * @param busiHistoryConference
     */
    void updateBusiHistoryParticipant(BusiHistoryParticipant busiHistoryParticipant, BusiHistoryConference busiHistoryConference);
}
