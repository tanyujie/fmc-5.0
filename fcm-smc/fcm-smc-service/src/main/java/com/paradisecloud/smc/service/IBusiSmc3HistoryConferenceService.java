package com.paradisecloud.smc.service;

import com.paradisecloud.com.fcm.smc.modle.ConferenceTimeType;
import com.paradisecloud.com.fcm.smc.modle.ParticipantRspDto;
import com.paradisecloud.com.fcm.smc.modle.SmcConferenceContext;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.smc.dao.model.BusiSmcHistoryConference;

import java.util.List;

/**
 * @author nj
 * @date 2023/6/21 10:00
 */
public interface IBusiSmc3HistoryConferenceService {
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

    /**
     * 保存历史记录
     * @author sinhy
     * @since 2021-12-14 17:24
     * @param conferenceContext void
     */
    BusiHistoryConference saveHistory(SmcConferenceContext conferenceContext);

    BusiHistoryConference saveHistory(BusiSmcHistoryConference historyConference, int rate, ConferenceTimeType conferenceTimeType);

    /**
     * 保存历史记录
     * @author sinhy
     * @since 2021-12-14 17:24
     * @param busiHistoryConference
     * @param conferenceContext void
     */
    BusiHistoryConference saveHistory(BusiHistoryConference busiHistoryConference, SmcConferenceContext conferenceContext);

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
     * 更新会议参会者
     *
     * @param conferenceContext
     * @param attendee
     */
    void updateBusiHistoryParticipant(SmcConferenceContext conferenceContext, ParticipantRspDto contentDTO, boolean updateMediaInfo,String subscription);

    /**
     * 更新参会者（结束会议时）
     *
     * @param busiHistoryParticipant
     * @param busiHistoryConference
     */
    void updateBusiHistoryParticipant(BusiHistoryParticipant busiHistoryParticipant, BusiHistoryConference busiHistoryConference);
}
