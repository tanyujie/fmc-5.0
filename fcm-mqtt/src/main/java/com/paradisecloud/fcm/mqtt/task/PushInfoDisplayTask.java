package com.paradisecloud.fcm.mqtt.task;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.AppType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.thread.PushMessageToIpTerminalThread;
import com.paradisecloud.fcm.dao.mapper.BusiInfoDisplayMapper;
import com.paradisecloud.fcm.dao.model.BusiInfoDisplay;
import com.paradisecloud.fcm.dao.model.BusiSmartRoom;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDoorplate;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.ResponseInfo;
import com.paradisecloud.fcm.mqtt.constant.TerminalTopic;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDoorplateCache;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author admin
 */
public class PushInfoDisplayTask extends Task {


    private static final Logger LOGGER = LoggerFactory.getLogger(PushInfoDisplayTask.class);

    public PushInfoDisplayTask(String id, long delayInMilliseconds, Long pushId) {
        super(id, delayInMilliseconds);
        this.pushId = pushId;
    }

    private Long pushId;


    @Override
    public void run() {
        LOGGER.info("服务器信息推送开始。ID:" + getId());
        BusiInfoDisplayMapper busiInfoDisplayMapper = BeanFactory.getBean(BusiInfoDisplayMapper.class);
        BusiInfoDisplay busiInfoDisplay = busiInfoDisplayMapper.selectBusiInfoDisplayById(pushId);
        if (busiInfoDisplay != null && busiInfoDisplay.getType() == 2) {
            String infoDisplayForIp = "";
            try {
                String fmcRootUrl = ExternalConfigCache.getInstance().getFmcRootUrl();
                String fmcIp = fmcRootUrl.replace("http://", "").replace("https://", "");
                if (fmcIp.indexOf(":") > 0) {
                    fmcIp.substring(0, fmcIp.indexOf(":"));
                }
                if (fmcIp.indexOf("/") > 0) {
                    fmcIp = fmcIp.substring(0, fmcIp.indexOf("/"));
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", busiInfoDisplay.getType());
                jsonObject.put("displayType", busiInfoDisplay.getDisplayType());
                jsonObject.put("durationTime", busiInfoDisplay.getDurationTime());
                setUrlData(jsonObject, fmcIp, busiInfoDisplay.getUrlData());
                JSONObject jObject = new JSONObject();
                jObject.put(MqttConfigConstant.CODE, ResponseInfo.CODE_200);
                jObject.put(MqttConfigConstant.MSG, ResponseInfo.SUCCESS);
                jObject.put(MqttConfigConstant.ACTION, TerminalTopic.INFO_DISPLAY);
                jObject.put(MqttConfigConstant.MESSAGE_ID, "");
                jObject.put(MqttConfigConstant.JSON_DATA_STR, jsonObject);
                infoDisplayForIp = jObject.toString();
            } catch (Exception e) {
            }
            try {
                String pushTerminalIds = busiInfoDisplay.getPushTerminalIds();
                Integer pushObject = busiInfoDisplay.getPushObject();
                Integer type = busiInfoDisplay.getPushType();
                if (type == 2) {
                    if (StringUtils.isNotEmpty(pushTerminalIds)) {
                        String[] split = pushTerminalIds.split(",");
                        for (String s : split) {
                            BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().get(Long.valueOf(s));
                            if (busiSmartRoom != null) {
                                Long boundDoorplateId = SmartRoomCache.getInstance().getBoundDoorplateId(busiSmartRoom.getId());
                                if (boundDoorplateId != null) {
                                    BusiSmartRoomDoorplate busiSmartRoomDoorplate = SmartRoomDoorplateCache.getInstance().get(boundDoorplateId);
                                    if (busiSmartRoomDoorplate != null) {
                                        String sn = busiSmartRoomDoorplate.getSn();

                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("type", busiInfoDisplay.getType());
                                        jsonObject.put("displayType", busiInfoDisplay.getDisplayType());
                                        jsonObject.put("durationTime", busiInfoDisplay.getDurationTime());
                                        setUrlData(jsonObject, busiSmartRoomDoorplate.getConnectIp(), busiInfoDisplay.getUrlData());
                                        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX_DOORPLATE + sn;
                                        String action = TerminalTopic.INFO_DISPLAY;
                                        ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, sn, "");
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (pushObject == 3) {
                        String[] split = pushTerminalIds.split(",");
                        for (String s : split) {
                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(Long.valueOf(s));
                            if (busiTerminal != null && StringUtils.isNotEmpty(busiTerminal.getSn())) {
                                String sn = busiTerminal.getSn();

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("type", busiInfoDisplay.getType());
                                jsonObject.put("displayType", busiInfoDisplay.getDisplayType());
                                jsonObject.put("durationTime", busiInfoDisplay.getDurationTime());
                                setUrlData(jsonObject, busiTerminal.getConnectIp(), busiInfoDisplay.getUrlData());
                                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + sn;
                                String action = TerminalTopic.INFO_DISPLAY;
                                ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, sn, "");
                            }
                            if (TerminalType.isOnlyIP(busiTerminal.getType())) {
                                if (StringUtils.isNotEmpty(infoDisplayForIp)) {
                                    pushToIp(busiTerminal.getIp(), infoDisplayForIp);
                                }
                            }
                        }
                    } else {
                        Set<Long> deptIdSet = new HashSet<>();
                        if (pushObject == 2) {
                            List<SysDept> subordinateDepts = SysDeptCache.getInstance().getSubordinateDepts(busiInfoDisplay.getDeptId());
                            for (SysDept subordinateDept : subordinateDepts) {
                                deptIdSet.add(subordinateDept.getDeptId());
                            }
                            deptIdSet.add(busiInfoDisplay.getDeptId());
                        }
                        List<AppType> mqttTypeList = AppType.getMqttTypeList();
                        for (AppType appType : mqttTypeList) {
                            Map<Long, BusiTerminal> appTypeTerminalMap = TerminalCache.getInstance().getAppTypeTerminalMap(appType.getCode());
                            if (appTypeTerminalMap != null) {
                                appTypeTerminalMap.forEach((k, busiTerminal) -> {
                                    Long deptId = busiTerminal.getDeptId();
                                    if (pushObject == 1) {
                                        if (deptId.longValue() == busiInfoDisplay.getDeptId().longValue()) {
                                            if (StringUtils.isNotEmpty(busiTerminal.getSn())) {

                                                String sn = busiTerminal.getSn();

                                                JSONObject jsonObject = new JSONObject();
                                                jsonObject.put("type", busiInfoDisplay.getType());
                                                jsonObject.put("displayType", busiInfoDisplay.getDisplayType());
                                                jsonObject.put("durationTime", busiInfoDisplay.getDurationTime());
                                                setUrlData(jsonObject, busiTerminal.getConnectIp(), busiInfoDisplay.getUrlData());
                                                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + sn;
                                                String action = TerminalTopic.INFO_DISPLAY;
                                                ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, sn, "");
                                            }
                                        }
                                    } else {
                                        boolean contains = deptIdSet.contains(deptId);
                                        if (contains) {
                                            if (StringUtils.isNotEmpty(busiTerminal.getSn())) {

                                                String sn = busiTerminal.getSn();

                                                JSONObject jsonObject = new JSONObject();
                                                jsonObject.put("type", busiInfoDisplay.getType());
                                                jsonObject.put("displayType", busiInfoDisplay.getDisplayType());
                                                jsonObject.put("durationTime", busiInfoDisplay.getDurationTime());
                                                setUrlData(jsonObject, busiTerminal.getConnectIp(), busiInfoDisplay.getUrlData());
                                                String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + sn;
                                                String action = TerminalTopic.INFO_DISPLAY;
                                                ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, sn, "");
                                            }
                                        }
                                    }
                                });
                            }
                        }
                        Map<Long, BusiTerminal> onlyIpTerminalsMap = TerminalCache.getInstance().getOnlyIpTerminalsMap();
                        for (BusiTerminal busiTerminal : onlyIpTerminalsMap.values()) {
                            Long deptId = busiTerminal.getDeptId();
                            if (pushObject == 1) {
                                if (deptId.longValue() == busiInfoDisplay.getDeptId().longValue()) {
                                    if (TerminalType.isOnlyIP(busiTerminal.getType())) {
                                        if (StringUtils.isNotEmpty(infoDisplayForIp)) {
                                            pushToIp(busiTerminal.getIp(), infoDisplayForIp);
                                        }
                                    }
                                }
                            } else {
                                boolean contains = deptIdSet.contains(deptId);
                                if (contains) {
                                    if (TerminalType.isOnlyIP(busiTerminal.getType())) {
                                        if (StringUtils.isNotEmpty(infoDisplayForIp)) {
                                            pushToIp(busiTerminal.getIp(), infoDisplayForIp);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                busiInfoDisplay.setLastPushTime(new Date());
                busiInfoDisplayMapper.updateBusiInfoDisplay(busiInfoDisplay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setUrlData(JSONObject jsonObject, String ip, String introduce) {
        String urlTemp = ExternalConfigCache.getInstance().getFmcRootUrl();
        String fmcRootUrlExternal = ExternalConfigCache.getInstance().getFmcRootUrlExternal();
        if (com.paradisecloud.common.utils.StringUtils.isNotEmpty(fmcRootUrlExternal)) {
            try {
                String externalIp = fmcRootUrlExternal.replace("http://", "").replace("https://", "");
                if (externalIp.indexOf(":") > 0) {
                    externalIp.substring(0, externalIp.indexOf(":"));
                }
                if (externalIp.indexOf("/") > 0) {
                    externalIp = externalIp.substring(0, externalIp.indexOf("/"));
                }
                if (externalIp.equals(ip)) {
                    urlTemp = fmcRootUrlExternal;
                }
            } catch (Exception e) {
            }
        }
        if (com.paradisecloud.common.utils.StringUtils.isNotEmpty(introduce)) {
            introduce = introduce.replace("{url}", urlTemp);
            jsonObject.put("urlData", introduce);
        }
    }

    private void pushToIp(String host, String message) {
        new PushMessageToIpTerminalThread(host, message).start();
    }
}
