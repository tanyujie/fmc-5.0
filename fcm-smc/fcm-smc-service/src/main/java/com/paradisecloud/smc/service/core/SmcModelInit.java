package com.paradisecloud.smc.service.core;

import com.paradisecloud.com.fcm.smc.modle.SmcConference;
import com.paradisecloud.com.fcm.smc.modle.request.SmcConferenceRequest;
import com.paradisecloud.com.fcm.smc.modle.response.SmcConferenceRep;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.fcm.smc.cache.modle.TokenManager;
import com.paradisecloud.smc.SmcWebsocketReconnecter;
import com.paradisecloud.smc.dao.model.BusiSmc;
import com.paradisecloud.smc.dao.model.BusiSmcDept;
import com.paradisecloud.smc.service.IBusiSmcDeptService;
import com.paradisecloud.smc.service.IBusiSmcService;
import com.paradisecloud.smc.service.cache.SmcTerminalCache;
import com.paradisecloud.system.service.ISysDeptService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * @author nj
 * @date 2022/8/15 16:56
 */

//@Component
//@Order(3)
public class SmcModelInit implements ApplicationRunner {

    private Logger logger= LoggerFactory.getLogger(SmcModelInit.class);

    @Resource
    private IBusiSmcService busiSmcService;

    @Resource
    private IBusiSmcDeptService busiSmcDeptService;

    @Resource
    private BusiTerminalMapper busiTerminalMapper;
    @Resource
    private ISysDeptService iSysDeptService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        logger.info("SMC配置初始化启动-----");

        try {
            List<BusiSmc> busiSmcs = busiSmcService.selectBusiSmcList(new BusiSmc());
            if(CollectionUtils.isNotEmpty(busiSmcs)){
                for (BusiSmc busiSmc : busiSmcs) {
                    SmcBridge smcBridge = new SmcBridge(busiSmc);
                    SmcBridgeCache.getInstance().init(smcBridge);
                }

                List<BusiSmcDept> busiSmcDepts = busiSmcDeptService.selectBusiSmcDeptList(new BusiSmcDept());
                if (CollectionUtils.isNotEmpty(busiSmcDepts)) {
                    for (BusiSmcDept busiSmcDept : busiSmcDepts) {
                        SmcBridgeCache.getInstance().update(busiSmcDept);
                    }
                }


                Map<Long, SmcBridge> deptIdSmcBridgeMap = SmcBridgeCache.getInstance().getDeptIdSmcBridgeMap();
                if (!Objects.isNull(deptIdSmcBridgeMap)) {
                    deptIdSmcBridgeMap.forEach((deptId, bridge) -> {
                        initTrueConference(deptId, bridge);
                        initFalseConferece(deptId, bridge);
                    });
                }
            }


            BusiTerminal terminal = new BusiTerminal();
            terminal.setType(TerminalType.SMC_SIP.getId());
            List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(terminal);
            if (!ts.isEmpty()) {
                for (BusiTerminal busiTerminal : ts) {
                    SmcTerminalCache.getInstance().put(busiTerminal.getId(), busiTerminal);
                    if (StringUtils.isNotEmpty(busiTerminal.getNumber())) {
                        SmcTerminalCache.getInstance().putNumber(busiTerminal.getNumber(), busiTerminal);
                    }

                }
            }


            SmcOnlineStatusMonitor.getInstance().init(busiTerminalMapper);
            //SmcOrganizationSyncMonitor.getInstance().init(iSysDeptService);
            Threads.sleep(3000);
            SmcWebsocketReconnecter.getInstance().start();

            for (SmcBridge smcBridge : SmcBridgeCache.getInstance().getInitSmcBridgeList())
            {
                SmcWebsocketReconnecter.getInstance().add(smcBridge);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("SMC配置初始化erro-----");
        }

        logger.info("SMC配置初始化完成-----");
    }

    private void initFalseConferece(Long deptId, SmcBridge bridge) {
        SmcConferenceRequest smcConferenceRequest = new SmcConferenceRequest();
        smcConferenceRequest.setActive(false);
        smcConferenceRequest.setDeptId(deptId);
        while (true) {
            SmcConferenceRep smcConferenceRep = bridge.getSmcConferencesInvoker().list(smcConferenceRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            if (smcConferenceRep != null && CollectionUtils.isNotEmpty(smcConferenceRep.getContent())) {
                List<SmcConference> content = smcConferenceRep.getContent();
                content.forEach(p -> {
                    SmcBridgeCache.getInstance().putAppointmentConferenceBridge(p.getId(), bridge);
                });
                if (smcConferenceRep.getLast()) {
                    break;
                } else {
                    smcConferenceRequest.setPage(smcConferenceRequest.getPage() + 1);
                }
            }else {
                break;
            }
        }
    }

    private void initTrueConference(Long deptId, SmcBridge bridge) {
        SmcConferenceRequest smcConferenceRequest = new SmcConferenceRequest();
        smcConferenceRequest.setDeptId(deptId);
        while (true) {
            SmcConferenceRep smcConferenceRep = bridge.getSmcConferencesInvoker().list(smcConferenceRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            if (smcConferenceRep != null && CollectionUtils.isNotEmpty(smcConferenceRep.getContent())) {
                List<SmcConference> content = smcConferenceRep.getContent();
                content.forEach(p -> {
                    SmcBridgeCache.getInstance().updateConferenceBridge(p.getId(), bridge);
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
