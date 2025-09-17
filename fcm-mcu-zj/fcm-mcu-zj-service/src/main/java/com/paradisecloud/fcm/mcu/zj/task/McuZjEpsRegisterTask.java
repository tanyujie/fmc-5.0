package com.paradisecloud.fcm.mcu.zj.task;

import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.utils.SipAccountUtil;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuZjTemplateParticipant;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.*;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.*;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class McuZjEpsRegisterTask extends DelayTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(McuZjEpsRegisterTask.class);

    private McuZjBridge mcuZjBridge;
    private List<BusiMcuZjTemplateParticipant> participants;

    public McuZjEpsRegisterTask(String id, long delayInMilliseconds, McuZjBridge mcuZjBridge, List<BusiMcuZjTemplateParticipant> participants) {
        super("register_p_" + id, delayInMilliseconds);
        this.mcuZjBridge = mcuZjBridge;
        this.participants = participants;
    }

    @Override
    public void run() {
        LOGGER.info("MCU_ZJ终端注册开始。ID:" + getId());

        if (mcuZjBridge != null) {
            BusiTerminalMapper busiTerminalMapper = BeanFactory.getBean(BusiTerminalMapper.class);
            List<String> terminalNumList = new ArrayList<>();
            Map<String, BusiTerminal> terminalNumMap = new HashMap<>();
            Map<String, BusiTerminal> terminalNumExistMap = new HashMap<>();
            Map<Long, BusiTerminal> processTerminalMap = new HashMap<>();
            Map<Long, BusiTerminal> processedTerminalMap = new HashMap<>();
            List<BusiTerminal> busiTerminalNotExistList = new ArrayList<>();
            for (BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant : participants) {
                Long terminalId = busiMcuZjTemplateParticipant.getTerminalId();
                if (busiMcuZjTemplateParticipant.getTerminalId() != null) {
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
                    if (TerminalType.isFSBC(busiTerminal.getType()) || TerminalType.isFCMSIP(busiTerminal.getType())) {
                        if (busiTerminal.getTerminalNum() != null) {
                            String terminalNumS = StringUtils.leftPad(busiTerminal.getTerminalNum().toString(), 5, "0");
                            terminalNumList.add(terminalNumS);
                            terminalNumMap.put(terminalNumS, busiTerminal);
                        }
                        processTerminalMap.put(busiTerminal.getId(), busiTerminal);
                    }
                }
            }
            for (String terminalNum : terminalNumList) {
                CmSearchUsrRequest cmSearchUsrRequest = new CmSearchUsrRequest();
                String[] filterType = new String[1];
                filterType[0] = "usr_mark";
                Object[] filterValue = new Object[1];
                filterValue[0] = terminalNum;
                cmSearchUsrRequest.setFilter_type(filterType);
                cmSearchUsrRequest.setFilter_value(filterValue);
                CmSearchUsrResponse cmSearchUsrResponse = mcuZjBridge.getConferenceManageApi().searchUsr(cmSearchUsrRequest);
                if (cmSearchUsrResponse != null && cmSearchUsrResponse.getUsr_ids().length > 0) {
                    // 存在
                    Integer[] usrIds = cmSearchUsrResponse.getUsr_ids();
                    CmGetUsrInfoRequest cmGetUsrInfoRequest = new CmGetUsrInfoRequest();
                    cmGetUsrInfoRequest.setUsr_ids(cmSearchUsrResponse.getUsr_ids());
                    Integer[] lastModifyDtms = new Integer[usrIds.length];
                    for (int i = 0; i < usrIds.length; i++) {
                        lastModifyDtms[i] = 0;
                    }
                    cmGetUsrInfoRequest.setLast_modify_dtms(lastModifyDtms);
                    CmGetUsrInfoResponse cmGetUsrInfoResponse = mcuZjBridge.getConferenceManageApi().getUsrInfo(cmGetUsrInfoRequest);
                    if (cmGetUsrInfoResponse != null && cmGetUsrInfoResponse.getUsr_ids().length > 0) {
                        for (int i = 0; i < cmGetUsrInfoResponse.getUsr_ids().length; i++) {
                            Integer usrId = cmGetUsrInfoResponse.getUsr_ids()[i];
                            String userMark = cmGetUsrInfoResponse.getUsr_marks()[i];
                            String callAddr = cmGetUsrInfoResponse.getCall_addrs()[i];
                            String nickName = cmGetUsrInfoResponse.getNick_names()[i];
                            BusiTerminal busiTerminal = terminalNumMap.get(userMark);
                            String remoteParty = TerminalCache.getInstance().getRemoteParty(busiTerminal);
                            if (remoteParty.equals(callAddr)) {
                                terminalNumExistMap.put(userMark, busiTerminal);
                                processedTerminalMap.put(busiTerminal.getId(), busiTerminal);
                                if (!nickName.equals(busiTerminal.getName())) {
                                    CmModUsrRequest cmModUsrRequest = new CmModUsrRequest();
                                    cmModUsrRequest.setUsr_id(usrId);
                                    cmModUsrRequest.setNick_name(busiTerminal.getName());
                                    CmModUsrResponse cmModUsrResponse = mcuZjBridge.getConferenceManageApi().modifyUsr(cmModUsrRequest);
                                    if (cmModUsrResponse != null) {
                                        nickName = busiTerminal.getName();
                                    }
                                }
                            } else {
                                CmModUsrRequest cmModUsrRequest = new CmModUsrRequest();
                                cmModUsrRequest.setUsr_id(usrId);
                                cmModUsrRequest.setCall_addr(remoteParty);
                                cmModUsrRequest.setNick_name(busiTerminal.getName());
                                CmModUsrResponse cmModUsrResponse = mcuZjBridge.getConferenceManageApi().modifyUsr(cmModUsrRequest);
                                if (cmModUsrResponse != null) {
                                    terminalNumExistMap.put(userMark, busiTerminal);
                                    processedTerminalMap.put(busiTerminal.getId(), busiTerminal);
                                }
                            }
                        }
                    }
                }
            }
            // 判断在MCU不存在的终端
            for (String terminalNum : terminalNumMap.keySet()) {
                BusiTerminal busiTerminal = terminalNumMap.get(terminalNum);
                if (!terminalNumExistMap.containsKey(terminalNum)) {
                    busiTerminalNotExistList.add(busiTerminal);
                }
            }
            // 添加在MCU不存在的终端
            for (BusiTerminal busiTerminal : busiTerminalNotExistList) {
                CmSearchUsrRequest cmSearchUsrRequest = new CmSearchUsrRequest();
                String[] filterType = new String[1];
                filterType[0] = "call_addr";
                cmSearchUsrRequest.setFilter_type(filterType);
                Object[] filterValue = new Object[1];
                filterValue[0] = TerminalCache.getInstance().getRemoteParty(busiTerminal);
                cmSearchUsrRequest.setFilter_value(filterValue);
                CmSearchUsrResponse cmSearchUsrResponse = mcuZjBridge.getConferenceManageApi().searchUsr(cmSearchUsrRequest);
                if (cmSearchUsrResponse != null && cmSearchUsrResponse.getUsr_ids().length > 0) {
                    // 删除
                    CmDeleteUsrRequest cmDeleteUsrRequest = new CmDeleteUsrRequest();
                    cmDeleteUsrRequest.setUsr_ids(cmSearchUsrResponse.getUsr_ids());
                    cmDeleteUsrRequest.setOption("endpoint");
                    CmDeleteUsrResponse cmDeleteUsrResponse = mcuZjBridge.getConferenceManageApi().deleteUsr(cmDeleteUsrRequest);
                    // 添加
                    addUsr(busiTerminalMapper, busiTerminal);
                    processedTerminalMap.put(busiTerminal.getId(), busiTerminal);
                } else {
                    addUsr(busiTerminalMapper, busiTerminal);
                    processedTerminalMap.put(busiTerminal.getId(), busiTerminal);
                }
            }
            // 添加未处理终端
            for (BusiTerminal busiTerminal : processTerminalMap.values()) {
                if (!processedTerminalMap.containsKey(busiTerminal.getId())) {
                    addUsr(busiTerminalMapper, busiTerminal);
                }
            }
        }
    }

    private void addUsr(BusiTerminalMapper busiTerminalMapper, BusiTerminal busiTerminal) {
        try {
            Integer newTerminalNum = busiTerminal.getTerminalNum();
            if (newTerminalNum == null) {
                newTerminalNum = busiTerminalMapper.getAvailableTerminalNum();
                if (newTerminalNum == null) {
                    newTerminalNum = 1;
                }
                if (newTerminalNum >= 50000) {
                    throw new Exception("MCU资源耗尽。非ZJ终端数超过50000");
                }
                busiTerminal.setTerminalNum(newTerminalNum);
                busiTerminalMapper.updateTerminalNum(busiTerminal);
            }
            String terminalNumStr = StringUtils.leftPad(newTerminalNum.toString(), 5, "0");
            String userMark = terminalNumStr;
            String longinId = SipAccountUtil.createZjAccount(newTerminalNum);
            String nickName = busiTerminal.getName();
            String callAddr = TerminalCache.getInstance().getRemoteParty(busiTerminal);
            CmAddUsrRequest cmAddUsrRequest = CmAddUsrRequest.buildDefaultRequestForAddEps();
            cmAddUsrRequest.setNick_name(nickName);
            cmAddUsrRequest.setLogin_id(longinId);
            cmAddUsrRequest.setLogin_pwd(terminalNumStr);
            cmAddUsrRequest.setUsr_mark(userMark);
            List<Integer> belongToDepartments = new ArrayList<>();
            belongToDepartments.add(mcuZjBridge.getTopDepartmentId());// 总部
            cmAddUsrRequest.setBelong_to_departments(belongToDepartments);
            cmAddUsrRequest.setCall_addr(callAddr);
            if (TerminalType.FSBC_H323.getId() == busiTerminal.getType()) {
                cmAddUsrRequest.setPtotocol_type(1);//  1 H323 协议， 2 SIP 协议， 3 多流协议， 5：RTSP 协议， 默认为多流协议
            } else {
                cmAddUsrRequest.setPtotocol_type(2);//  1 H323 协议， 2 SIP 协议， 3 多流协议， 5：RTSP 协议， 默认为多流协议
            }
            cmAddUsrRequest.setOption("endpoint");
            cmAddUsrRequest.setIs_endpoint(1);
            CmAddUsrResponse cmAddUsrResponse = mcuZjBridge.getConferenceManageApi().addUsr(cmAddUsrRequest);
            if (cmAddUsrResponse != null) {

            }
        } catch (Exception e) {
            LOGGER.error("添加MCU终端错误。", e);
        }
    }
}
