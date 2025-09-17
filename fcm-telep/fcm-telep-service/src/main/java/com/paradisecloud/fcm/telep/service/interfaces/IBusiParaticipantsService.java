package com.paradisecloud.fcm.telep.service.interfaces;

import com.paradisecloud.fcm.telep.model.busi.participants.TeleParticipant;
import com.paradisecloud.fcm.telep.model.request.EnumerateFilter;
import com.paradisecloud.fcm.telep.model.request.ParticipantAddRequest;
import com.paradisecloud.fcm.telep.model.request.ParticipantFecc;
import com.paradisecloud.fcm.telep.model.request.ParticipantMessage;

import java.util.List;

/**
 * @author nj
 * @date 2022/10/14 15:56
 */
public interface IBusiParaticipantsService {
    /**
     * 查询与会者列表
     * @param numericId
     * @param ip
     * @return
     */
    List<TeleParticipant> getList(String numericId,String ip, EnumerateFilter enumerateFilter);

    /**
     * Connect
     * @param ip
     * @param request
     */
    void participantConnect(String ip,TeleParticipant request);

    /**
     * DisConnect
     * @param ip
     * @param request
     */
    void participantDisConnect(String ip,TeleParticipant request);

    /**
     * Status
     * @param ip
     * @param request
     */
    void participantStatus(String ip,TeleParticipant request);

    /**
     * Status
     * @param ip
     * @param request
     */
    void participantModify(String ip,TeleParticipant request);

    /**
     *
     * @param ip
     * @param participantFecc
     */
    void participantFecc(String ip, ParticipantFecc participantFecc);

    /**
     * 发送消息
     * @param ip
     * @param message
     */
    void participantMessage(String ip, ParticipantMessage message);

    /**
     * participant.remove
     * @param ip
     * @param request
     */
    void participantRemove(String ip,TeleParticipant request);

    /**
     * participant.add
     * @param participantAddRequest
     */
    void participantAdd(String ip,ParticipantAddRequest participantAddRequest);
}
