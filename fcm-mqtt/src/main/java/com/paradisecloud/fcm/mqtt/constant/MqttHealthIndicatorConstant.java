package com.paradisecloud.fcm.mqtt.constant;

public interface MqttHealthIndicatorConstant {
	
	//节点名称
	String NODE_NAME = "nodeName";
	
	//EMQ X 使用的 Erlang/OTP 版本
	String OTP_RELEASE = "otpRelease";
	
	//EMQ X 运行时间
	String UPTIME = "upTime";
	
	//节点状态
	String NODE_STATUS = "nodeStatus";
	
	//可用的进程数量
	String PROCESS_AVAILABLE = "processAvailable";
	
	//已占用的进程数量
	String PROCESS_USED = "processUsed";
	
	//	VM 已分配的系统内存
	String MEMORY_TOTAL = "memoryTotal";
	
	//VM 已占用的内存大小
	String MEMORY_USED = "memoryUsed";
	
	//1 分钟内的 CPU 平均负载
	String LOAD_ONE = "loadOne";
	
	//5 分钟内的 CPU 平均负载
	String LOAD_FIVE = "loadFive";
	
	//15 分钟内的 CPU 平均负载
	String LOAD_FIFTEEN = "loadFifteen";
	
	//操作系统的最大文件描述符限制
	String MAX_FDS = "maxFds";
	
	//当前连接数量
	String CONNECTIONS_COUNT = "connectionsCount";
	
	//连接数量的历史最大值
	String CONNECTIONS_MAX = "connectionsMax";
	
	//当前主题数量
	String TOPICS_COUNT = "topicsCount";
	
	//主题数量的历史最大值
	String TOPICS_MAX = "topicsMax";
	
	//当前保留消息数量
	String RETAINED_COUNT = "retainedCount";
	
	//	保留消息的历史最大值
	String RETAINED_MAX = "retainedMax";
	
	//当前订阅者数量
	String SUBSCRIBERS_COUNT = "subscribersCount";
	
	//订阅者数量的历史最大值
	String SUBSCRIBERS_MAX = "subscribersMax";
	
	//当前会话数量
	String SESSIONS_COUNT = "sessionsCount";
	
	//会话数量的历史最大值
	String SESSIONS_MAX = "sessionsMax";
	
	//当前共享订阅数量
	String SUBSCRIPTIONS_SHARED_COUNT = "subscriptionsSharedCount";
	
	//共享订阅数量的历史最大值
	String SUBSCRIPTIONS_SHARED_MAX = "subscriptionsSharedMax";
	
	//表示无值
	String HORIZONTAL_LINE = "--";
	
}
