package com.paradisecloud.fcm.mqtt.common;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.DeptHwcloudMappingCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.DeptMcuZjMappingCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mqtt.constant.FcmConfig;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.SipAccountProperty;
import com.paradisecloud.fcm.mqtt.constant.TerminalTopic;
import com.paradisecloud.fcm.mqtt.interfaces.ITerminalActionService;
import com.paradisecloud.fcm.mqtt.model.Live;
import com.paradisecloud.fcm.mqtt.task.UpdateConferenceTerminalTask;
import com.paradisecloud.fcm.smc2.cache.DeptSmc2MappingCache;
import com.paradisecloud.fcm.smc2.cache.Smc2Bridge;
import com.paradisecloud.fcm.smc2.cache.Smc2BridgeCache;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.common.SshRemoteServerOperate;
import com.paradisecloud.fcm.terminal.fs.interfaces.IBusiFreeSwitchService;
import com.paradisecloud.fcm.terminal.fs.interfaces.IFreeSwitchUserService;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import com.paradisecloud.fcm.terminal.fs.model.FreeSwitchUser;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import com.paradisecloud.fcm.terminal.fsbc.cache.DeptFsbcMappingCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.smc3.busi.cache.DeptSmc3MappingCache;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

public abstract class TerminalSipAccount {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Long key:severId
     */
    private ConcurrentHashMap<Long, SipServerInfo> fsAccountMap = new ConcurrentHashMap();
    /** 是否正在获取SIP服务器信息 */
    private volatile boolean isGetFsSipServerInfoWorking = false;

    private static final TerminalSipAccount INSTANCE = new TerminalSipAccount() {

    };

    public static TerminalSipAccount getInstance() {
        return INSTANCE;
    }

    public void terminalGetSipAccount(String messageId, BusiTerminal busiTerminal) {
        List<BusiRegisterTerminal> busiRegisterTerminals = null;
        BusiTerminal busiTerminalExist = TerminalCache.getInstance().getBySn(busiTerminal.getSn());
        if (busiTerminalExist != null) {
            busiTerminal.setType(busiTerminalExist.getType());
        } else {
            BusiRegisterTerminal registerTerminal = new BusiRegisterTerminal();
            registerTerminal.setMac(busiTerminal.getSn());
            BusiRegisterTerminalMapper busiRegisterTerminalMapper = (BusiRegisterTerminalMapper) SpringContextUtil.getBean("busiRegisterTerminalMapper");
            busiRegisterTerminals = busiRegisterTerminalMapper.selectBusiRegisterTerminalList(registerTerminal);
        }
        if (busiTerminalExist != null || (null != busiRegisterTerminals && busiRegisterTerminals.size() > 0)) {
            if (TerminalType.isFSBC(busiTerminal.getType())) {

                //vhd终端获取sipAccount
                this.vhdTerminalGetSipAcc(messageId, busiTerminal);
            } else if (TerminalType.isFCMSIP(busiTerminal.getType())) {

                //机顶盒获取sipAccount
                this.setTopBoxGetSipAccount(messageId, busiTerminal);
            } else if (TerminalType.isZJ(busiTerminal.getType())) {

                // zj
                this.zjTerminalGetSipAcc(messageId, busiTerminal);
            } else if (TerminalType.isSMCSIP(busiTerminal.getType())) {

                // smc
                this.smcTerminalGetSipAcc(messageId, busiTerminal);
            } else if (TerminalType.isSMC2SIP(busiTerminal.getType())) {

                // smc2
                this.smc2TerminalGetSipAcc(messageId, busiTerminal);
            }
        }
    }

    /**
     * 获取终端SIP账户信息
     *
     * @param busiTerminal
     * @return
     */
    public SipAccountInfo getTerminalSipAccountInfo(BusiTerminal busiTerminal, boolean isExternalRequest) {
        SipAccountInfo sipAccountInfo = null;
        try {
            if (TerminalType.isFSBC(busiTerminal.getType())) {
                SipServerInfo sipServerInfo = getVhdSipServerInfo(busiTerminal.getDeptId());
                sipAccountInfo = new SipAccountInfo(sipServerInfo);
                sipAccountInfo.setSipServer(sipServerInfo.getAuthDomain());
                sipAccountInfo.setSipPort(sipServerInfo.getPort());
                if (StringUtils.isNotEmpty(sipServerInfo.getDomainName())) {
                    if (isExternalRequest) {
                        sipAccountInfo.setAuthDomain(sipServerInfo.getDomainName());
                        sipAccountInfo.setSipServer(sipServerInfo.getDomainName());
                        sipAccountInfo.setProxyServer(sipServerInfo.getDomainName());
                    }
                }
                sipAccountInfo.setSipUserName(busiTerminal.getCredential());
                sipAccountInfo.setSipPassword(busiTerminal.getPassword());
                sipAccountInfo.setTerminalId(busiTerminal.getId());
                sipAccountInfo.setDisplayName(busiTerminal.getName());
                if (sipAccountInfo.getSipPort() == 5060) {
                    sipAccountInfo.setProtocol("tcp");
                } else {
                    sipAccountInfo.setProtocol("tls");
                }
            } else if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                SipServerInfo sipServerInfo = getFsSipServerInfo(busiTerminal.getDeptId(), busiTerminal.getFsServerId());
                sipAccountInfo = new SipAccountInfo(sipServerInfo);
                if (isExternalRequest) {
                    if (StringUtils.isNotEmpty(sipServerInfo.getDomainName())) {
                        sipAccountInfo.setSipServer(sipServerInfo.getDomainName());
                        sipAccountInfo.setProxyServer(sipServerInfo.getDomainName());
                    }
                }
                sipAccountInfo.setSipUserName(busiTerminal.getCredential());
                sipAccountInfo.setSipPassword(busiTerminal.getPassword());
                sipAccountInfo.setTerminalId(busiTerminal.getId());
                sipAccountInfo.setDisplayName(busiTerminal.getName());
                sipAccountInfo.setProtocol("tcp");
            } else if (TerminalType.isZJ(busiTerminal.getType())) {
                SipServerInfo sipServerInfo = getZjSipServerInfo(busiTerminal.getDeptId());
                sipAccountInfo = new SipAccountInfo(sipServerInfo);
                sipAccountInfo.setSipServer(sipServerInfo.getAuthDomain());
                sipAccountInfo.setSipPort(sipServerInfo.getPort());
                if (isExternalRequest) {
                    if (StringUtils.isNotEmpty(sipServerInfo.getDomainName())) {
                        sipAccountInfo.setAuthDomain(sipServerInfo.getDomainName());
                        sipAccountInfo.setSipServer(sipServerInfo.getDomainName());
                        sipAccountInfo.setProxyServer(sipServerInfo.getDomainName());
                    }
                }
                String userName = busiTerminal.getCredential();
                if (StringUtils.isNotEmpty(sipServerInfo.getMcuDomain())) {
                    userName += "@" + sipServerInfo.getMcuDomain();
                }
                sipAccountInfo.setSipUserName(userName);
                sipAccountInfo.setSipPassword(busiTerminal.getPassword());
                sipAccountInfo.setTerminalId(busiTerminal.getId());
                sipAccountInfo.setDisplayName(busiTerminal.getName());
                if (sipAccountInfo.getSipPort() == 5060) {
                    sipAccountInfo.setProtocol("tcp");
                } else {
                    sipAccountInfo.setProtocol("tls");
                }
            } else if (TerminalType.isSMCSIP(busiTerminal.getType())) {
                SipServerInfo sipServerInfo = getSmcSipServerInfo(busiTerminal.getDeptId());
                sipAccountInfo = new SipAccountInfo(sipServerInfo);
                sipAccountInfo.setSipServer(sipServerInfo.getAuthDomain());
                sipAccountInfo.setSipPort(sipServerInfo.getPort());
                if (isExternalRequest) {
                    if (StringUtils.isNotEmpty(sipServerInfo.getDomainName())) {
                        sipAccountInfo.setAuthDomain(sipServerInfo.getDomainName());
                        sipAccountInfo.setSipServer(sipServerInfo.getDomainName());
                        sipAccountInfo.setProxyServer(sipServerInfo.getDomainName());
                    }
                }
                String userName = busiTerminal.getCredential();
                if (StringUtils.isNotEmpty(sipServerInfo.getMcuDomain())) {
                    userName += "@" + sipServerInfo.getMcuDomain();
                }
                sipAccountInfo.setSipUserName(userName);
                sipAccountInfo.setSipPassword(busiTerminal.getPassword());
                sipAccountInfo.setTerminalId(busiTerminal.getId());
                sipAccountInfo.setDisplayName(busiTerminal.getName());
                if (sipAccountInfo.getSipPort() == 5060) {
                    sipAccountInfo.setProtocol("tcp");
                } else {
                    sipAccountInfo.setProtocol("tls");
                }
            } else if (TerminalType.isSMC2SIP(busiTerminal.getType())) {
                SipServerInfo sipServerInfo = getSmc2SipServerInfo(busiTerminal.getDeptId());
                sipAccountInfo = new SipAccountInfo(sipServerInfo);
                sipAccountInfo.setSipServer(sipServerInfo.getAuthDomain());
                sipAccountInfo.setSipPort(sipServerInfo.getPort());
                if (isExternalRequest) {
                    if (StringUtils.isNotEmpty(sipServerInfo.getDomainName())) {
                        sipAccountInfo.setAuthDomain(sipServerInfo.getDomainName());
                        sipAccountInfo.setSipServer(sipServerInfo.getDomainName());
                        sipAccountInfo.setProxyServer(sipServerInfo.getDomainName());
                    }
                }
                String userName = busiTerminal.getCredential();
                if (StringUtils.isNotEmpty(sipServerInfo.getMcuDomain())) {
                    userName += "@" + sipServerInfo.getMcuDomain();
                }
                sipAccountInfo.setSipUserName(userName);
                sipAccountInfo.setSipPassword(busiTerminal.getPassword());
                sipAccountInfo.setTerminalId(busiTerminal.getId());
                sipAccountInfo.setDisplayName(busiTerminal.getName());
                if (sipAccountInfo.getSipPort() == 5060) {
                    sipAccountInfo.setProtocol("tcp");
                } else {
                    sipAccountInfo.setProtocol("tls");
                }
            } else if (TerminalType.isHwCloud(busiTerminal.getType())) {
                SipServerInfo sipServerInfo = getHwCloudSipServerInfo(busiTerminal.getDeptId());
                sipAccountInfo = new SipAccountInfo(sipServerInfo);
                sipAccountInfo.setSipServer(sipServerInfo.getAuthDomain());
                sipAccountInfo.setSipPort(sipServerInfo.getPort());
                if (isExternalRequest) {
                    if (StringUtils.isNotEmpty(sipServerInfo.getDomainName())) {
                        sipAccountInfo.setAuthDomain(sipServerInfo.getDomainName());
                        sipAccountInfo.setSipServer(sipServerInfo.getDomainName());
                        sipAccountInfo.setProxyServer(sipServerInfo.getDomainName());
                    }
                }
                String userName = busiTerminal.getCredential();
                if (StringUtils.isNotEmpty(sipServerInfo.getMcuDomain())) {
                    userName += "@" + sipServerInfo.getMcuDomain();
                }
                sipAccountInfo.setSipUserName(userName);
                sipAccountInfo.setSipPassword(busiTerminal.getPassword());
                sipAccountInfo.setTerminalId(busiTerminal.getId());
                sipAccountInfo.setDisplayName(busiTerminal.getName());
                if (sipAccountInfo.getSipPort() == 5060) {
                    sipAccountInfo.setProtocol("tcp");
                } else {
                    sipAccountInfo.setProtocol("tls");
                }
            }
        } catch (Exception e) {
            logger.error("获取SIP账号信息错误。", e);
        }
        if (sipAccountInfo == null) {
            sipAccountInfo = new SipAccountInfo();
        }
        return sipAccountInfo;
    }

    public void vhdTerminalGetSipAcc(String messageId, BusiTerminal terminal) {
        BusiTerminalMapper busiTerminalMapper = (BusiTerminalMapper) SpringContextUtil.getBean("busiTerminalMapper");
        List<BusiTerminal> busiTerminals = busiTerminalMapper.selectBusiTerminalList(terminal);
        if (null != busiTerminals && busiTerminals.size() > 0) {
            this.vhdTermminalGetSipAccount(busiTerminals.get(0), messageId, terminal.getSn());
        }
    }

    public void vhdTermminalGetSipAccount(BusiTerminal busiTerminal, String messageId, String sn) {
        FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId()).getFsbcServerId());
        if (null != fsbcBridge) {

            BusiFsbcRegistrationServer busiFsbcRegistrationServer = fsbcBridge.getBusiFsbcRegistrationServer();
            JSONObject obj = new JSONObject();

//            BusiLiveSetting busiLiveSetting = new BusiLiveSetting();
//            busiLiveSetting.setDeptId(busiTerminal.getDeptId());
//            busiLiveSetting.setStatus(1);
//            BusiLiveSettingMapper liveUrlList = (BusiLiveSettingMapper) SpringContextUtil.getBean("busiLiveSettingMapper");
//            List<BusiLiveSetting> busiLiveSettings = liveUrlList.selectBusiLiveSettingList(busiLiveSetting);

//            List<String> liveUrl = new ArrayList<>();
            List<Live> liveList = new ArrayList<>();

            ITerminalActionService iTerminalActionService = BeanFactory.getBean(ITerminalActionService.class);
            List<BusiLiveSetting> busiLiveSettingByDeptList = iTerminalActionService.getBusiLiveSettingByDeptId(busiTerminal.getDeptId());

            if (busiLiveSettingByDeptList != null && busiLiveSettingByDeptList.size() > 0) {
                for (BusiLiveSetting liveSetting : busiLiveSettingByDeptList) {
                    if (liveSetting.getStatus() == 1) {
//                        liveUrl.add(liveSetting.getUrl());
                        Live live = new Live();
                        live.setName(liveSetting.getName());
                        live.setUrl(liveSetting.getUrl());
                        liveList.add(live);
                        //jsonObject1.put(busiLiveSettings.get(i).getName(),busiLiveSettings.get(i).getUrl());
                    }
                }
            }

            if (null != busiFsbcRegistrationServer) {
                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + sn;
                String action = TerminalTopic.GET_SIP_ACCOUNT;
                String ipAddress = busiFsbcRegistrationServer.getCallIp();
                if (StringUtils.isEmpty(ipAddress)) {
                    ipAddress = busiFsbcRegistrationServer.getDataSyncIp();
                }
                if (StringUtils.isNotEmpty(busiFsbcRegistrationServer.getDomainName())) {
                    if (busiFsbcRegistrationServer.getDomainName().equals(busiTerminal.getConnectIp())) {
                        ipAddress = busiFsbcRegistrationServer.getDomainName();
                    }
                }
                obj.put("proxyServer", ipAddress);
                obj.put("AuthDomain", ipAddress);
                obj.put("userName", busiTerminal.getCredential());
                obj.put("port", busiFsbcRegistrationServer.getSipPort());
                if (busiFsbcRegistrationServer.getSipPort() == 5060) {
                    obj.put("protocol", "tcp");
                } else {
                    obj.put("protocol", "tls");
                }
                obj.put("name", busiTerminal.getName());
                obj.put("deptId", busiTerminal.getDeptId());
                obj.put("deptName", SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName());
                obj.put("liveUrl", liveList);
                obj.put(MqttConfigConstant.PASSWORD, busiTerminal.getPassword());
                obj.put("displayName", busiTerminal.getName());
                obj.put("sipServer", ipAddress);
                obj.put("sipPort", busiFsbcRegistrationServer.getSipPort());
                obj.put("sipUserName", busiTerminal.getCredential());
                obj.put("sipPassword", busiTerminal.getPassword());

                obj.put(SipAccountProperty.TURN_SERVER, ipAddress);
                obj.put(SipAccountProperty.TURN_PORT, 0);
                obj.put(SipAccountProperty.TURN_USER_NAME, "test");
                obj.put(SipAccountProperty.TURN_PASSWORD, "test");

                obj.put(SipAccountProperty.STUN_SERVER, ipAddress);
                obj.put(SipAccountProperty.STUN_PORT, 0);
                obj.put("type", busiTerminal.getType());
                // 用户id
                Long userId = null;
                BusiUserTerminalMapper busiUserTerminalMapper = (BusiUserTerminalMapper) SpringContextUtil.getBean("busiUserTerminalMapper");
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminal.getId());
                if (busiUserTerminal != null) {
                    userId = busiUserTerminal.getUserId();
                }
                obj.put("userId", userId);
                ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, obj, busiTerminal.getSn(), messageId);
            }
        }
    }

    public SipServerInfo getVhdSipServerInfo(Long deptId) {
        SipServerInfo sipServerInfo = new SipAccountInfo();
        FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(DeptFsbcMappingCache.getInstance().get(deptId).getFsbcServerId());
        if (null != fsbcBridge) {
            BusiFsbcRegistrationServer busiFsbcRegistrationServer = fsbcBridge.getBusiFsbcRegistrationServer();
            if (null != busiFsbcRegistrationServer) {
                sipServerInfo.setProxyServer(busiFsbcRegistrationServer.getCallIp());
                sipServerInfo.setAuthDomain(busiFsbcRegistrationServer.getDataSyncIp());
                sipServerInfo.setPort(busiFsbcRegistrationServer.getSipPort());
                sipServerInfo.setType(TerminalType.FSBC_SIP.getId());
                sipServerInfo.setDomainName(busiFsbcRegistrationServer.getDomainName());
                sipServerInfo.setServerIp(busiFsbcRegistrationServer.getCallIp());
            }
        }
        return sipServerInfo;
    }

    public void setTopBoxGetSipAccount(String messageId, BusiTerminal busiTerminal) {
        JSONObject jObject = new JSONObject();
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminal.getSn();
        if (busiTerminal != null && busiTerminal.getId() == null) {
            BusiTerminalMapper busiTerminalMapper = (BusiTerminalMapper) SpringContextUtil.getBean("busiTerminalMapper");
            List<BusiTerminal> busiTerminalLists = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
            if (null != busiTerminalLists && busiTerminalLists.size() > 0) {
                busiTerminal = busiTerminalLists.get(0);
            }
        }

        //构建sip注册的数据
        JSONObject jsonObject = buildSipData(messageId, busiTerminal);

        if (jsonObject != null) {

            BusiTerminalMapper busiTerminalMapper = (BusiTerminalMapper) SpringContextUtil.getBean("busiTerminalMapper");
            String ip = null;
            Object ipObj = jsonObject.get(MqttConfigConstant.IP);
            jsonObject.remove(MqttConfigConstant.IP);
            if (ipObj != null) {
                ip = (String) ipObj;
            }
            if (ip != null) {
                BusiTerminal busiTerminalExist = null;
                BusiTerminal busiTerminalCon = new BusiTerminal();
                busiTerminalCon.setSn(busiTerminal.getSn());
                List<BusiTerminal> busiTerminals = busiTerminalMapper.selectBusiTerminalList(busiTerminalCon);

                if (busiTerminals != null && busiTerminals.size() > 0) {
                    busiTerminalExist = busiTerminals.get(0);
                }
                if (busiTerminalExist != null) {
                    BusiTerminal busiTerminalCache = TerminalCache.getInstance().get(busiTerminalExist.getId());
                    busiTerminalCache.setIp(ip);
                    try {
                        Long serverId = jsonObject.getLong("serverId");
                        busiTerminalCache.setFsServerId(serverId);
                        if (serverId != busiTerminalExist.getFsServerId()) {
                            UpdateConferenceTerminalTask updateConferenceTerminalTask = new UpdateConferenceTerminalTask(busiTerminalExist.getId().toString(), 0, busiTerminalExist.getId());
                            BeanFactory.getBean(TaskService.class).addTask(updateConferenceTerminalTask);
                        }
                    } catch (Exception e) {
                    }
                    busiTerminalMapper.updateBusiTerminal(busiTerminalCache);
                }
            }
            // 用户id
            Long userId = null;
            BusiUserTerminalMapper busiUserTerminalMapper = (BusiUserTerminalMapper) SpringContextUtil.getBean("busiUserTerminalMapper");
            BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminal.getId());
            if (busiUserTerminal != null) {
                userId = busiUserTerminal.getUserId();
            }
            jsonObject.put("userId", userId);
        }
        String action = TerminalTopic.GET_SIP_ACCOUNT;
        jObject.put(MqttConfigConstant.JSON_DATA_STR, jsonObject);
        ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, busiTerminal.getSn(), messageId);
    }


    public JSONObject buildSipData(String messageId, BusiTerminal busiTerminal) {

        //根据clientId绑定的租户，服务器上获取
        JSONObject jsonObj = this.bindTenantFsInfo(messageId, busiTerminal);

        return jsonObj;
    }

    private JSONObject bindTenantFsInfo(String messageId, BusiTerminal busiTerminal) {
        JSONObject jsonObject = new JSONObject();
        IFreeSwitchUserService freeSwitchUserService = (IFreeSwitchUserService) SpringContextUtil.getBean("freeSwitchUserService");
        FreeSwitchUser freeSwitchUser = freeSwitchUserService.selectFreeSwitchUserById(busiTerminal.getCredential(), busiTerminal.getDeptId());
        if (null != freeSwitchUser) {
            String password = freeSwitchUser.getPassword();
            try {
                SipServerInfo sipServerInfo = getFsSipServerInfo(busiTerminal.getDeptId(), busiTerminal.getFsServerId());

                String sipServer = sipServerInfo.getSipServer();
                if (StringUtils.isEmpty(sipServerInfo.getSipServer())) {
                    throw new Exception();
                }
                if (StringUtils.isNotEmpty(sipServerInfo.getDomainName())) {
                    if (sipServerInfo.getDomainName().equals(busiTerminal.getConnectIp())) {
                        sipServer = sipServerInfo.getDomainName();
                    }
                }

                jsonObject.put(MqttConfigConstant.IP, sipServerInfo.getSipServer());
                jsonObject.put("serverId", sipServerInfo.getServerId());
                jsonObject.put(MqttConfigConstant.MESSAGE_ID, messageId);
                jsonObject.put(SipAccountProperty.SIP_SERVER, sipServer);
                jsonObject.put(SipAccountProperty.SIP_PORT, sipServerInfo.getSipPort());
                jsonObject.put("protocol", "tcp");
                jsonObject.put(SipAccountProperty.PROXY_SERVER, sipServer);
                jsonObject.put(SipAccountProperty.DISPLAY_NAME, busiTerminal.getName());
                jsonObject.put(SipAccountProperty.SIP_USER_NAME, busiTerminal.getCredential());
                jsonObject.put(SipAccountProperty.SIP_PASSWORD, password);

                jsonObject.put(SipAccountProperty.TURN_SERVER, sipServerInfo.getTurnServer());
                jsonObject.put(SipAccountProperty.TURN_PORT, sipServerInfo.getTurnPort());
                jsonObject.put(SipAccountProperty.TURN_USER_NAME, sipServerInfo.getTurnUserName());
                jsonObject.put(SipAccountProperty.TURN_PASSWORD, sipServerInfo.getTurnPassword());

                jsonObject.put(SipAccountProperty.STUN_SERVER, sipServerInfo.getStunServer());
                jsonObject.put(SipAccountProperty.STUN_PORT, sipServerInfo.getStunPort());
                jsonObject.put(SipAccountProperty.EXTRA, null);
                jsonObject.put("type", busiTerminal.getType());

//            BusiLiveSetting busiLiveSetting = new BusiLiveSetting();
//            busiLiveSetting.setDeptId(busiTerminal.getDeptId());
//            busiLiveSetting.setStatus(1);
//            BusiLiveSettingMapper liveUrlList = (BusiLiveSettingMapper) SpringContextUtil.getBean("busiLiveSettingMapper");
//            List<BusiLiveSetting> busiLiveSettings = liveUrlList.selectBusiLiveSettingList(busiLiveSetting);

//            List<String> liveUrl = new ArrayList<>();
                List<Live> liveList = new ArrayList<>();

                ITerminalActionService iTerminalActionService = BeanFactory.getBean(ITerminalActionService.class);
                List<BusiLiveSetting> busiLiveSettingByDeptList = iTerminalActionService.getBusiLiveSettingByDeptId(busiTerminal.getDeptId());

                if (busiLiveSettingByDeptList != null && busiLiveSettingByDeptList.size() > 0) {
                    for (BusiLiveSetting liveSetting : busiLiveSettingByDeptList) {
                        if (liveSetting.getStatus() == 1) {
//                        liveUrl.add(liveSetting.getUrl());
                            Live live = new Live();
                            live.setName(liveSetting.getName());
                            live.setUrl(liveSetting.getUrl());
                            liveList.add(live);
                            //jsonObject1.put(busiLiveSettings.get(i).getName(),busiLiveSettings.get(i).getUrl());
                        }
                    }
                }
//                if (liveUrl == null){
//                    jsonObject.put("liveUrl","");
//                }
                jsonObject.put("liveUrl", liveList);
                jsonObject.put("deptName", SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName());
                jsonObject.put("deptId", busiTerminal.getDeptId());

                //保存终端的对应的sip信息
//                this.saveTerminalSipInfo(jsonObject, clientId, terminalId, deptId);

            } catch (Exception e) {
                throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "租户绑定FS服务异常!");
            }
        }
        return jsonObject;
    }

    public SipServerInfo getFsSipServerInfo(Long deptId, Long serverId) throws Exception {
        SipServerInfo sipServerInfo = new SipServerInfo();
        try {
            FcmBridge fcmBridge = getConnectServerFcmBridge(deptId, serverId);
            serverId = fcmBridge.getBusiFreeSwitch().getId();
            SipServerInfo sipServerInfoCached = fsAccountMap.get(serverId);
            if (sipServerInfoCached != null) {
                Date expiredDate = DateUtils.getDiffDate(sipServerInfoCached.getCreateTime(), 1, TimeUnit.MINUTES);
                Date now = new Date();
                if (expiredDate.before(now)) {
                    if (!isGetFsSipServerInfoWorking) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isGetFsSipServerInfoWorking) {
                                    isGetFsSipServerInfoWorking = true;
                                    try {
                                        getFsSipServerInfo(fcmBridge);
                                    } catch (Exception e) {
                                    }
                                    isGetFsSipServerInfoWorking = false;
                                }
                            }
                        });
                    }
                }
                BeanUtils.copyBeanProp(sipServerInfo, sipServerInfoCached);
            } else {
                synchronized (this) {
                    sipServerInfoCached = fsAccountMap.get(serverId);
                    if (sipServerInfoCached != null) {
                        BeanUtils.copyBeanProp(sipServerInfo, sipServerInfoCached);
                    } else {
                        SipServerInfo sipServerInfoTemp = getFsSipServerInfo(fcmBridge);
                        BeanUtils.copyBeanProp(sipServerInfo, sipServerInfoTemp);
                    }
                }
            }

            String domainName = fcmBridge.getBusiFreeSwitch().getDomainName();
            if (StringUtils.isNotEmpty(domainName)) {
                sipServerInfo.setDomainName(domainName);
            } else {
                sipServerInfo.setDomainName(null);
            }
            sipServerInfo.setServerIp(fcmBridge.getBusiFreeSwitch().getIp());
        } catch (Exception e) {
            logger.error("获取SIP服务器信息错误。", e);
        }

        return sipServerInfo;
    }

    private SipServerInfo getFsSipServerInfo(FcmBridge fcmBridge) throws Exception {
        SipServerInfo sipServerInfo = new SipAccountInfo();
        String region = ExternalConfigCache.getInstance().getRegion();
        if ("ops".equalsIgnoreCase(region)) {
            sipServerInfo.setTurnUserName("user");
            sipServerInfo.setTurnPassword("paradise");
            sipServerInfo.setSipServer(fcmBridge.getBusiFreeSwitch().getIp());
            sipServerInfo.setSipPort(25380);
            sipServerInfo.setTurnServer(sipServerInfo.getSipServer());
            sipServerInfo.setTurnPort(3678);
            sipServerInfo.setStunServer(sipServerInfo.getSipServer());
            sipServerInfo.setStunPort(3678);
            sipServerInfo.setProxyServer(sipServerInfo.getSipServer() + MqttConfigConstant.COLON + sipServerInfo.getSipPort());
            sipServerInfo.setServerId(fcmBridge.getBusiFreeSwitch().getId());
            sipServerInfo.setCreateTime(new Date());
            sipServerInfo.setType(TerminalType.FCM_SIP.getId());

            if (StringUtils.isNotEmpty(sipServerInfo.getSipServer())) {
                fsAccountMap.put(fcmBridge.getBusiFreeSwitch().getId(), sipServerInfo);
            }
            return sipServerInfo;
        }
        try {
            connectServer(fcmBridge);
            Map<String, Object> map = new HashMap<String, Object>();
            Map<String, Object> turnMap = new HashMap<String, Object>();
            String fileStr = SshRemoteServerOperate.getInstance().execCommand("cat ".concat(FcmConfig.TURN_SERVER_PATH));
            IBusiFreeSwitchService busiFreeSwitchService = (IBusiFreeSwitchService) SpringContextUtil.getBean("busiFreeSwitchService");
            Map<String, Object> turnServerMap = busiFreeSwitchService.dealTurnServerConfFile(fileStr, turnMap);
            Map<String, Object> turnsMap = busiFreeSwitchService.dealTurnServerConf(turnServerMap, map);

            String varsXml = SshRemoteServerOperate.getInstance().execCommand("cat ".concat(MqttConfigConstant.XML_FILE_PATH + MqttConfigConstant.VARS_XML));
            turnsMap = busiFreeSwitchService.xmlStrConvertJson(varsXml, turnsMap);
            String externalIp = (String) turnsMap.get(FcmConfig.USER_EXTERNAL_IP_KEY);
            String listeningPort = (String) turnsMap.get(FcmConfig.LISTENING_PORT_KEY);
            String internalSipPort = (String) turnsMap.get(FcmConfig.INTERNAL_SIP_PORT_KEY);
            String user = (String) turnsMap.get(FcmConfig.USER);
            String[] userInfo = user.split(MqttConfigConstant.COLON);
            if (userInfo.length > 1) {
                sipServerInfo.setTurnUserName(userInfo[0]);
                sipServerInfo.setTurnPassword(userInfo[1]);
            }
            sipServerInfo.setSipServer(externalIp);
            if (StringUtils.isNotEmpty(internalSipPort)) {
                sipServerInfo.setSipPort(Integer.valueOf(internalSipPort));
            }
            sipServerInfo.setTurnServer(externalIp);
            if (StringUtils.isNotEmpty(listeningPort)) {
                sipServerInfo.setTurnPort(Integer.valueOf(listeningPort));
            }
            sipServerInfo.setStunServer(externalIp);
            if (StringUtils.isNotEmpty(listeningPort)) {
                sipServerInfo.setStunPort(Integer.valueOf(listeningPort));
            }
            sipServerInfo.setProxyServer(externalIp + MqttConfigConstant.COLON + internalSipPort);
            sipServerInfo.setServerId(fcmBridge.getBusiFreeSwitch().getId());
            sipServerInfo.setCreateTime(new Date());
            sipServerInfo.setType(TerminalType.FCM_SIP.getId());

            if (StringUtils.isNotEmpty(sipServerInfo.getSipServer())) {
                fsAccountMap.put(fcmBridge.getBusiFreeSwitch().getId(), sipServerInfo);
            }
        } catch (Exception e) {
            SshRemoteServerOperate.getInstance().closeSession();
            logger.error("获取FS SIP服务器信息错误。", e);
            throw e;
        } finally {
            SshRemoteServerOperate.getInstance().closeSession();
        }
        return sipServerInfo;
    }

    public void zjTerminalGetSipAcc(String messageId, BusiTerminal terminal) {
        BusiTerminalMapper busiTerminalMapper = (BusiTerminalMapper) SpringContextUtil.getBean("busiTerminalMapper");
        List<BusiTerminal> busiTerminals = busiTerminalMapper.selectBusiTerminalList(terminal);
        if (null != busiTerminals && busiTerminals.size() > 0) {
            this.zjTerminalGetSipAccount(busiTerminals.get(0), messageId, terminal.getSn());
        }
    }

    public void zjTerminalGetSipAccount(BusiTerminal busiTerminal, String messageId, String sn) {
        McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(busiTerminal.getZjServerId());
        if (null != mcuZjBridge) {

            BusiMcuZj busiMcuZj = mcuZjBridge.getBusiMcuZj();
            JSONObject obj = new JSONObject();

            BusiLiveSetting busiLiveSetting = new BusiLiveSetting();
            busiLiveSetting.setDeptId(busiTerminal.getDeptId());
            BusiLiveSettingMapper liveUrlList = (BusiLiveSettingMapper) SpringContextUtil.getBean("busiLiveSettingMapper");
            List<BusiLiveSetting> busiLiveSettings = liveUrlList.selectBusiLiveSettingList(busiLiveSetting);

            List<String> liveUrl = new ArrayList<>();
            List<Live> liveList = new ArrayList<>();

            if (busiLiveSettings != null && busiLiveSettings.size() > 0) {
                for (int i = 0; i < busiLiveSettings.size(); i++) {
                    if (busiLiveSettings.get(i).getStatus() == 1) {
                        liveUrl.add(busiLiveSettings.get(i).getUrl());
                        Live live = new Live();
                        live.setName(busiLiveSettings.get(i).getName());
                        live.setUrl(busiLiveSettings.get(i).getUrl());
                        liveList.add(live);
                        //jsonObject1.put(busiLiveSettings.get(i).getName(),busiLiveSettings.get(i).getUrl());
                    }
                }
            }

            if (null != busiMcuZj) {
                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + sn;
                String action = TerminalTopic.GET_SIP_ACCOUNT;
                String ipAddress = busiMcuZj.getIp();
                if (StringUtils.isNotEmpty(busiMcuZj.getCucmIp())) {
                    if (busiMcuZj.getCucmIp().equals(busiTerminal.getConnectIp())) {
                        ipAddress = busiMcuZj.getCucmIp();
                    }
                }
                obj.put("proxyServer", ipAddress);
                obj.put("AuthDomain", ipAddress);
                String userName = busiTerminal.getCredential();
                if (StringUtils.isNotEmpty(busiMcuZj.getMcuDomain())) {
                    userName += "@" + busiMcuZj.getMcuDomain();
                }
                obj.put("userName", userName);
                Integer port = 5060;
                if (busiMcuZj.getCallPort() != null) {
                    port = busiMcuZj.getCallPort();
                }
                obj.put("port", port);
                if (port == 5060) {
                    obj.put("protocol", "tcp");
                } else {
                    obj.put("protocol", "tls");
                }
                obj.put("name", busiTerminal.getName());
                obj.put("deptId", busiTerminal.getDeptId());
                obj.put("deptName", SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName());
                obj.put("liveUrl", liveList);
                obj.put(MqttConfigConstant.PASSWORD, busiTerminal.getPassword());
                obj.put("displayName", busiTerminal.getName());
                obj.put("sipServer", ipAddress);
                obj.put("sipPort", port);
                obj.put("sipUserName", userName);
                obj.put("sipPassword", busiTerminal.getPassword());

                obj.put(SipAccountProperty.TURN_SERVER, ipAddress);
                obj.put(SipAccountProperty.TURN_PORT, 0);
                obj.put(SipAccountProperty.TURN_USER_NAME, "test");
                obj.put(SipAccountProperty.TURN_PASSWORD, "test");

                obj.put(SipAccountProperty.STUN_SERVER, ipAddress);
                obj.put(SipAccountProperty.STUN_PORT, 0);
                obj.put("type", busiTerminal.getType());
                // 用户id
                Long userId = null;
                BusiUserTerminalMapper busiUserTerminalMapper = (BusiUserTerminalMapper) SpringContextUtil.getBean("busiUserTerminalMapper");
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminal.getId());
                if (busiUserTerminal != null) {
                    userId = busiUserTerminal.getUserId();
                }
                obj.put("userId", userId);
                ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, obj, busiTerminal.getSn(), messageId);
            }
        }
    }

    public SipServerInfo getZjSipServerInfo(Long deptId) {
        SipServerInfo sipServerInfo = new SipAccountInfo();
        McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(DeptMcuZjMappingCache.getInstance().get(deptId).getMcuId());
        if (null != mcuZjBridge) {
            BusiMcuZj busiMcuZj = mcuZjBridge.getBusiMcuZj();
            if (null != busiMcuZj) {
                sipServerInfo.setProxyServer(busiMcuZj.getIp());
                sipServerInfo.setAuthDomain(busiMcuZj.getIp());
                sipServerInfo.setPort(busiMcuZj.getCallPort());
                sipServerInfo.setType(TerminalType.ZJ_SIP.getId());
                sipServerInfo.setDomainName(busiMcuZj.getCucmIp());
                sipServerInfo.setMcuDomain(busiMcuZj.getMcuDomain());
            }
        }
        return sipServerInfo;
    }

    public void smcTerminalGetSipAcc(String messageId, BusiTerminal terminal) {
        BusiTerminalMapper busiTerminalMapper = (BusiTerminalMapper) SpringContextUtil.getBean("busiTerminalMapper");
        List<BusiTerminal> busiTerminals = busiTerminalMapper.selectBusiTerminalList(terminal);
        if (null != busiTerminals && busiTerminals.size() > 0) {
            this.smcTerminalGetSipAccount(busiTerminals.get(0), messageId, terminal.getSn());
        }
    }

    public void smcTerminalGetSipAccount(BusiTerminal busiTerminal, String messageId, String sn) {
        String scRegisterAddress = null;
        Object scRegisterAddressObj = busiTerminal.getBusinessProperties().get("scRegisterAddress");
        if (scRegisterAddressObj != null) {
            scRegisterAddress = scRegisterAddressObj.toString();
        }
        if (scRegisterAddress != null) {
            JSONObject obj = new JSONObject();

            BusiLiveSetting busiLiveSetting = new BusiLiveSetting();
            busiLiveSetting.setDeptId(busiTerminal.getDeptId());
            BusiLiveSettingMapper liveUrlList = (BusiLiveSettingMapper) SpringContextUtil.getBean("busiLiveSettingMapper");
            List<BusiLiveSetting> busiLiveSettings = liveUrlList.selectBusiLiveSettingList(busiLiveSetting);

            List<String> liveUrl = new ArrayList<>();
            List<Live> liveList = new ArrayList<>();

            if (busiLiveSettings != null && busiLiveSettings.size() > 0) {
                for (int i = 0; i < busiLiveSettings.size(); i++) {
                    if (busiLiveSettings.get(i).getStatus() == 1) {
                        liveUrl.add(busiLiveSettings.get(i).getUrl());
                        Live live = new Live();
                        live.setName(busiLiveSettings.get(i).getName());
                        live.setUrl(busiLiveSettings.get(i).getUrl());
                        liveList.add(live);
                        //jsonObject1.put(busiLiveSettings.get(i).getName(),busiLiveSettings.get(i).getUrl());
                    }
                }
            }

            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + sn;
            String action = TerminalTopic.GET_SIP_ACCOUNT;
            String ipAddress = scRegisterAddress;
            obj.put("proxyServer", ipAddress);
            obj.put("AuthDomain", ipAddress);
            String userName = busiTerminal.getCredential();
            obj.put("userName", userName);
            Integer port = 5060;
            obj.put("port", port);
            if (port == 5060) {
                obj.put("protocol", "tcp");
            } else {
                obj.put("protocol", "tls");
            }
            obj.put("name", busiTerminal.getName());
            obj.put("deptId", busiTerminal.getDeptId());
            obj.put("deptName", SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName());
            obj.put("liveUrl", liveList);
            obj.put(MqttConfigConstant.PASSWORD, busiTerminal.getPassword());
            obj.put("displayName", busiTerminal.getName());
            obj.put("sipServer", ipAddress);
            obj.put("sipPort", port);
            obj.put("sipUserName", userName);
            obj.put("sipPassword", busiTerminal.getPassword());

            obj.put(SipAccountProperty.TURN_SERVER, ipAddress);
            obj.put(SipAccountProperty.TURN_PORT, 0);
            obj.put(SipAccountProperty.TURN_USER_NAME, "test");
            obj.put(SipAccountProperty.TURN_PASSWORD, "test");

            obj.put(SipAccountProperty.STUN_SERVER, ipAddress);
            obj.put(SipAccountProperty.STUN_PORT, 0);
            obj.put("type", busiTerminal.getType());
            // 用户id
            Long userId = null;
            BusiUserTerminalMapper busiUserTerminalMapper = (BusiUserTerminalMapper) SpringContextUtil.getBean("busiUserTerminalMapper");
            BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminal.getId());
            if (busiUserTerminal != null) {
                userId = busiUserTerminal.getUserId();
            }
            obj.put("userId", userId);
            ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, obj, busiTerminal.getSn(), messageId);
        }
    }

    public SipServerInfo getSmcSipServerInfo(Long deptId) {
        SipServerInfo sipServerInfo = new SipAccountInfo();
        Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().get(DeptSmc3MappingCache.getInstance().get(deptId).getMcuId());
        if (null != smc3Bridge) {
            BusiMcuSmc3 busiMcuSmc3 = smc3Bridge.getBusiSMC();
            if (null != busiMcuSmc3) {
                sipServerInfo.setProxyServer(busiMcuSmc3.getIp());
                sipServerInfo.setAuthDomain(busiMcuSmc3.getIp());
                sipServerInfo.setPort(busiMcuSmc3.getCallPort());
                sipServerInfo.setType(TerminalType.SMC_SIP.getId());
                sipServerInfo.setDomainName(busiMcuSmc3.getCucmIp());
                sipServerInfo.setMcuDomain(busiMcuSmc3.getMcuDomain());
            }
        }
        return sipServerInfo;
    }

    public void smc2TerminalGetSipAcc(String messageId, BusiTerminal terminal) {
        BusiTerminalMapper busiTerminalMapper = (BusiTerminalMapper) SpringContextUtil.getBean("busiTerminalMapper");
        List<BusiTerminal> busiTerminals = busiTerminalMapper.selectBusiTerminalList(terminal);
        if (null != busiTerminals && busiTerminals.size() > 0) {
            this.smc2TerminalGetSipAccount(busiTerminals.get(0), messageId, terminal.getSn());
        }
    }

    public void smc2TerminalGetSipAccount(BusiTerminal busiTerminal, String messageId, String sn) {
        Smc2Bridge smc2Bridge = Smc2BridgeCache.getInstance().get(DeptSmc2MappingCache.getInstance().get(busiTerminal.getDeptId()).getMcuId());
        if (null != smc2Bridge) {

            BusiMcuSmc2 busiMcuSmc2 = smc2Bridge.getBusiSmc2();
            JSONObject obj = new JSONObject();

            BusiLiveSetting busiLiveSetting = new BusiLiveSetting();
            busiLiveSetting.setDeptId(busiTerminal.getDeptId());
            BusiLiveSettingMapper liveUrlList = (BusiLiveSettingMapper) SpringContextUtil.getBean("busiLiveSettingMapper");
            List<BusiLiveSetting> busiLiveSettings = liveUrlList.selectBusiLiveSettingList(busiLiveSetting);

            List<String> liveUrl = new ArrayList<>();
            List<Live> liveList = new ArrayList<>();

            if (busiLiveSettings != null && busiLiveSettings.size() > 0) {
                for (int i = 0; i < busiLiveSettings.size(); i++) {
                    if (busiLiveSettings.get(i).getStatus() == 1) {
                        liveUrl.add(busiLiveSettings.get(i).getUrl());
                        Live live = new Live();
                        live.setName(busiLiveSettings.get(i).getName());
                        live.setUrl(busiLiveSettings.get(i).getUrl());
                        liveList.add(live);
                        //jsonObject1.put(busiLiveSettings.get(i).getName(),busiLiveSettings.get(i).getUrl());
                    }
                }
            }

            if (null != busiMcuSmc2) {
                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + sn;
                String action = TerminalTopic.GET_SIP_ACCOUNT;
                String ipAddress = busiMcuSmc2.getIp();
                obj.put("proxyServer", ipAddress);
                obj.put("AuthDomain", ipAddress);
                String userName = busiTerminal.getCredential();
                if (StringUtils.isNotEmpty(busiMcuSmc2.getMcuDomain())) {
                    userName += "@" + busiMcuSmc2.getMcuDomain();
                }
                obj.put("userName", userName);
                Integer port = 5060;
                if (busiMcuSmc2.getCallPort() != null) {
                    port = busiMcuSmc2.getCallPort();
                }
                obj.put("port", port);
                if (port == 5060) {
                    obj.put("protocol", "tcp");
                } else {
                    obj.put("protocol", "tls");
                }
                obj.put("name", busiTerminal.getName());
                obj.put("deptId", busiTerminal.getDeptId());
                obj.put("deptName", SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName());
                obj.put("liveUrl", liveList);
                obj.put(MqttConfigConstant.PASSWORD, busiTerminal.getPassword());
                obj.put("displayName", busiTerminal.getName());
                obj.put("sipServer", ipAddress);
                obj.put("sipPort", port);
                obj.put("sipUserName", userName);
                obj.put("sipPassword", busiTerminal.getPassword());

                obj.put(SipAccountProperty.TURN_SERVER, ipAddress);
                obj.put(SipAccountProperty.TURN_PORT, 0);
                obj.put(SipAccountProperty.TURN_USER_NAME, "test");
                obj.put(SipAccountProperty.TURN_PASSWORD, "test");

                obj.put(SipAccountProperty.STUN_SERVER, ipAddress);
                obj.put(SipAccountProperty.STUN_PORT, 0);
                obj.put("type", busiTerminal.getType());
                // 用户id
                Long userId = null;
                BusiUserTerminalMapper busiUserTerminalMapper = (BusiUserTerminalMapper) SpringContextUtil.getBean("busiUserTerminalMapper");
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminal.getId());
                if (busiUserTerminal != null) {
                    userId = busiUserTerminal.getUserId();
                }
                obj.put("userId", userId);
                ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, obj, busiTerminal.getSn(), messageId);
            }
        }
    }

    public SipServerInfo getSmc2SipServerInfo(Long deptId) {
        SipServerInfo sipServerInfo = new SipAccountInfo();
        Smc2Bridge smc2Bridge = Smc2BridgeCache.getInstance().get(DeptSmc2MappingCache.getInstance().get(deptId).getMcuId());
        if (null != smc2Bridge) {
            BusiMcuSmc2 busiMcuSmc2 = smc2Bridge.getBusiSmc2();
            if (null != busiMcuSmc2) {
                sipServerInfo.setProxyServer(busiMcuSmc2.getIp());
                sipServerInfo.setAuthDomain(busiMcuSmc2.getIp());
                sipServerInfo.setPort(busiMcuSmc2.getCallPort());
                sipServerInfo.setType(TerminalType.SMC2_SIP.getId());
                sipServerInfo.setDomainName(busiMcuSmc2.getCucmIp());
                sipServerInfo.setMcuDomain(busiMcuSmc2.getMcuDomain());
            }
        }
        return sipServerInfo;
    }
    public void hwCloudTerminalGetSipAcc(String messageId, BusiTerminal terminal) {
        BusiTerminalMapper busiTerminalMapper = (BusiTerminalMapper) SpringContextUtil.getBean("busiTerminalMapper");
        List<BusiTerminal> busiTerminals = busiTerminalMapper.selectBusiTerminalList(terminal);
        if (null != busiTerminals && busiTerminals.size() > 0) {
            this.hwCloudTerminalGetSipAccount(busiTerminals.get(0), messageId, terminal.getSn());
        }
    }

    public void hwCloudTerminalGetSipAccount(BusiTerminal busiTerminal, String messageId, String sn) {
        HwcloudBridge hwcloudBridge = HwcloudBridgeCache.getInstance().get(DeptHwcloudMappingCache.getInstance().get(busiTerminal.getDeptId()).getMcuId());
        if (null != hwcloudBridge) {

            BusiMcuHwcloud busiMcuHwcloud = hwcloudBridge.getBusiHwcloud();
            JSONObject obj = new JSONObject();

            BusiLiveSetting busiLiveSetting = new BusiLiveSetting();
            busiLiveSetting.setDeptId(busiTerminal.getDeptId());
            BusiLiveSettingMapper liveUrlList = (BusiLiveSettingMapper) SpringContextUtil.getBean("busiLiveSettingMapper");
            List<BusiLiveSetting> busiLiveSettings = liveUrlList.selectBusiLiveSettingList(busiLiveSetting);

            List<String> liveUrl = new ArrayList<>();
            List<Live> liveList = new ArrayList<>();

            if (busiLiveSettings != null && busiLiveSettings.size() > 0) {
                for (int i = 0; i < busiLiveSettings.size(); i++) {
                    if (busiLiveSettings.get(i).getStatus() == 1) {
                        liveUrl.add(busiLiveSettings.get(i).getUrl());
                        Live live = new Live();
                        live.setName(busiLiveSettings.get(i).getName());
                        live.setUrl(busiLiveSettings.get(i).getUrl());
                        liveList.add(live);
                        //jsonObject1.put(busiLiveSettings.get(i).getName(),busiLiveSettings.get(i).getUrl());
                    }
                }
            }

            if (null != busiMcuHwcloud) {
                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + sn;
                String action = TerminalTopic.GET_SIP_ACCOUNT;
                String ipAddress = "";
                obj.put("proxyServer", ipAddress);
                obj.put("AuthDomain", ipAddress);
                String userName = busiTerminal.getCredential();
                obj.put("userName", userName);
                Integer port = 5061;
                obj.put("port", port);
                if (port == 5060) {
                    obj.put("protocol", "tcp");
                } else {
                    obj.put("protocol", "tls");
                }
                obj.put("name", busiTerminal.getName());
                obj.put("deptId", busiTerminal.getDeptId());
                obj.put("deptName", SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName());
                obj.put("liveUrl", liveList);
                obj.put(MqttConfigConstant.PASSWORD, busiTerminal.getPassword());
                obj.put("displayName", busiTerminal.getName());
                obj.put("sipServer", ipAddress);
                obj.put("sipPort", port);
                obj.put("sipUserName", userName);
                obj.put("sipPassword", busiTerminal.getPassword());

                obj.put(SipAccountProperty.TURN_SERVER, ipAddress);
                obj.put(SipAccountProperty.TURN_PORT, 0);
                obj.put(SipAccountProperty.TURN_USER_NAME, "test");
                obj.put(SipAccountProperty.TURN_PASSWORD, "test");

                obj.put(SipAccountProperty.STUN_SERVER, ipAddress);
                obj.put(SipAccountProperty.STUN_PORT, 0);
                obj.put("type", busiTerminal.getType());
                // 用户id
                Long userId = null;
                BusiUserTerminalMapper busiUserTerminalMapper = (BusiUserTerminalMapper) SpringContextUtil.getBean("busiUserTerminalMapper");
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminal.getId());
                if (busiUserTerminal != null) {
                    userId = busiUserTerminal.getUserId();
                }
                obj.put("userId", userId);
                ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, obj, busiTerminal.getSn(), messageId);
            }
        }
    }

    public SipServerInfo getHwCloudSipServerInfo(Long deptId) {
        SipServerInfo sipServerInfo = new SipAccountInfo();
        HwcloudBridge hwcloudBridge = HwcloudBridgeCache.getInstance().get(DeptHwcloudMappingCache.getInstance().get(deptId).getMcuId());
        if (null != hwcloudBridge) {
            BusiMcuHwcloud busiMcuHwcloud = hwcloudBridge.getBusiHwcloud();
            if (null != busiMcuHwcloud) {
                sipServerInfo.setProxyServer(busiMcuHwcloud.getCallIp());
                sipServerInfo.setAuthDomain(busiMcuHwcloud.getCallIp());
                sipServerInfo.setPort(busiMcuHwcloud.getCallPort());
                sipServerInfo.setType(TerminalType.HW_CLOUD.getId());
                sipServerInfo.setDomainName("");
                sipServerInfo.setMcuDomain("");
            }
        }
        return sipServerInfo;
    }

    /**
     * @param jsonObject //保存终端的对应的sip信息
     * @param clientId
     * @param terminalId
     * @param deptId
     * @param deptId
     */
    private void saveTerminalSipInfo(JSONObject jsonObject, String clientId, Long terminalId, Long deptId) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                BusiSipAccount busiSipAccount = new BusiSipAccount();
                busiSipAccount.setSn(clientId);
                busiSipAccount.setCreateTime(new Date());
                busiSipAccount.setTerminalId(terminalId);
                busiSipAccount.setDeptId(deptId);
                busiSipAccount.setSipServer(jsonObject.getString(SipAccountProperty.SIP_SERVER));
                busiSipAccount.setTurnServer(jsonObject.getString(SipAccountProperty.TURN_SERVER));
                busiSipAccount.setStunServer(jsonObject.getString(SipAccountProperty.STUN_SERVER));
                busiSipAccount.setProxyServer(jsonObject.getString(SipAccountProperty.PROXY_SERVER));
                busiSipAccount.setSipPassword(jsonObject.getString(SipAccountProperty.SIP_PASSWORD));
                busiSipAccount.setTurnPassword(jsonObject.getString(SipAccountProperty.TURN_PASSWORD));
                busiSipAccount.setTurnUserName(jsonObject.getString(SipAccountProperty.TURN_USER_NAME));
                busiSipAccount.setSipPort(Integer.valueOf(jsonObject.getString(SipAccountProperty.SIP_PORT)));
                busiSipAccount.setStunPort(Integer.valueOf(jsonObject.getString(SipAccountProperty.STUN_PORT)));
                busiSipAccount.setTurnPort(Integer.valueOf(jsonObject.getString(SipAccountProperty.TURN_PORT)));
                busiSipAccount.setSipUserName(Long.valueOf(jsonObject.getString(SipAccountProperty.SIP_USER_NAME)));

                BusiSipAccountMapper SipAccountMapper = (BusiSipAccountMapper) SpringContextUtil.getBean("busiSipAccountMapper");
                SipAccountMapper.insertBusiSipAccount(busiSipAccount);
            }
        }, "保存sipAccount信息的 ==================> sip1").start();

    }

    private FcmBridge getConnectServerFcmBridge(Long deptId, Long serverId) {
        FcmBridge fcmBridge = null;
//        if (serverId != null) {
//            fcmBridge = FcmBridgeCache.getInstance().get(serverId);
//            if (fcmBridge != null) {
//                if (fcmBridge.isAvailable()) {
//                    return fcmBridge;
//                }
//            }
//        }
        BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(deptId);
        if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
            FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(fsd.getServerId());
            if (fcmBridgeCluster != null) {
                List<FcmBridge> fcmBridges = fcmBridgeCluster.getAvailableFcmBridges();
                int size = fcmBridges.size();
                if (size > 0) {
                    if (serverId != null) {
                        fcmBridge = fcmBridges.get(0);
                        for (int i = 0; i < fcmBridges.size(); i++) {
                            if (serverId == fcmBridges.get(i).getBusiFreeSwitch().getId()) {
                                if (i < fcmBridges.size() - 1) {
                                    fcmBridge = fcmBridges.get(i + 1);
                                }
                                break;
                            }
                        }
                    }
                    if (fcmBridge == null) {
                        Random random = new Random();
                        int i = random.nextInt(size);
                        fcmBridge = fcmBridges.get(i);
                    }
                }
            }
        } else {
            fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
        }
        if (fcmBridge == null) {
            if (serverId != null) {
                fcmBridge = FcmBridgeCache.getInstance().get(serverId);
            }
        }
        return fcmBridge;
    }

    /**
     * 与服务器的连接
     */
    public boolean connectServer(FcmBridge fcmBridge) throws SystemException {
        boolean isConnected = false;
        BusiFreeSwitch busiFreeSwitch = fcmBridge.getBusiFreeSwitch();
        if (fcmBridge != null) {
            if (null != busiFreeSwitch) {
                Integer serverPort = busiFreeSwitch.getPort();
                String userName = busiFreeSwitch.getUserName();
                String password = busiFreeSwitch.getPassword();
                String ipAddr = busiFreeSwitch.getIp();

                try {
                    SshRemoteServerOperate.getInstance().sshRemoteCallLogin(ipAddr, userName, password, serverPort);
                    isConnected = true;
                } catch (Exception e) {
                    throw new SystemException(100334, "连接FS服务异常!");
                }
            }
        }
        return isConnected;
    }
}
