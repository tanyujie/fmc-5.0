package com.paradisecloud.fcm.mqtt.client;

import com.paradisecloud.fcm.mqtt.constant.DeviceAction;
import com.paradisecloud.fcm.mqtt.interfaces.*;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDeviceCache;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.mqtt.common.TerminalSipAccount;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.TerminalTopic;

import javax.annotation.Resource;

/**
 * @author zyz
 *
 */
@Component
public class MessageCallback implements MqttCallback 
{
	
	private static final Logger LOG = LoggerFactory.getLogger(MqttCallback.class);
	
	@Resource
	private ITerminalActionService terminalActionService;

	@Resource
	private IBusiRegisterTerminalService busiRegisterTerminalService;

	@Resource
	private IDeviceActionService deviceActionService;

	@Resource
	private IOpsActionService opsActionService;

	@Resource
	private IClientActionService clientActionService;
	
	/**
	 * 当客户端丢失了服务端的连接之后，触发的回调函数
	 */
	public void connectionLost(Throwable cause) 
	{
		LOG.error("当客户端丢失了服务端的连接之后，触发的回调函数=====> {}" , cause.getCause());
//		emqClient.reConnect();
	}
	

	/**
	 * 当订阅者收到消息了，消息到达了上层应用(触发的回调)
	 * 注意：该方法由mqtt客户端同步调用，在此方法未正确返回之前，客户端不会发送ack确认消息到broker
	 * 一旦该方法向外抛出了异常，客户端会异常关闭，当再次连接时，所有Qos1,Qos2且客户端未进行ack确认的消息,都将由broker服务器再次发送到客户端
	 */
	public void messageArrived(String topic, MqttMessage message) throws Exception 
	{
		LOG.info("订阅到了消息;topic={},messageid={},qos={},msg={}",topic,message.getId(),message.getQos(),new String(message.getPayload()));

		// 物联网设备
		if (topic.startsWith("platform/lot")) {
			try {
				String[] topicArr = topic.split("/");
				String clientId = null;
				Integer lotId = null;
				Integer channel = null;
				String action = null;
				if (topicArr.length >= 3) {
					clientId = topicArr[2];
					lotId = Integer.valueOf(clientId.replace("lot", ""));
				}
				if (topicArr.length >= 4) {
					channel = Integer.valueOf(topicArr[3]);
				}
				if (topicArr.length >= 5) {
					action = topicArr[4];
				}
				if (lotId != null && channel != null) {
					SmartRoomDeviceCache.getInstance().updateLotDeviceOnlineTime(lotId, channel, System.currentTimeMillis());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}

		JSONObject jsonObject = JSONObject.parseObject(new String(message.getPayload()));
		String action = jsonObject.getString(MqttConfigConstant.ACTION);
		String clientId = jsonObject.getString(MqttConfigConstant.CLIENTID);
		String messageId = jsonObject.getString(MqttConfigConstant.MESSAGE_ID);
		JSONObject jsonS = jsonObject.getJSONObject(MqttConfigConstant.JSON_DATA_STR);

		// 电子门牌
		if (topic.startsWith("platform/doorplate")) {
			if (jsonObject != null && action != null) {
				try {
					switch (action) {
						case DeviceAction.REGISTER:

							deviceActionService.register(jsonS, clientId);
							break;
						case DeviceAction.MEETING_ROOM_INFO:

							deviceActionService.meetingRoomInfo(jsonS, clientId);
							break;
						case DeviceAction.CREATE_SMARTROOM_BOOK:

							deviceActionService.createSmartRoomBook(jsonS, clientId);
							break;
						case DeviceAction.INFO_DISPLAY:

							deviceActionService.pushInfoDisplay(jsonS, clientId);
							break;
						default:
							break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return;
		}

		// OPS
		if (topic.startsWith("platform/ops")) {
			if (jsonObject != null && action != null) {
				try {
					switch (action) {
						case DeviceAction.REGISTER:

							opsActionService.register(jsonS, clientId);
							break;
						default:
							break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return;
		}

		// 客户端
		if (topic.startsWith("platform/client")) {
			if (jsonObject != null && action != null) {
				try {
					switch (action) {
						case DeviceAction.REGISTER:

							clientActionService.register(jsonS, clientId);
							break;
						case DeviceAction.ASR_SIGN:

							clientActionService.asrSign(clientId);
							break;
						default:
							break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return;
		}
		
		if(jsonObject != null && action != null) {
			
			switch (action) {
			case TerminalTopic.GET_SIP_ACCOUNT:
				if(null != jsonS) {
					BusiTerminal busiTerminal = new BusiTerminal();
					busiTerminal.setSn(clientId);
//					busiTerminal.setType(Integer.valueOf((String) jsonS.get("type")));
					//终端获取sip账号  
					TerminalSipAccount.getInstance().terminalGetSipAccount(messageId, busiTerminal);
				}
				
				break;
			case TerminalTopic.UPDATE_SIP_ACCOUNT:
					
				//终端修改sip账号信息
				terminalActionService.terminalUpdateSipAccount(jsonS, clientId);
				break;
			case TerminalTopic.SIP_REGISTER:
					
				//sip注册  
				busiRegisterTerminalService.sipRegister(jsonS, clientId, messageId);
				break;
			case TerminalTopic.CREATE_CONFERENCE:
				
				//创建会议
				terminalActionService.terminalCreateConference(jsonS, clientId, messageId);
				break;
			case TerminalTopic.MODIFY_TERMINAL_INFO:
				
				//修改终端的信息
				terminalActionService.modifyTerminalInfo(jsonS, clientId, messageId);
				break;
			case TerminalTopic.OPEN_PRESENTATION:
				
				//打开终端的双流
				terminalActionService.terminalPresentationOpenOrClose(jsonS, clientId, messageId, action);
				break;
			case TerminalTopic.CLOSE_PRESENTATION:
				
				//关闭终端的双流
				terminalActionService.terminalPresentationOpenOrClose(jsonS, clientId, messageId, action);
				break;
			case TerminalTopic.TERMINAL_SYS_INFO:
				
				//终端系统信息
				terminalActionService.terminalSysInfo(jsonS, clientId, messageId);
				break;
			case TerminalTopic.END_CONFERENCE:
				
				//终端上主持人结束会议
				terminalActionService.hostEndConference(jsonS, clientId, messageId);
				break;
			case TerminalTopic.KICK_PARTICIPANT:
				
				//终端上主持人移除与会者
				terminalActionService.hostKickParticipant(jsonS, clientId, messageId);
				break;
			case TerminalTopic.ROLL_CALL:
				
				//终端上主持人点名
				terminalActionService.hostRollCall(jsonS, clientId, messageId);
				break;
			case TerminalTopic.CANCEL_ROLL_CALL:
				
				//终端上主持人取消点名
				terminalActionService.hostCancleRollCall(jsonS, clientId, messageId);
				break;
			case TerminalTopic.OPEN_MIXING:
				
				//主持人对终端开麦
				terminalActionService.hostOpenOrCloseMixing(jsonS, clientId, action, messageId);
				break;
			case TerminalTopic.CLOSE_MIXING:
				
				//主持人对终端关麦
				terminalActionService.hostOpenOrCloseMixing(jsonS, clientId, action, messageId);
				break;
			case TerminalTopic.EXTEND_MINUTES:
				
				//主持人延长会议时间
				terminalActionService.hostExtendMinutes(jsonS, clientId, messageId);
				break;
			case TerminalTopic.CONFERENCE_MIXING:
				
				//主持人对会场开音或者闭音
				terminalActionService.hostOpenOrCloseConferenceMixing(jsonS, clientId, messageId);
				break;
			case TerminalTopic.REINVITE_TERMINAL:
				
				//主持人重邀终端
				terminalActionService.hostReinviteTerminal(jsonS, clientId, action, messageId);
				break;
			case TerminalTopic.QUICK_INVITE_PARTICIPANTS:
				
				//一键邀请
				terminalActionService.hostReinviteTerminal(jsonS, clientId, action, messageId);
				break;
			case TerminalTopic.CHANGE_HOST:
				
				//主持人转换
				terminalActionService.hostChange(jsonS, clientId, messageId);
				break;
			case TerminalTopic.RECORD_CONFERENCE:
				
				//录制会议
				terminalActionService.recordConference(jsonS, clientId, messageId);
				break;
			case TerminalTopic.LOCK_CONFERENCE:
				
				//锁定会议室
				terminalActionService.lockConference(jsonS, clientId, messageId);
			case TerminalTopic.ADD_PARTICIPANTS:
				
				//添加参会人
				terminalActionService.addParticipants(jsonS, clientId, messageId);
				break;
			case TerminalTopic.SET_CONFERENCE_CAPTION:
				
				//设置字幕横幅	
				terminalActionService.setConferenceCaption(jsonS, clientId, messageId);
				break;
			case TerminalTopic.UPDATE_MQTT_CONFIG:
				
				//终端订阅到修改mqtt配置信息，删除连接	
				terminalActionService.deleteMqttBrokerTerminal(jsonS, clientId);
				break;
			case TerminalTopic.ADDRESS_BOOK:
				
				//终端所属部门树
				terminalActionService.byTerminalIdGetDeptTree(jsonS, clientId, messageId);
				break;
			case TerminalTopic.DEPT_DOWN_TERMINAL:
				
				//部门下的终端
				terminalActionService.deptDownTerminal(jsonS, clientId, messageId);
				break;
			case TerminalTopic.CONFERENCE_LAYOUT:
				
				//会议布局
				terminalActionService.conferenceViewLayout(jsonS, clientId, messageId);
				break;
			case TerminalTopic.LEAVE_CONFERENCE:
				
				//退出会议
				terminalActionService.leaveConference(jsonS, clientId, messageId);
				break;
			case TerminalTopic.CHOOSE_SEE:
				
				//选看
				terminalActionService.chooseSee(jsonS, clientId, messageId);
				break;
			case TerminalTopic.CONFERENCE_LIST_NEW:
				//会议列表
				try {
					terminalActionService.conferenceList(jsonS, clientId, messageId,null);
				}catch (Exception e){
					LOG.info("conferenceListNew 接口报错==",e);
				}
				break;
			case TerminalTopic.ALLOW_ALL_PRESENTATION:
				
				//会议辅流控制
				terminalActionService.conferencePresentationControl(jsonS, clientId, messageId);
				break;
			case TerminalTopic.CAMERA_CONTROL:
				
				//会议镜头控制
				terminalActionService.conferenceCameraControl(jsonS, clientId);
				break;
			case TerminalTopic.CONFERENCE_DETAILS:
				
				//会议详情
				terminalActionService.conferenceDetails(jsonS, clientId, messageId);
				break;
			case TerminalTopic.FUZZY_QUERY_TERMINAL:
				
				//模糊查询终端
				terminalActionService.getFuzzyQueryTerminal(jsonS, clientId, messageId);
				break;
			case TerminalTopic.DISCUSS:
				
				//会场讨论
				terminalActionService.conferenceDiscuss(jsonS, clientId, messageId);
				break;
			case TerminalTopic.BACK_DEFAULT_LAYOUT:
				
				//快捷键,返回上次默认布局
				terminalActionService.backDefaultLayout(jsonS, clientId, messageId);
				break;
			case TerminalTopic.INTERACTIVE_RAISE_HAND:
				
				//互动举手
				terminalActionService.interactiveRaiseHand(jsonS, clientId, messageId);
				break;
				
			case TerminalTopic.DIALOGUE:
				
				//对话
				terminalActionService.terminalDialogue(jsonS, clientId, messageId);
				break;
			case TerminalTopic.ASK_SIP_ACCOUNT_CONDITION:
				
				//询问终端的sip账号的情况
				terminalActionService.askSipAccountCondition(jsonS, clientId, messageId);
				break;
			case TerminalTopic.UNBIND_SIP_ACCOUNT:
				
				//同意解除终端sip账号的绑定
				terminalActionService.unbindTerminalSipAccount(jsonS, clientId, messageId);
				break;
			case TerminalTopic.RECEIVE_DEL_ACCOUNT_INFO:
				
				//机顶盒接收到管理员删除账号信息
				terminalActionService.receiveDelAccountInfo(jsonS, clientId, messageId);
				break;
			case TerminalTopic.URI_INVITE:
				
				//通过ip地址邀请终端
				terminalActionService.byUriInviteTerminal(jsonS, clientId, messageId);
				break;
			case TerminalTopic.FUZZY_QUERY_CONFERENCE:
				
				//模糊查询会议信息
				terminalActionService.fuzzyQueryConference(jsonS, clientId, messageId);
				break;
			case TerminalTopic.ACCEPT_RAISE_HAND:
				
				//同意举手
				terminalActionService.hostAcceptRaiseHand(jsonS, clientId, messageId);
				break;
			case TerminalTopic.REJECT_RAISE_HAND:
				
				//拒绝举手
				terminalActionService.hostRejectRaiseHand(jsonS, clientId, messageId);
				break;
			case TerminalTopic.TERMINAL_STATUS:

				//终端状态异常处理
				terminalActionService.terminalStatus(jsonObject);
				break;
			case TerminalTopic.PUSH_LIVE:

				//推送直播
				terminalActionService.pushLive(jsonS, clientId, messageId);
				break;
			case TerminalTopic.LIVE_TERMINAL:

				//直播终端处理
				terminalActionService.liveTerminal(jsonS, clientId, messageId);
				break;
			case TerminalTopic.RECORDING_LIST:

				//录制列表
				terminalActionService.recordingList(jsonS, clientId, messageId);
				break;
			case TerminalTopic.RECORDING_INFO:

				//录制信息
				terminalActionService.recordingInfo(jsonS, clientId, messageId);
				break;
			case TerminalTopic.SERVER_INFO:

				//服务器信息
				terminalActionService.getServerInfo(jsonS, clientId, messageId);
				break;
			case TerminalTopic.CHECK_APP_VERSION:

				//根据类型和客户ID获取版本信息
				terminalActionService.checkAppVersion(jsonS, clientId, messageId);
				break;
			case TerminalTopic.CONFERENCE_INFO:

				//会议信息
				terminalActionService.sendConferenceInfo(jsonS, clientId, messageId);
				break;

			case TerminalTopic.LIVE_LIST:

				// 直播列表
				terminalActionService.sendLiveListInfo(jsonS, clientId, messageId);
				break;
			case TerminalTopic.CHANGE_PRESENTER:

				// 设置主持人
				terminalActionService.changePresenter(jsonS, clientId, messageId);
				break;
			case TerminalTopic.CONFERENCE_ATTENDEE_LIST:

				// 与会者列表
				terminalActionService.conferenceAttendeeList(jsonS, clientId, messageId);
				break;
			case TerminalTopic.UPDATE_DEFAULTVIEWCONFIGINFO:

				// 修改会议布局
				terminalActionService.updateDefaultViewConfigInfo(jsonS, clientId, messageId);
				break;
			case TerminalTopic.RE_CALL:

				// 会议中重呼
				terminalActionService.reCall(jsonS, clientId, messageId);
				break;
			case TerminalTopic.INFO_DISPLAY:

				// 推送信息展示
				terminalActionService.pushInfoDisplay(jsonS, clientId, messageId);
				break;
			default:
				break;
			}
		}
		else if (jsonObject != null){
			try {
				terminalActionService.terminalStatus(jsonObject);
			}catch (Exception e){
				LOG.error("Mqtt推送终端上下线状态===",e);
			}
		}
		
//		String userName = (String)jSONObject.get(MqttConfigConstant.USERNAME);
//		String ipAddr = (String)jSONObject.get(MqttConfigConstant.IPADDRESS);
//		Integer actionNum = Integer.valueOf(action);
//		
//		//通知会控终端上线,没有此终端新增，有更新状态
//		//0、上线   , 1、下线 , 2、绑定
//		if(actionNum == TerminalActionEnum.JOIN_LIVE.value() || actionNum == TerminalActionEnum.CONFERENCE_SPEECH.value()) 
//		{
//			//直播申请入会或者会议发言
//			terminalActionService.terminalLiveLaunchAction(clientId, conferenceNum, actionNum);
//		}
//		
//		if(actionNum == TerminalActionEnum.UPDATE_CONNECT_IP.value()) 
//		{
//			
//			terminalActionService.deleteMqttBrokerTerminal(clientId, ipAddr, userName);
//		}
	}
	
	/**
	 * 针对的是发布者，发布者发布消息完成之后，产生的方法回调
	 * 不同的Qos，认定发布完成的时机，是不一样的
	 * Qos0:消息被网络发出后触发一次
	 * Qos1:当收到broker的puback消息后触发
	 * Qos2:当收到broker的pubcom消息后触发
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken token)
	{
		int messageId = token.getMessageId();
//		int inFlightMessageCount = token.getClient().getInFlightMessageCount();
//		IMqttDeliveryToken[] bufferedMessageCount = token.getClient().getPendingDeliveryTokens();
//		String clientId = token.getClient().getClientId();
//		try 
//		{
//			JSONObject jSONObject = (JSONObject)JSONObject.parse(new String(token.getMessage().getPayload()));
//			String conferenceNum = (String)jSONObject.get("conferenceNum");
//			Integer action = (Integer)jSONObject.get("action");
//			直播申请入会或者会议发言
//			terminalActionService.terminalLiveLaunchAction(clientId, conferenceNum, action);
			LOG.info("发布者消息发送成功之后,messageId={}", messageId);
//		} 
//		catch (MqttException e) 
//		{
//			e.printStackTrace();
//		}
	}

}
