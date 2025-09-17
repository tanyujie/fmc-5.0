package com.paradisecloud.fcm.smc2.service;

import com.paradisecloud.com.fcm.smc.modle.DetailConference;
import com.paradisecloud.com.fcm.smc.modle.request.*;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;

import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2023/4/20 17:20
 */
public interface Smc2ConferenceService {

    /**
     * <pre>根据模板会议数据库ID启动会议</pre>
     * @author lilinhai
     * @since 2021-01-30 16:53
     * @param templateConferenceId void
     */
    Smc2ConferenceContext startTemplateConference(long templateConferenceId);

    /**
     *
     * @param encryptConferenceId
     * @param endType
     * @param endReasonsType
     */
    void endConference(String encryptConferenceId, int endType, int endReasonsType);

    void lockConference(String conferenceId);

    void unlockConference(String conferenceId);

    void recallAll(String conferenceId);

    void setMuteAll(String conferenceId, boolean b);

    void setQuietAll(String conferenceId, boolean enable);

    void extendTime(ExtendTimeReq extendTimeReq);

    void textTipsSetting(TextTipsSetting textTipsSetting);

    void lockPresenter(String conferenceId, Boolean lock);

    DetailConference conferencesDetailInfo(String conferenceId);

    Object count();

    /**
     * 设置多画面
     * @param multiPicInfoReq
     */
    void createMulitiPic(com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq multiPicInfoReq);

    /**
     * 选看多画面
     * @param conferenceId
     * @param participantId
     * @param multiPicInfoDTO
     */
    void conferencesControlChoose(String conferenceId, String participantId, MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO);

    /**
     * 设置主席轮询
     * @param multiPicPollRequest
     */
    void chairmanParticipantMultiPicPoll(MultiPicPollRequest multiPicPollRequest);

    /**
     * 查询主席轮询
     * @param conferenceId
     * @param participantId
     * @return
     */
    MultiPicPollRequest chairmanParticipantMultiPicPollQuery(String conferenceId, String participantId);

    /**
     * 主席轮询操作
     * @param chairmanPollOperateReq
     */
    void chairmanParticipantMultiPicPollOperate(ChairmanPollOperateReq chairmanPollOperateReq);

    /**
     * 查询多画面轮询
     * @param conferenceId
     * @return
     */
    Object queryMulitiPicPoll(String conferenceId);

    /**
     * 设置多换吗轮询
     * @param multiPicPollRequest
     */
    void setMultiPicPoll(MultiPicPollRequest multiPicPollRequest);

    void stopMultiPicPoll(String conferenceId);

    void cancelMultiPicPoll(String conferenceId);

    void startMultiPicPoll(String conferenceId);

    void multiPicBroad(String conferenceId, boolean enable);

    void setVoiceActive(String conferenceId, Boolean enable);

    void setMuteConf(String conferenceId, Boolean enable);

    Object conferenceStat();

    Map<String, Object> reportConferenceOfIndex(Long deptId, String startTime, String endTime);

    void order(ParticipantOrderRequest participantOrderRequest);

    List<SmcParitipantsStateRep.ContentDTO> orderQuery(SmcConferenceRequest conferenceRequest);

    void orderCancel(ParticipantOrderRequest participantOrderRequest);

    Object terminalStat();

    void chooseMultiPicManly(String conferenceId, String participantId, MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO);

    Object activeConferences();
}
