package com.paradisecloud.fcm.terminal.fs.constant;

public interface FcmConfigConstant {
	
	//斜杠
	String SLASH = "/";
	
	String  COLON = ":";
	
	//默认密码
	String DEFAULT_PASSWORD = "P@rad1se";
	
	//根据data获取json数据
	String JSON_DATA_STR = "data";
	
	//服务器的用户名
	String SERVER_DEFAULT_USER_NAME = "root";
	
	//服务器的密码
	String SERVER_DEFAULT_PASSWORD = "P@rad1se";
	
	//服务严格的密钥检查
	String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
	
	//no
	String NO = "no";
	
	//执行exec
	String EXEC = "exec";
	
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
	
	Integer SUCCESS = 1;
	
	String MQTT_BACK_NAME = "admin";
	
	String MQTT_BACK_PASSWORD = "public";
	
	String HTTP = "http://";
	
	//api公共部分
	String API_AND_VERSION = "/api/v4";
}
