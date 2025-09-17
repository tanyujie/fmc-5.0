package com.paradisecloud.fcm.mqtt.common;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalUpgradeMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiTerminalUpgrade;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.TerminalTopic;
import com.paradisecloud.fcm.mqtt.impls.BusiMqttServiceImpl;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AppUpgradeTask extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusiMqttServiceImpl.class);

    public AppUpgradeTask(String id, long delayInMilliseconds) {
        super(id, delayInMilliseconds);
    }

    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {
        LOGGER.info("APP更新推送开始。ID:" + getId());
        BusiTerminalUpgradeMapper busiTerminalUpgradeMapper = (BusiTerminalUpgradeMapper) SpringContextUtil.getBean("busiTerminalUpgradeMapper");
        BusiTerminalUpgrade busiTerminalUpgrade = busiTerminalUpgradeMapper.selectBusiTerminalUpgradeById(Long.valueOf(getId()));

        if (busiTerminalUpgrade != null) {

            Map<Long, BusiTerminal> busiTerminalMap = TerminalCache.getInstance().getAppTypeTerminalMap(busiTerminalUpgrade.getTerminalType());
            if (busiTerminalMap != null && busiTerminalMap.size() > 0) {
                for (BusiTerminal busiTerminal : busiTerminalMap.values()) {
                    boolean hasAppUpdate = false;
                    if (StringUtils.isEmpty(busiTerminal.getAppVersionCode()) || busiTerminalUpgrade.getVersionNum().length() > busiTerminal.getAppVersionCode().length() || busiTerminalUpgrade.getVersionNum().compareTo(busiTerminal.getAppVersionCode()) > 0) {
                        if (busiTerminal.getMqttOnlineStatus() != null && TerminalOnlineStatus.ONLINE.getValue() == busiTerminal.getMqttOnlineStatus()) {
                            hasAppUpdate = true;
                        }
                    }
                    if (hasAppUpdate) {
                        JSONObject jsonObject = new JSONObject();
                        Map<String, String> map = new HashMap<>();

                        String clientId = busiTerminal.getSn();
                        jsonObject.put("clientId", clientId);
                        jsonObject.put("action", TerminalTopic.TERMINAL_UPGRADE);
                        map.put("serverUrl", busiTerminalUpgrade.getServerUrl());
                        map.put("versionName", busiTerminalUpgrade.getVersionName());
                        map.put("versionNum", busiTerminalUpgrade.getVersionNum());
                        map.put("description", busiTerminalUpgrade.getVersionDescription());
                        map.put("checkNow", "true");
                        jsonObject.put("data", map);

                        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
                        PublisMessage.getInstance().publishTopicMsg(terminalTopic, clientId, jsonObject.toString(), false);
                    }
                }
            }
        }
    }
}
