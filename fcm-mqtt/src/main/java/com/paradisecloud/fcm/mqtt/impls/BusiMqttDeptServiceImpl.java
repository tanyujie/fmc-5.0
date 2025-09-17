package com.paradisecloud.fcm.mqtt.impls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiMqttClusterMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMqttDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiMqtt;
import com.paradisecloud.fcm.dao.model.BusiMqttCluster;
import com.paradisecloud.fcm.dao.model.BusiMqttClusterMap;
import com.paradisecloud.fcm.dao.model.BusiMqttDept;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.mqtt.cache.MqttBridgeCache;
import com.paradisecloud.fcm.mqtt.cache.MqttClusterCache;
import com.paradisecloud.fcm.mqtt.cache.MqttDeptMappingCache;
import com.paradisecloud.fcm.mqtt.client.EmqClient;
import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.MqttTipConstant;
import com.paradisecloud.fcm.mqtt.enums.MqttType;
import com.paradisecloud.fcm.mqtt.enums.QosEnum;
import com.paradisecloud.fcm.mqtt.enums.TerminalActionEnum;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiMqttDeptService;
import com.paradisecloud.fcm.mqtt.model.MqttBridge;
import com.paradisecloud.fcm.mqtt.model.MqttBridgeCluster;
import com.paradisecloud.fcm.mqtt.model.MqttBridgeCollection;
import com.paradisecloud.fcm.mqtt.model.MqttPublishData;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;

/**
   * 租户分配mqtt资源业务层处理
 * 
 * @author zyz
 * @date 2021-07-21
 */
@Transactional
@Service
public class BusiMqttDeptServiceImpl implements IBusiMqttDeptService 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(BusiMqttDeptServiceImpl.class);
	
    @Autowired
    private BusiMqttDeptMapper busiMqttDeptMapper;
    
    @Autowired
    private BusiTerminalMapper busiTerminalMapper;
    
    @Autowired
    private BusiMqttClusterMapMapper busiMqttClusterMapMapper;
    
    private HttpRequester httpRequester = HttpObjectCreator.getInstance().createHttpRequester(MqttConfigConstant.MQTT_BACK_NAME, MqttConfigConstant.MQTT_BACK_PASSWORD, false);

    /**
             * 查询租户分配mqtt资源
     * 
     * @param id 
     * @return BusiMqttDept
     */
    @Override
    public BusiMqttDept selectBusiMqttDeptById(Long id)
    {
        return busiMqttDeptMapper.selectBusiMqttDeptById(id);
    }

    /**
             * 查询租户分配mqtt资源列表
     * 
     * @param busiMqttDept 
     * @return List<BusiMqttDept>
     */
    @Override
    public List<ModelBean> selectBusiMqttDeptList(BusiMqttDept busiMqttDept)
    {
    	List<BusiMqttDept> busiMqttDeptList = busiMqttDeptMapper.selectBusiMqttDeptList(busiMqttDept);
    	List<ModelBean> mdb = new ArrayList<>();
    	for (BusiMqttDept mqttDept : busiMqttDeptList) {
    		mdb.add(conversionModelBean(mqttDept));
		}
        return mdb;
    }

    public ModelBean conversionModelBean(BusiMqttDept mqttDept) {
    	
    	ModelBean mlb = new ModelBean(mqttDept);
    	mlb.put("mqttStatus", TerminalOnlineStatus.OFFLINE.getValue());
    	
    	//返回mqtt服务器的运行状态
    	mlb = this.mqttServerStatus(mlb, mqttDept);
    	
    	mlb.put("deptName", SysDeptCache.getInstance().get(mqttDept.getDeptId()).getDeptName());
    	mlb.put("mqttTypeName", MqttType.convert(mqttDept.getMqttType()).getName());
        StringBuilder mqttBuilder = new StringBuilder();
        if (MqttType.convert(mqttDept.getMqttType()) == MqttType.CLUSTER)
        {
            BusiMqttCluster busiMqttCluster = MqttClusterCache.getInstance().get(mqttDept.getMqttId());
            mqttBuilder.append("【").append(busiMqttCluster.getMqttClusterName()).append("】");
        }
        else
        {
            MqttBridge mqttBridge = MqttBridgeCache.getInstance().get(mqttDept.getMqttId());
            mqttBuilder.append("【").append(null != mqttBridge ? mqttBridge.getBusiMqtt().getMqttName() : "暂无").append("】");
        }
    	
        MqttBridgeCollection mqttBridgeCollection = MqttBridgeCache.getInstance().getDeptAboutMqttBridge(mqttDept.getDeptId());
        if(null == mqttBridgeCollection) 
        {
        	mqttBuilder.append("-").append("当前无可用的FMQ节点信息");
        	mlb.put("mqtts", new ArrayList<>());
        }
        else 
        {
        	mlb.put("usefulMqttBridge", mqttBridgeCollection);
        	mqttBuilder.append("FMQ[");
             
            List<String> mqtts = new ArrayList<String>();
            StringBuilder mqttIpBuilder = new StringBuilder();
            mqttBridgeCollection.getMqttBridges().forEach((mqttBridge) -> {
                 if (!ObjectUtils.isEmpty(mqttIpBuilder))
                 {
                	 mqttIpBuilder.append(", ");
                 }
                 mqttIpBuilder.append(mqttBridge.getBusiMqtt().getIp());
                 
                 mqtts.add(mqttBridge.getBusiMqtt().getIp());
             });
            mqttBuilder.append(mqttIpBuilder);
            mqttBuilder.append("]");
            mlb.put("mqtts", mqtts);
         }
        
        mlb.put("mqttInfo", mqttBuilder.toString());
		return mlb;
	}

	/**
	 * mqtt服务器的运行状态
	 * @param mlb
	 * @param mqttDept
	 * @return
	 */
	private ModelBean mqttServerStatus(ModelBean mlb, BusiMqttDept mqttDept) {
		 if (MqttType.convert(mqttDept.getMqttType()) == MqttType.CLUSTER)
	        {
			 MqttBridgeCollection mqttBridgeCollection = MqttBridgeCache.getInstance().getDeptAboutMqttBridge(mqttDept.getDeptId());
		        if(null == mqttBridgeCollection) 
		        {
		        	mlb.put("mqttStatus", TerminalOnlineStatus.OFFLINE.getValue());
		        }
		        else 
		        {
		        	List<MqttBridge> mqttBridges = mqttBridgeCollection.getMqttBridges();
		        	if(null != mqttBridges && mqttBridges.size() > 0) {
		        		for (MqttBridge mqttBridge : mqttBridges) {
		        			BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();
		        			
		        			//获取mqtt的运行状态
		        			mlb = this.getMqttStatus(busiMqtt, mlb);
						}
		        	}
		        }
	        }
	        else
	        {
	            MqttBridge mqttBridge = MqttBridgeCache.getInstance().get(mqttDept.getMqttId());
	            
	           //获取mqtt的运行状态
    		   mlb = this.getMqttStatus(mqttBridge.getBusiMqtt(), mlb);
	        }
		 
		return mlb;
	}

	/**
	 * 获取mqtt的运行状态
	 * @param busiMqtt
	 * @param mlb
	 * @return
	 */
	private ModelBean getMqttStatus(BusiMqtt busiMqtt, ModelBean mlb) {
		int tcpPort = busiMqtt.getTcpPort();
		if (busiMqtt.getUseSsl() != null && busiMqtt.getUseSsl() == 1) {
			tcpPort = MqttConfigConstant.DEFAULT_SSL_TCP_PORT;
		}
		Boolean isSuccess = ResponseTerminal.getInstance().pingIpAndPort(busiMqtt.getIp(), tcpPort);
		if(isSuccess) 
		{
			mlb.put("mqttStatus", TerminalOnlineStatus.ONLINE.getValue());
		}
		return mlb;
	}

	/**
            * 新增租户分配mqtt资源
     * 
     * @param busiMqttDept 
     * @return int
     */
    @Override
    public int insertBusiMqttDept(BusiMqttDept busiMqttDept)
    {
        busiMqttDept.setCreateTime(new Date());
        if(MqttDeptMappingCache.getInstance().containsKey(busiMqttDept.getDeptId())) 
        {
        	throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, MqttTipConstant.TENANT_BIND_INFO_TIP);
        }
        
        //会控连接MQTT服务器
//    	fcmConnecntMqttServer(busiMqttDept);
        int d = busiMqttDeptMapper.insertBusiMqttDept(busiMqttDept);
        if(d > 0) 
        {	
        	//终端绑定mqtt服务，并发布主题
//        	this.terminalBindMqttServerData(busiMqttDept);
        	
        	MqttDeptMappingCache.getInstance().put(busiMqttDept.getDeptId(), busiMqttDept);
        }
        return d;
    }

	private void fcmConnecntMqttServer(BusiMqttDept busiMqttDept) {
		String ip = null;
		Integer port = null;
		String password = null;
		String userName = null;
		BusiMqttDept mqttDept = new BusiMqttDept();
		mqttDept.setMqttId(busiMqttDept.getMqttId());
		mqttDept.setMqttType(busiMqttDept.getMqttType());
		List<BusiMqttDept> depts = busiMqttDeptMapper.selectBusiMqttDeptList(mqttDept);
		Integer nodeCount = depts.size();
		
    	if(busiMqttDept.getMqttType() == 1) {
    		MqttBridge mqttBridge = MqttBridgeCache.getInstance().get(busiMqttDept.getMqttId());
			ip = mqttBridge.getBusiMqtt().getIp();
			port = mqttBridge.getBusiMqtt().getManagementPort();
			password = mqttBridge.getBusiMqtt().getPassword();
			userName = mqttBridge.getBusiMqtt().getUserName();
			
    	}else {
			 MqttBridgeCluster mqttBridgeCluster = MqttBridgeCache.getInstance().getByMqttClusterId(busiMqttDept.getMqttId());
			 MqttBridgeCluster bridgeCluster = MqttBridgeCache.getInstance().getByMqttClusterId(mqttBridgeCluster.getBusiMqttCluster().getId());
			 BusiMqttClusterMap busiMqttClusterMap = new BusiMqttClusterMap();
			 busiMqttClusterMap.setClusterId(bridgeCluster.getBusiMqttCluster().getId());
			 List<BusiMqttClusterMap> clusterMapList = busiMqttClusterMapMapper.selectBusiMqttClusterMapList(busiMqttClusterMap);
			 if(null != clusterMapList && nodeCount > 0) {
				 Optional<BusiMqttClusterMap> max = clusterMapList.stream().max(Comparator.comparingInt(BusiMqttClusterMap::getWeight));
				 BusiMqttClusterMap mqttClusterMap = max.get();
				 MqttBridge bridge = MqttBridgeCache.getInstance().get(mqttClusterMap.getMqttId());
				 ip = bridge.getBusiMqtt().getIp();
				 port = bridge.getBusiMqtt().getManagementPort();
				 password = bridge.getBusiMqtt().getPassword();
				 userName = bridge.getBusiMqtt().getUserName();
			 }
    	}
		
		String fcmClientId = "FCMSYSTEM";
		String httpUrl = MqttConfigConstant.HTTP + ip + MqttConfigConstant.COLON + port + MqttConfigConstant.API_AND_VERSION + "/clients/" + fcmClientId;
		
		//判断指定节点否连接再mqtt上
    	Boolean isExist = ResponseTerminal.getInstance().checkNodeConnectMqtt(httpUrl);
    	if(!isExist){
    		Boolean isConnect = ResponseTerminal.getInstance().connectMqttServer(userName, password, fcmClientId, ip);
    		if(isConnect) {
    			EmqClient emqClient = (EmqClient) SpringContextUtil.getBean("emqClient");
    			emqClient.subscribe("platform/#", QosEnum.QOS2);
    		}
    	}else {
    		EmqClient emqClient = (EmqClient) SpringContextUtil.getBean("emqClient");
			emqClient.subscribe("platform/#", QosEnum.QOS2);
		}
	}

	/**
     * 终端绑定mqtt服务，并发布主题
     * @param busiMqttDept
     */
    private void terminalBindMqttServerData(BusiMqttDept busiMqttDept) 
    {
    	//对比ip,单节点相同或者集群包含，就不用发布主题
    	List<String> busiMqtts = this.ComparedIp(busiMqttDept);
    	BusiTerminal busiTerminal = new BusiTerminal();
		busiTerminal.setDeptId(busiMqttDept.getDeptId());
		busiTerminal.setType(TerminalType.FMQ.getId());
		List<BusiTerminal> busiTerminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
		if(null != busiTerminalList && busiTerminalList.size() > 0)
		{
			for (BusiTerminal buTerminal : busiTerminalList) 
			{
				if(StringUtils.isNotEmpty(buTerminal.getIp()) && StringUtils.isNotEmpty(buTerminal.getSn()))
				{
					if(!busiMqtts.contains(buTerminal.getIp())) 
					{
						//租户绑定mqtt服务，发布修改ip主题，通知终端
						this.updateTopicIpPublishTerminal(buTerminal, busiMqtts.get(0));
					}
				}
			}
		}
	}

	/**
	 * 对比ip,单节点相同或者集群包含，就不用发布主题
	 * @param busiMqttDept
	 * @return
	 */
	private List<String> ComparedIp(BusiMqttDept busiMqttDept) {
		List<String> busiList = new ArrayList<String>();
		if(MqttType.CLUSTER.getValue() == busiMqttDept.getMqttType()) 
		{
			MqttBridgeCollection deptAboutMqttBridge = MqttBridgeCache.getInstance().getDeptAboutMqttBridge(busiMqttDept.getDeptId());
			if(null != deptAboutMqttBridge && null != deptAboutMqttBridge.getMqttBridges() && deptAboutMqttBridge.getMqttBridges().size() > 0)
			{
				List<MqttBridge> mqttBridges = deptAboutMqttBridge.getMqttBridges();
				if(null != mqttBridges && mqttBridges.size() > 0) 
				{
					for (MqttBridge mqttBridge : mqttBridges) {
						BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();
						if(null != busiMqtt) 
						{
							busiList.add(busiMqtt.getIp());
						}
					}
				}
			}
		}
		else 
		{
			MqttBridge mqttBridge = MqttBridgeCache.getInstance().getMqttBridgeById(busiMqttDept.getMqttId());
			if(null != mqttBridge && null != mqttBridge.getBusiMqtt()) 
			{
				busiList.add(mqttBridge.getBusiMqtt().getIp());
			}
		}
		return busiList;
	}

	/**
	 * 租户绑定mqtt服务，发布修改ip主题，通知终端
	 * @param buTerminal
	 * @param ipAddr 
	 * @throws IOException 
	 */
	private void updateTopicIpPublishTerminal(BusiTerminal buTerminal, String ipAddr) 
	{
		Map<String, String> params = new HashMap<String, String>();
		MqttBridge mqttBridge = MqttBridgeCache.getInstance().getMqttBridgeByIp(buTerminal.getIp());
		if(null != mqttBridge && null != mqttBridge.getBusiMqtt()) 
		{
			BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();
			MqttPublishData publishData = new MqttPublishData();
			String userName = busiMqtt.getUserName();
			String password = busiMqtt.getPassword();
			String terminalId = buTerminal.getSn() + MqttConfigConstant.XOR + buTerminal.getName();
			
			publishData.setClientId(buTerminal.getSn());
			publishData.setIp(ipAddr);
			publishData.setAction(String.valueOf(TerminalActionEnum.UPDATE_CONNECT_IP.value()));
			publishData.setUserName(userName);
			publishData.setPassword(password);
			
			String topicSp = MqttConfigConstant.TOPIC_PREFIX + TerminalActionEnum.UPDATE_CONNECT_IP.value() + MqttConfigConstant.SLASH + buTerminal.getSn();
			params.put(MqttConfigConstant.CLIENTID, buTerminal.getSn());
			params.put(MqttConfigConstant.TOPIC, topicSp);
			params.put(MqttConfigConstant.QOS, String.valueOf(QosEnum.QOS2.value()));
//			params.put(MqttConfigConstant.PAYLOAD,gson.toJson(publishData));
//			params.put(MqttConfigConstant.RETAIN, "true");
			
			//判断该终端是否连接服务器
			Boolean connectMqtt = this.currentTerminalConnectMqtt(buTerminal, busiMqtt, terminalId);
			if(connectMqtt) {
//				 terminalActionService.publishTopicMsg(topicSp, buTerminal.getSn(), gson.toJson(publishData), true);
			}else {
				Boolean isConnectFlg = ResponseTerminal.getInstance().connectMqttServer(userName, password, terminalId, buTerminal.getIp());
				if (isConnectFlg) 
				{
//					terminalActionService.publishTopicMsg(topicSp, buTerminal.getSn(), gson.toJson(publishData), true);
				}
			}
		}
		
	}

	/**
	 * 判断该终端是否连接服务器
	 * @param buTerminal
	 * @param busiMqtt 
	 * @param terminalId 
	 */
	private Boolean currentTerminalConnectMqtt(BusiTerminal buTerminal, BusiMqtt busiMqtt, String terminalId) {
		Boolean isConnectFlg = false;
		List<String> arrayList = new ArrayList<String>();
		String httpUrl = MqttConfigConstant.HTTP + buTerminal.getIp() + MqttConfigConstant.COLON + busiMqtt.getManagementPort() + MqttConfigConstant.API_AND_VERSION;
		String connUrl = httpUrl + "/clients/" + terminalId;
		httpRequester.get(connUrl, new HttpResponseProcessorAdapter() {
			
			@Override
			public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
				try {
                    
					arrayList.clear();
                    String nodeData = getBodyContent(httpResponse);
                    if(StringUtils.isNotEmpty(nodeData)) 
    				{
                    	JSONObject jsonObject = (JSONObject) JSONObject.parse(nodeData);
                    	String data = jsonObject.getString(MqttConfigConstant.JSON_DATA_STR);
    					JSONArray array = (JSONArray) JSONArray.parse(data);
    					if(null != array && array.size() > 0) 
    					{
    						arrayList.add("1");
    					}
    	        		 
    				}
				} catch (Exception e) {
					throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "终端连接FMQ服务异常!");
				}
			}
			
			@Override
			public void fail(HttpResponse httpResponse) {
				LOGGER.info("获取用户信息失败！");
			}
		});
		
		if(!arrayList.isEmpty()) {
			isConnectFlg = true;
		}
		return isConnectFlg;
	}

	/**
	 * 根据deptId,区分租户绑定的是单节点还是集群，并返回ip和端口
	 * @param busiMqttDept
	 * @return
	 */
	private BusiMqtt tenantBindSingleOrClusterNode(BusiMqttDept busiMqttDept) {
		Integer mPort = null;
		String ip = null;
		BusiMqtt busiMqtt = new BusiMqtt();
		
		if(MqttType.CLUSTER.getValue() == busiMqttDept.getMqttType()) 
		{
			MqttBridgeCollection deptAboutMqttBridge = MqttBridgeCache.getInstance().getDeptAboutMqttBridge(busiMqttDept.getDeptId());
			if(null != deptAboutMqttBridge && null != deptAboutMqttBridge.getMqttBridges() && deptAboutMqttBridge.getMqttBridges().size() > 0)
			{
				List<MqttBridge> mqttBridges = deptAboutMqttBridge.getMqttBridges();
				if(null != mqttBridges && mqttBridges.size() > 0) 
				{
					mPort = mqttBridges.get(0).getBusiMqtt().getManagementPort();
					ip = mqttBridges.get(0).getBusiMqtt().getIp();
				}
			}
		}
		else 
		{
			MqttBridge mqttBridge = MqttBridgeCache.getInstance().getMqttBridgeById(busiMqttDept.getMqttId());
			if(null != mqttBridge && null != mqttBridge.getBusiMqtt()) 
			{
				mPort = mqttBridge.getBusiMqtt().getManagementPort();
				ip = mqttBridge.getBusiMqtt().getIp();
			}
		}
		
		busiMqtt.setManagementPort(mPort);
		busiMqtt.setIp(ip);
		return busiMqtt;
	}

	/**
            * 修改租户分配mqtt资源
     * 
     * @param busiMqttDept 
     * @return int
     */
    @Override
    public int updateBusiMqttDept(BusiMqttDept busiMqttDept)
    {
    	busiMqttDept.setUpdateTime(new Date());
    	int d = busiMqttDeptMapper.updateBusiMqttDept(busiMqttDept);
    	if (d > 0) 
    	{	
    		//终端绑定mqtt服务，并发布主题
        	this.terminalBindMqttServerData(busiMqttDept);
			MqttDeptMappingCache.getInstance().put(busiMqttDept.getDeptId(), busiMqttDeptMapper.selectBusiMqttDeptById(busiMqttDept.getId()));
		}
        return d;
    }

    /**
             * 批量删除租户分配mqtt资源
     * 
     * @param ids 
     * @return int
     */
    @Override
    public int deleteBusiMqttDeptByIds(Long[] ids)
    {
        return busiMqttDeptMapper.deleteBusiMqttDeptByIds(ids);
    }

    /**
             * 删除租户分配mqtt资源信息
     * 
     * @param id 
     * @return int
     */
    @Override
    public int deleteBusiMqttDeptById(Long id)
    {	
    	BusiMqttDept busiMqttDept = busiMqttDeptMapper.selectBusiMqttDeptById(id);
    	
//    	tenantDelMqttServer(busiMqttDept);
		int d = busiMqttDeptMapper.deleteBusiMqttDeptById(id);
		if(d > 0) 
		{
			MqttDeptMappingCache.getInstance().remove(busiMqttDept.getId());
			Collection<BusiMqttDept> values = MqttDeptMappingCache.getInstance().values();
			values.removeIf(b-> b.getId() == busiMqttDept.getId());
			
			//解绑单节点或者集群，同时需要删除mqtt服务器上的会话 ???
//			this.deleteMqttTerminalInfo(busiMqttDept);
		}
    	return d;
    }
    
    
    private void tenantDelMqttServer(BusiMqttDept busiMqttDept) {
    	String ip = null;
		Integer port = null;
		BusiMqttDept mqttDept = new BusiMqttDept();
		mqttDept.setMqttId(busiMqttDept.getMqttId());
		mqttDept.setMqttType(busiMqttDept.getMqttType());
		List<BusiMqttDept> depts = busiMqttDeptMapper.selectBusiMqttDeptList(mqttDept);
		Integer nodeCount = depts.size();
		
    	if(busiMqttDept.getMqttType() == 1) {
    		MqttBridge mqttBridge = MqttBridgeCache.getInstance().get(busiMqttDept.getMqttId());
			ip = mqttBridge.getBusiMqtt().getIp();
			port = mqttBridge.getBusiMqtt().getManagementPort();
    	}else {
			 MqttBridgeCluster mqttBridgeCluster = MqttBridgeCache.getInstance().getByMqttClusterId(busiMqttDept.getMqttId());
			 MqttBridgeCluster bridgeCluster = MqttBridgeCache.getInstance().getByMqttClusterId(mqttBridgeCluster.getBusiMqttCluster().getId());
			 BusiMqttClusterMap busiMqttClusterMap = new BusiMqttClusterMap();
			 busiMqttClusterMap.setClusterId(bridgeCluster.getBusiMqttCluster().getId());
			 List<BusiMqttClusterMap> clusterMapList = busiMqttClusterMapMapper.selectBusiMqttClusterMapList(busiMqttClusterMap);
			 if(null != clusterMapList && nodeCount > 0) {
				 Optional<BusiMqttClusterMap> max = clusterMapList.stream().max(Comparator.comparingInt(BusiMqttClusterMap::getWeight));
				 BusiMqttClusterMap mqttClusterMap = max.get();
				 MqttBridge bridge = MqttBridgeCache.getInstance().get(mqttClusterMap.getMqttId());
				 ip = bridge.getBusiMqtt().getIp();
				 port = bridge.getBusiMqtt().getManagementPort();
			 }
    	}
		
		if(nodeCount == 1) {
			EmqClient emqClient = (EmqClient) SpringContextUtil.getBean("emqClient");
	    	emqClient.unSubscribe("platform/#");
    	}
	}

	/**
	 * 解绑单节点或者集群，同时需要删除mqtt服务器上的会话
	 * @param id
	 */
	private void deleteMqttTerminalInfo(BusiMqttDept busiMqttDept) {
		BusiMqtt buMqtt = this.tenantBindSingleOrClusterNode(busiMqttDept);
		if(null != buMqtt) 
		{
			BusiTerminal busiTerminal = new BusiTerminal();
			busiTerminal.setDeptId(busiMqttDept.getDeptId());
//			busiTerminal.setType(TerminalType.MQTT.getId());
			busiTerminal.setMqttOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
			List<BusiTerminal> busiTerminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
			if(null != busiTerminalList && busiTerminalList.size() > 0) 
			{
				for (BusiTerminal busiTerminal2 : busiTerminalList) {
					if(StringUtils.isNotEmpty(busiTerminal2.getSn())) {
						String delUrl = MqttConfigConstant.HTTP + buMqtt.getIp() 
						+ MqttConfigConstant.COLON + buMqtt.getManagementPort() 
						+ MqttConfigConstant.API_AND_VERSION + "/clients/" + busiTerminal2.getSn() + MqttConfigConstant.XOR + busiTerminal2.getName();
						httpRequester.delete(delUrl, new HttpResponseProcessorAdapter() {
							
							@Override
							public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
								String success = getBodyContent(httpResponse);
			                    LOGGER.info(success + "=======> 删除会话成功:{}",httpResponse.getStatusLine().getStatusCode());
							}
						});
					}
				}
			}
		}
	}
}
