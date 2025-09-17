package com.paradisecloud.fcm.mcu.zj.task;

import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmGetUsrOnlineStatusRequest;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmGetUsrOnlineStatusResponse;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.spring.BeanFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetUsrOnlineStatusTask extends DelayTask {

    private List<BusiTerminal> terminalList;
    private McuZjBridge mcuZjBridge;

    public GetUsrOnlineStatusTask(String id, long delayInMilliseconds, List<BusiTerminal> terminalList, McuZjBridge mcuZjBridge) {
        super("get_usr_online_" + id, delayInMilliseconds);
        this.terminalList = terminalList;
        this.mcuZjBridge = mcuZjBridge;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        if (terminalList != null && terminalList.size() > 0 && mcuZjBridge != null) {
            List<Integer> userIdList = new ArrayList<>();
            for (BusiTerminal busiTerminal : terminalList) {
                if (TerminalType.isZJ(busiTerminal.getType())) {
                    userIdList.add(busiTerminal.getZjUserId().intValue());
                }
            }
            Map<Integer, Boolean> onlineMap = new HashMap<>();
            if (userIdList.size() > 0) {
                Integer[] userIds = new Integer[userIdList.size()];
                for (int i = 0; i< userIds.length; i++) {
                    userIds[i] = userIdList.get(i);
                }
                CmGetUsrOnlineStatusRequest cmGetUsrOnlineStatusRequest = new CmGetUsrOnlineStatusRequest();
                cmGetUsrOnlineStatusRequest.setUsr_ids(userIds);
                CmGetUsrOnlineStatusResponse cmGetUsrOnlineStatusResponse = mcuZjBridge.getConferenceManageApi().getUsrOnlineStatus(cmGetUsrOnlineStatusRequest);
                if (cmGetUsrOnlineStatusResponse != null && cmGetUsrOnlineStatusResponse.getUsr_ids() != null) {
                    for (int i = 0; i < cmGetUsrOnlineStatusResponse.getUsr_ids().length; i++) {
                        boolean isOnline = false;
                        Integer online_status = cmGetUsrOnlineStatusResponse.getOnline_status()[i];
                        if (online_status != null && online_status > 0) {
                            isOnline = true;
                        }
                        onlineMap.put(cmGetUsrOnlineStatusResponse.getUsr_ids()[i], isOnline);
                    }
                }
            }
            BusiTerminalMapper busiTerminalMapper = BeanFactory.getBean(BusiTerminalMapper.class);
            for (BusiTerminal busiTerminalTemp : terminalList) {
                BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiTerminalTemp.getId());
                if (TerminalType.isZJ(busiTerminal.getType())) {
                    boolean isOnline = false;
                    Boolean isOnlineTemp = onlineMap.get(busiTerminal.getZjUserId().intValue());
                    if (isOnlineTemp != null) {
                        isOnline = isOnlineTemp;
                    }
                    TerminalOnlineStatus oldStatus = TerminalOnlineStatus.convert(busiTerminal.getOnlineStatus());
                    TerminalOnlineStatus newStatus = TerminalOnlineStatus.OFFLINE;
                    if (isOnline) {
                        newStatus = TerminalOnlineStatus.ONLINE;
                    }
                    processTerminalInfo(busiTerminal, oldStatus, newStatus, busiTerminalMapper);
                }
            }
        }
    }

    private void processTerminalInfo(BusiTerminal busiTerminal, TerminalOnlineStatus oldStatus, TerminalOnlineStatus realStatus, BusiTerminalMapper busiTerminalMapper)
    {
        if (oldStatus != realStatus)
        {
            busiTerminal.setOnlineStatus(realStatus.getValue());
            busiTerminalMapper.updateBusiTerminal(busiTerminal);
        }
    }
}
