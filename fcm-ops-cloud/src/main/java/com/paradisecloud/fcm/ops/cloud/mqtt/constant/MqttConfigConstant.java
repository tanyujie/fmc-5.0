package com.paradisecloud.fcm.ops.cloud.mqtt.constant;

public interface MqttConfigConstant {
	
	String HTTP = "http://";

	String HTTPS = "https://";
	
	String TCP = "tcp://";

	String SSL = "ssl://";

	//录制列表
	String RECORDING_LIST = "recordingList";

	//录制信息
	String RECORDING_INFO = "recordingInfo";

	//是否加入直播
	String ISLIVE = "isLive";
	
	//节点信息
	String NODE_INFO = "nodeInfo";
	
	//部门信息
	String DEPT_INFO = "deptInfo";
	
	//运行时统计
	String RUN_STATISTICS = "runStatistics";
	
	//cpu告警
	String CPU_ALARMS = "cpuAlarms";
	
	//节点监控指标
	String NODE_METRICS = "nodeMetrics";
	
	//终端id
	String CLIENTID = "clientId";

	//与会者id
	String ATTENDEE_ID = "attendeeId";
	
	//其他参会的终端的sn
	String ELSE_CLIENT_ID = "elseClientId";

	String TIME = "time";
	
	//终端序列号sn
	String SN = "sn";
	
	//会议列表
	String CONFERENCE_LIST = "conferenceList";
	
	//终端类型
	String TERMINAL_TYPE = "terminalType";

	//终端类型
	String TERMINAL_ID = "terminalId";
	
	//mqtt终端id
	String CLIENT_ID = "clientid";
	
	//终端发起的动作
	String ACTION = "action";
	
	//终端mac地址
	String MAC = "mac";
	
	//消息id
	String MESSAGE_ID = "msgId";
	
	//终端源 IP 地址
	String IPADDRESS = "ipaddress";
	
	//mqtt服务器地址
	String BROKERURL = "brokerUrl";
	
	//主题前缀
	String TOPIC_PREFIX = "terminal/";

	//fmc主题前缀
	String TOPIC_PREFIX_FMC = "fmc/";

	//会议主题前缀
	String TOPIC_PREFIX_CONFERENCE = "conference/";

	//电子门牌主题前缀
	String TOPIC_PREFIX_DOORPLATE = "doorplate/";

	//OPS主题前缀
	String TOPIC_PREFIX_OPS = "ops/";

	//OPS推送主题前缀
	String TOPIC_PREFIX_PLATFORM_OPS = "platform/ops/";
	
	//是否同意
	String ISAGREE = "isAgree";
	
	//主题
	String TOPIC = "topic";
	
	//消息服务质量
	String QOS = "qos";

	//终端版本号
	String versionCode = "versionCode";

	//终端版本名
	String versionName = "versionName";
	
	//消息体
	String PAYLOAD = "payload";
	
	//会议号码
	String CONFERENCENUM = "conferenceNum";

	// 会议名称
	String CONFERENCE_NAME = "conferenceName";

	//会议Id
	String CONFERENCE_ID = "conferenceId";

	String COSPACEID = "coSpaceId";
	
	//终端认证完成并成功接入系统后
	String CLIENT_CONNECT = "client_connect";
	
	String CLIENT_CONNACK = "client_connack";
	
	//终端下线
	String CLIENT_DISCONNECTED = "client_disconnected";
	
	//终端下线原因
	String TERMINAL_OFF_LINE_REASON = "reason";
	
	//mqtt节点名称
	String MQTT_NODE_NAME = "node";
	
	//斜杠
	String SLASH = "/";
	
	//用户名
	String USERNAME = "username";
	
	//密码
	String PASSWORD = "password";
	
	//会议信息
	String CONFERENCE_INFO = "conferenceInfo";
	
	//响应码
	String CODE = "code";
	
	//响应信息
	String MSG = "msg";
	
	//逗号
	String COMMA = ",";
	
	//与符号
	String AND_SYMBOL = "\\^";
	
	String  AT = "@";
	
	String  COLON = ":";
	
	//默认用户名
	String DEFAULT_USER_NAME = "admin";
	
	//默认密码
	String DEFAULT_PASSWORD = "P@rad1se";

	//默认用户名
	String DEFAULT_SSL_USER_NAME = "fmqadmin";

	//默认密码
	String DEFAULT_SSL_PASSWORD = "FMQ@parad1se";
	
	//api公共部分
	String API_AND_VERSION = "/api/v4";
	
	//mqtt节点状态
	String NODE_STATUS_NAME = "node_status";
	
	//mqtt健康运行
	String MQTT_SERVER_RUNNING = "Running";
	
	//mqtt状态 1、在线  2、离线
	String MQTT_STATUS_STR = "status";
	
	//根据data获取json数据
	String JSON_DATA_STR = "data";
	
	//终端名字
	String NAME = "name";
	
	//原因描述
	String DESCRIPTION = "description";
	
	//客户端连接服务器成功
	String CLIENT_CONNECTED_COUNT = "client.connected";
	
	//客户端断开服务器
	String CLIENT_DISCONNECTED_COUNT = "client.disconnected";
	
	//发送时由于消息队列满而被丢弃的 QoS 不为 0 的消息数量
	String DELIVERY_DROPPED_QUEUE_FULL = "delivery.dropped.queue_full";
	
	//连接上mqtt服务器的数量
	String CONNECTED_NUM = "connectedNum";
	
	//连接上mqtt服务器的数量
	String DISCONNECTED_NUM = "disconnectedNum";
	
	//连接上mqtt服务器的数量
	String DROPPED_QUEUE_FULL_NUM = "droppedQueueFullNum";
	
	//mqtt后台用户名
	String MQTT_BACK_NAME = "admin";
	
	//mqtt后台密码
	String MQTT_BACK_PASSWORD = "public";
	
	String MQTT_SERVER_PREFIX = "emqx";
	
	//mqtt服务名称
	String MQTT_NAME = "mqttName";
	
	//mqtt服务器重启命令
	String MQTT_RESTART_COMMOND = "./emqx restart";
	
	//服务器的用户名
	String SERVER_DEFAULT_USER_NAME = "root";
	
	//服务器的密码
	String SERVER_DEFAULT_PASSWORD = "P@rad1se";
	
	//服务严格的密钥检查
	String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
	
	//保留消息
	String RETAIN = "retain";
	
	//no
	String NO = "no";
	
	//执行exec
	String EXEC = "exec";
	
	String ID = "id";
	
	String IP = "ip";

	String CONNECT_IP = "connectIp";
	
	String MANAGEMENT_PORT = "managementPort";
	
	String TCP_PORT = "tcpPort";
	
	String EMQX_NODE_NAME = "nodeName";
	
	//freeSwitch下面的xml路径
	String XML_FILE_PATH = "/usr/local/freeswitch/conf/";
	
	String WSS_PEM_PATH = "/usr/local/freeswitch/certs/";
	
	String DEFAULT_PUBLIC_PATH = "/usr/local/freeswitch/conf/dialplan/";
	
	String VARS_XML = "vars.xml";
	
	String DISTRIBUTOR_CONF_XML = "autoload_configs/distributor.conf.xml";
	
	String VERTO_CONF_XML = "autoload_configs/verto.conf.xml";
	
	String DEFAULT_XML = "dialplan/default.xml";
	
	String PUBLIC_XML = "dialplan/public.xml";
	
	String AUTOLOAD_CONFIGS = "autoload_configs/";
	
	String RESTART_SUCCESS = "started successfully";
	
	String FCM_SYSTEM = "FCMSYSTEM";
	
//	String MQTT_CONFIG_PATH = "/etc/emqx/";
	
	String MQTT_CONFIG_PATH = "/home/emqx/etc/";
	
	String MQTT_CONFIG_PLUGINS = "/etc/emqx/plugins";
	
	//mqtt服务器默认启动路径
	String DEFAULT_MQTT_STARTUP_PATH = "/home/emqx/bin/";
	
	String EMQX_CONF = "emqx.conf";
	
	String EMQX_DASHBOARD_CONF = "emqx_dashboard.conf";
	
	String EMQX_MANAGEMENT_CONF = "emqx_management.conf";
	
	String LISTENER_TCP_EXTERNAL = "listener.tcp.external ";
	
	String MANAGEMENT_LISTENER_HTTP = "management.listener.http ";
	
	String DASHBOARD_LISTENER_HTTP = "dashboard.listener.http ";
	
	String NODE_NAME = "node.name ";
	
	String XOR = "$";
	
	/*#####################################################sip相关，登陆主题返回的信令信息######################################################################################*/
	String STUN_IP = "stunIp";
	
	String STUN_PORT = "stunPort";
	
	String TURN_IP = "turnIp";
	
	String TURN_PORT = "turnPort";
	
	String TRUN_USER_NAME = "trunUserName";
	
	String TURN_PASSWORD = "turnPassword";
	
	/*###############################################################################################################################*/
	
	//mqtt  1、在线
//	Integer MQTT_ONLINE_STATUS = 1;
//	
//	//mqtt  2、离线
//	Integer MQTT_OFFLINE_STATUS = 2;
	
	//默认tcp端口号
	Integer DEFAULT_TCP_PORT = 1883;
	Integer DEFAULT_SSL_TCP_PORT = 8883;
	
	//默认dashboard端口号
	Integer DEFAULT_DASHBOARD_PORT = 18083;
	
	//默认management端口号
	Integer DEFAULT_MANAGEMENT_PORT = 8081;
	
	//服务器默认端口号
	Integer DEFAULT_SERVER_PORT = 2233;
	
	//65535
	Integer SIX_F_F_T_FIVE = 65535;
	
	//100000
	Integer EXCEPTION_ONE_ZERO = 100000;
	
	//100003
	Integer EXCEPTION_ONE_TH = 100003;
	
	//100013
	Integer EXCEPTION_ONE_ONE_TH = 100013;
	
	//100016
	Integer EXCEPTION_ONE_ONE_SIX = 100016;
	
	//100444
	Integer EXCEPTION_ONE_F_F_F = 100016;
	
	//100334
	Integer EXCEPTION_ONE_th_th_F = 100334;
	
	//zero
    Integer ZERO = 0;
	
	//代表失败1
	Integer FAILED = 4;
	
	Long CONVERSION_1024 = (long) (1024*1024);

	//mqtt的版本
	Integer MQTT_VERSION = 4;
	
	//飞行窗口的值
	Integer MAX_INFLIGHT = 300000;
	
	//心跳时间
	Integer KEEP_ALIVE = 20;
	
	Integer SUCCESS = 1;
}
