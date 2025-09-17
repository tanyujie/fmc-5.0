package com.paradisecloud.fcm.ops.cloud.mqtt.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.CommonConfigCache;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.cache.LicenseCache;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiOpsInfoMapper;
import com.paradisecloud.fcm.dao.model.BusiOpsInfo;
import com.paradisecloud.fcm.ops.cloud.mqtt.client.OpsEmqClient;
import com.paradisecloud.fcm.ops.cloud.mqtt.common.PublishMessage;
import com.paradisecloud.fcm.ops.cloud.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.ops.cloud.mqtt.enums.QosEnum;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.service.ISysUserService;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Component
public class MonitorMqttServer extends Thread implements InitializingBean , ApplicationRunner {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${application.version:}")
	private String appVersion;

	@Resource
	private ISysUserService sysUserService;
	
	@Override
    public void run()
    {
		//监测FMQ服务和FMQ的用户
		logger.info("===========> 监测云FMQ服务是否正常！");
		ThreadUtils.sleep(30000);
		if ("ops".equalsIgnoreCase(ExternalConfigCache.getInstance().getRegion())) {
			if (ExternalConfigCache.getInstance().getCloudMqttIp() != null && ExternalConfigCache.getInstance().getCloudMqttPort() != null) {
				while (true) {
					try {
						OpsEmqClient emqClient = (OpsEmqClient) SpringContextUtil.getBean("opsEmqClient");
						if (!emqClient.isConnected()) {
							String sn = LicenseCache.getInstance().getSn();
							if (StringUtils.isEmpty(sn)) {
									sn = com.paradisecloud.fcm.common.constant.MqttConfigConstant.OPS_TEST_SN;
							}
							if (StringUtils.isNotEmpty(sn)) {
								Boolean connect = emqClient.connect(sn);
								if (connect) {
									emqClient.subscribe("ops/" + sn, QosEnum.QOS2);
									register(sn);
								}
							}
						}
					} catch (Throwable e) {
						logger.error("云FMQ服务出现异常！", e);
					} finally {
						ThreadUtils.sleep(30000);
					}
				}
			}
		}
    }

    private void register(String clientId) {
		String topic = MqttConfigConstant.TOPIC_PREFIX_PLATFORM_OPS + clientId;
		HashMap<String, Object> messageMap = new HashMap<>();
		messageMap.put("action", "register");
		messageMap.put("clientId", clientId);
		HashMap<String, Object> dataMap = new HashMap<>();
		dataMap.put("sn", LicenseCache.getInstance().getSn());
		dataMap.put("ip", "");
		BusiOpsInfoMapper busiOpsInfoMapper = BeanFactory.getBean(BusiOpsInfoMapper.class);
		List<BusiOpsInfo> busiOpsInfoList = busiOpsInfoMapper.selectBusiOpsInfoList(new BusiOpsInfo());
		if (busiOpsInfoList != null && busiOpsInfoList.size() > 0) {
			dataMap.put("ip", busiOpsInfoList.get(0).getIpAddress());
		}
		dataMap.put("connectIp", ExternalConfigCache.getInstance().getCloudMqttIp());
		dataMap.put("terminalType", "ops");
		String versionCode = "";
		if (CommonConfigCache.getInstance().getJarCreateDate() != null) {
			versionCode = DateUtil.convertDateToString(CommonConfigCache.getInstance().getJarCreateDate(), "yyyyMMdd");
		}
		dataMap.put("versionCode", versionCode);
		dataMap.put("versionName",appVersion);
		SysUser sysUser = sysUserService.selectUserByUserName("admin");
		if (sysUser != null) {
			dataMap.put("email", sysUser.getEmail());
			dataMap.put("phoneNumber", sysUser.getPhonenumber());
		}
		messageMap.put("data", dataMap);
		String message = JSONObject.toJSONString(messageMap);
		PublishMessage.getInstance().publishTopicMsg(topic, "", message, false);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.start();
	}

	/**
	 * Callback used to run the bean.
	 *
	 * @param args incoming application arguments
	 * @throws Exception on error
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
	}
}
