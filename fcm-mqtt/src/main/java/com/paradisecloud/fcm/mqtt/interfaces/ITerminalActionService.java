package com.paradisecloud.fcm.mqtt.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.dao.model.BusiLiveSetting;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.mqtt.enums.QosEnum;
import com.paradisecloud.fcm.mqtt.model.TerminalLive;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;

import java.util.List;

/**
 * @author zyz
 *
 */
public interface ITerminalActionService 
{
	
	/**
	 * 通知会控处理终端数据
	 * @param action 
	 * @param clientid 
	 * @param userName 
	 * @param ipAddr 
	 * @param terminalIp 
	 */
	public void notifyFcmDealData(String clientid, Integer action, String userName, String mqttIp, String terminalIp);
	
	
	/**
	 * 解绑终端
	 * @param clientid
	 * @param brokerUrl
	 * @return
	 */
//	public Boolean deleteBrokerTerminal(String clientid, String brokerUrl, String userName);
	
	
	/**
	 * 重新绑定EMQX服务器节点，并给终端发布主题，通知终端修改连接服务器的地址
	 * @param clientid
	 * @param brokerUrl
	 */
	public void bindBrokerNodeAndPubTopic(String clientid, String mqttIp, String userName, String password, String terminalIp);
	
	
	/**
	 * 根据clientid,针对终端,邀请入会和预约会议
	 * clientid用,隔开
	 * @param clientid
	 * @param meetType 邀请会议或者预约会议
	 */
//	public void terminalJoinMeetingWay(String clientid, Integer meetType);
	
	/**
	 * fcm发布审批直播申请入会的信息或会议发言
	 * @param clientid
	 * @param conferenceNum
	 */
	public void fcmPublishAgreeLiveAction(String clientid, String conferenceNum, Integer action);
	

	/**
	 * 直播入会和会议发言
	 * @param clientId
	 * @param conferenceNum
	 * @param action
	 */
	public void terminalLiveLaunchAction(String clientId, String conferenceNum, Integer action);
	
	
	/**
	 * 增加默认连接mqtt服务器用户的信息
	 * @param clientId
	 * @param conferenceNum
	 * @param action
	 */
	public int addDefaultUserInfo(String userName, String password, String brokerUrl);


	/**
	 * 发布主题消息
	 * @param publishTopic
	 * @param clientid
	 * @param conferenceNum
	 * @param userName 
	 * @param ipAddr 
	 */
//	public void publishTopicMsg(String publishTopic, String clientid, String message, Boolean flg);


	/**
	 * @param clientId
	 * @param ipAddr
	 * @param userName
	 */
	public void deleteMqttBrokerTerminal(JSONObject jsonS, String clientId);
	
	
	/**获取management端口
	 * @param brokerUrl
	 * @return
	 */
//	public Integer byIpGetBusiMqttInfo(String brokerUrl);

	
//	public Boolean terminalIsConnect(String clientId);

	/**
	 * 连接mqtt服务器
	 * @param username
	 * @param password
	 * @param clientid
	 * @param brokerUrl
	 * @return
	 */
//	public Boolean connectMqttServer(String username, String password, String clientid, String brokerUrl);


//	public void terminalGetSipAccount(String messageId, BusiTerminal busiTerminal);


	public void terminalUpdateSipAccount(JSONObject json, String clientId);


	public void terminalCreateConference(JSONObject jsonS, String clientId, String messageId);


	public void modifyTerminalInfo(JSONObject jsonS, String clientId, String messageId);


	public void terminalPresentationOpenOrClose(JSONObject jsonS, String clientId, String messageId, String action);

	
	public void terminalSysInfo(JSONObject jsonS, String clientId, String messageId);


	public void hostEndConference(JSONObject jsonS, String clientId, String messageId);


	public void hostKickParticipant(JSONObject jsonS, String clientId, String messageId);


	public void hostRollCall(JSONObject jsonS, String clientId, String messageId);


	public void hostOpenOrCloseMixing(JSONObject jsonS, String clientId, String action, String messageId);


	public void hostExtendMinutes(JSONObject jsonS, String clientId, String messageId);


	public void hostOpenOrCloseConferenceMixing(JSONObject jsonS, String clientId, String messageId);


	public void hostReinviteTerminal(JSONObject jsonS, String clientId, String action, String messageId);


	public void hostChange(JSONObject jsonS, String clientId, String messageId);


	public void recordConference(JSONObject jsonS, String clientId, String messageId);


	public void lockConference(JSONObject jsonS, String clientId, String messageId);


	public void addParticipants(JSONObject jsonS, String clientId, String messageId);

	
	public void setConferenceCaption(JSONObject jsonS, String clientId, String messageId);


	public void hostCancleRollCall(JSONObject jsonS, String clientId, String messageId);
	
	
	public JSONObject byTerminalIdGetDeptTree(JSONObject jsonS, String sn, String messageId);


	public void deptDownTerminal(JSONObject jsonS, String clientId, String messageId);


	public void conferenceViewLayout(JSONObject jsonS, String clientId, String messageId);


	public void leaveConference(JSONObject jsonS, String clientId, String messageId);


	public void chooseSee(JSONObject jsonS, String clientId, String messageId);


	public void conferenceList(JSONObject jsonS, String clientId, String messageId, List<BusiTemplateConference> busiTemplateConferencesList);


	public void conferencePresentationControl(JSONObject jsonS, String clientId, String messageId);
	

	public void conferenceCameraControl(JSONObject jsonS, String clientId);
	
	
	public void responseTerminal(String terminalTopic, String action, JSONObject jsonObject, String clientId, String messageId);

	public void responseTerminalByQOS(String terminalTopic, String action, JSONObject jObj, String messageId, QosEnum qos);

	public void conferenceDetails(JSONObject jsonS, String clientId, String messageId);


	public void getFuzzyQueryTerminal(JSONObject jsonS, String clientId, String messageId);


	public void conferenceDiscuss(JSONObject jsonS, String clientId, String messageId);


	public void backDefaultLayout(JSONObject jsonS, String clientId, String messageId);


	public void interactiveRaiseHand(JSONObject jsonS, String clientId, String messageId);


	public void terminalDialogue(JSONObject jsonS, String clientId, String messageId);


	public void askSipAccountCondition(JSONObject jsonS, String clientId, String messageId);


	public void unbindTerminalSipAccount(JSONObject jsonS, String clientId, String messageId);


	public void receiveDelAccountInfo(JSONObject jsonS, String clientId, String messageId);


	public void byUriInviteTerminal(JSONObject jsonS, String clientId, String messageId);


	public void fuzzyQueryConference(JSONObject jsonS, String clientId, String messageId);


	public void hostAcceptRaiseHand(JSONObject jsonS, String clientId, String messageId);


	public void hostRejectRaiseHand(JSONObject jsonS, String clientId, String messageId);

    public void terminalStatus(JSONObject jsonObject);

	public void liveTerminal(JSONObject jsonS, String clientId, String messageId);

	public List<TerminalLive> liveTerminalList(BaseConferenceContext conferenceContext);

	public void pushLive(JSONObject jsonS, String clientId, String messageId);

    public void recordingList(JSONObject jsonS, String clientId, String messageId);

	public void recordingInfo(JSONObject jsonS, String clientId, String messageId);

	boolean isOpenSecondaryStream(String conferenceId, long id, boolean isOpen);

    void getServerInfo(JSONObject jsonS, String clientId, String messageId);

	void sendServerInfo();

	void checkAppVersion(JSONObject jsonS, String clientId, String messageId);

	void sendConferenceInfo(JSONObject jsonS, String clientId, String messageId);

	boolean canControlConference(String clientId, String conferenceId);

	boolean canCreateConference(String clientId);

    void sendLiveListInfo(JSONObject jsonS, String clientId, String messageId);

	List<BusiLiveSetting> getBusiLiveSettingByDeptId(Long deptId);

	void changePresenter(JSONObject jsonS, String clientId, String messageId);

	void conferenceAttendeeList(JSONObject jsonS, String clientId, String messageId);

    void updateDefaultViewConfigInfo(JSONObject jsonS, String clientId, String messageId);

	Integer getLiveTerminalCount(String conferenceId);

	void reCall(JSONObject jsonS, String clientId, String messageId);

	int isInviteLiveTerminal(String mac, String conferenceId, String status);

	void pushInfoDisplay(JSONObject jsonS, String clientId, String messageId);
}
