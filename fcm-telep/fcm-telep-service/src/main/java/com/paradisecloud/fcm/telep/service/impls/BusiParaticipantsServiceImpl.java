package com.paradisecloud.fcm.telep.service.impls;


import com.paradisecloud.fcm.telep.cache.TeleBridgeCache;
import com.paradisecloud.fcm.telep.cache.TelepBridge;
import com.paradisecloud.fcm.telep.cache.invoker.TeleParticipantApiInvoker;
import com.paradisecloud.fcm.telep.model.busi.ConferencesResponse;
import com.paradisecloud.fcm.telep.model.busi.TeleConference;
import com.paradisecloud.fcm.telep.model.busi.participants.TeleParticipant;
import com.paradisecloud.fcm.telep.model.request.*;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiParaticipantsService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author nj
 * @date 2022/10/14 15:57
 */
@Service
public class BusiParaticipantsServiceImpl implements IBusiParaticipantsService {


    @Override
    public List<TeleParticipant> getList(String numericId, String ip, EnumerateFilter enumerateFilter) {

        Map<String, TelepBridge> ipToTeleBridgeMap = TeleBridgeCache.getInstance().getIpToTeleBridgeMap();
        TelepBridge telepBridge = ipToTeleBridgeMap.get(ip);
        if (telepBridge == null) {
            return null;
        }
        TeleParticipantApiInvoker teleParticipantApiInvoker = telepBridge.getTeleParticipantApiInvoker();

        ConferencesResponse response = null;
        try {
            response = telepBridge.getTeleConferenceApiInvoker().enumerateBean(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response != null) {
            List<TeleConference> conferences = response.getConferences();
            if (!CollectionUtils.isEmpty(conferences)) {
                Optional<TeleConference> first = conferences.stream().filter(p -> Objects.equals(numericId, p.getNumericId())).findFirst();
                if (first.isPresent()) {
                    String conferenceName = first.get().getConferenceName();
                    List<TeleParticipant> result = new ArrayList<>();
                    List<TeleParticipant> enumerate = teleParticipantApiInvoker.enumerate(enumerateFilter, null, result);
                    //List<TeleParticipant> enumerate =TelePModelInit.teleParticpantlist;

                    if (!CollectionUtils.isEmpty(enumerate)) {

                        List<TeleParticipant> collect = enumerate.stream().filter(e -> Objects.equals(e.getConferenceName(), conferenceName)).collect(Collectors.toList());
                        return collect;

                    }

                }
            }
        }
        return null;
    }

    @Override
    public void participantConnect(String ip,TeleParticipant request) {
        TelepBridge telepBridge = TeleBridgeCache.getInstance().getIpToTeleBridgeMap().get(ip);
        if (telepBridge == null) {
            return ;
        }
        TeleParticipantApiInvoker teleParticipantApiInvoker = telepBridge.getTeleParticipantApiInvoker();
        ParticipantDisconnectOrConnectRequest req=new ParticipantDisconnectOrConnectRequest();
        BeanUtils.copyProperties(request,req);
        teleParticipantApiInvoker.participantConnect(req);
    }

    @Override
    public void participantDisConnect(String ip, TeleParticipant request) {
        TelepBridge telepBridge = TeleBridgeCache.getInstance().getIpToTeleBridgeMap().get(ip);
        if (telepBridge == null) {
            return ;
        }
        TeleParticipantApiInvoker teleParticipantApiInvoker = telepBridge.getTeleParticipantApiInvoker();
        ParticipantDisconnectOrConnectRequest req=new ParticipantDisconnectOrConnectRequest();
        BeanUtils.copyProperties(request,req);
        teleParticipantApiInvoker.participantDisconnect(req);
    }

    @Override
    public void participantStatus(String ip, TeleParticipant request) {
        TelepBridge telepBridge = TeleBridgeCache.getInstance().getIpToTeleBridgeMap().get(ip);
        if (telepBridge == null) {
            return ;
        }
        TeleParticipantApiInvoker teleParticipantApiInvoker = telepBridge.getTeleParticipantApiInvoker();
        ParticipantDisconnectOrConnectRequest req=new ParticipantDisconnectOrConnectRequest();
        BeanUtils.copyProperties(request,req);
        teleParticipantApiInvoker.participantStatus(req);
    }

    @Override
    public void participantModify(String ip, TeleParticipant request) {
        TelepBridge telepBridge = TeleBridgeCache.getInstance().getIpToTeleBridgeMap().get(ip);
        if (telepBridge == null) {
            return ;
        }
        TeleParticipantApiInvoker teleParticipantApiInvoker = telepBridge.getTeleParticipantApiInvoker();
        teleParticipantApiInvoker.participantModify(request);
    }

    @Override
    public void participantFecc(String ip, ParticipantFecc participantFecc) {
        TelepBridge telepBridge = TeleBridgeCache.getInstance().getIpToTeleBridgeMap().get(ip);
        if (telepBridge == null) {
            return ;
        }
        TeleParticipantApiInvoker teleParticipantApiInvoker = telepBridge.getTeleParticipantApiInvoker();
        teleParticipantApiInvoker.participantFecc(participantFecc);
    }

    @Override
    public void participantMessage(String ip, ParticipantMessage message) {
        TelepBridge telepBridge = TeleBridgeCache.getInstance().getIpToTeleBridgeMap().get(ip);
        if (telepBridge == null) {
            return ;
        }
        TeleParticipantApiInvoker teleParticipantApiInvoker = telepBridge.getTeleParticipantApiInvoker();
        teleParticipantApiInvoker.participantMessage(message);

    }

    @Override
    public void participantRemove(String ip, TeleParticipant request) {
        TelepBridge telepBridge = TeleBridgeCache.getInstance().getIpToTeleBridgeMap().get(ip);
        if (telepBridge == null) {
            return ;
        }
        TeleParticipantApiInvoker teleParticipantApiInvoker = telepBridge.getTeleParticipantApiInvoker();
        ParticipantDisconnectOrConnectRequest req=new ParticipantDisconnectOrConnectRequest();
        BeanUtils.copyProperties(request,req);
        teleParticipantApiInvoker.participantRemove(req);
    }

    @Override
    public void participantAdd(String ip ,ParticipantAddRequest participantAddRequest) {
        TelepBridge telepBridge = TeleBridgeCache.getInstance().getIpToTeleBridgeMap().get(ip);
        if (telepBridge == null) {
            return ;
        }
        TeleParticipantApiInvoker teleParticipantApiInvoker = telepBridge.getTeleParticipantApiInvoker();
        teleParticipantApiInvoker.participantAdd(participantAddRequest);
    }
}
