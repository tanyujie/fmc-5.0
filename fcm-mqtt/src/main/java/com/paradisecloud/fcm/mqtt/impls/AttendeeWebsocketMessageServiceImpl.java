package com.paradisecloud.fcm.mqtt.impls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.dao.mapper.BusiRecordSettingMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiRecordSetting;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeWebsocketMessageService;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeDataCache;
import com.paradisecloud.fcm.fme.conference.impls.BusiRecordsServiceImpl;
import com.paradisecloud.fcm.fme.conference.task.UpdateRecordsTask;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.message.AttendeeStatusMessage;
import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.TerminalTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttendeeWebsocketMessageServiceImpl implements IAttendeeWebsocketMessageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MqttServiceImpl.class);

	@Resource
	private BusiTerminalMapper busiTerminalMapper;

	@Resource
	private BusiRecordSettingMapper busiRecordSettingMapper;

	@Resource
	private TaskService taskService;

	@Override
	public void subscribe(AttendeeStatusMessage wsMsg) {
//		String action = TerminalTopic.ATTENDEE_CONFERENCE_INFO;
		Attendee attendee = wsMsg.getAttendee();
//		List<BusiTerminal> terminals = new ArrayList<BusiTerminal>();
		LOGGER.info("===================> AttendeeWebsocketMessageServiceImpl" + attendee.toString());
		if (null != attendee) {
			ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(attendee.getContextKey());

			if (attendee.getName().equals("录播服务") || conferenceContext.isRecorded()) {
				updateBusiRecords(true, conferenceContext.getConferenceNumber(), conferenceContext);
			}
//			if (null != conferenceContext) {
//				Attendee masterAttendee = conferenceContext.getMasterAttendee();
//				if (null != masterAttendee) {
//
//					//获取当前状态改变的参会人
//					List<BusiTerminal> attendeeTerminals = this.getAttendeeTerminal(attendee);
//
//					//获取主持人的终端信息
//					List<BusiTerminal> hostTerminals = this.getAttendeeTerminal(masterAttendee);
//
//					if (null != hostTerminals && hostTerminals.size() > 0) {
//						if (attendeeTerminals != null && attendeeTerminals.size() > 0) {
//							terminals.add(attendeeTerminals.get(0));
//							terminals.add(hostTerminals.get(0));
//							if (null != terminals && terminals.size() > 0) {
//								for (BusiTerminal busiTerminal : terminals) {
//									if (busiTerminal.getSn() != null) {
//										String clientId = busiTerminal.getSn();
//										String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
//										JSONObject jsonO = (JSONObject) JSON.toJSON(attendee);
//										jsonO.put("hostClientId", hostTerminals.get(0).getSn());
//
//										if (null != attendeeTerminals && attendeeTerminals.size() > 0) {
//											jsonO.put(MqttConfigConstant.CLIENTID, busiTerminal.getSn());
//										}
//
//										ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonO, clientId, "");
//									}
//								}
//							}
//						}
//					}
//				}
//			}
		}
	}

	private List<BusiTerminal> getAttendeeTerminal(Attendee attendee) {
		String remoteParty = attendee.getRemoteParty();
		BusiTerminal busiTerminal = new BusiTerminal();
		if (remoteParty.contains("@")) {
			String[] remoteTerminal = remoteParty.split("@");
			if (remoteTerminal.length > 1) {
				busiTerminal.setCredential(remoteTerminal[0]);
			}
		} else {
			busiTerminal.setIntranetIp(remoteParty);
		}
		List<BusiTerminal> busiTerminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
		return busiTerminalList;
	}

	public int updateBusiRecords(boolean recording, String conferenceNumber, ConferenceContext conferenceContext) {
		int num = 0;
		try {
			Long deptId = conferenceContext.getDeptId();
			String coSpaceId = FmeDataCache.getCoSpaceByConferenceNumber(conferenceContext.getDeptId(), conferenceNumber).getId();
			BusiRecordSetting busiRecordSetting = new BusiRecordSetting();
			busiRecordSetting.setDeptId(conferenceContext.getDeptId());
			busiRecordSetting.setStatus(1);
			List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(busiRecordSetting);
			if (busiRecordSettings != null && busiRecordSettings.size() > 0) {
				if (recording) {
					BusiRecordsServiceImpl busiRecordsService = new BusiRecordsServiceImpl();
					int spaceStatus = busiRecordsService.getRecordSpaceStatus();
					if (spaceStatus == 0) {
						Assert.isTrue(false, "不能开启录制，未设置录制空间容量！");
					} else if (spaceStatus == 2) {
						Assert.isTrue(false, "不能开启录制，磁盘空间不可用！");
					}
				}
			}

			if (!recording) {
				UpdateRecordsTask updateRecordsTask = new UpdateRecordsTask(coSpaceId, 20000, deptId, coSpaceId, conferenceNumber, conferenceContext.getName());
				taskService.addTask(updateRecordsTask);
			}
		} catch (Exception e) {
			LOGGER.info(String.valueOf(e));
		}
		return num;
	}

}
