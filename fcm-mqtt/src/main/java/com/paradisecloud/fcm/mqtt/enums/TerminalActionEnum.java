package com.paradisecloud.fcm.mqtt.enums;

/**
 * @author zyz
 */
public enum TerminalActionEnum 
{
	//终端上线
	ON_LINE(0),
	
	//终端下线
	OFF_LINE(1),
	
	//绑定服务节点
	BIND_BROKER_NODE(2),
	
	//邀请入会
	JOIN_MEETING(3),
	
	//登陆主题
	LOGIN(4),
	
	//预约会议
	SCHEDULE_MEETING(5),
	
	//直播入会
	JOIN_LIVE(6),
	
	//会议发言
	CONFERENCE_SPEECH(7),
	
	//终端解绑
	DEL_BROKER_NODE(8),
	
	//修改终端的ip
	UPDATE_CONNECT_IP(9);
	
	private final int value;
	
	TerminalActionEnum(int value)
	{
		this.value = value;
	}
	
	public int value() 
	{
		return this.value;
	}
	
}
