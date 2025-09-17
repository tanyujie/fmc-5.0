package com.paradisecloud.fcm.mqtt.constant;

public interface MqttHealthConstant {
	
	//节点名称
	String NODE_NAME = "node";
	
	//EMQ X 使用的 Erlang/OTP 版本
	String OTP_RELEASE = "otp_release";
	
	//EMQ X 运行时间
	String UPTIME = "uptime";
	
	//节点状态
	String NODE_STATUS = "node_status";
	
	//可用的进程数量
	String PROCESS_AVAILABLE = "process_available";
	
	//已占用的进程数量
	String PROCESS_USED = "process_used";
	
	//	VM 已分配的系统内存
	String MEMORY_TOTAL = "memory_total";
	
	//VM 已占用的内存大小
	String MEMORY_USED = "memory_used";
	
	//1 分钟内的 CPU 平均负载
	String LOAD_ONE = "load1";
	
	//5 分钟内的 CPU 平均负载
	String LOAD_FIVE = "load5";
	
	//15 分钟内的 CPU 平均负载
	String LOAD_FIFTEEN = "load15";
	
	//操作系统的最大文件描述符限制
	String MAX_FDS = "max_fds";
	
	//当前连接数量
	String CONNECTIONS_COUNT = "connections.count";
	
	//连接数量的历史最大值
	String CONNECTIONS_MAX = "connections.max";
	
	//当前主题数量
	String TOPICS_COUNT = "topics.count";
	
	//主题数量的历史最大值
	String TOPICS_MAX = "topics.max";
	
	//当前保留消息数量
	String RETAINED_COUNT = "retained.count";
	
	//	保留消息的历史最大值
	String RETAINED_MAX = "retained.max";
	
	//当前订阅者数量
	String SUBSCRIBERS_COUNT = "subscribers.count";
	
	//订阅者数量的历史最大值
	String SUBSCRIBERS_MAX = "subscribers.max";
	
	//当前会话数量
	String SESSIONS_COUNT = "sessions.count";
	
	//会话数量的历史最大值
	String SESSIONS_MAX = "sessions.max";
	
	//当前共享订阅数量
	String SUBSCRIPTIONS_SHARED_COUNT = "subscriptions.shared.count";
	
	//共享订阅数量的历史最大值
	String SUBSCRIPTIONS_SHARED_MAX = "subscriptions.shared.max";
	
}
