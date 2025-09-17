package com.paradisecloud.fcm.mqtt.constant;

public interface MqttTipConstant {
	
	String IP_TIP = "IP格式不正确!";
	
	String TCP_PORT_TIP = "TCP的端口号不正确!";
	
	String IP_EXISTED_TIP = "新增的IP已经存在,请勿重复添加!";
	
	String ADD_INFO_TIP = "新增的FMQ的名称, IP不能为空!";
	
	String DASHBOARD_PORT_TIP = "DASHBOARD的端口号不正确!";
	
	String MANAGEMENT_PORT_TIP = "MANAGEMENTPORT的端口号不正确!";
	
	String MQTT_CLUSTER_TENANT_TIP = "该集群被租户使用，不能删除！";
	
	String MQTT_NODE_TENANT_TIP = "FMQ节点正被租户使用，不能删除！";
	
	String MQTT_NODE_ONLINE = "FMQ节点是在线状态，不能删除！";
	
	String MQTT_NODE_DEL_TIP = "FMQ节点作为备用已被其它关联，不能删除！";
	
	String TENANT_BIND_INFO_TIP = "该租户已绑定FMQ节点或集群，不能绑定多个!";
	
	String CLUSTER_SAME_NODE_TIP = "同一个FMQ集群中，同一个mqtt节点只能添加一次!";
	
	String MQTT_NODE_CLUSTER_TIP = "FMQ节点在集群中，不能删除，请先从集群中剔除，再删除！";
	
	String NO_EDIT_MQTT_CONFIG_INFO = "FMQ节点的IP, 配置的端口不支持修改，若是要修改，请先删除该FMQ节点信息，重新创建!";
	
	String MQTT_NODE_NAME = "FMQ节点名称不能为空!";
	
	String SERVER_IP_TIP = "服务器的ip不能为空!";
	
	String SERVER_NAME = "服务器名称不能为空!";
}
