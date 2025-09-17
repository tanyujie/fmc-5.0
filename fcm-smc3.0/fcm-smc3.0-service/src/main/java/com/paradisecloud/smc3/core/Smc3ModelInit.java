package com.paradisecloud.smc3.core;


import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3DeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3TemplateConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.cache.DeptSmc3MappingCache;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.cache.Smc3TerminalCache;
import com.paradisecloud.smc3.busi.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.smc3.busi.templateconference.StartConference;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.SmcConference;
import com.paradisecloud.smc3.model.request.SmcConferenceRequest;
import com.paradisecloud.smc3.model.response.SmcConferenceRep;
import com.paradisecloud.smc3.monitor.ConferenceAttendeeMonitorThread;
import com.paradisecloud.smc3.monitor.Smc3OnlineStatusMonitor;
import com.paradisecloud.smc3.service.impls.BusiSmc3ConferenceServiceImpl;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3HistoryConferenceService;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3Service;
import com.paradisecloud.smc3.service.interfaces.Smc3ServiceZoneId;
import com.paradisecloud.smc3.websocket.client.SMC3WebsocketClient;
import com.paradisecloud.smc3.websocket.client.Smc3WebSocketProcessor;
import com.paradisecloud.smc3.websocket.client.Smc3WebsocketContext;
import com.paradisecloud.smc3.websocket.client.Smc3WebsocketReconnecter;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author nj
 * @date 2022/8/15 16:56
 */

@Component
@Order(1)
public class Smc3ModelInit implements ApplicationRunner {

    private Logger logger = LoggerFactory.getLogger(Smc3ModelInit.class);

    @Resource
    private IBusiMcuSmc3Service busiMcuSmc3Service;
    @Resource
    private Smc3ServiceZoneId smc3ServiceZoneId;

    @Resource
    private BusiMcuSmc3DeptMapper busiMcuSmc3DeptMapper;

    @Resource
    private BusiTerminalMapper busiTerminalMapper;
    @Resource
    private IBusiMcuSmc3HistoryConferenceService iBusiMcuSmc3HistoryConferenceService;
    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;
    @Resource
    private BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        logger.info("SMC配置初始化启动-----");
        List<BusiMcuSmc3Dept> busiSmcDepts = new ArrayList<>();
        try {
            List<BusiMcuSmc3> busiSmcs = busiMcuSmc3Service.selectBusiMcuSmc3List(new BusiMcuSmc3());
            if (CollectionUtils.isNotEmpty(busiSmcs)) {
                for (BusiMcuSmc3 busiSmc : busiSmcs) {
                    Smc3Bridge smcBridge = new Smc3Bridge(busiSmc);
                    Smc3BridgeCache.getInstance().update(smcBridge);
                }

                busiSmcDepts = busiMcuSmc3DeptMapper.selectBusiMcuSmc3DeptList(new BusiMcuSmc3Dept());
                if (CollectionUtils.isNotEmpty(busiSmcDepts)) {
                    for (BusiMcuSmc3Dept busiSmcDept : busiSmcDepts) {
                        DeptSmc3MappingCache.getInstance().put(busiSmcDept.getDeptId(), busiSmcDept);
                        Smc3Bridge smcBridge = Smc3BridgeCache.getInstance().getBridgesByDept(busiSmcDept.getDeptId());
                        if (smcBridge != null) {
                            smc3ServiceZoneId.getOrganizationsList(busiSmcDept.getDeptId());
                        }
                    }
                }

            }


            BusiTerminal terminal = new BusiTerminal();
            terminal.setType(TerminalType.SMC_SIP.getId());
            List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(terminal);
            if (!ts.isEmpty()) {
                for (BusiTerminal busiTerminal : ts) {
                    Smc3TerminalCache.getInstance().put(busiTerminal.getId(), busiTerminal);
                    if (StringUtils.isNotEmpty(busiTerminal.getNumber())) {
                        Smc3TerminalCache.getInstance().putNumber(busiTerminal.getNumber(), busiTerminal);
                    }

                }
            }


            Smc3OnlineStatusMonitor.getInstance().init(busiTerminalMapper);
            Threads.sleep(3000);
            Smc3WebsocketReconnecter.getInstance().start();

            Map<Long, Smc3Bridge> idToTeleBridgeMap = Smc3BridgeCache.getInstance().getIdToTeleBridgeMap();

            for (Smc3Bridge smcBridge : idToTeleBridgeMap.values()) {
                Smc3WebsocketReconnecter.getInstance().add(smcBridge);
            }

            List<BusiMcuSmc3Dept> finalBusiSmcDepts = busiSmcDepts;
            long startTime = System.currentTimeMillis();
            new Thread(()->{

                while (true){
                    long endTime = System.currentTimeMillis();
                    if(endTime-startTime>1000*10*20){
                        break;
                    }
                    int count = 0;
                    for (Smc3Bridge smcBridge : idToTeleBridgeMap.values()) {
                        if(smcBridge.isAvailable()){
                            count++;
                        }
                    }
                    if(count== idToTeleBridgeMap.size()){
                        for (BusiMcuSmc3Dept busiSmcDept : finalBusiSmcDepts) {
                            initFalseConferece(busiSmcDept.getDeptId(), Smc3BridgeCache.getInstance().getIdToTeleBridgeMap().get(busiSmcDept.getMcuId()));
                            initTrueConference(busiSmcDept.getDeptId(), Smc3BridgeCache.getInstance().getIdToTeleBridgeMap().get(busiSmcDept.getMcuId()));
                        }
                        break;
                    }
                }

            }).start();

            new ConferenceAttendeeMonitorThread().start();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("SMC配置初始化erro-----");
        }

        logger.info("SMC配置初始化完成-----");
    }

    private void initFalseConferece(Long deptId, Smc3Bridge bridge) {
        try {
            SmcConferenceRequest smcConferenceRequest = new SmcConferenceRequest();
            smcConferenceRequest.setActive(false);
            smcConferenceRequest.setDeptId(deptId);
            while (true) {
                SmcConferenceRep smcConferenceRep = bridge.getSmcConferencesInvoker().list(smcConferenceRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                if (smcConferenceRep != null && CollectionUtils.isNotEmpty(smcConferenceRep.getContent())) {
                    List<SmcConference> content = smcConferenceRep.getContent();
                    content.forEach(p -> {
                        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(p.getId());
                        if (conferenceContext != null && conferenceContext.isStart()) {
                            conferenceContext.setEnd(true);
                            BeanFactory.getBean(BusiSmc3ConferenceServiceImpl.class).endConference(p.getAccessCode(), EndReasonsType.AUTO_END, false, false);
                        } else {
                            String accessCode = p.getAccessCode();
                            String tenantId = bridge.getTenantId();
                            if (Strings.isNotBlank(tenantId)) {
                                accessCode = accessCode.substring(tenantId.length());
                            }
                            BusiHistoryConference bs = new BusiHistoryConference();
                            bs.setNumber(accessCode);
                            bs.setCallId(p.getId());
                            bs.setMcuType(McuType.SMC3.name());
                            List<BusiHistoryConference> busiHistoryConferenceList = iBusiMcuSmc3HistoryConferenceService.selectBusiHistoryConferenceList(bs);

                            if (CollectionUtils.isNotEmpty(busiHistoryConferenceList)) {
                                BusiHistoryConference busiHistoryConference = busiHistoryConferenceList.get(0);
                                if (busiHistoryConference.getConferenceEndTime() == null) {
                                    busiHistoryConference.setUpdateTime(new Date());
                                    busiHistoryConference.setConferenceEndTime(new Date());
                                    busiHistoryConference.setConferenceEndTime(new Date());
                                    busiHistoryConference.setEndReasonsType(EndReasonsType.ABNORMAL_END);
                                    busiHistoryConference.setDuration((int)((conferenceContext.getEndTime().getTime() - conferenceContext.getStartTime().getTime()) / 1000));
                                    busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConference);

                                    BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(Long.valueOf(busiHistoryConference.getCallLegProfileId()));
                                    if (busiMcuSmc3TemplateConference != null) {
                                        if (busiMcuSmc3TemplateConference.getIsAutoCreateConferenceNumber() == 1) {
                                            busiMcuSmc3TemplateConference.setConferenceNumber(null);
                                            busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiMcuSmc3TemplateConference);
                                        }
                                    }

                                }
                            }
                        }
                    });
                    if (smcConferenceRep.getLast()) {
                        break;
                    } else {
                        smcConferenceRequest.setPage(smcConferenceRequest.getPage() + 1);
                    }
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTrueConference(Long deptId, Smc3Bridge bridge) {
        SmcConferenceRequest smcConferenceRequest = new SmcConferenceRequest();
        smcConferenceRequest.setDeptId(deptId);

        while (true) {
            SmcConferenceRep smcConferenceRep = bridge.getSmcConferencesInvoker().list(smcConferenceRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            if (smcConferenceRep != null && CollectionUtils.isNotEmpty(smcConferenceRep.getContent())) {
                List<SmcConference> content = smcConferenceRep.getContent();
                content.forEach(p -> {
                    String accessCode = p.getAccessCode();
                    String tenantId = bridge.getTenantId();
                    if (Strings.isNotBlank(tenantId)) {
                        accessCode = accessCode.substring(tenantId.length());
                    }
                    BusiHistoryConference bs = new BusiHistoryConference();
                    bs.setNumber(accessCode);
                    bs.setCallId(p.getId());
                    bs.setMcuType(McuType.SMC3.name());

                    List<BusiHistoryConference> busiHistoryConferenceList = iBusiMcuSmc3HistoryConferenceService.selectBusiHistoryConferenceList(bs);

                    if (CollectionUtils.isNotEmpty(busiHistoryConferenceList)) {
                        BusiHistoryConference busiHistoryConference = busiHistoryConferenceList.get(0);
                        if (busiHistoryConference.getConferenceEndTime() == null) {
                            String key = EncryptIdUtil.generateContextKey(Long.valueOf(busiHistoryConference.getCallLegProfileId()), McuType.SMC3);
                            Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(key);
                            if (conferenceContext == null || conferenceContext.isEnd()) {
                                try {
                                    BusiMcuSmc3TemplateConferenceMapper bean = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class);
                                    BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = bean.selectBusiMcuSmc3TemplateConferenceById(Long.valueOf(busiHistoryConference.getCallLegProfileId()));
                                    String s;
                                    if (Objects.equals(ConstAPI.CASCADE, busiMcuSmc3TemplateConference.getCategory())) {
                                        s = new StartConference().startConference(Long.valueOf(busiHistoryConference.getCallLegProfileId()));
                                        logger.info("恢复多级模板会议："+busiMcuSmc3TemplateConference.getName()+",会议号："+busiMcuSmc3TemplateConference.getConferenceNumber());
                                    }else {
                                        logger.info("开始恢复模板会议："+busiMcuSmc3TemplateConference.getName()+",会议号："+busiMcuSmc3TemplateConference.getConferenceNumber());
                                        s = new StartConference().startConference(Long.valueOf(busiHistoryConference.getCallLegProfileId()), busiHistoryConference.getCallId());
                                        logger.info("恢复模板会议："+busiMcuSmc3TemplateConference.getName()+",会议号："+busiMcuSmc3TemplateConference.getConferenceNumber());
                                    }
                                    if (s == null) {
                                        busiHistoryConference.setUpdateTime(new Date());
                                        busiHistoryConference.setConferenceEndTime(new Date());
                                        busiHistoryConference.setEndReasonsType(EndReasonsType.ABNORMAL_END);
                                        busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConference);
                                    }
                                    Threads.sleep(1000);
                                    Map<String, SMC3WebsocketClient> smcWebsocketClientMap = Smc3WebsocketContext.getSmcWebsocketClientMap();

                                     conferenceContext = Smc3ConferenceContextCache.getInstance().get(key);

                                    if(conferenceContext!=null){

                                        SMC3WebsocketClient smc3WebsocketClient = smcWebsocketClientMap.get(conferenceContext.getSmc3Bridge().getBridgeIp());
                                        if (smc3WebsocketClient != null) {
                                            Smc3WebSocketProcessor webSocketProcessor = smc3WebsocketClient.getWebSocketProcessor();
                                            logger.info("恢复会议，发送订阅消息");
                                            webSocketProcessor.firstSubscription(p.getId());
                                        }
                                        String chairmanId = conferenceContext.getChairmanId();
                                        if (Strings.isNotBlank(chairmanId)) {
                                            ChangeMasterAttendeeOperation changeMasterAttendeeOperation = new ChangeMasterAttendeeOperation(conferenceContext, conferenceContext.getAttendeeBySmc3Id(chairmanId));
                                            conferenceContext.setAttendeeOperation(changeMasterAttendeeOperation);
                                            changeMasterAttendeeOperation.operate();
                                        }
                                    }




                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    }
                });
                if (smcConferenceRep.getLast()) {
                    break;
                } else {
                    smcConferenceRequest.setPage(smcConferenceRequest.getPage() + 1);
                }
            }
            if (smcConferenceRep == null || CollectionUtils.isEmpty(smcConferenceRep.getContent())) {
                break;
            }
        }
    }
}
