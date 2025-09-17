package com.paradisecloud.fcm.mqtt.cache;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.constant.MqttConfigConstant;
import com.paradisecloud.fcm.common.constant.ResponseInfo;
import com.paradisecloud.fcm.common.constant.TerminalTopic;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.listener.IpTerminalEventListener;
import com.paradisecloud.fcm.common.thread.PushMessageToIpTerminalThread;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.RecordsSearchVo;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.listener.MqttForFmePushMessageCache;
import com.paradisecloud.fcm.fme.conference.listener.MqttForFmePushMessageListener;
import com.paradisecloud.fcm.fme.conference.task.UpdateRecordsTask;
import com.paradisecloud.fcm.mcu.kdc.listener.MqttForMcuKdcPushMessageCache;
import com.paradisecloud.fcm.mcu.kdc.listener.MqttForMcuKdcPushMessageListener;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcTemplateConferenceService;
import com.paradisecloud.fcm.mcu.plc.listener.MqttForMcuPlcPushMessageCache;
import com.paradisecloud.fcm.mcu.plc.listener.MqttForMcuPlcPushMessageListener;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcTemplateConferenceService;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.listener.MqttForMcuZjPushMessageCache;
import com.paradisecloud.fcm.mcu.zj.listener.MqttForMcuZjPushMessageListener;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjTemplateConferenceService;
import com.paradisecloud.fcm.mqtt.enums.QosEnum;
import com.paradisecloud.fcm.mqtt.interfaces.ITerminalActionService;
import com.paradisecloud.fcm.mqtt.model.Live;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2TemplateConferenceService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3TemplateConferenceService;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.model.MqttBridge;
import com.paradisecloud.fcm.mqtt.model.MqttBridgeCollection;

import javax.annotation.Resource;

@Component
@Order(105)
public class MqttInfoCache implements ApplicationRunner
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MqttInfoCache.class);
	
	@Resource
	private BusiMqttMapper busiMqttMapper;
	@Resource
	private BusiMqttClusterMapper busiMqttClusterMapper;
	@Resource
	private BusiMqttDeptMapper busiMqttDeptMapper;
	@Resource
	private BusiMqttClusterMapMapper busiMqttClusterMapMapper;
	@Resource
	private BusiConferenceAppointmentMapper busiConferenceAppointmentMapper;
	@Resource
	private BusiMcuZjConferenceAppointmentMapper busiMcuZjConferenceAppointmentMapper;
	@Resource
	private BusiMcuPlcConferenceAppointmentMapper busiMcuPlcConferenceAppointmentMapper;
	@Resource
	private BusiMcuKdcConferenceAppointmentMapper busiMcuKdcConferenceAppointmentMapper;
	@Resource
	private BusiMcuSmc2ConferenceAppointmentMapper busiMcuSmc2ConferenceAppointmentMapper;
	@Resource
	private BusiMcuSmc3ConferenceAppointmentMapper busiMcuSmc3ConferenceAppointmentMapper;

	@Resource
	private IBusiTemplateConferenceService busiTemplateConferenceService;
	@Resource
	private IBusiMcuZjTemplateConferenceService busiMcuZjTemplateConferenceService;
	@Resource
	private IBusiMcuSmc3TemplateConferenceService busiMcuSmc3TemplateConferenceService;
	@Resource
	private IBusiMcuSmc2TemplateConferenceService busiMcuSmc2TemplateConferenceService;
	@Resource
	private IBusiMcuKdcTemplateConferenceService busiMcuKdcTemplateConferenceService;
	@Resource
	private IBusiMcuPlcTemplateConferenceService busiMcuPlcTemplateConferenceService;
	@Resource
	private BusiInfoDisplayMapper busiInfoDisplayMapper;
	
	@Override
	public void run(ApplicationArguments args) throws Exception 
	{
		
		try {
			//获取busiMqtt信息放入缓存
			this.getBusiMqttPutCache();
			
			//获取BusiMqttCluster信息放入缓存
			this.getBusiMqttClusterPutCache();
			
			//获取BusiMqttDept信息放入缓存
			this.getBusiMqttDeptPutCache();
			
			//获取BusiMqttClusterMap信息放入缓存
			this.getBusiMqttClusterMapPutCache();

			//获取预约会议列表缓存
			this.appointmentCache();
			
			//获取mqtt配置信息
			ResponseTerminal.getInstance().getMqttConfigInfo();

			//各个会议监听
			this.initMqttPushMessageListener();
		} catch (Exception e) {
			LOGGER.error("mqtt存放缓存数据异常", e);
		}
		try {
			//信息展示缓存
			this.infoDisplayCache();

			//初始化IP终端推送消息监听器
			this.initIpPushMessageListener();
		} catch (Exception e) {
			LOGGER.error("MQTT信息展示初始化异常", e);
		}
	}

	//预约会议信息放入缓存
	private void appointmentCache() {
		List<BusiConferenceAppointment> busiConferenceAppointments = busiConferenceAppointmentMapper.selectBusiConferenceAppointmentList(new BusiConferenceAppointment());
		if (busiConferenceAppointments != null && busiConferenceAppointments.size() > 0) {
			for (BusiConferenceAppointment busiConferenceAppointment : busiConferenceAppointments) {
				ModelBean templateConferenceDetails = busiTemplateConferenceService.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
				busiConferenceAppointment.setParams(templateConferenceDetails);
				AppointmentCache.getInstance().put(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.FME), busiConferenceAppointment);
			}
		}
		List<BusiMcuZjConferenceAppointment> busiMcuZjConferenceAppointments = busiMcuZjConferenceAppointmentMapper.selectBusiMcuZjConferenceAppointmentList(new BusiMcuZjConferenceAppointment());
		if (busiMcuZjConferenceAppointments != null && busiMcuZjConferenceAppointments.size() > 0) {
			for (BusiMcuZjConferenceAppointment busiConferenceAppointment : busiMcuZjConferenceAppointments) {
				ModelBean templateConferenceDetails = this.busiMcuZjTemplateConferenceService.selectBusiMcuZjTemplateConferenceById(busiConferenceAppointment.getTemplateId());
				busiConferenceAppointment.setParams(templateConferenceDetails);
				AppointmentCache.getInstance().put(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_ZJ), busiConferenceAppointment);
			}
		}
		List<BusiMcuPlcConferenceAppointment> busiMcuPlcConferenceAppointmentList = busiMcuPlcConferenceAppointmentMapper.selectBusiMcuPlcConferenceAppointmentList(new BusiMcuPlcConferenceAppointment());
		if (busiMcuPlcConferenceAppointmentList != null && busiMcuPlcConferenceAppointmentList.size() > 0) {
			for (BusiMcuPlcConferenceAppointment busiConferenceAppointment : busiMcuPlcConferenceAppointmentList) {
				ModelBean templateConferenceDetails = busiMcuPlcTemplateConferenceService.selectBusiMcuPlcTemplateConferenceById(busiConferenceAppointment.getTemplateId());
				busiConferenceAppointment.setParams(templateConferenceDetails);
				AppointmentCache.getInstance().put(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_PLC), busiConferenceAppointment);
			}
		}
		List<BusiMcuKdcConferenceAppointment> busiMcuKdcConferenceAppointmentList = busiMcuKdcConferenceAppointmentMapper.selectBusiMcuKdcConferenceAppointmentList(new BusiMcuKdcConferenceAppointment());
		if (busiMcuKdcConferenceAppointmentList != null && busiMcuKdcConferenceAppointmentList.size() > 0) {
			for (BusiMcuKdcConferenceAppointment busiConferenceAppointment : busiMcuKdcConferenceAppointmentList) {
				ModelBean templateConferenceDetails = busiMcuKdcTemplateConferenceService.selectBusiMcuKdcTemplateConferenceById(busiConferenceAppointment.getTemplateId());
				busiConferenceAppointment.setParams(templateConferenceDetails);
				AppointmentCache.getInstance().put(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_KDC), busiConferenceAppointment);
			}
		}
		List<BusiMcuSmc2ConferenceAppointment> busiMcuSmc2ConferenceAppointmentList = busiMcuSmc2ConferenceAppointmentMapper.selectBusiMcuSmc2ConferenceAppointmentList(new BusiMcuSmc2ConferenceAppointment());
		if (busiMcuSmc2ConferenceAppointmentList != null && busiMcuSmc2ConferenceAppointmentList.size() > 0) {
			for (BusiMcuSmc2ConferenceAppointment busiConferenceAppointment : busiMcuSmc2ConferenceAppointmentList) {
				ModelBean templateConferenceDetails = busiMcuSmc2TemplateConferenceService.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
				busiConferenceAppointment.setParams(templateConferenceDetails);
				AppointmentCache.getInstance().put(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.SMC2), busiConferenceAppointment);
			}
		}
		List<BusiMcuSmc3ConferenceAppointment> busiMcuSmc3ConferenceAppointmentList = busiMcuSmc3ConferenceAppointmentMapper.selectBusiMcuSmc3ConferenceAppointmentList(new BusiMcuSmc3ConferenceAppointment());
		if (busiMcuSmc3ConferenceAppointmentList != null && busiMcuSmc3ConferenceAppointmentList.size() > 0) {
			for (BusiMcuSmc3ConferenceAppointment busiConferenceAppointment : busiMcuSmc3ConferenceAppointmentList) {
				ModelBean templateConferenceDetails = busiMcuSmc3TemplateConferenceService.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
				busiConferenceAppointment.setParams(templateConferenceDetails);
				AppointmentCache.getInstance().put(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.SMC3), busiConferenceAppointment);
			}
		}

	}

	/**
	 * 获取BusiMqttClusterMap信息放入缓存
	 */
	private void getBusiMqttClusterMapPutCache() {
		
		BusiMqttClusterMap busiMqttClusterMap = new BusiMqttClusterMap();
		List<BusiMqttClusterMap> busiMqttClusterMapList = busiMqttClusterMapMapper.selectBusiMqttClusterMapList(busiMqttClusterMap);
		if(null != busiMqttClusterMapList) 
		{
			for (BusiMqttClusterMap busiMqttClusterMap2 : busiMqttClusterMapList) {
				MqttBridgeCache.getInstance().updateMqttCluster(busiMqttClusterMap2);
			}
		}
	}

	private void getBusiMqttDeptPutCache() 
	{
		BusiMqttDept busiMqttDept = new BusiMqttDept();
		List<BusiMqttDept> mqttDeptList = busiMqttDeptMapper.selectBusiMqttDeptList(busiMqttDept);
		if(null != mqttDeptList) 
		{
			for (BusiMqttDept busiMqttDept2 : mqttDeptList) 
			{
				MqttDeptMappingCache.getInstance().put(busiMqttDept2.getDeptId(), busiMqttDept2);
			}
		}
	}

	private void getBusiMqttClusterPutCache() 
	{
		BusiMqttCluster busiMqttCluster = new BusiMqttCluster();
		List<BusiMqttCluster> busiMqttClusters = busiMqttClusterMapper.selectBusiMqttClusterList(busiMqttCluster);
		if(null != busiMqttClusters) 
		{
			for (BusiMqttCluster busiMqttCluster2 : busiMqttClusters) 
			{
				MqttClusterCache.getInstance().put(busiMqttCluster2.getId(), busiMqttCluster2);
			}
		}
	}

	private void getBusiMqttPutCache() 
	{
		
		BusiMqtt busiMqtt = new BusiMqtt();
		List<BusiMqtt> busiMqttList = busiMqttMapper.selectBusiMqttList(busiMqtt);
		if (null != busiMqttList) 
		{
			MqttBridgeCollection mqttBridgeCollection = new MqttBridgeCollection();
			for (BusiMqtt busiMqtt2 : busiMqttList) 
			{
				MqttBridge mqttBridge = new MqttBridge();
				mqttBridge.setBusiMqtt(busiMqtt2);
				MqttBridgeCache.getInstance().updateMqttBridge(mqttBridge);
				mqttBridgeCollection.addMqttBridge(mqttBridge);
			}
		}
	}

	private void initMqttPushMessageListener() {
		MqttForFmePushMessageCache.getInstance().setMqttForFmePushMessageListener(new MqttForFmePushMessageListener() {
			@Override
			public void onPushMessage(Integer code, String message, String topic, String action, JSONObject jObj, String clientId, String messageId) {
				ResponseTerminal.getInstance().responseTerminal(code, message, topic, action, jObj, clientId, messageId);
			}

			@Override
			public void onPushMessage(Integer code, String message, String topic, String action, JSONObject jObj, String messageId, int qos) {
				QosEnum qosEnum = QosEnum.QOS0;
				if (qos == QosEnum.QOS2.value()) {
					qosEnum = QosEnum.QOS2;
				} else if (qos == QosEnum.QOS1.value()) {
					qosEnum = QosEnum.QOS1;
				}
				ResponseTerminal.getInstance().responseTerminalByQOS(code, message, topic, action, jObj, messageId, qosEnum);
			}
		});
		MqttForMcuZjPushMessageCache.getInstance().setMqttForMcuZjPushMessageListener(new MqttForMcuZjPushMessageListener() {
			@Override
			public void onPushMessage(Integer code, String message, String topic, String action, JSONObject jObj, String clientId, String messageId) {
				ResponseTerminal.getInstance().responseTerminal(code, message, topic, action, jObj, clientId, messageId);
			}

			@Override
			public void onPushMessage(Integer code, String message, String topic, String action, JSONObject jObj, String messageId, int qos) {
				QosEnum qosEnum = QosEnum.QOS0;
				if (qos == QosEnum.QOS2.value()) {
					qosEnum = QosEnum.QOS2;
				} else if (qos == QosEnum.QOS1.value()) {
					qosEnum = QosEnum.QOS1;
				}
				ResponseTerminal.getInstance().responseTerminalByQOS(code, message, topic, action, jObj, messageId, qosEnum);
			}
		});
		MqttForMcuPlcPushMessageCache.getInstance().setMqttForMcuPlcPushMessageListener(new MqttForMcuPlcPushMessageListener() {
			@Override
			public void onPushMessage(Integer code, String message, String topic, String action, JSONObject jObj, String clientId, String messageId) {
				ResponseTerminal.getInstance().responseTerminal(code, message, topic, action, jObj, clientId, messageId);
			}

			@Override
			public void onPushMessage(Integer code, String message, String topic, String action, JSONObject jObj, String messageId, int qos) {
				QosEnum qosEnum = QosEnum.QOS0;
				if (qos == QosEnum.QOS2.value()) {
					qosEnum = QosEnum.QOS2;
				} else if (qos == QosEnum.QOS1.value()) {
					qosEnum = QosEnum.QOS1;
				}
				ResponseTerminal.getInstance().responseTerminalByQOS(code, message, topic, action, jObj, messageId, qosEnum);
			}
		});
		MqttForMcuKdcPushMessageCache.getInstance().setMqttForMcuKdcPushMessageListener(new MqttForMcuKdcPushMessageListener() {
			@Override
			public void onPushMessage(Integer code, String message, String topic, String action, JSONObject jObj, String clientId, String messageId) {
				ResponseTerminal.getInstance().responseTerminal(code, message, topic, action, jObj, clientId, messageId);
			}

			@Override
			public void onPushMessage(Integer code, String message, String topic, String action, JSONObject jObj, String messageId, int qos) {
				QosEnum qosEnum = QosEnum.QOS0;
				if (qos == QosEnum.QOS2.value()) {
					qosEnum = QosEnum.QOS2;
				} else if (qos == QosEnum.QOS1.value()) {
					qosEnum = QosEnum.QOS1;
				}
				ResponseTerminal.getInstance().responseTerminalByQOS(code, message, topic, action, jObj, messageId, qosEnum);
			}
		});
	}

	//信息展示缓存
	private void infoDisplayCache() {
		List<BusiInfoDisplay> busiInfoDisplayList = busiInfoDisplayMapper.selectBusiInfoDisplayList(new BusiInfoDisplay());
		for (BusiInfoDisplay busiInfoDisplay : busiInfoDisplayList) {
			InfoDisplayCache.getInstance().add(busiInfoDisplay);
		}
	}

	//初始化IP终端推送消息监听器
	private void initIpPushMessageListener() {
		TerminalCache.getInstance().setIpTerminalEventListener(new IpTerminalEventListener() {

			@Override
			public void pushAll(long terminalId) {
				pushServerInfo(terminalId);
				pushGetSipAccount(terminalId);
//				pushAddrBook(terminalId);
//				pushRecordList(terminalId);
//				pushInfoDisplay(terminalId);
			}

			@Override
			public void pushServerInfo(long terminalId) {
				BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(MqttConfigConstant.TIME, System.currentTimeMillis());
				JSONObject jObject = new JSONObject();
				jObject.put(MqttConfigConstant.CODE, ResponseInfo.CODE_200);
				jObject.put(MqttConfigConstant.MSG, ResponseInfo.SUCCESS);
				jObject.put(MqttConfigConstant.ACTION, TerminalTopic.SERVER_INFO);
				jObject.put(MqttConfigConstant.MESSAGE_ID, "");
				jObject.put(MqttConfigConstant.JSON_DATA_STR, jsonObject);
				String message = jObject.toString();
				new PushMessageToIpTerminalThread(busiTerminal.getIp(), message).start();
			}

			@Override
			public void pushGetSipAccount(long terminalId) {
				BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("displayName", busiTerminal.getName());
				jsonObject.put("type", busiTerminal.getType());
				jsonObject.put("fcmServerUrl", ExternalConfigCache.getInstance().getFmcRootUrl());
				List<Live> liveList = new ArrayList<>();
				ITerminalActionService iTerminalActionService = BeanFactory.getBean(ITerminalActionService.class);
				List<BusiLiveSetting> busiLiveSettingByDeptList = iTerminalActionService.getBusiLiveSettingByDeptId(busiTerminal.getDeptId());

				if (busiLiveSettingByDeptList != null && busiLiveSettingByDeptList.size() > 0) {
					for (BusiLiveSetting liveSetting : busiLiveSettingByDeptList) {
						if (liveSetting.getStatus() == 1) {
							Live live = new Live();
							live.setName(liveSetting.getName());
							live.setUrl(liveSetting.getUrl());
							liveList.add(live);
						}
					}
				}
				jsonObject.put("liveUrl", liveList);
				jsonObject.put("deptName", SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName());
				jsonObject.put("deptId", busiTerminal.getDeptId());
				JSONObject jObject = new JSONObject();
				jObject.put(MqttConfigConstant.CODE, ResponseInfo.CODE_200);
				jObject.put(MqttConfigConstant.MSG, ResponseInfo.SUCCESS);
				jObject.put(MqttConfigConstant.ACTION, TerminalTopic.GET_SIP_ACCOUNT);
				jObject.put(MqttConfigConstant.MESSAGE_ID, "");
				jObject.put(MqttConfigConstant.JSON_DATA_STR, jsonObject);
				String message = jObject.toString();
				new PushMessageToIpTerminalThread(busiTerminal.getIp(), message).start();
			}

			@Override
			public void pushAddrBook(long terminalId) {
				BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
				SysDept sysDept = SysDeptCache.getInstance().get(busiTerminal.getDeptId());
				ModelBean sysDeptMb = new ModelBean();
				sysDeptMb.put("id", sysDept.getDeptId());
				sysDeptMb.put("label", sysDept.getDeptName());

				Integer page = 0;
				Integer size = 100;
				PaginationData<BusiTerminal> pd = new PaginationData<>();
				List<BusiTerminal> terminals = new ArrayList<BusiTerminal>();
				Map<Long, BusiTerminal> deptMap = TerminalCache.getInstance().getByDept(sysDept.getDeptId());
				if (null != deptMap && !deptMap.isEmpty()) {
					deptMap.forEach((key, value) -> {
//						if (TerminalType.isOnlyIP(value.getType())) {
							terminals.add(value);
//						}
					});

					int fromIndex = page * size;
					int toIndex = fromIndex + size;
					if (toIndex >= terminals.size()) {
						toIndex = terminals.size();
					}

					if (fromIndex >= toIndex) {
						pd.setRecords(new ArrayList<BusiTerminal>());
					} else {
						pd.setRecords(terminals.subList(fromIndex, toIndex));
					}

					pd.setTotal(terminals.size());
					pd.setPage(page);
					pd.setSize(size);
;
					sysDeptMb.put("terminalPage", pd);
				}
				JSONObject jsonObject = (JSONObject) JSON.toJSON(sysDeptMb);
				JSONObject jObject = new JSONObject();
				jObject.put(MqttConfigConstant.CODE, ResponseInfo.CODE_200);
				jObject.put(MqttConfigConstant.MSG, ResponseInfo.SUCCESS);
				jObject.put(MqttConfigConstant.ACTION, TerminalTopic.ADDRESS_BOOK);
				jObject.put(MqttConfigConstant.MESSAGE_ID, "");
				jObject.put(MqttConfigConstant.JSON_DATA_STR, jsonObject);
				String message = jObject.toString();
				new PushMessageToIpTerminalThread(busiTerminal.getIp(), message).start();
			}

			@Override
			public void pushRecordList(long terminalId) {
				BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
				Integer recordsType = 0;
				Integer page = 1;
				Integer size = 100;
				Integer startIndex = 0;
				Integer endIndex = startIndex + size;
				List<Map> list = new ArrayList<>();
				BusiRecordSettingMapper busiRecordSettingMapper = BeanFactory.getBean(BusiRecordSettingMapper.class);
				BusiRecordsMapper busiRecordsMapper = BeanFactory.getBean(BusiRecordsMapper.class);
				BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
				JSONObject jsonObject = new JSONObject();
				BusiRecordSetting busiRecordSetting = new BusiRecordSetting();
				busiRecordSetting.setDeptId(busiTerminal.getDeptId());
				List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(busiRecordSetting);
				LOGGER.info("busiRecordSettings:" + busiRecordSettings);
				if (!(busiRecordSettings == null || busiRecordSettings.isEmpty())) {

					try {
						Long deptId = (busiTerminal.getDeptId() == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiTerminal.getDeptId();
						RecordsSearchVo recordsSearchVo = new RecordsSearchVo();
						if (recordsSearchVo.getPageNum() == null || recordsSearchVo.getPageNum() <= 0) {
							recordsSearchVo.setPageNum(1);
						}
						if (recordsSearchVo.getPageSize() == null || recordsSearchVo.getPageSize() > 100) {
							recordsSearchVo.setPageSize(100);
						}
						List<BusiRecordsSearchResult> busiRecordsSearchResultList;
						if (recordsType == 2) {
							busiRecordsSearchResultList = busiRecordsMapper.selectBusiRecordsListForGroupForMyJoinedConference(deptId, recordsSearchVo.getSearchKey(), busiTerminal.getId());
						} else if (recordsType == 1) {
							BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminal.getId());
							if (busiUserTerminal != null) {
								busiRecordsSearchResultList = busiRecordsMapper.selectBusiRecordsListForGroupForMyConference(deptId, recordsSearchVo.getSearchKey(), busiUserTerminal.getUserId());
							} else {
								busiRecordsSearchResultList = new ArrayList<>();
							}
						} else {
							busiRecordsSearchResultList = busiRecordsMapper.selectBusiRecordsListForGroup(deptId, recordsSearchVo.getSearchKey());
						}
						if (busiRecordsSearchResultList != null && busiRecordsSearchResultList.size() > 0) {
							for (BusiRecordsSearchResult busiRecordsSearchResult : busiRecordsSearchResultList) {

								Map<String, Object> stringObjectMap = new HashMap<>();
								String coSpaceId = busiRecordsSearchResult.getCoSpaceId();
								stringObjectMap.put("coSpaceId", coSpaceId);
								if (StringUtils.isNotEmpty(coSpaceId) && (coSpaceId.endsWith("-zj") || coSpaceId.endsWith("-plc") || coSpaceId.endsWith("-kdc"))) {
									stringObjectMap.put("conferenceNumber", coSpaceId.substring(0, coSpaceId.indexOf("-")));
								} else {
									stringObjectMap.put("conferenceNumber", busiRecordsSearchResult.getConferenceNumber().toString());
								}
								stringObjectMap.put("deptId", busiRecordsSearchResult.getDeptId());
								stringObjectMap.put("fileSize", 0);
								stringObjectMap.put("recordFileNum", busiRecordsSearchResult.getRecordFileNum());
								stringObjectMap.put("recordingTimeOfLate", busiRecordsSearchResult.getRecordingTimeOfLate());
								stringObjectMap.put("conferenceName", busiRecordsSearchResult.getName());

								// recordInfo
								List<Map<String, Object>> folders = new ArrayList<>();
								Set<String> conferenceNumberSet = new HashSet<>();

								List<BusiRecords> busiRecordsList;
								if (recordsType == 2) {
									busiRecordsList = busiRecordsMapper.selectBusiRecordsByCoSpaceIdForMyJoinedConference(deptId, coSpaceId, Boolean.FALSE, busiTerminal.getId());
								} else if (recordsType == 1) {
									BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminal.getId());
									if (busiUserTerminal != null) {
										busiRecordsList = busiRecordsMapper.selectBusiRecordsByCoSpaceIdForMyConference(deptId, coSpaceId, Boolean.FALSE, busiUserTerminal.getUserId());
									} else {
										busiRecordsList = new ArrayList<>();
									}
								} else {
									busiRecordsList = busiRecordsMapper.selectBusiRecordsByCoSpaceId(deptId, coSpaceId, Boolean.FALSE);
								}
								for (int i = 0; i < busiRecordsList.size(); i++) {
									BusiRecords busiRecords = busiRecordsList.get(i);
									if (StringUtils.isNotEmpty(busiRecords.getRealName())) {
										Map<String, Object> map = new HashMap<>();
										String coSpaceIdTemp = busiRecords.getCoSpaceId();
										String comUrl = (busiRecordSettings != null && busiRecordSettings.size() > 0) ? busiRecordSettings.get(0).getUrl() : "";
										if (comUrl.lastIndexOf("/") < comUrl.length() - 1) {
											comUrl += "/" + coSpaceIdTemp;
										} else {
											comUrl += coSpaceIdTemp;
										}
										String conferenceNumber;
										if (StringUtils.isNotEmpty(coSpaceId) && (coSpaceId.endsWith("-zj") || coSpaceId.endsWith("-plc") || coSpaceId.endsWith("-kdc"))) {
											conferenceNumber = coSpaceId.substring(0, coSpaceId.indexOf("-"));
										} else {
											conferenceNumber = busiRecords.getConferenceNumber().toString();
										}
										conferenceNumberSet.add(conferenceNumber);
										String url = comUrl + "/" + busiRecords.getRealName();
										map.put("fileName", busiRecords.getFileName());
										map.put("realName", busiRecords.getRealName());
										map.put("recordingTime", busiRecords.getCreateTime());
										map.put("fileSize", busiRecords.getFileSize());
										map.put("url", url);
										map.put("id", busiRecords.getId());
										map.put("coSpaceId", coSpaceIdTemp);
										map.put("deptId", deptId);
										folders.add(map);
									}
								}

								stringObjectMap.put("recordInfoList", folders);
								list.add(stringObjectMap);
							}
						}
					} catch (Exception e) {
						LOGGER.error("录制列表异常===", e);
					}
					jsonObject.put("total", list.size());
					jsonObject.put("page", page);
					jsonObject.put("size", size);

					List<Map> arrayList = new ArrayList<>();
					if (startIndex < list.size() && endIndex >= list.size()) {
						endIndex = list.size();
						arrayList = list.subList(startIndex, endIndex);
					} else if (startIndex < list.size() && endIndex < list.size()) {
						arrayList = list.subList(startIndex, endIndex);
					}
					jsonObject.put("data", arrayList);
					JSONObject jObject = new JSONObject();
					jObject.put(MqttConfigConstant.CODE, ResponseInfo.CODE_200);
					jObject.put(MqttConfigConstant.MSG, ResponseInfo.SUCCESS);
					jObject.put(MqttConfigConstant.ACTION, TerminalTopic.RECORDING_LIST);
					jObject.put(MqttConfigConstant.MESSAGE_ID, "");
					jObject.put(MqttConfigConstant.JSON_DATA_STR, jsonObject);
					String message = jObject.toString();
					new PushMessageToIpTerminalThread(busiTerminal.getIp(), message).start();
				}
			}

			@Override
			public void pushInfoDisplay(long terminalId) {
				boolean isPush = false;
				BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
				JSONObject jsonObject = new JSONObject();
				BusiInfoDisplay busiInfoDisplayTemp = new BusiInfoDisplay();
				Long terminalDeptId = busiTerminal.getDeptId();
				SysDept sysDept = SysDeptCache.getInstance().get(terminalDeptId);
				String ancestors = sysDept.getAncestors();

				List<BusiInfoDisplay> busiInfoDisplayList = InfoDisplayCache.getInstance().getByDeptId(terminalDeptId);
				if (busiInfoDisplayList != null && busiInfoDisplayList.size() > 0) {
					for (BusiInfoDisplay busiInfoDisplay : busiInfoDisplayList) {
						if (busiInfoDisplay.getStatus() == 1 && busiInfoDisplay.getType() == 1 && busiInfoDisplay.getPushType() == 1) {
							busiInfoDisplayTemp = busiInfoDisplay;
							isPush = true;
						}
					}
				} else {
					if (StringUtils.isNotEmpty(ancestors)) {
						String[] split = ancestors.split(",");
						for (int i = split.length - 1; i >= 0; i--) {
							String deptId = split[i];
							List<BusiInfoDisplay> busiInfoDisplayListTemp = InfoDisplayCache.getInstance().getByDeptId(Long.valueOf(deptId));
							if (busiInfoDisplayListTemp != null && busiInfoDisplayListTemp.size() > 0) {
								for (BusiInfoDisplay busiInfoDisplay : busiInfoDisplayListTemp) {
									if (busiInfoDisplay.getStatus() == 1 && busiInfoDisplay.getType() == 1 && busiInfoDisplay.getPushType() == 1) {
										busiInfoDisplayTemp = busiInfoDisplay;
										isPush = true;
									}
								}
							}
						}
					}
				}
				if (!isPush) {
					BusiInfoDisplay busiInfoDisplay = new BusiInfoDisplay();
					busiInfoDisplay.setStatus(1);
					busiInfoDisplay.setPushType(1);
					busiInfoDisplay.setType(1);
					List<BusiInfoDisplay> busiInfoDisplays = busiInfoDisplayMapper.selectBusiInfoDisplayList(busiInfoDisplay);
					if (busiInfoDisplays != null && busiInfoDisplays.size() > 0) {
						for (BusiInfoDisplay infoDisplay : busiInfoDisplays) {
							String pushTerminalIds = infoDisplay.getPushTerminalIds();
							if (StringUtils.isNotEmpty(pushTerminalIds)) {
								String[] split = pushTerminalIds.split(",");
								for (String idStr : split) {
									Long id = Long.valueOf(idStr);
									if (id.longValue() == busiTerminal.getId().longValue()) {
										busiInfoDisplayTemp = busiInfoDisplays.get(0);
										isPush = true;
									}
								}
							}
						}
					}
				}

				if (isPush) {
					String urlTemp = ExternalConfigCache.getInstance().getFmcRootUrl();
					String introduce = busiInfoDisplayTemp.getUrlData();
					if (com.paradisecloud.common.utils.StringUtils.isNotEmpty(introduce)) {
						introduce = introduce.replace("{url}", urlTemp);
						jsonObject.put("urlData", introduce);
					}

					jsonObject.put("type", busiInfoDisplayTemp.getType());
					jsonObject.put("displayType", busiInfoDisplayTemp.getDisplayType());
				} else {

					jsonObject.put("type", 0);
				}
				JSONObject jObject = new JSONObject();
				jObject.put(com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant.CODE, com.paradisecloud.fcm.mqtt.constant.ResponseInfo.CODE_200);
				jObject.put(com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant.MSG, com.paradisecloud.fcm.mqtt.constant.ResponseInfo.SUCCESS);
				jObject.put(com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant.ACTION, com.paradisecloud.fcm.mqtt.constant.TerminalTopic.INFO_DISPLAY);
				jObject.put(com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant.MESSAGE_ID, "");
				jObject.put(com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant.JSON_DATA_STR, jsonObject);
				String message = jObject.toString();
				new PushMessageToIpTerminalThread(busiTerminal.getIp(), message).start();
			}
		});
	}
	
}
