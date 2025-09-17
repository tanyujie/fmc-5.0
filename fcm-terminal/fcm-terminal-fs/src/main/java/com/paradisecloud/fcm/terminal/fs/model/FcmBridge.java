package com.paradisecloud.fcm.terminal.fs.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.paradisecloud.fcm.common.enumer.FcmBridgeStatus;
import com.paradisecloud.fcm.terminal.fs.db.manager.RegistrationsManager;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.dao.mapper.BusiMqttDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMqttMapper;
import com.paradisecloud.fcm.dao.mapper.BusiRegisterTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;
import com.paradisecloud.fcm.dao.model.BusiMqtt;
import com.paradisecloud.fcm.dao.model.BusiMqttDept;
import com.paradisecloud.fcm.dao.model.BusiRegisterTerminal;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import com.paradisecloud.fcm.terminal.fs.interfaces.IFreeSwitchUserService;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;

public class FcmBridge {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private HttpRequester httpRequester = HttpObjectCreator.getInstance().createHttpRequester(FcmConfigConstant.MQTT_BACK_NAME, FcmConfigConstant.MQTT_BACK_PASSWORD, false);
	
	private volatile BusiFreeSwitch busiFreeSwitch;
	private volatile int weight;
	private volatile FcmBridgeStatus bridgeStatus = FcmBridgeStatus.NOT_AVAILABLE;
	private FcmLogger fcmLogger;
	private FcmBridgeCluster fcmBridgeCluster;
	
	public int addFreeSwitchUser(String credential, String password) {
		int user = 0;
		logger.info("======================》 添加FCM-sip账户的！！ping+" + pingIpAndPort(busiFreeSwitch.getIp(), busiFreeSwitch.getPort()));
//		if (busiFreeSwitch != null && pingIpAndPort(busiFreeSwitch.getIp(), busiFreeSwitch.getPort())){
		if (busiFreeSwitch != null){
			IFreeSwitchUserService freeSwitchUserService = (IFreeSwitchUserService) SpringContextUtil.getBean("freeSwitchUserService");
			FreeSwitchUser switchUser = new FreeSwitchUser();

			switchUser.setUserId(credential);
			switchUser.setPassword(password);
			user = freeSwitchUserService.insertFreeSwitchUser(switchUser, busiFreeSwitch);
		}

		return user;
	}
	
	public int updateFreeSwitchUser(String credential, String password) {
		int user = 0;
		logger.info("======================》 修改FCM-sip账户的！！ping+" + pingIpAndPort(busiFreeSwitch.getIp(), busiFreeSwitch.getPort()));
//		if (busiFreeSwitch != null && pingIpAndPort(busiFreeSwitch.getIp(), busiFreeSwitch.getPort())){
		if (busiFreeSwitch != null) {
			IFreeSwitchUserService freeService = (IFreeSwitchUserService) SpringContextUtil.getBean("freeSwitchUserService");

			FreeSwitchUser switchUser = new FreeSwitchUser();
			switchUser.setUserId(credential);
			switchUser.setPassword(password);

			logger.info("======================》 更新FCM账户的状态！！！！");
			user = freeService.updateFreeSwitchUser(switchUser, busiFreeSwitch);
		}
		return user;
	}

	public int deleteFreeSwitchUserByIds(String... credential)
	{
		try
		{
//			logger.info("======================》 删除FCM-sip账户的！！ping+" + pingIpAndPort(busiFreeSwitch.getIp(), busiFreeSwitch.getPort()));
//			if (busiFreeSwitch != null && pingIpAndPort(busiFreeSwitch.getIp(), busiFreeSwitch.getPort())) {
			if (busiFreeSwitch != null) {
				IFreeSwitchUserService service = (IFreeSwitchUserService) SpringContextUtil.getBean("freeSwitchUserService");

				logger.info("======================》 删除FCM账户！！！！");
				service.deleteFreeSwitchUserByIds(credential, busiFreeSwitch);
			}
			else {
				return FcmConfigConstant.ZERO;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return FcmConfigConstant.ZERO;
		}

		return FcmConfigConstant.SUCCESS;
	}
	
	
	private void kickOutTerminal(String credential) {
		BusiRegisterTerminalMapper busiRegisterTerminalMapper = (BusiRegisterTerminalMapper)SpringContextUtil.getBean("busiRegisterTerminalMapper");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				BusiRegisterTerminal registerTerminal = new BusiRegisterTerminal();
				registerTerminal.setCredential(credential);
				List<BusiRegisterTerminal> registerTerminals = busiRegisterTerminalMapper.selectBusiRegisterTerminalList(registerTerminal);
				if(null != registerTerminals && registerTerminals.size() > 0) {
					BusiRegisterTerminal busiRegisterTerminal = registerTerminals.get(0);
					
					//获取部门上绑定的mqtt信息
					getAllMqtt(busiRegisterTerminal.getSn());
				}
			}
			
		},"删除sip账号踢除终端").start();
	}

	private void getAllMqtt(String sn) {
		BusiMqtt busiMqtt = new BusiMqtt();
		BusiMqttMapper busiMqttMapper = (BusiMqttMapper)SpringContextUtil.getBean("busiMqttMapper");
		List<BusiMqtt> mqttLists = busiMqttMapper.selectBusiMqttList(busiMqtt);
		if(null != mqttLists && mqttLists.size() > 0) {
			for (BusiMqtt busiMqtt2 : mqttLists) {
				
				//剔除mqtt上的终端和会话
				getTerminalConnection(busiMqtt2, sn);
			}
		}
	}
	
	public List<BusiMqttDept> getBindMqttNode(Long deptId) 
	{
		BusiMqttDeptMapper busiMqttDeptMapper = (BusiMqttDeptMapper)SpringContextUtil.getBean("busiMqttDeptMapper");
		BusiMqttDept busiMqttDept = new BusiMqttDept();
		busiMqttDept.setDeptId(deptId);
		List<BusiMqttDept> busiMqttDeptList = busiMqttDeptMapper.selectBusiMqttDeptList(busiMqttDept);
		if(busiMqttDeptList.size() <= 0) 
		{
			SysDept sysDept = SysDeptCache.getInstance().get(deptId);
			if(null != sysDept.getParentId() && sysDept.getParentId().longValue() > 0) 
			{
				return getBindMqttNode(sysDept.getParentId());
			}
			else
			{
				return null;
			}
			
		}
		return busiMqttDeptList;
	}

	private void getTerminalConnection(BusiMqtt busiMqtt, String sn) {
		String httpUrl = FcmConfigConstant.HTTP + busiMqtt.getIp() + FcmConfigConstant.COLON + busiMqtt.getManagementPort() + FcmConfigConstant.API_AND_VERSION;
		String connUrl = httpUrl + "/clients/" + sn;
		
		httpRequester.get(connUrl, new HttpResponseProcessorAdapter() {
			
			@Override
			public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
				try {
					
                    String nodeData = getBodyContent(httpResponse);
                    if(StringUtils.isNotEmpty(nodeData)) 
    				{	
    					JSONObject jsonObject = (JSONObject) JSONObject.parse(nodeData);
                    	String data = jsonObject.getString(FcmConfigConstant.JSON_DATA_STR);
    					JSONArray array = (JSONArray) JSONArray.parse(data);
    					if(null != array && array.size() > 0) {
							JSONObject jsonObj = (JSONObject)array.get(0);
							Boolean connect = jsonObj.getBoolean("connected");
							if(connect) {
								lastKickOutTerminal(connUrl, sn);
							}
    					}
    				}
				} catch (Exception e) {
					throw new SystemException(FcmConfigConstant.EXCEPTION_ONE_th_th_F, "查询终端连接MQTT异常!");
				}
			}
		});
	}

	
	private void lastKickOutTerminal(String delUrl, String sn) {
		
		httpRequester.delete(delUrl, new HttpResponseProcessorAdapter() {
		
			@Override
			public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
//				String success = getBodyContent(httpResponse);
			}
		});
		
	}
	
	private void deleteBusiRegisterTerminal(String credential) {
		BusiRegisterTerminalMapper registerTerminalMapper = (BusiRegisterTerminalMapper)SpringContextUtil.getBean("busiRegisterTerminalMapper");
		BusiRegisterTerminal busiRegisterTerminal = new BusiRegisterTerminal();
		busiRegisterTerminal.setCredential(credential);
		List<BusiRegisterTerminal> terminalList = registerTerminalMapper.selectBusiRegisterTerminalList(busiRegisterTerminal);
		if(null != terminalList && terminalList.size() > 0) {
			Long registerId = terminalList.get(0).getId();
			registerTerminalMapper.deleteBusiRegisterTerminalById(registerId);
		}
	}

	public Set<String> getFsOnlineUser() {
		long startTime = System.currentTimeMillis();
		logger.info("======================》 开始查询FCM账户的状态");
//		logger.info("======================》 maps" + maps.toString());
		Set<String> onlineSet = null;
		
		if(null != busiFreeSwitch) {
			RegistrationsManager registrationsManager = new RegistrationsManager(busiFreeSwitch.getIp());
			onlineSet = registrationsManager.queryAllForOnline();
		} else {
			onlineSet = new HashSet<>();
		}
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		logger.info("======================》 获取FS账号信息共耗时" + totalTime);
		return onlineSet;
	}
	
	  
    public Boolean pingIpAndPort(String ip, Integer port) {
		if (null == ip || 0 == ip.length() || port < 1024 || port > 65535)
		{
			  return false;
		}
		
	    if (!pingIp(ip)) 
	    {
	        return false;
	    }
	    
	    Socket s = new Socket();
	    try 
	    {
	         SocketAddress add = new InetSocketAddress(ip, port);
	         s.connect(add, 500);// 超时3秒
	         return true;
	    } 
	    catch (IOException e) 
	    {
	          return false;
	    } 
	    finally 
	    {
	        try 
	        {
	              s.close();
	         } 
	         catch (Exception e) 
	        {
	        	 
	        }
	    }
	}
	
	public Boolean pingIp(String ip) 
	{
      if (null == ip || 0 == ip.length()) {
           return false;
      }
      
      try 
      {
          InetAddress.getByName(ip);
          return true;
     } 
     catch (IOException e) 
     {
          return false;
     }
	}

	public FcmBridge(BusiFreeSwitch busiFreeSwitch) {
		super();
		this.busiFreeSwitch = busiFreeSwitch;
		fcmLogger = new FcmLogger(this);
	}
	
	/**
     * <p>Get Method   :   fcmLogger FcmLogger</p>
     * @return fcmLogger
     */
    public FcmLogger getFcmLogger()
    {
        return fcmLogger;
    }

    public BusiFreeSwitch getBusiFreeSwitch() 
	{
		return busiFreeSwitch;
	}

	/**
	 * <p>Get Method   :   weight int</p>
	 * @return weight
	 */
	public Integer getWeight()
	{
		return weight;
	}

	/**
	 * <p>Set Method   :   weight int</p>
	 * @param weight
	 */
	public void setWeight(int weight)
	{
		this.weight = weight;
	}

	/**
	 * <p>Get Method   :   isAvailable boolean</p>
	 * @return isAvailable
	 */
	public boolean isAvailable()
	{
		return bridgeStatus == FcmBridgeStatus.AVAILABLE;
	}

	/**
	 * <p>Set Method   :   bridgeStatus FcmBridgeStatus</p>
	 * @param bridgeStatus
	 */
	public synchronized void setBridgeStatus(FcmBridgeStatus bridgeStatus)
	{
		this.bridgeStatus = bridgeStatus;
	}

	/**
	 * <p>Get Method   :   bridgeStatus FmeBridgeStatus</p>
	 * @return bridgeStatus
	 */
	public FcmBridgeStatus getBridgeStatus()
	{
		return bridgeStatus;
	}

	/**
	 * <p>Set Method   :   fcmBridgeCluster FcmBridgeCluster</p>
	 * @param fcmBridgeCluster
	 */
	public void setFcmBridgeCluster(FcmBridgeCluster fcmBridgeCluster)
	{
		this.fcmBridgeCluster = fcmBridgeCluster;
	}
	
}
