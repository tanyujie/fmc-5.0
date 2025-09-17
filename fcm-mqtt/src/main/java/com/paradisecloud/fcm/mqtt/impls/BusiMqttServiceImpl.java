package com.paradisecloud.fcm.mqtt.impls;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.mapper.BusiMqttClusterMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMqttDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMqttMapper;
import com.paradisecloud.fcm.dao.model.BusiMqtt;
import com.paradisecloud.fcm.dao.model.BusiMqttClusterMap;
import com.paradisecloud.fcm.dao.model.BusiMqttDept;
import com.paradisecloud.fcm.mqtt.cache.MqttBridgeCache;
import com.paradisecloud.fcm.mqtt.cache.MqttDeptMappingCache;
import com.paradisecloud.fcm.mqtt.client.EmqClient;
import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.common.SshConnectionRemoteServer;
import com.paradisecloud.fcm.mqtt.constant.FcmConfig;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.MqttTipConstant;
import com.paradisecloud.fcm.mqtt.enums.MqttType;
import com.paradisecloud.fcm.mqtt.enums.QosEnum;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiMqttService;
import com.paradisecloud.fcm.mqtt.interfaces.IEmqxBrokerHealthService;
import com.paradisecloud.fcm.mqtt.model.MqttBridge;
import com.paradisecloud.fcm.mqtt.model.MqttStatusData;
import com.paradisecloud.fcm.mqtt.utils.MqttThreadPool;
import com.paradisecloud.fcm.terminal.fs.common.FileConvert;
import com.paradisecloud.fcm.terminal.fs.common.SshRemoteServerOperate;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import com.sinhy.exception.SystemException;
import com.sinhy.utils.RegExpUtils;

/**
 * mqtt配置信息业务层处理
 * 
 * @author zyz
 * @param <V>
 * @date 2021-07-21
 */
@Transactional
@Service
public class BusiMqttServiceImpl implements IBusiMqttService 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(BusiMqttServiceImpl.class);
	
    @Autowired
    private BusiMqttMapper busiMqttMapper;
    
    @Autowired
    private BusiMqttDeptMapper busiMqttDeptMapper;
    
    @Autowired
    private BusiMqttClusterMapMapper busiMqttClusterMapMapper;

    /**
     * 查询mqtt配置信息
     * 
     * @param id 
     * @return BusiMqtt
     */
    @Override
    public BusiMqtt selectBusiMqttById(Long id)
    {
        return busiMqttMapper.selectBusiMqttById(id);
    }

    /**
     * 查询mqtt配置信息列表
     * 
     * @param busiMqtt 
     * @return List<BusiMqtt>
     */
    @Override
    public List<BusiMqtt> selectBusiMqttList(BusiMqtt busiMqtt)
    {
        return busiMqttMapper.selectBusiMqttList(busiMqtt);
    }

    /**
     * 新增mqtt配置信息
     * 
     * @param busiMqtt
     * @return int
     */
    @Override
    public BusiMqtt insertBusiMqtt(BusiMqtt busiMqtt)
    {
    	if(StringUtils.isEmpty(busiMqtt.getIp()) || StringUtils.isEmpty(busiMqtt.getMqttName())) 
    	{
    		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ZERO, MqttTipConstant.ADD_INFO_TIP);
    	}
    	
    	if(StringUtils.isEmpty(busiMqtt.getNodeName())) 
    	{
    		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ZERO, MqttTipConstant.MQTT_NODE_NAME);
    	}
    	
    	if(!RegExpUtils.isIP(busiMqtt.getIp())) 
    	{
    		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ONE_TH, MqttTipConstant.IP_TIP);
    	}
    	
    	if(StringUtils.isEmpty(busiMqtt.getUserName())) 
    	{
    		busiMqtt.setUserName(MqttConfigConstant.DEFAULT_USER_NAME);
    	}
    	
    	if(StringUtils.isEmpty(busiMqtt.getPassword())) 
    	{
    		busiMqtt.setPassword(MqttConfigConstant.DEFAULT_PASSWORD);
    	}
    	
    	if(null == busiMqtt.getTcpPort()) 
    	{
			Integer mqttPort = ExternalConfigCache.getInstance().getMqttPort();
			if (mqttPort != null) {
				busiMqtt.setTcpPort(mqttPort);
			} else {
				busiMqtt.setTcpPort(MqttConfigConstant.DEFAULT_TCP_PORT);
			}
    	}
    	
    	if(null == busiMqtt.getDashboardPort()) 
    	{
    		busiMqtt.setDashboardPort(MqttConfigConstant.DEFAULT_DASHBOARD_PORT);
    	}
    	
    	if(null == busiMqtt.getManagementPort()) 
    	{
    		busiMqtt.setManagementPort(MqttConfigConstant.DEFAULT_MANAGEMENT_PORT);
    	}
    	
    	if(null == busiMqtt.getServerPort())
    	{
    		busiMqtt.setServerPort(MqttConfigConstant.DEFAULT_SERVER_PORT);
    	}
    	
    	if(StringUtils.isEmpty(busiMqtt.getMqttStartupPath()))
    	{
    		busiMqtt.setMqttStartupPath(MqttConfigConstant.DEFAULT_MQTT_STARTUP_PATH);
    	}
    	
    	if(StringUtils.isEmpty(busiMqtt.getServerUserName()))
    	{
    		busiMqtt.setServerUserName(MqttConfigConstant.SERVER_DEFAULT_USER_NAME);
    	}
    	
    	if(StringUtils.isEmpty(busiMqtt.getServerPassword()))
    	{
    		busiMqtt.setServerPassword(MqttConfigConstant.SERVER_DEFAULT_PASSWORD);
    	}
    	
    	if(StringUtils.isEmpty(busiMqtt.getMqttStartupPath()))
    	{
    		busiMqtt.setMqttStartupPath(MqttConfigConstant.DEFAULT_MQTT_STARTUP_PATH);
    	}
    	
    	
    	if(busiMqtt.getTcpPort() <= MqttConfigConstant.ZERO || busiMqtt.getTcpPort() > MqttConfigConstant.SIX_F_F_T_FIVE) 
    	{
    		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_TH, MqttTipConstant.TCP_PORT_TIP);
    	}
    	
    	if(busiMqtt.getTcpPort() <= MqttConfigConstant.ZERO || busiMqtt.getTcpPort() > MqttConfigConstant.SIX_F_F_T_FIVE) 
    	{
    		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_TH, MqttTipConstant.DASHBOARD_PORT_TIP);
    	}
    	
    	if(busiMqtt.getTcpPort() <= MqttConfigConstant.ZERO || busiMqtt.getTcpPort() > MqttConfigConstant.SIX_F_F_T_FIVE) 
    	{
    		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_TH, MqttTipConstant.MANAGEMENT_PORT_TIP);
    	}
    	
    	if (null != MqttBridgeCache.getInstance().getMqttBridgeByIp(busiMqtt.getIp())) 
    	{
    		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ZERO, MqttTipConstant.IP_EXISTED_TIP + "=====>" + busiMqtt.getIp());
		}
    	
    	BusiMqtt bsMqtt = new BusiMqtt();
    	bsMqtt.setIp(busiMqtt.getIp());
    	bsMqtt.setTcpPort(busiMqtt.getTcpPort());
    	bsMqtt.setDashboardPort(busiMqtt.getDashboardPort());
    	bsMqtt.setManagementPort(busiMqtt.getManagementPort());
    	
    	List<BusiMqtt> busiMqttList = busiMqttMapper.selectBusiMqttList(bsMqtt);
    	if (!ObjectUtils.isEmpty(busiMqttList)) 
    	{
    		BusiMqtt oldBusiMqtt = null;
    		oldBusiMqtt = busiMqttList.get(0);
    		busiMqtt.setId(oldBusiMqtt.getId());
    		busiMqtt.setUpdateTime(new Date());
		} 
    	else 
		{
    		busiMqtt.setStatus(2);
			busiMqtt.setCreateTime(new Date());
		}
    	
    	busiMqttMapper.insertBusiMqtt(busiMqtt);
    	this.initBusiMqtt(new MqttBridge(busiMqtt));
    	return busiMqtt;
    }

	/**
     * 修改mqtt配置信息
     * 
     * @param busiMqtt 
     * @return int
     */
	@Override
    public void updateBusiMqtt(BusiMqtt busiMqtt)
    {
    	MqttBridge mqttBridge = MqttBridgeCache.getInstance().getMqttBridgeById(busiMqtt.getId());
    	if (!ObjectUtils.isEmpty(mqttBridge)) 
    	{	
    		BusiMqtt buMqtt = mqttBridge.getBusiMqtt();
			if((StringUtils.isNotEmpty(busiMqtt.getIp()) && !busiMqtt.getIp().equals(mqttBridge.getBusiMqtt().getIp()))
					|| (null != busiMqtt.getDashboardPort() && busiMqtt.getDashboardPort() != mqttBridge.getBusiMqtt().getDashboardPort()) 
					|| (null != busiMqtt.getManagementPort() && busiMqtt.getManagementPort() != mqttBridge.getBusiMqtt().getManagementPort())) 
//					|| (null != busiMqtt.getStatus() && busiMqtt.getStatus() != mqttBridge.getBusiMqtt().getStatus())) 
			{
				throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ZERO, MqttTipConstant.NO_EDIT_MQTT_CONFIG_INFO);
			}
			
			buMqtt.setUpdateTime(new Date());
			buMqtt.setMqttName(StringUtils.isNotEmpty(busiMqtt.getMqttName()) ? busiMqtt.getMqttName() : buMqtt.getMqttName());

			buMqtt.setNodeName(busiMqtt.getNodeName());
			buMqtt.setDomainName(busiMqtt.getDomainName());
			if (busiMqtt.getTcpPort() != null) {
				buMqtt.setTcpPort(busiMqtt.getTcpPort());
			}
			if (busiMqtt.getUseSsl() != null) {
				buMqtt.setUseSsl(busiMqtt.getUseSsl());
			}
			
			MqttBridgeCache.getInstance().updateMqttBridge(mqttBridge);
			busiMqttMapper.updateBusiMqtt(buMqtt);
		}
    }

    /**
     * 批量删除mqtt配置信息
     * 
     * @param ids 
     * @return int
     */
    @Override
    public int deleteBusiMqttByIds(Long[] ids)
    {
        return busiMqttMapper.deleteBusiMqttByIds(ids);
    }

    /**
     * 删除mqtt配置信息
     * 
     * @param id 
     * @return int
     */
    @Override
    public void deleteBusiMqttById(Long id)
    {
    	if(null != id) 
    	{
    		MqttBridge mqttBridge = MqttBridgeCache.getInstance().getMqttBridgeById(id);
    		if(!ObjectUtils.isEmpty(mqttBridge)) 
    		{
    			BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();
    			if(null != busiMqtt && busiMqtt.getStatus() == 1) {
    				throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ONE_SIX, MqttTipConstant.MQTT_NODE_ONLINE);
    			}
    			
    			BusiMqttDept busiMqttDept = new BusiMqttDept();
    			busiMqttDept.setMqttId(id);
    			List<BusiMqttDept> busiMqttDeptList = busiMqttDeptMapper.selectBusiMqttDeptList(busiMqttDept);
    			if(!ObjectUtils.isEmpty(busiMqttDeptList)) 
    			{
    				throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ONE_SIX, MqttTipConstant.MQTT_NODE_TENANT_TIP);
    			}
    			
    			BusiMqttClusterMap busiMqttClusterMap = new BusiMqttClusterMap();
    			busiMqttClusterMap.setMqttId(id);
    			List<BusiMqttClusterMap> busiMqttClusterMapList = busiMqttClusterMapMapper.selectBusiMqttClusterMapList(busiMqttClusterMap);
    			if(!ObjectUtils.isEmpty(busiMqttClusterMapList)) 
    			{
    				throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ONE_SIX, MqttTipConstant.MQTT_NODE_CLUSTER_TIP);
    			}
    			
    			MqttBridge mBridge = MqttBridgeCache.getInstance().getMqttBridgeById(id);
    			MqttBridgeCache.getInstance().delMqttBridgeByIp(mBridge.getBusiMqtt().getIp());
    			MqttBridgeCache.getInstance().delMqttBridge(id);
    			busiMqttMapper.deleteBusiMqttById(id);
    		}
    	}
    }
    

	/**
	 *缓存BusiMqtt信息
	 */
	@Override
	public void initBusiMqtt(MqttBridge mqttBridge) {
		MqttBridgeCache.getInstance().updateMqttBridge(mqttBridge);
	}

	@Override
	public List<ModelBean> getMqttConfigurationInfo(HttpServletResponse response) throws UnknownHostException, IOException {
		List<ModelBean> modelBeans = new ArrayList<ModelBean>();
		List<MqttBridge> mqttBridges = MqttBridgeCache.getInstance().getMqttBridges();
		for (MqttBridge mqttBridge : mqttBridges) {
			ModelBean mdb = new ModelBean(mqttBridge.getBusiMqtt());
			BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();
			String nodeName = busiMqtt.getNodeName();
			mdb.remove("password");
			mdb.remove("dashboardPort");
			mdb.remove("userName");
			mdb.remove("serverPort");
			mdb.remove("serverPassword");
			mdb.remove("managementPort");
			mdb.remove("serverUserName");
			mdb.remove("serverUserName");
			
			mdb.put("bindTenantCount", MqttDeptMappingCache.getInstance().getBindMqttNodeCount(MqttType.SINGLE_NODE, busiMqtt.getId()));
			
			mdb.put("nodeName", nodeName);
			
			mdb.put("mqttStartupPath", busiMqtt.getMqttStartupPath());
			
			//ping ip不通，直接返回
			int tcpPort = busiMqtt.getTcpPort();
			if (busiMqtt.getUseSsl() != null && busiMqtt.getUseSsl() == 1) {
				tcpPort = MqttConfigConstant.DEFAULT_SSL_TCP_PORT;
			}
			Boolean isSuccess = ResponseTerminal.getInstance().pingIpAndPort(busiMqtt.getIp(), tcpPort);
			mdb.remove("tcpPort");
			if(isSuccess) 
			{
//				Map<String, Object> brokerHealthMap = emqxBrokerHealthService.emqxBrokerHealth(busiMqtt.getIp(), busiMqtt.getManagementPort(), nodeName, response);
				Map<String, Object> brokerHealthMap = mqttBridge.getBrokerHealthMap();
				if(null != brokerHealthMap && !brokerHealthMap.isEmpty()) 
				{
					String nodeInfo = (String)brokerHealthMap.get(MqttConfigConstant.NODE_INFO);
					String metricsInfo = (String)brokerHealthMap.get(MqttConfigConstant.NODE_METRICS);
					
					//获取mqtt服务器是否在线
					if(StringUtils.isNotEmpty(nodeInfo)) 
					{
						//处理json字符串
						MqttStatusData statusData = this.dealJsonStr(nodeInfo,MqttConfigConstant.NODE_STATUS_NAME);
						if(!ObjectUtils.isEmpty(statusData) && MqttConfigConstant.MQTT_SERVER_RUNNING.equals(statusData.getStatus()))
						{
							mdb.put(MqttConfigConstant.MQTT_STATUS_STR, TerminalOnlineStatus.ONLINE.getValue());
						}
						else 
						{
							mdb.put(MqttConfigConstant.MQTT_STATUS_STR, TerminalOnlineStatus.ONLINE.getValue());
						}
					}
					
					if(StringUtils.isNotEmpty(metricsInfo)) 
					{
						//处理json字符串
						MqttStatusData statusData1 = this.dealJsonStr(metricsInfo,MqttConfigConstant.NODE_METRICS);
						mdb.put(MqttConfigConstant.CONNECTED_NUM, statusData1.getConnected());
						mdb.put(MqttConfigConstant.DISCONNECTED_NUM, statusData1.getDisconnected());
						mdb.put(MqttConfigConstant.DROPPED_QUEUE_FULL_NUM, statusData1.getDropMessageNum());
					}
				}
			}
			else 
			{
				mdb.put(MqttConfigConstant.MQTT_STATUS_STR, TerminalOnlineStatus.OFFLINE.getValue());
				mdb.put(MqttConfigConstant.CONNECTED_NUM, MqttConfigConstant.ZERO);
				mdb.put(MqttConfigConstant.DISCONNECTED_NUM,  MqttConfigConstant.ZERO);
				mdb.put(MqttConfigConstant.DROPPED_QUEUE_FULL_NUM, MqttConfigConstant.ZERO);
				
//				throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "["+busiMqtt.getIp()+"]服务器上MQTT服务异常,请排查!");
			}
			modelBeans.add(mdb);
		}
		return modelBeans;
	}

	private MqttStatusData dealJsonStr(String dataInfo, String statusKey) {
		MqttStatusData mqttStatusData = new MqttStatusData();
		JSONObject jso=JSON.parseObject(dataInfo);
		JSONObject jsonObject = jso.getJSONObject(MqttConfigConstant.JSON_DATA_STR);
		if(statusKey.equals(MqttConfigConstant.NODE_STATUS_NAME))
		{
			String status = jsonObject.getString(statusKey);
			mqttStatusData.setStatus(status);
		}
		
		if(statusKey.equals(MqttConfigConstant.NODE_METRICS)) 
		{
			String connected = jsonObject.getString(MqttConfigConstant.CLIENT_CONNECTED_COUNT);
			String disconnect = jsonObject.getString(MqttConfigConstant.CLIENT_DISCONNECTED_COUNT);
			String dropMessage = jsonObject.getString(MqttConfigConstant.DELIVERY_DROPPED_QUEUE_FULL);
			mqttStatusData.setConnected(Integer.valueOf(connected));
			mqttStatusData.setDisconnected(Integer.valueOf(disconnect));
			mqttStatusData.setDropMessageNum(Integer.valueOf(dropMessage));
		}
		
		return mqttStatusData;
	}

	@Override
	public Boolean restartMqttListen(Long id) {
		Boolean isRestart = false;
		 MqttBridge mqttBridge = MqttBridgeCache.getInstance().getMqttBridgeById(id);
		    if(null != mqttBridge && null != mqttBridge.getBusiMqtt()) 
		    {
		    	 BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();

	    		 try {
	    			 
	    			 String restartIsSuccess = SshConnectionRemoteServer.execCommand("cd " + busiMqtt.getMqttStartupPath() + " && " + FcmConfig.EMQX_STATUS);
					 if(StringUtils.isNotEmpty(restartIsSuccess)) 
					 {
						 if(restartIsSuccess.contains(FcmConfig.STARTED)) {
							 isRestart = true;
							 SshConnectionRemoteServer.closeSession();
						 }
					 }else {
						throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "请检查FMQ服务是否启动!");
					 }
				} catch (Exception e) {
					throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "请检查FMQ服务是否启动!");
				}
		    	 
		    }
		
		return isRestart;
	}

	@Override
	public Boolean nameIsRepeat(BusiMqtt busiMqtt) {
		Boolean isRepeat = false;
		List<BusiMqtt> mqtts = busiMqttMapper.selectBusiMqttList(busiMqtt);
		if(null != mqtts && mqtts.size() > 0) {
			isRepeat = true;
		}
		return isRepeat;
	}

	@Override
	public void publishTopicMsg(String terminalTopic, String clientId, String msg, boolean flag) {
		if(!MqttThreadPool.isShutDown()) 
		{
			MqttThreadPool.exec(new Runnable() 
			{
				
				@Override
				public void run() 
				{
					if(StringUtils.isNotEmpty(terminalTopic)) 
					{
						String[] topicArr = terminalTopic.split(MqttConfigConstant.SLASH);
						if(topicArr.length >= 1) 
						{
							try {
								EmqClient emqClient = (EmqClient) SpringContextUtil.getBean("emqClient");
								emqClient.publish(terminalTopic, msg, QosEnum.QOS2, flag);
							} catch (Exception e) {
								LOGGER.error("发布主题失败!" , e);
							}
						}
					}
				}
			});
		}
	}
}
