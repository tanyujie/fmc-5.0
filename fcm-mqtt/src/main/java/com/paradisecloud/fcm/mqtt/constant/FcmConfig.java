package com.paradisecloud.fcm.mqtt.constant;

public interface FcmConfig {
	
	//用户默认密码
	String DEFAULT_PASSWORD = "default_password";
	
	//用户本地ip
	String USER_LOCAL_IP = "user_local_ip";
	
	//用户外部ip
	String USER_EXTERNAL_IP = "user_external_ip";
	
	//sip端口号
	String INTERNAL_SIP_PORT = "internal_sip_port";
	
	//用户默认密码
	String DEFAULT_PASSWORD_KEY = "defaultPassword";

	//公网ip
	String USER_PUBLIC_IP = "user_public_ip";
	
	//用户本地ip
	String USER_LOCAL_IP_KEY  = "userLocalIp";
	
	//用户外部ip
	String USER_EXTERNAL_IP_KEY  = "userExternalIp";
	
	//sip端口号
	String INTERNAL_SIP_PORT_KEY  = "internalSipPort";

	//公网ip的key
	String USER_PUBLIC_IP_KEY = "userPublicIp";
	
	
	//fme ip 
	String FME_IP = "fmeIp";
	
	String CONFIGURATION = "configuration";
	
	String PROFILES = "profiles";
	
	String PROFILE = "profile";
	
	String PARAM = "param";
	
	String PARAMS = "params";
	
	String SECURE = "secure";
	
	String PASSWORD = "password";
	
	String NAME = "name";
	
	String VALUE = "value";
	
	String BIND_LOCAL = "bind-local";
	
	String VERTOPORT_WS = "vertoPortWs";
	
	String VERTOPORT_WSS = "vertoPortWss";
	
	String LISTS = "lists";
	
	String LIST = "list";
	
	String NODE = "node";
	
	String INCLUDE = "include";
	
	String X_PRE_PROCESS = "X-PRE-PROCESS";
	
	String DATA = "data";
	
	String EQUAL = "=";
	
	String LOCAL_IP_V4 = "$${local_ip_v4}:";

	String WSS_PEM = "wss.pem";
	
	String DEFAULT_XML = "default.xml";
	
	String PUBLIC_XML = "public.xml";
	
	String DISTRIBUTOR_CONF_XML = "distributor.conf.xml";
	
	String CATALOG = "user.dir";
	
	String XML_PARENT = "/xmlFiles/";
	
	String TURN_SERVER_PATH = "/etc/turnserver.conf";
	
	String LISTENING_DEVICE = "listening-device";
	
	String LISTENING_PORT = "listening-port";
	
	String REALM = "realm";
	
	String MIN_PORT = "min-port";
	
	String MAX_PORT = "max-port";
	
	String USER = "user";
	
	String RELAY_IP = "relay-ip";
	
	String EXTERNAL_IP = "external-ip";
	
	String LISTENING_DEVICE_KEY = "listeningDevice";
	
	String LISTENING_PORT_KEY = "listeningPort";
	
	String REALM_KEY = "realm";
	
	String MIN_PORT_KEY = "minPort";
	
	String MAX_PORT_KEY = "maxPort";
	
	String USER_KEY = "user";
	
	String RELAY_IP_KEY = "relayIp";
	
	String EXTERNAL_IP_KEY = "externalIp";
	
	String NEW_LINE = "\n";
	
	String TURN_SERVER_CONF = "turnserver.conf";
	
	String RECORDER_DISABLE = "recorder disable";
	
	String RECORDER_NFS = "recorder nfs ";
	
	String RECORDER_ENABLE = "recorder enable";
	
	String RECORDER = "recorder";
	
	String FME_DEFAULT_USER_NAME = "admin";
	
	String FME_DEFAULT_PASSWORD = "P@rad1se";
	
	String FME_DEFAULT_PORT = "22";
	
	//重新启动freeSwitch命令
	String RESTART_FREESWITCH_SERVICE = "systemctl restart freeswitch.service"; 
	
	String FREE_SWITCH_USER = "/usr/local/freeswitch/conf/directory/default/";
	
//	String FREE_SWITCH_USER = "C:/Users/Administrator/Desktop/freeswitch/";
	
	String AT_ID = "id";
	
	String DOTTED_XML = ".xml";
	
	String SPLIT_LINE = "-";
	
	String ID = "id";
	
	String VARIABLES = "variables";
	
	String VM_PASSWORD = "vm-password";
	
	String ACCOUNTCODE = "accountcode";
	
	String EFFECTIVE_CALLER_ID_NAME = "effective_caller_id_name";
	
	String EFFECTIVE_CALLER_ID_NUMBER = "effective_caller_id_number";
	
	String VALUE_NAME = "value";
	
	String EXTENSION = "Extension";
	
	String TEMPLATE_1000_PATH = "/template/1001.xml";
	
	String ENCODING = "utf-8";
	
	String EMQX_STATUS = "./emqx_ctl status";
	
	String STARTED = "is started";
	
	String DOTTED = ".";
}
