package com.paradisecloud.fcm.mqtt.constant;

public interface TerminalTopic {
	
	//获取sip账号
	String GET_SIP_ACCOUNT = "getSipAccount";
	
	//修改sip账号
	String UPDATE_SIP_ACCOUNT = "updateSipAccount";
	
	//sip注册
	String SIP_REGISTER = "sipRegister";
	
	//创建会议
	String CREATE_CONFERENCE = "createConference";
	
	//会议详情
	String CONFERENCE_DETAILS = "conferenceDetails";
	
	//终端重启
	String TERMINAL_REBOOT = "reboot";
	
	//终端关机
	String TERMINAL_POWEROFF = "powerOff";
	
	//终端恢复出厂设置
	String TERMINAL_RESET = "reset"; 
	
	//修改设备信息
	String MODIFY_TERMINAL_INFO = "modifyTerminalInfo";
	
	//打开双流
	String OPEN_PRESENTATION = "openPresentation";
	
	//关闭双流
	String CLOSE_PRESENTATION = "closePresentation";
	
	//摄像头控制
	String CAMERA_CONTROL = "cameraControl";
	
	//终端系统信息
	String TERMINAL_SYS_INFO = "terminalSysInfo";
	
	//终端升级
	String TERMINAL_UPGRADE = "terminalUpgrade";
	
	//终端采集日志
	String TERMINAL_COLLECT_LOGS = "collectLogs";
	
	//邀请入会
	String INVITE_CONFERENCE = "inviteConference";
	
	//结束会议
	String END_CONFERENCE = "endConference";
	
	//移除与会者
	String KICK_PARTICIPANT = "kickParticipant";
	
	//终端主持人点名
	String ROLL_CALL = "rollCall";
	
	//终端主持人点名
	String CANCEL_ROLL_CALL = "cancelRollCall";
	
	//主持人对终端开麦
	String OPEN_MIXING = "openMixing";
	
	//主持人对终端关麦
	String CLOSE_MIXING = "closeMixing";
	
	//延长会议时间
	String EXTEND_MINUTES = "extendMinutes";
	
	//主持人对会场开麦闭音
	String CONFERENCE_MIXING = "conferenceMixing";
	
	//主持人对终端重呼
	String REINVITE_TERMINAL = "reinviteTerminal";
	
	//主持人转换
	String CHANGE_HOST = "changeHost";
	
	//录制会议
	String RECORD_CONFERENCE = "recordConference";
	
	//锁定会议
	String LOCK_CONFERENCE = "lockConference";
	
	//添加参会人
	String ADD_PARTICIPANTS = "addParticipants";
	
	//一键重邀未在会的人
	String QUICK_INVITE_PARTICIPANTS = "quickInviteParticipants";
	
	//设置字幕横幅
	String SET_CONFERENCE_CAPTION = "setConferenceCaption";
	
	//修改mqtt配置信息
	String UPDATE_MQTT_CONFIG = "updateMqttConfig";
	
	//通讯录
	String ADDRESS_BOOK = "addressBook";
	
	//查询部门下的终端
	String DEPT_DOWN_TERMINAL = "deptDownTerminal";
	
	//会议显示布局
	String CONFERENCE_LAYOUT = "conferenceLayout";
	
	//退出会议
	String LEAVE_CONFERENCE = "leaveConference";
	
	//选看
	String CHOOSE_SEE = "chooseSee";

	//会议列表（新）
	String CONFERENCE_LIST_NEW = "conferenceListNew";

	//会议室辅流控制
	String ALLOW_ALL_PRESENTATION = "allowAllPresentation";
	
	//横幅
	String SET_BANNER = "setBanner";
	
	//滚动字幕
	String SCROLL_BANNER = "scrollBanner";
	
	//模糊查询终端
	String FUZZY_QUERY_TERMINAL = "fuzzyQueryTerminal";
	
	//会场讨论
	String DISCUSS = "discuss";
	
	//返回默认布局
	String BACK_DEFAULT_LAYOUT = "backDefaultLayout";
	
	//互动举手
	String INTERACTIVE_RAISE_HAND = "interactiveRaiseHand";
	
	//直播入会
	String JOIN_LIVE = "joinLive";
	
	//推送直播
	String PUSH_LIVE = "pushLive";
	
	//对话
	String DIALOGUE = "dialogue";
	
	//询问sip账号绑定情况
	String ASK_SIP_ACCOUNT_CONDITION = "askSipAccountCondition";
	
	//解绑sip账号
	String UNBIND_SIP_ACCOUNT = "unbindSipAccount";
	
	//管理员删除终端账号信息
	String ADMIN_DELETE_TERMINAL = "adminDeleteTerminal";
	
	//接收到管理员删除终端账号信息
	String RECEIVE_DEL_ACCOUNT_INFO = "receiveDelAccountInfo";
	
	//接收到管理员删除终端账号信息
	String URI_INVITE = "uriInvite";
	
	//参会人的会议信息
	String ATTENDEE_CONFERENCE_INFO = "attendeeConferenceInfo";
	
	//模糊查询会议信息
	String FUZZY_QUERY_CONFERENCE = "fuzzyQueryConference";
	
	//同意举手
	String ACCEPT_RAISE_HAND = "acceptRaiseHand";
	
	//拒绝举手
	String REJECT_RAISE_HAND = "rejectRaiseHand";

	//终端状态异常处理
	String TERMINAL_STATUS = "terminalStatus";

	//直播列表
	String LIVE_TERMINAL = "liveTerminal";

	//录制列表
	String RECORDING_LIST = "recordingList";

	//录制信息
	String RECORDING_INFO = "recordingInfo";

	//终端开启或关闭辅流presentation
	String OPEN_SECONDARYSTREAM = "openSecondaryStream";

	//系统消息
	String SERVER_INFO = "serverInfo";

	//终端检查版本
	String CHECK_APP_VERSION = "checkAppVersion";

	//会议信息
	String CONFERENCE_INFO = "conferenceInfo";

	//直播列表
	String LIVE_LIST = "liveList";

	// 设置会议的主持人
	String CHANGE_PRESENTER = "changePresenter";

	// 与合作列表
	String CONFERENCE_ATTENDEE_LIST = "conferenceAttendeeList";

	// 修改会议布局
	String UPDATE_DEFAULTVIEWCONFIGINFO = "updateDefaultViewConfigInfo";

	// 重呼
	String RE_CALL = "reCall";

	// 进入会议
	String JOIN_CONFERENCE = "joinConference";

	// 离开会议
	String LEFT_CONFERENCE = "leftConference";

	// 会议即将结束
	String CONFERENCE_COMING_TO_END = "conferenceComingToEnd";

	// 轮询到终端
	String POLLING_ATTEND = "pollingAttend";

	// 信息展示
	String INFO_DISPLAY = "infoDisplay";
}


