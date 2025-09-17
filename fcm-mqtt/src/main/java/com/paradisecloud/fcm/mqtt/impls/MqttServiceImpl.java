package com.paradisecloud.fcm.mqtt.impls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.AttendType;
import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.McuTypeVo;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.task.McuKdcDelayTaskService;
import com.paradisecloud.fcm.mcu.kdc.task.McuKdcSendConferenceInfoToTerminalTask;
import com.paradisecloud.fcm.mcu.kdc.task.McuKdcSendJoinConferenceToTerminalTask;
import com.paradisecloud.fcm.mcu.kdc.task.McuKdcSendLeftConferenceToTerminalTask;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.task.McuPlcDelayTaskService;
import com.paradisecloud.fcm.mcu.plc.task.McuPlcSendConferenceInfoToTerminalTask;
import com.paradisecloud.fcm.mcu.plc.task.McuPlcSendJoinConferenceToTerminalTask;
import com.paradisecloud.fcm.mcu.plc.task.McuPlcSendLeftConferenceToTerminalTask;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.task.DelayTaskService;
import com.paradisecloud.fcm.mcu.zj.task.McuZjSendConferenceInfoToTerminalTask;
import com.paradisecloud.fcm.mcu.zj.task.McuZjSendJoinConferenceToTerminalTask;
import com.paradisecloud.fcm.mcu.zj.task.McuZjSendLeftConferenceToTerminalTask;
import com.paradisecloud.fcm.mqtt.common.PublisMessage;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiAllMcuService;
import com.paradisecloud.fcm.ops.cloud.cache.OpsCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDoorplateCache;
import com.paradisecloud.fcm.smartroom.model.MeetingRoomInfo;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomBookService;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeviceMapService;
import com.paradisecloud.fcm.mqtt.constant.DeviceAction;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.attendee.task.SendJoinConferenceToTerminalTask;
import com.paradisecloud.fcm.fme.attendee.task.SendLeftConferenceToTerminalTask;
import com.paradisecloud.fcm.fme.conference.task.SendConferenceInfoToTerminalTask;
import com.paradisecloud.fcm.mqtt.cache.AppointmentCache;
import com.paradisecloud.fcm.mqtt.cache.TerminalLiveCache;
import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.ResponseInfo;
import com.paradisecloud.fcm.mqtt.constant.TerminalTopic;
import com.paradisecloud.fcm.mqtt.model.TerminalLive;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomParticipantService;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.service.ISysUserService;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class MqttServiceImpl implements IMqttService {

	@Resource
	private BusiTemplateConferenceMapper busiTemplateConferenceMapper;
	@Resource
	private BusiUserTerminalMapper busiUserTerminalMapper;
	@Resource
	private TaskService taskService;
	@Resource
	private DelayTaskService delayTaskService;
	@Resource
	private McuPlcDelayTaskService mcuPlcDelayTaskService;
	@Resource
	private McuKdcDelayTaskService mcuKdcDelayTaskService;
	@Resource
	private IBusiSmartRoomBookService busiSmartRoomBookService;
	@Resource
	private IBusiSmartRoomDeviceMapService busiSmartRoomDeviceMapService;
	@Resource
	private ISysUserService sysUserService;
	@Resource
	private IBusiSmartRoomParticipantService busiSmartRoomParticipantService;
	@Resource
	private IBusiAllMcuService busiAllMcuService;
	@Resource
	private ViewConferenceAppointmentMapper viewConferenceAppointmentMapper;
	@Resource
	private ViewTemplateConferenceMapper viewTemplateConferenceMapper;

	private static final Logger LOGGER = LoggerFactory.getLogger(MqttServiceImpl.class);

	@Override
	public void pushConferenceInfo(String conferenceId, String confencePassword, List<? extends BaseAttendee> meetingJoinTerminals, List<? extends BaseAttendee> liveWatchTerminals) {

		//获取会议缓存
		String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
		BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
		if(null != conferenceContext) {
			List<BaseAttendee> liveTerminals = conferenceContext.getLiveTerminals();
			List<TerminalLive> terminalLiveList = new ArrayList<>();
			if (liveTerminals != null && liveTerminals.size() > 0) {
				for (int i = 0; i < liveTerminals.size(); i++) {
					if (liveTerminals.get(i).getSn() != null) {
						TerminalLive terminalLive = new TerminalLive();
						terminalLive.setMac(liveTerminals.get(i).getSn());
						terminalLive.setId(liveTerminals.get(i).getTerminalId());
						terminalLiveList.add(terminalLive);
						//TerminalActionServiceImpl.terminalCache().put(conferenceContext.getTemplateConferenceId(),terminalLiveList);
					}
				}
				TerminalLiveCache.getInstance().add(conferenceContext.getContextKey(), terminalLiveList);
			}
			//TerminalActionServiceImpl.terminalCache().remove(conferenceNumber);

			//邀请参会人
			if (null != meetingJoinTerminals && meetingJoinTerminals.size() > 0) {
				//邀请参会人分发会议消息
				this.invitePushConferenceInfo(meetingJoinTerminals, conferenceContext, conferenceContext.getConferenceNumber(), confencePassword);
			}

			//观看直播
			if (conferenceContext.getStreamingUrl() != null) {
				this.watchLive(liveWatchTerminals, conferenceContext, conferenceContext.getConferenceNumber());
			}
		}

	}

	private void watchLive(List<? extends BaseAttendee> liveWatchTerminals, BaseConferenceContext conferenceContext, String conferenceNumber) {

		String actionW = TerminalTopic.PUSH_LIVE;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		BaseAttendee terminalAttendee = new BaseAttendee();
		String conferenceEtime = null;
		Date conferenceSdate = conferenceContext.getStartTime();
		Date conferenceEdate = conferenceContext.getEndTime();
		String conferenceStime = formatter.format(conferenceSdate);
		JSONObject object = new JSONObject();
		JSONObject object1 = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		Integer isAutoCreateStreamUrl = conferenceContext.getIsAutoCreateStreamUrl();
		String streamingUrl = conferenceContext.getStreamingUrl();
		List<String> stringList = new ArrayList<>();
		if (isAutoCreateStreamUrl == 1) {
//			try {
//				BusiLiveDept busiLiveDept = LiveDeptCache.getInstance().get(conferenceContext.getDeptId());
//				List<BusiLive> streamUrlList = getStreamUrlList(conferenceContext.getDeptId(), conferenceContext.getConferenceNumber(), busiLiveDept);
//				if (streamUrlList != null && streamUrlList.size() > 0) {
//					for (BusiLive busiLive : streamUrlList) {
//						if (busiLive.getDomainName() != null && busiLive.getDomainName().length() > 0) {
//							busiLive.setIp(busiLive.getDomainName());
//						}
//						String url = busiLive.getProtocolType() + "://" + busiLive.getIp() + "/" + busiLive.getUriPath() + "/" + conferenceContext.getConferenceNumber();
//						stringList.add(url);
//					}
//				}
//			} catch (Exception e) {
//			}
			stringList = conferenceContext.getStreamUrlList();
		} else {
			if (StringUtils.isNotEmpty(conferenceContext.getStreamingUrl())) {
				stringList.add(conferenceContext.getStreamingUrl());
			}
		}
		/* 去除参会者列表：数据超长导致mqtt断开
		Map<Long, BaseAttendee> terminalAttendeeMap = conferenceContext.getTerminalAttendeeMap();
		terminalAttendeeMap.forEach((k, v) -> {
			JSONObject objs = new JSONObject();
			BaseAttendee attendee = v;
			String attendeeId = v.getId();
			String remoteP = attendee.getRemoteParty();
			objs.put("name", attendee.getName());
			objs.put("meetingStatus", attendee.getMeetingStatus());
			objs.put("onlineStatus", attendee.getOnlineStatus());
			objs.put("attendeeId", attendeeId);
			objs.put("terminalId", k);
			if(null != attendee) {
				String remoteParty = attendee.getRemoteParty();
				if(remoteP.equals(remoteParty)) {
					objs.put("host", true);
				}else {
					objs.put("host", false);
				}
			}

			jsonArray.add(objs);
		});
		*/
		for (int i = 0; i < liveWatchTerminals.size(); i++) {
			Long terminalId = liveWatchTerminals.get(i).getTerminalId();
			BusiTerminal terminal = TerminalCache.getInstance().get(terminalId);
			if(null != terminal) {
				if (terminal.getMqttOnlineStatus() != TerminalOnlineStatus.ONLINE.getValue()) {
					// 不在线的终端不推送消息
					continue;
				}
				String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + terminal.getSn();
				int nextInt = 0;
				if (stringList.size() > 0) {
					nextInt = new Random().nextInt(stringList.size());
					streamingUrl = stringList.get(nextInt);
				}
				object.put("liveUrl", streamingUrl);
				object.put("liveUrlList", stringList);
				object.put("status", 1);
				object.put("conferenceRemark", conferenceContext.getRemarks());
				object.put("conferenceName", conferenceContext.getName());
				object.put(MqttConfigConstant.CONFERENCENUM, conferenceNumber);
				object.put("conferenceStime", conferenceStime);
				object.put("conferenceEtime", conferenceEtime);
				object.put("attendeeTerminal", jsonArray);
				object.put("joinConference", AttendType.LIVE.getValue());

				//object.put("attendeeId",conferenceContext.getAttendees());
				ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, actionW, object, terminal.getSn(), "");
			}
		}
	}

	@Override
	public List<BusiLive> getStreamUrlList(long deptId, BusiLiveDept busiLiveDept) {
		List<BusiLive> streamUrlList = new ArrayList<>();
		if (busiLiveDept.getLiveType() == 100) {
			BusiLiveClusterMap busiLiveClusterMap = new BusiLiveClusterMap();
			busiLiveClusterMap.setClusterId(busiLiveDept.getLiveId());
			List<BusiLiveClusterMap> busiLiveClusterMaps = BeanFactory.getBean(BusiLiveClusterMapMapper.class).selectBusiLiveClusterMapList(busiLiveClusterMap);
			if (busiLiveClusterMaps != null && busiLiveClusterMaps.size() > 0) {
				for (BusiLiveClusterMap liveClusterMap : busiLiveClusterMaps) {
					BusiLive busiLive = BeanFactory.getBean(BusiLiveMapper.class).selectBusiLiveById(liveClusterMap.getLiveId());
					streamUrlList.add(busiLive);
				}
			}
		} else {
			BusiLive busiLive = BeanFactory.getBean(BusiLiveMapper.class).selectBusiLiveById(busiLiveDept.getLiveId());
			if (busiLive != null) {
				streamUrlList.add(busiLive);
			}
		}
		return streamUrlList;
	}

	private void invitePushConferenceInfo(List<? extends BaseAttendee> meetingJoinTerminals,BaseConferenceContext conferenceContext, String conferenceNumber, String confencePassword) {
		String actionI = TerminalTopic.INVITE_CONFERENCE;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		for (int i = 0; i < meetingJoinTerminals.size(); i++) {
			JSONObject object = new JSONObject();
			JSONArray jsonArray = new JSONArray();

			BaseAttendee terminalAttendee = meetingJoinTerminals.get(i);
			Long terminalId = terminalAttendee.getTerminalId();
			int attendType = terminalAttendee.getAttendType();
			BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
			if(null != busiTerminal) {
				if (busiTerminal.getMqttOnlineStatus() != TerminalOnlineStatus.ONLINE.getValue()) {
					// 不在线的终端不推送消息
					continue;
				}
				String conferenceEtime = null;
				String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminal.getSn();
				String conferenceName = conferenceContext.getName();
				String remarks = conferenceContext.getRemarks();
				Date conferenceSdate = conferenceContext.getStartTime();
				Date conferenceEdate = conferenceContext.getEndTime();
				String conferenceStime = formatter.format(conferenceSdate);
				if (null != conferenceEdate) {
					conferenceEtime = formatter.format(conferenceEdate);
				}

				/* 去除参会者列表：数据超长导致mqtt断开
				Map<Long, BaseAttendee> terminalAttendeeMap = conferenceContext.getTerminalAttendeeMap();
				terminalAttendeeMap.forEach((k, v) -> {
					JSONObject objs = new JSONObject();
					BaseAttendee attendee = v;
					String attendeeId = v.getId();
					String remoteP = attendee.getRemoteParty();
					objs.put("name", attendee.getName());
					objs.put("meetingStatus", attendee.getMeetingStatus());
					objs.put("onlineStatus", attendee.getOnlineStatus());
					objs.put("attendeeId", attendeeId);
					objs.put("terminalId", k);
					if (null != attendee) {
						String remoteParty = attendee.getRemoteParty();
						if (remoteP.equals(remoteParty)) {
							objs.put("host", true);
						} else {
							objs.put("host", false);
						}
					}

					jsonArray.add(objs);
				});
				*/

				object.put(MqttConfigConstant.CONFERENCENUM, conferenceNumber);
				object.put(MqttConfigConstant.PASSWORD, confencePassword);
				List<String> stringList = new ArrayList<>();
				if (conferenceContext.getIsAutoCreateStreamUrl() == 1) {
					stringList = conferenceContext.getStreamUrlList();
				} else {
					if (StringUtils.isNotEmpty(conferenceContext.getStreamingUrl())) {
						stringList.add(conferenceContext.getStreamingUrl());
					}
				}
				int nextInt = 0;
				String streamingUrl = conferenceContext.getStreamingUrl();
				if (stringList.size() > 0) {
					nextInt = new Random().nextInt(stringList.size());
					streamingUrl = stringList.get(nextInt);
				}
				object.put("liveUrl", streamingUrl);
				object.put("liveUrlList", stringList);
				object.put("conferencePassword", confencePassword);
				object.put("conferenceRemark", remarks);
				object.put("conferenceName", conferenceName);
				object.put("conferenceStime", conferenceStime);
				object.put("conferenceEtime", conferenceEtime);
				object.put("joinConference", attendType);
				object.put("attendeeTerminal", jsonArray);

				ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, actionI, object, busiTerminal.getSn(), "");
			}
		}
	}

	private BaseConferenceContext getConferenceContext(String conferenceId) {
		String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
		BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
		return conferenceContext;
	}

	@Override
	public void acceptRaiseHand(String conferenceNumber, BaseAttendee ta) {
		String action = TerminalTopic.INTERACTIVE_RAISE_HAND;
		String clientId = TerminalCache.getInstance().get(ta.getTerminalId()).getSn();
		String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;

		JSONObject jsonObject = new JSONObject();
		jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNumber);
		jsonObject.put(MqttConfigConstant.CLIENTID, clientId);
		jsonObject.put("isAgree", true);
		ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, clientId, "");
	}

	@Override
	public void rejectRaiseHand(String conferenceNumber, BaseAttendee ta) {
		String action = TerminalTopic.INTERACTIVE_RAISE_HAND;
		LOGGER.info("===================================> rejectRaiseHand0000" + conferenceNumber);
		LOGGER.info("===================================> rejectRaiseHand1111" + ta.toString());
		LOGGER.info("===================================> rejectRaiseHand2222" + ta.getTerminalId());
		String clientId = TerminalCache.getInstance().get(ta.getTerminalId()).getSn();
		String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;

		JSONObject jsonObject = new JSONObject();
		jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNumber);
		jsonObject.put(MqttConfigConstant.CLIENTID, clientId);
		jsonObject.put("isAgree", false);
		ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, clientId, "");
	}


	@Override
	public void endConference(String conferenceNumber, List meetingJoinTerminals,List liveWatchTerminals,BaseConferenceContext conferenceContext) {
		LOGGER.info("=====================>推动结束会议&&&" , meetingJoinTerminals.size());
		if(null != meetingJoinTerminals && meetingJoinTerminals.size() > 0) {
			String action = TerminalTopic.END_CONFERENCE;
			LOGGER.info("=====================>推动结束会议$$$$" , JSON.toJSON(meetingJoinTerminals));
			for (int i = 0; i < meetingJoinTerminals.size(); i++) {
				BaseAttendee terminalAttendee = (BaseAttendee)meetingJoinTerminals.get(i);
				Long terminalId = terminalAttendee.getTerminalId();
				BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
				if (StringUtils.isNotEmpty(busiTerminal.getSn())) {
					String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminal.getSn();

					JSONObject jsonObject = new JSONObject();
					jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceNumber);
					jsonObject.put("endConference", true);
					ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, busiTerminal.getSn(), "");
				}
			}
		}
		//推送直播结束会议
		if (liveWatchTerminals != null &&  liveWatchTerminals.size() > 0 ){
			String url = null;
			//this.isInviteLiveTerminal(conferenceNumber, URl, );
			//getConferenceContext(conferenceNumber).getTemplateConferenceId();
			//this.isInviteLiveTerminal( conferenceNumber,  url, getConferenceContext(conferenceNumber).getTemplateConferenceId());
			List<TerminalLive> terminalLiveList = TerminalLiveCache.getInstance().getById(conferenceContext.getContextKey());
			for (int i = 0; i < liveWatchTerminals.size(); i++) {
				terminalLiveList.get(i).setOutTime(null);
				terminalLiveList.get(i).setJoinTime(null);
				terminalLiveList.get(i).setStatus(0);
				BaseAttendee  baseAttendee = (BaseAttendee)liveWatchTerminals.get(i);
				if (StringUtils.isNotEmpty(baseAttendee.getSn())) {
					String terminalTopic = "terminal/" + baseAttendee.getSn();
					String action = "liveTerminal";
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("conferenceNum", conferenceNumber);
					jsonObject.put("liveUrl", url);
					jsonObject.put("status",0);
					ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, baseAttendee.getSn(), "");
					TerminalLiveCache.getInstance().removeById(conferenceContext.getContextKey());
					LOGGER.info("会议结束===终端" + baseAttendee.getSn() + "直播结束直播消息===发送成功");
				}
			}
		}
		// 云会议
		if (conferenceContext instanceof TencentConferenceContext) {
			TencentConferenceContext tencentConferenceContext = (TencentConferenceContext) conferenceContext;
			BusiMcuTencentConferenceAppointment conferenceAppointment = tencentConferenceContext.getConferenceAppointment();
			if (conferenceAppointment == null) {
				BusiMcuTencentConferenceAppointmentMapper busiMcuTencentConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuTencentConferenceAppointmentMapper.class);
				List<BusiMcuTencentConferenceAppointment> busiMcuTencentConferenceAppointmentList = busiMcuTencentConferenceAppointmentMapper.selectBusiMcuTencentConferenceAppointmentByTemplateId(conferenceContext.getTemplateConferenceId());
				if (busiMcuTencentConferenceAppointmentList.size() > 0) {
					conferenceAppointment = busiMcuTencentConferenceAppointmentList.get(0);
				}
			}
			if (conferenceAppointment != null) {
				if (conferenceAppointment.getOpsId() != null) {
					BusiOps busiOps = OpsCache.getInstance().get(conferenceAppointment.getOpsId());
					if (busiOps != null) {
						if (busiOps.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
							String terminalTopic = MqttConfigConstant.TOPIC_PREFIX_OPS + busiOps.getSn();
							String action = TerminalTopic.END_CONFERENCE;

							JSONObject jsonObject = new JSONObject();
							jsonObject.put(MqttConfigConstant.CONFERENCE_ID, tencentConferenceContext.getId());
							jsonObject.put("endConference", true);
							ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, busiOps.getSn(), "");
						}
					}
				}
			}
		} else if (conferenceContext instanceof HwcloudConferenceContext) {
			HwcloudConferenceContext hwcloudConferenceContext = (HwcloudConferenceContext) conferenceContext;
			BusiMcuHwcloudConferenceAppointment conferenceAppointment = hwcloudConferenceContext.getConferenceAppointment();
			if (conferenceAppointment == null) {
				BusiMcuHwcloudConferenceAppointmentMapper busiMcuHwcloudConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuHwcloudConferenceAppointmentMapper.class);
				List<BusiMcuHwcloudConferenceAppointment> busiMcuHwcloudConferenceAppointmentList = busiMcuHwcloudConferenceAppointmentMapper.selectBusiMcuHwcloudConferenceAppointmentByTemplateId(conferenceContext.getTemplateConferenceId());
				if (busiMcuHwcloudConferenceAppointmentList.size() > 0) {
					conferenceAppointment = busiMcuHwcloudConferenceAppointmentList.get(0);
				}
			}
			if (conferenceAppointment != null) {
				if (conferenceAppointment.getOpsId() != null) {
					BusiOps busiOps = OpsCache.getInstance().get(conferenceAppointment.getOpsId());
					if (busiOps != null) {
						if (busiOps.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
							String terminalTopic = MqttConfigConstant.TOPIC_PREFIX_OPS + busiOps.getSn();
							String action = TerminalTopic.END_CONFERENCE;

							JSONObject jsonObject = new JSONObject();
							jsonObject.put(MqttConfigConstant.CONFERENCE_ID, hwcloudConferenceContext.getId());
							jsonObject.put("endConference", true);
							ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, busiOps.getSn(), "");
						}
					}
				}
			}
		}
	}

	@Override
	public void setBanner(String cn, BaseAttendee ta, JSONObject params) {
		if(null != ta) {
			String sn = TerminalCache.getInstance().get(ta.getTerminalId()).getSn();
			if(StringUtils.isNotEmpty(sn)) {
				String action = TerminalTopic.SET_BANNER;
				String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + sn;

				params.put(MqttConfigConstant.CONFERENCENUM, cn);
				ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, params, sn, "");
			}
		}
	}

	@Override
	public void isInviteLiveTerminal(String contextKey, Boolean streaming , String url, Long templateConferenceId) {

		BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
		List<TerminalLive> terminalLives = TerminalLiveCache.getInstance().getById(conferenceContext.getContextKey());

		String streamingUrl = conferenceContext.getStreamingUrl();
		List<String> stringList = new ArrayList<>();

		if (terminalLives != null && terminalLives.size() > 0) {
			if (conferenceContext.getIsAutoCreateStreamUrl() == 1) {
				stringList = conferenceContext.getStreamUrlList();
			}

			boolean isTemp = !stringList.contains(url);
			if (isTemp && streaming) {
				conferenceContext.setStreamingUrl(url);
			}

			for (int i = 0; i < terminalLives.size(); i++) {
				if (StringUtils.isNotEmpty(terminalLives.get(i).getMac())) {
					String terminalTopic = "terminal/" + terminalLives.get(i).getMac();
					String action = "liveTerminal";
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("conferenceNum", conferenceContext.getConferenceNumber());
					jsonObject.put("conferenceName", conferenceContext.getName());
					if (streaming) {
						if (isTemp) {
							streamingUrl = url;
						} else {
							int nextInt = 0;
							if (stringList.size() > 0) {
								nextInt = new Random().nextInt(stringList.size());
								streamingUrl = stringList.get(nextInt);
							}
						}
					}
					jsonObject.put("liveUrl", streamingUrl);
					jsonObject.put("liveUrlList", stringList);
					if (streaming) {
						jsonObject.put("status", 1);
					} else {
						jsonObject.put("status", 0);
						terminalLives.get(i).setStatus(0);
						TerminalLiveCache.getInstance().update(conferenceContext.getContextKey(), terminalLives);
					}
					ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, terminalLives.get(i).getMac(), "");
					LOGGER.info("直播===终端" + terminalLives.get(i).getMac() + "直播消息===发送成功");
				}
			}
		}
	}

	@Override
	public void conferenceList(List snList) {

		if (snList != null && snList.size() > 0) {
			for (Object clientId : snList) {
				BusiTerminal busiTerminal = TerminalCache.getInstance().getBySn(clientId.toString());
				if (busiTerminal != null) {
					if (busiTerminal.getMqttOnlineStatus() != TerminalOnlineStatus.ONLINE.getValue()) {
						// 不在线的终端不推送消息
						continue;
					}
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(ResponseInfo.PAGE, 0);
					jsonObject.put(ResponseInfo.SIZE, 8);
					jsonObject.put(MqttConfigConstant.CLIENTID, clientId);

					Integer page = (Integer) jsonObject.getInteger(ResponseInfo.PAGE);
					Integer size = (Integer) jsonObject.getInteger(ResponseInfo.SIZE);
					String sn = (String) jsonObject.getString(MqttConfigConstant.CLIENTID);

					List<BusiTemplateConference> busiTemplateConferences = busiTemplateConferenceMapper.selectAllBusiTemplateConferenceList(new BusiTemplateConference());

					new TerminalActionServiceImpl().conferenceList(jsonObject, (String) clientId, "", busiTemplateConferences);
				}
			}
		}
	}

	@Override
	public BusiConferenceAppointment getAppointmentCache(String key) {
		return AppointmentCache.getInstance().get(key);
	}

	@Override
	public BusiConferenceAppointment putAppointmentCache(String key, BusiConferenceAppointment busiConferenceAppointment) {

		return AppointmentCache.getInstance().put( key,busiConferenceAppointment);
	}

	@Override
	public Boolean removeAppointmentCache(String key, BusiConferenceAppointment busiConferenceAppointment) {
		return AppointmentCache.getInstance().remove(key);
	}

	@Override
	public BusiConferenceAppointment updateAppointmentCache(String key, BusiConferenceAppointment busiConferenceAppointment) {
		return AppointmentCache.getInstance().update(key, busiConferenceAppointment);
	}

	/**
	 * 封装信息，给终端发送对应的响应信息
	 *
	 * @param terminalTopic
	 * @param action
	 * @param jObj
	 * @param clientId
	 * @param messageId
	 */
	@Override
	public void responseTerminal(String terminalTopic, String action, JSONObject jObj, String clientId, String messageId) {
		ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jObj, clientId, messageId);
	}

	/**
	 * 推送会议信息给目标终端
	 * @param conferenceContext
	 */
	@Override
	public void sendConferenceInfoToPushTargetTerminal(BaseConferenceContext conferenceContext) {
		sendConferenceInfoToPushTargetTerminal(conferenceContext, null);
	}

	/**
	 * 推送会议信息给目标终端
	 * @param conferenceContext
	 * @param newPresenter
	 */
	@Override
	public void sendConferenceInfoToPushTargetTerminal(BaseConferenceContext conferenceContext, Long newPresenter) {
		if (conferenceContext instanceof ConferenceContext) {
			SendConferenceInfoToTerminalTask sendConferenceInfoToTerminalTask = new SendConferenceInfoToTerminalTask(conferenceContext.getId(), 500, (ConferenceContext) conferenceContext, newPresenter);
			taskService.addTask(sendConferenceInfoToTerminalTask);
		} else if (conferenceContext instanceof McuZjConferenceContext) {
			McuZjSendConferenceInfoToTerminalTask sendConferenceInfoToTerminalTask = new McuZjSendConferenceInfoToTerminalTask(conferenceContext.getId(), 500, (McuZjConferenceContext) conferenceContext, newPresenter);
			delayTaskService.addTask(sendConferenceInfoToTerminalTask);
		} else if (conferenceContext instanceof McuPlcConferenceContext) {
			McuPlcSendConferenceInfoToTerminalTask sendConferenceInfoToTerminalTask = new McuPlcSendConferenceInfoToTerminalTask(conferenceContext.getId(), 500, (McuPlcConferenceContext) conferenceContext, newPresenter);
			mcuPlcDelayTaskService.addTask(sendConferenceInfoToTerminalTask);
		} else if (conferenceContext instanceof McuKdcConferenceContext) {
			McuKdcSendConferenceInfoToTerminalTask sendConferenceInfoToTerminalTask = new McuKdcSendConferenceInfoToTerminalTask(conferenceContext.getId(), 500, (McuKdcConferenceContext) conferenceContext, newPresenter);
			mcuKdcDelayTaskService.addTask(sendConferenceInfoToTerminalTask);
		}
	}

	/**
	 * 推送入会消息给目标终端
	 * @param conferenceContext
	 * @param attendee
	 */
	@Override
	public void sendJoinConferenceToPushTargetTerminal(BaseConferenceContext conferenceContext, BaseAttendee attendee) {
		if (conferenceContext instanceof ConferenceContext) {
			SendJoinConferenceToTerminalTask sendJoinConferenceToTerminalTask = new SendJoinConferenceToTerminalTask(conferenceContext.getId(), 500, (ConferenceContext) conferenceContext, (Attendee) attendee);
			taskService.addTask(sendJoinConferenceToTerminalTask);
		} else if (conferenceContext instanceof McuZjConferenceContext) {
			McuZjSendJoinConferenceToTerminalTask sendJoinConferenceToTerminalTask = new McuZjSendJoinConferenceToTerminalTask(conferenceContext.getId(), 500, (McuZjConferenceContext) conferenceContext, (AttendeeForMcuZj) attendee);
			delayTaskService.addTask(sendJoinConferenceToTerminalTask);
		} else if (conferenceContext instanceof McuPlcConferenceContext) {
			McuPlcSendJoinConferenceToTerminalTask sendJoinConferenceToTerminalTask = new McuPlcSendJoinConferenceToTerminalTask(conferenceContext.getId(), 500, (McuPlcConferenceContext) conferenceContext, (AttendeeForMcuPlc) attendee);
			mcuPlcDelayTaskService.addTask(sendJoinConferenceToTerminalTask);
		} else if (conferenceContext instanceof McuKdcConferenceContext) {
			McuKdcSendJoinConferenceToTerminalTask sendJoinConferenceToTerminalTask = new McuKdcSendJoinConferenceToTerminalTask(conferenceContext.getId(), 500, (McuKdcConferenceContext) conferenceContext, (AttendeeForMcuKdc) attendee);
			mcuKdcDelayTaskService.addTask(sendJoinConferenceToTerminalTask);
		}
	}

	@Override
	public void sendLeftConferenceToPushTargetTerminal(BaseConferenceContext conferenceContext, BaseAttendee attendee) {
		if (conferenceContext instanceof ConferenceContext) {
			SendLeftConferenceToTerminalTask sendLeftConferenceToTerminalTask = new SendLeftConferenceToTerminalTask(conferenceContext.getId(), 500, (ConferenceContext) conferenceContext, (Attendee) attendee);
			taskService.addTask(sendLeftConferenceToTerminalTask);
		} else if (conferenceContext instanceof McuZjConferenceContext) {
			McuZjSendLeftConferenceToTerminalTask sendLeftConferenceToTerminalTask = new McuZjSendLeftConferenceToTerminalTask(conferenceContext.getId(), 500, (McuZjConferenceContext) conferenceContext, (AttendeeForMcuZj) attendee);
			delayTaskService.addTask(sendLeftConferenceToTerminalTask);
		} else if (conferenceContext instanceof McuPlcConferenceContext) {
			McuPlcSendLeftConferenceToTerminalTask sendLeftConferenceToTerminalTask = new McuPlcSendLeftConferenceToTerminalTask(conferenceContext.getId(), 500, (McuPlcConferenceContext) conferenceContext, (AttendeeForMcuPlc) attendee);
			mcuPlcDelayTaskService.addTask(sendLeftConferenceToTerminalTask);
		} else if (conferenceContext instanceof McuKdcConferenceContext) {
			McuKdcSendLeftConferenceToTerminalTask sendLeftConferenceToTerminalTask = new McuKdcSendLeftConferenceToTerminalTask(conferenceContext.getId(), 500, (McuKdcConferenceContext) conferenceContext, (AttendeeForMcuKdc) attendee);
			mcuKdcDelayTaskService.addTask(sendLeftConferenceToTerminalTask);
		}
	}

	@Override
	public void sendConferenceComingToEndMessage(BaseConferenceContext conferenceContext, long min, long s) {
		if (conferenceContext != null) {
			BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);

			Set<BusiTerminal> busiTerminalList = new HashSet<>();
			Long createUserId = conferenceContext.getCreateUserId();
			BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByUserId(createUserId);
			if (busiUserTerminal != null) {
				BaseAttendee terminalAttendee = conferenceContext.getAttendeeByTerminalId(busiUserTerminal.getTerminalId());
				if (terminalAttendee != null && terminalAttendee.getMeetingStatus() == AttendeeMeetingStatus.IN.getValue()) {
					BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiUserTerminal.getTerminalId());
					if (StringUtils.isNotEmpty(busiTerminal.getSn())) {
						if (busiTerminal.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
							busiTerminalList.add(busiTerminal);
						}
					}
				}
			}
			Long presenter = conferenceContext.getPresenter();
			BusiUserTerminal busiUserTerminalPresenter = busiUserTerminalMapper.selectBusiUserTerminalByUserId(presenter);
			if (busiUserTerminalPresenter != null) {
				BaseAttendee terminalAttendeePresenter = conferenceContext.getAttendeeByTerminalId(busiUserTerminalPresenter.getTerminalId());
				if (terminalAttendeePresenter != null && terminalAttendeePresenter.getMeetingStatus() == AttendeeMeetingStatus.IN.getValue()) {
					BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeePresenter.getTerminalId());
					if (StringUtils.isNotEmpty(busiTerminal.getSn())) {
						if (busiTerminal.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
							busiTerminalList.add(busiTerminal);
						}
					}
				}
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("conferenceNum", conferenceContext.getTenantId() + conferenceContext.getConferenceNumber());
			jsonObject.put("conferenceName", conferenceContext.getName());
			jsonObject.put("conferenceComingToEnd", true);
			jsonObject.put("remainingMinutes", min);
			jsonObject.put("remainingSeconds", s);
			String action = TerminalTopic.CONFERENCE_COMING_TO_END;
			if (busiTerminalList != null && busiTerminalList.size() > 0) {
				for (BusiTerminal busiTerminal : busiTerminalList) {
					String terminalTopic = "terminal/" + busiTerminal.getSn();
					ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, busiTerminal.getSn(), "");
				}
			}
		}

	}

	@Override
	public void sendPollingAttendMessage(BaseAttendee attendee, BaseConferenceContext conferenceContext, boolean pollingAttend) {
		if (StringUtils.isNotEmpty(attendee.getSn()) && attendee.getMeetingStatus() == AttendeeMeetingStatus.IN.getValue()) {
			BusiTerminal busiTerminal = TerminalCache.getInstance().getBySn(attendee.getSn());
			if (busiTerminal != null) {
				if (busiTerminal.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
					String terminalTopic = "terminal/" + busiTerminal.getSn();
					String action = TerminalTopic.POLLING_ATTEND;
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("pollingAttend", pollingAttend);
					jsonObject.put(MqttConfigConstant.CONFERENCENUM, conferenceContext.getTenantId() + conferenceContext.getConferenceNumber());
					ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, busiTerminal.getSn(), "");
				}
			}
		}
	}

	@Override
	public void inviteAttendeeJoinConference(BaseAttendee terminalAttendee, BaseConferenceContext conferenceContext, Integer joinType) {
		String actionI = TerminalTopic.INVITE_CONFERENCE;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		JSONObject object = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		Long terminalId = terminalAttendee.getTerminalId();
		BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
		if (null != busiTerminal) {
			if (busiTerminal.getMqttOnlineStatus() != TerminalOnlineStatus.ONLINE.getValue()) {
				// 不在线的终端不推送消息
				return;
			}
			String conferenceEtime = null;
			String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminal.getSn();
			String conferenceName = conferenceContext.getName();
			String remarks = conferenceContext.getRemarks();
			Date conferenceSdate = conferenceContext.getStartTime();
			Date conferenceEdate = conferenceContext.getEndTime();
			String conferenceStime = formatter.format(conferenceSdate);
			if (null != conferenceEdate) {
				conferenceEtime = formatter.format(conferenceEdate);
			}

			object.put(MqttConfigConstant.CONFERENCENUM, conferenceContext.getConferenceNumber());
			object.put(MqttConfigConstant.PASSWORD, conferenceContext.getConferencePassword());
			List<String> stringList = new ArrayList<>();
			if (conferenceContext.getIsAutoCreateStreamUrl() == 1) {
				stringList = conferenceContext.getStreamUrlList();
			} else {
				if (StringUtils.isNotEmpty(conferenceContext.getStreamingUrl())) {
					stringList.add(conferenceContext.getStreamingUrl());
				}
			}
			int nextInt = 0;
			String streamingUrl = conferenceContext.getStreamingUrl();
			if (stringList.size() > 0) {
				nextInt = new Random().nextInt(stringList.size());
				streamingUrl = stringList.get(nextInt);
			}
			object.put("liveUrl", streamingUrl);
			object.put("liveUrlList", stringList);
			object.put("conferencePassword", conferenceContext.getConferencePassword());
			object.put("conferenceRemark", remarks);
			object.put("conferenceName", conferenceName);
			object.put("conferenceStime", conferenceStime);
			object.put("conferenceEtime", conferenceEtime);
			object.put("joinConference", joinType);
			object.put("attendeeTerminal", jsonArray);

			ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, actionI, object, busiTerminal.getSn(), "");
		}

	}

	/**
	 * 向电子门牌推送注册信息
	 *
	 * @param doorplateSn 电子门牌sn
	 */
	@Override
	public void pushRegister(String doorplateSn) {
		if (StringUtils.isNotEmpty(doorplateSn)) {
			String terminalTopic = MqttConfigConstant.TOPIC_PREFIX_DOORPLATE + doorplateSn;
			String action = DeviceAction.REGISTER;
			JSONObject jsonObject = new JSONObject();
			BusiSmartRoomDoorplate busiSmartRoomDoorplate = SmartRoomDoorplateCache.getInstance().getBySn(doorplateSn);
			if (busiSmartRoomDoorplate != null) {
				jsonObject.put("registered", true);
				jsonObject.put("bound", false);
				BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().getRoomByDoorplate(busiSmartRoomDoorplate);
				if (busiSmartRoom != null) {
					jsonObject.put("bound", true);
					jsonObject.put("roomName", busiSmartRoom.getRoomName());
					String position = "";
					if (busiSmartRoom.getCity() != null) {
						position += busiSmartRoom.getCity();
					}
					if (busiSmartRoom.getBuilding() != null) {
						position += busiSmartRoom.getBuilding();
					}
					if (busiSmartRoom.getFloor() != null) {
						position += busiSmartRoom.getFloor();
					}
					jsonObject.put("position", position);
					jsonObject.put("roomType", busiSmartRoom.getRoomType());
					jsonObject.put("roomLevel", busiSmartRoom.getRoomLevel());
					Long userId = busiSmartRoom.getUserId();
					SysUser sysUser = sysUserService.selectUserById(userId);
					boolean canBookOnlineConference = false;
					if (sysUser != null) {
						Long deptIdTemp = sysUser.getDeptId();
						McuTypeVo defaultMcuType = busiAllMcuService.getDefaultMcuType(deptIdTemp);
						if (defaultMcuType != null) {
							canBookOnlineConference = true;
						}
						jsonObject.put("deptId", deptIdTemp);
					}
					jsonObject.put("userId", userId);
					jsonObject.put("canBookOnlineConference", canBookOnlineConference);
				}
			} else {
				jsonObject.put("registered", false);
			}
			ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, doorplateSn, "");
		}
	}

	/**
	 * 向电子门牌推送会议室信息
	 *
	 * @param doorplateSn 电子门牌sn
	 * @author sinhy
	 * @since 2021-11-18 14:07
	 */
	@Override
	public void pushMeetingRoomInfo(String doorplateSn) {
		BusiSmartRoomDoorplate busiSmartRoomDoorplate = SmartRoomDoorplateCache.getInstance().getBySn(doorplateSn);
		if (busiSmartRoomDoorplate != null) {
			BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().getRoomByDoorplate(busiSmartRoomDoorplate);
			if (busiSmartRoom != null) {
				String terminalTopic = MqttConfigConstant.TOPIC_PREFIX_DOORPLATE + doorplateSn;
				String action = DeviceAction.MEETING_ROOM_INFO;
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("roomName", busiSmartRoom.getRoomName());
				String fmcRootUrl = getFmcRooUrl(busiSmartRoomDoorplate.getConnectIp());
				String bookUrl = ExternalConfigCache.getInstance().getBookUrl();
				if (!(StringUtils.isNotEmpty(bookUrl) && bookUrl.startsWith("http"))) {
					bookUrl = fmcRootUrl + bookUrl;
				}
				String signInUrl = ExternalConfigCache.getInstance().getSignInUrl();
				if (!(StringUtils.isNotEmpty(signInUrl) && signInUrl.startsWith("http"))) {
					signInUrl = fmcRootUrl + signInUrl;
				}
				jsonObject.put("bookUrl", bookUrl + "?roomId=" + busiSmartRoom.getId());
				JSONArray jsonArrayNextTerm = new JSONArray();

				BusiSmartRoomBook terminalBook = SmartRoomCache.getInstance().getTerminalBook(busiSmartRoom.getId());
				if (terminalBook != null) {
					JSONObject book = new JSONObject();
					book.put("bookName", terminalBook.getBookName());
					book.put("startTime", DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", terminalBook.getStartTime()));
					book.put("endTime", DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", terminalBook.getEndTime()));
					Map<String, Object> params = terminalBook.getParams();
					String createByDeptName = String.valueOf(params.get("createByDeptName"));
					String createBy = String.valueOf(params.get("createByName"));
					book.put("createBy", createBy);
					book.put("deptName", createByDeptName);
					book.put("remark", terminalBook.getRemark());
					jsonObject.put("current", book);
				}

				Date date = new Date();
				MeetingRoomInfo meetingRoomInfo = SmartRoomCache.getInstance().getMeetingRoomInfo(busiSmartRoom.getId(), date);
				if (meetingRoomInfo != null) {
					jsonObject.put("position", meetingRoomInfo.getPosition());
					BusiSmartRoomBook busiSmartRoomBookCurrent = meetingRoomInfo.getCurrent();
					BusiSmartRoomBook busiSmartRoomBookToBegin = meetingRoomInfo.getToBegin();
					List<BusiSmartRoomBook> busiSmartRoomBookList = meetingRoomInfo.getNextList();
					if (busiSmartRoomBookCurrent != null) {
						JSONObject book = new JSONObject();
						book.put("bookName", busiSmartRoomBookCurrent.getBookName());
						book.put("startTime", DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", busiSmartRoomBookCurrent.getStartTime()));
						book.put("endTime", DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", busiSmartRoomBookCurrent.getEndTime()));
						String createBy = "";
						String createByDeptName = "";
						if (StringUtils.isNotEmpty(busiSmartRoomBookCurrent.getCreateBy())) {
							SysUser sysUser = sysUserService.selectUserByUserName(busiSmartRoomBookCurrent.getCreateBy());
							if (sysUser != null) {
								createBy = sysUser.getNickName();
								if (sysUser.getDept() != null) {
									createByDeptName = sysUser.getDept().getDeptName();
								}
							}
						}
						book.put("createBy", createBy);
						book.put("deptName", createByDeptName);
						book.put("remark", busiSmartRoomBookCurrent.getRemark());
						book.put("signInUrl", signInUrl + "?bookId=" + busiSmartRoomBookCurrent.getId());
						String mcuType = busiSmartRoomBookCurrent.getMcuType();
						Long appointmentConferenceId = busiSmartRoomBookCurrent.getAppointmentConferenceId();
						if (StringUtils.isNotEmpty(mcuType) && appointmentConferenceId != null) {
							McuType convert = McuType.convert(mcuType);
							ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuType, appointmentConferenceId);
							if (viewConferenceAppointment != null) {
								ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuType, viewConferenceAppointment.getTemplateId());
								if (viewTemplateConference != null) {
									book.put("conferenceNumber", viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber());
								}
							}
							book.put("mcuAlias", convert.getAlias());
						}
						jsonObject.put("current", book);
						JSONArray jsonArraySignInList = new JSONArray();
						BusiSmartRoomParticipant busiSmartRoomParticipantCon = new BusiSmartRoomParticipant();
						busiSmartRoomParticipantCon.setBookId(busiSmartRoomBookCurrent.getId());
						List<BusiSmartRoomParticipant> busiSmartRoomParticipantList = busiSmartRoomParticipantService.selectBusiSmartRoomParticipantList(busiSmartRoomParticipantCon);
						for (BusiSmartRoomParticipant busiSmartRoomParticipant : busiSmartRoomParticipantList) {
							JSONObject signIn = new JSONObject();
							String signInTimeStr = "";
							if (busiSmartRoomParticipant.getSignInTime() != null) {
								signInTimeStr = DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", busiSmartRoomParticipant.getSignInTime());
							}
							signIn.put("signInTime", signInTimeStr);
							signIn.put("userName", busiSmartRoomParticipant.getUserName());
							String deptName = "";
							if (busiSmartRoomParticipant.getUserId() != null) {
								SysUser sysUser = sysUserService.selectUserById(busiSmartRoomParticipant.getUserId());
								if (sysUser != null) {
									if (sysUser.getDept() != null) {
										deptName = sysUser.getDept().getDeptName();
									}
								}
							}
							signIn.put("deptName", deptName);
							jsonArraySignInList.add(signIn);
						}
						jsonObject.put("signInList", jsonArraySignInList);
					}
					if (busiSmartRoomBookToBegin != null) {
						JSONObject bookNext = new JSONObject();
						bookNext.put("bookName", busiSmartRoomBookToBegin.getBookName());
						bookNext.put("startTime", DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", busiSmartRoomBookToBegin.getStartTime()));
						bookNext.put("endTime", DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", busiSmartRoomBookToBegin.getEndTime()));
						String createBy = "会议管理者";
						String createByDeptName = "";
						if (StringUtils.isNotEmpty(busiSmartRoomBookToBegin.getCreateBy())) {
							SysUser sysUser = sysUserService.selectUserByUserName(busiSmartRoomBookToBegin.getCreateBy());
							if (sysUser != null) {
								createBy = sysUser.getNickName();
								if (sysUser.getDept() != null) {
									createByDeptName = sysUser.getDept().getDeptName();
								}
							}
						}
						bookNext.put("createBy", createBy);
						bookNext.put("deptName", createByDeptName);
						bookNext.put("remark", busiSmartRoomBookToBegin.getRemark());
						bookNext.put("signInUrl", signInUrl + "?bookId=" + busiSmartRoomBookToBegin.getId());
						String mcuType = busiSmartRoomBookToBegin.getMcuType();
						Long appointmentConferenceId = busiSmartRoomBookToBegin.getAppointmentConferenceId();
						if (StringUtils.isNotEmpty(mcuType) && appointmentConferenceId != null) {
							McuType convert = McuType.convert(mcuType);
							ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuType, appointmentConferenceId);
							if (viewConferenceAppointment != null) {
								ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuType, viewConferenceAppointment.getTemplateId());
								if (viewTemplateConference != null) {
									bookNext.put("conferenceNumber", viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber());
								}
							}
							bookNext.put("mcuAlias", convert.getAlias());
						}
						jsonObject.put("toBegin", bookNext);
						JSONArray jsonArraySignInList = new JSONArray();
						BusiSmartRoomParticipant busiSmartRoomParticipantCon = new BusiSmartRoomParticipant();
						busiSmartRoomParticipantCon.setBookId(busiSmartRoomBookToBegin.getId());
						List<BusiSmartRoomParticipant> busiSmartRoomParticipantList = busiSmartRoomParticipantService.selectBusiSmartRoomParticipantList(busiSmartRoomParticipantCon);
						for (BusiSmartRoomParticipant busiSmartRoomParticipant : busiSmartRoomParticipantList) {
							JSONObject signIn = new JSONObject();
							String signInTimeStr = "";
							if (busiSmartRoomParticipant.getSignInTime() != null) {
								signInTimeStr = DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", busiSmartRoomParticipant.getSignInTime());
							}
							signIn.put("signInTime", signInTimeStr);
							signIn.put("userName", busiSmartRoomParticipant.getUserName());
							String deptName = "";
							if (busiSmartRoomParticipant.getUserId() != null) {
								SysUser sysUser = sysUserService.selectUserById(busiSmartRoomParticipant.getUserId());
								if (sysUser != null) {
									if (sysUser.getDept() != null) {
										deptName = sysUser.getDept().getDeptName();
									}
								}
							}
							signIn.put("deptName", deptName);
							jsonArraySignInList.add(signIn);
						}
						jsonObject.put("signInList", jsonArraySignInList);
					}
					if (busiSmartRoomBookList.size() > 0) {
						for (int i = 0; i < busiSmartRoomBookList.size(); i++) {
							BusiSmartRoomBook busiSmartRoomBookNextTerm = busiSmartRoomBookList.get(i);
							JSONObject bookNextTerm = new JSONObject();
							bookNextTerm.put("bookName", busiSmartRoomBookNextTerm.getBookName());
							bookNextTerm.put("startTime", DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", busiSmartRoomBookNextTerm.getStartTime()));
							bookNextTerm.put("endTime", DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", busiSmartRoomBookNextTerm.getEndTime()));
							String createBy = "会议管理者";
							String createByDeptName = "";
							if (StringUtils.isNotEmpty(busiSmartRoomBookNextTerm.getCreateBy())) {
								SysUser sysUser = sysUserService.selectUserByUserName(busiSmartRoomBookNextTerm.getCreateBy());
								if (sysUser != null) {
									createBy = sysUser.getNickName();
									if (sysUser.getDept() != null) {
										createByDeptName = sysUser.getDept().getDeptName();
									}
								}
							}
							bookNextTerm.put("createBy", createBy);
							bookNextTerm.put("deptName", createByDeptName);
							bookNextTerm.put("remark", busiSmartRoomBookNextTerm.getRemark());
							bookNextTerm.put("signInUrl", signInUrl + "?bookId=" + busiSmartRoomBookNextTerm.getId());
							String mcuType = busiSmartRoomBookNextTerm.getMcuType();
							Long appointmentConferenceId = busiSmartRoomBookNextTerm.getAppointmentConferenceId();
							if (StringUtils.isNotEmpty(mcuType) && appointmentConferenceId != null) {
								McuType convert = McuType.convert(mcuType);
								ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuType, appointmentConferenceId);
								if (viewConferenceAppointment != null) {
									ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuType, viewConferenceAppointment.getTemplateId());
									if (viewTemplateConference != null) {
										bookNextTerm.put("conferenceNumber", viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber());
									}
								}
								bookNextTerm.put("mcuAlias", convert.getAlias());
							}
							jsonArrayNextTerm.add(bookNextTerm);
						}
					}
				}

				if (terminalBook != null) {
					jsonObject.put("status", 3);
				} else {
					jsonObject.put("status", meetingRoomInfo.getStatus());
				}
				jsonObject.put("nextList", jsonArrayNextTerm);

				JSONArray jsonArrayNextDayTerm = new JSONArray();
				Date diffDate = DateUtils.getDiffDate(date, +1, TimeUnit.DAYS);
				Date startTime = DateUtils.getDayStartTime(diffDate);
				Date endTime = DateUtils.getDayEndTime(diffDate);
				List<BusiSmartRoomBook> busiSmartRoomBookList = busiSmartRoomBookService.selectBusiSmartRoomBookListForNextTerm(busiSmartRoom.getId(), startTime, endTime);
				if (busiSmartRoomBookList != null && busiSmartRoomBookList.size() > 0) {
					BusiSmartRoomBook busiSmartRoomBookNextTerm = busiSmartRoomBookList.get(0);
					JSONObject bookNextTerm = new JSONObject();
					bookNextTerm.put("bookName", busiSmartRoomBookNextTerm.getBookName());
					bookNextTerm.put("startTime", DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", busiSmartRoomBookNextTerm.getStartTime()));
					bookNextTerm.put("endTime", DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", busiSmartRoomBookNextTerm.getEndTime()));
					String createBy = "会议管理者";
					String createByDeptName = "";
					if (StringUtils.isNotEmpty(busiSmartRoomBookNextTerm.getCreateBy())) {
						SysUser sysUser = sysUserService.selectUserByUserName(busiSmartRoomBookNextTerm.getCreateBy());
						if (sysUser != null) {
							createBy = sysUser.getNickName();
							if (sysUser.getDept() != null) {
								createByDeptName = sysUser.getDept().getDeptName();
							}
						}
					}
					bookNextTerm.put("createBy", createBy);
					bookNextTerm.put("deptName", createByDeptName);
					bookNextTerm.put("remark", busiSmartRoomBookNextTerm.getRemark());
					bookNextTerm.put("signInUrl", signInUrl + "?bookId=" + busiSmartRoomBookNextTerm.getId());
					String mcuType = busiSmartRoomBookNextTerm.getMcuType();
					Long appointmentConferenceId = busiSmartRoomBookNextTerm.getAppointmentConferenceId();
					if (StringUtils.isNotEmpty(mcuType) && appointmentConferenceId != null) {
						McuType convert = McuType.convert(mcuType);
						ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuType, appointmentConferenceId);
						if (viewConferenceAppointment != null) {
							ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuType, viewConferenceAppointment.getTemplateId());
							if (viewTemplateConference != null) {
								bookNextTerm.put("conferenceNumber", viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber());
							}
						}
						bookNextTerm.put("mcuAlias", convert.getAlias());
					}
					jsonArrayNextDayTerm.add(bookNextTerm);
				}
				jsonObject.put("nextDayList", jsonArrayNextDayTerm);

				// 更新最后推送时间
				SmartRoomCache.getInstance().updateLastPushTime(busiSmartRoom.getId());

				ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, doorplateSn, "");
			}
		}
	}

	/**
	 * 向电子门牌推送会议室信息
	 *
	 * @author sinhy
	 * @since 2021-11-18 14:07
	 */
	@Override
	public void pushMsg(String topic, byte[] msg) {
		PublisMessage.getInstance().publishTopicMsg(topic, msg, false);
	}

	private String getFmcRooUrl(String connectIp) {
		if (StringUtils.isNotEmpty(connectIp)) {
			String fmcRootUrlExternal = ExternalConfigCache.getInstance().getFmcRootUrlExternal();
			if (StringUtils.isNotEmpty(fmcRootUrlExternal)) {
				String externalIp = fmcRootUrlExternal.replace("http://", "").replace("https://", "");
				if (externalIp.indexOf(":") > 0) {
					externalIp.substring(0, externalIp.indexOf(":"));
				}
				if (externalIp.indexOf("/") > 0) {
					externalIp = externalIp.substring(0, externalIp.indexOf("/"));
				}
				if (externalIp.equals(connectIp)) {
					return fmcRootUrlExternal;
				}
			}
		}
		return ExternalConfigCache.getInstance().getFmcRootUrl();
	}
}
