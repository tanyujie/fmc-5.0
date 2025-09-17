package com.paradisecloud.fcm.mqtt.impls;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

import javax.annotation.Resource;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;

import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.AppType;
import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.DeptHwcloudMappingCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridgeCache;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.mcu.zj.cache.DeptMcuZjMappingCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mqtt.cache.TerminalLiveCache;
import com.paradisecloud.fcm.mqtt.common.*;
import com.paradisecloud.fcm.mqtt.interfaces.*;
import com.paradisecloud.fcm.mqtt.model.*;
import com.paradisecloud.fcm.mqtt.scheduler.MonitorMqttServeAndUser;
import com.paradisecloud.fcm.smc2.cache.DeptSmc2MappingCache;
import com.paradisecloud.fcm.smc2.cache.Smc2Bridge;
import com.paradisecloud.fcm.smc2.cache.Smc2BridgeCache;
import com.paradisecloud.fcm.terminal.fs.db.FreeSwitchTransaction;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import com.paradisecloud.smc3.busi.cache.DeptSmc3MappingCache;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiRegisterTerminalMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalActionMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;
import com.paradisecloud.fcm.dao.model.BusiFsbcRegistrationServer;
import com.paradisecloud.fcm.dao.model.BusiMqtt;
import com.paradisecloud.fcm.dao.model.BusiRegisterTerminal;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiTerminalAction;
import com.paradisecloud.fcm.dao.model.BusiTerminalLog;
import com.paradisecloud.fcm.dao.model.BusiTerminalUpgrade;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.mqtt.cache.MqttBridgeCache;
import com.paradisecloud.fcm.mqtt.constant.CameraControlParams;
import com.paradisecloud.fcm.mqtt.constant.InstantMeetingParam;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.TerminalTopic;
import com.paradisecloud.fcm.mqtt.constant.TerminalUpgradeParams;
import com.paradisecloud.fcm.mqtt.enums.TerminalActionEnum;
import com.paradisecloud.fcm.sdk.video.service.interfaces.IVideoConferenceSDKService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.DeptFsbcMappingCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;

/**
 * 终端动作Service业务层处理
 * 
 * @author zyz
 * @date 2021-07-31
 */
@Transactional
@Service
public class BusiTerminalActionServiceImpl implements IBusiTerminalActionService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BusiMqttServiceImpl.class);

    /**
     * context path
     */
    @Value("${server.servlet.context-path}")
    private String contextPath;

	@Value("${application.home}")
	private String projectPath;
	
    @Autowired
    private BusiTerminalActionMapper busiTerminalActionMapper;
    
    @Autowired
    private ITerminalActionService terminalActionService;
    
    @Autowired
    private IBusiTerminalService busiTerminalService;
    
    @Autowired
    private IBusiTerminalSysInfoService busiTerminalSysInfoService;
    
    @Autowired
    private IBusiTerminalUpgradeService busiTerminalUpgradeService;
    
    @Autowired
    private IBusiTerminalLogService busiTerminalLogService;
    
    @Autowired
    private BusiTerminalMapper busiTerminalMapper;
    
    @Autowired
    private IBusiRegisterTerminalService busiRegisterTerminalService;
    
    @Autowired
    private IVideoConferenceSDKService videoConferenceSDKService;
    
    @Autowired
    private BusiRegisterTerminalMapper busiRegisterTerminalMapper;
    
    @Autowired
    private IAttendeeService attendeeService;

	@Resource
	private BusiTerminalUpgradeMapper busiTerminalUpgradeMapper;

	@Resource
	private TaskService taskService;

	@Resource
	private IMqttService iMqttService;

//	private static HashMap<Long, Boolean> appUpgradeMap = new HashMap<>();

    /**
     * 查询终端动作具体信息
     * 
     * @param id 
     * @return 
     */
    @Override
    public BusiTerminalAction selectBusiTerminalActionById(Long id)
    {
        return busiTerminalActionMapper.selectBusiTerminalActionById(id);
    }

    /**
     * 查询终端动作信息列表
     * 
     * @param busiTerminalAction 
     * @return List<BusiTerminalAction>
     */
    @Override
    public List<BusiTerminalAction> selectBusiTerminalActionList(BusiTerminalAction busiTerminalAction)
    {
        return busiTerminalActionMapper.selectBusiTerminalActionList(busiTerminalAction);
    }

    /**
     * 新增终端动作信息
     * 
     * @param busiTerminalAction 
     * @return int
     */
    @Override
    public int insertBusiTerminalAction(BusiTerminalAction busiTerminalAction)
    {
        busiTerminalAction.setCreateTime(new Date());
        return busiTerminalActionMapper.insertBusiTerminalAction(busiTerminalAction);
    }

    /**
     * 修改终端动作信息
     * 
     * @param busiTerminalAction 
     * @return int
     */
    @Override
    public int updateBusiTerminalAction(BusiTerminalAction busiTerminalAction)
    {
        busiTerminalAction.setUpdateTime(new Date());
        return busiTerminalActionMapper.updateBusiTerminalAction(busiTerminalAction);
    }

    /**
     * 批量删除终端动作信息
     * 
     * @param ids 
     * @return int
     */
    @Override
    public int deleteBusiTerminalActionByIds(Long[] ids)
    {
        return busiTerminalActionMapper.deleteBusiTerminalActionByIds(ids);
    }

    /**
     * 删除终端动作具体信息
     * 
     * @param id 
     * @return int
     */
    @Override
    public int deleteBusiTerminalActionById(Long id)
    {
        return busiTerminalActionMapper.deleteBusiTerminalActionById(id);
    }

	@Override
	public int isAgreeTerminalAction(Long id, Boolean isAgree) {
		int successFlg = MqttConfigConstant.ZERO;
		if(null != id) 
		{
			BusiTerminalAction busiTerminalAction = busiTerminalActionMapper.selectBusiTerminalActionById(id);
			if(null != busiTerminalAction)
			{
				if(busiTerminalAction.getActionType() == TerminalActionEnum.JOIN_LIVE.value())
				{
					//直播入会
					this.terminalLiveMeeting(busiTerminalAction, isAgree);
				}
				else 
				{
					//会议发言
					this.terminalConferenceSpeech(busiTerminalAction, isAgree);
				}
				
				successFlg = MqttConfigConstant.SUCCESS;
			}
		}
		return successFlg;
	}

	private void terminalConferenceSpeech(BusiTerminalAction busiTerminalAction, Boolean isAgree) {
		JSONObject object = new JSONObject();
		String action = TerminalTopic.INTERACTIVE_RAISE_HAND;
		String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminalAction.getTerminalSn();
		
		int del = busiTerminalActionMapper.deleteBusiTerminalActionById(busiTerminalAction.getId());
		if(del > 0) {
			Long conferenceNum = busiTerminalAction.getConferenceNum();
			object.put("isAgree", isAgree);
			object.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
			terminalActionService.responseTerminal(terminalTopic, action, object, busiTerminalAction.getTerminalSn(), "");
			
			if(isAgree) {
				BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiTerminalAction.getTerminalSn());
				ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getContext(conferenceNum.toString(), busiTerminal);
				if(null != conferenceContext) {
					String attendeeId = conferenceContext.getTerminalAttendeeMap().get(busiTerminalAction.getTerminalId()).getId();
					
					//主持人对终端开麦
					attendeeService.openMixing(conferenceNum.toString(), attendeeId);
				}
			}
		}
	}

	private void terminalLiveMeeting(BusiTerminalAction busiTerminalAction, Boolean isAgree) {
		JSONObject object = new JSONObject();
		String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminalAction.getTerminalSn();
		String action = TerminalTopic.JOIN_LIVE;
		
		int delLive = busiTerminalActionMapper.deleteBusiTerminalActionById(busiTerminalAction.getId());
		if(delLive > 0) {
			Long conferenceNum = busiTerminalAction.getConferenceNum();
			if(isAgree) {
				object.put(MqttConfigConstant.CONFERENCENUM, conferenceNum);
				BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiTerminalAction.getTerminalSn());
				ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getContext(conferenceNum.toString(), busiTerminal);
				List<TerminalLive> terminalLiveList = TerminalLiveCache.getInstance().getById(conferenceContext.getContextKey());
				if (terminalLiveList != null && terminalLiveList.size() >0){
					for (int i = 0; i < terminalLiveList.size(); i++) {
						if (terminalLiveList.get(i).getMac() == busiTerminalAction.getTerminalSn()){
							terminalLiveList.get(i).setStatus(2);
							TerminalLiveCache.getInstance().update(conferenceContext.getContextKey(),terminalLiveList);
//							TerminalActionServiceImpl.terminalCache().remove(conferenceContext.getTemplateConferenceId());
//							TerminalActionServiceImpl.terminalCache().put(conferenceContext.getTemplateConferenceId(),terminalLiveList);
						}
					}
				}

				if(null != conferenceContext) {
					object.put(MqttConfigConstant.PASSWORD, conferenceContext.getConferencePassword());
				}
			}else {
				object.put(MqttConfigConstant.CONFERENCENUM, "");
			}
			
			object.put("isAgree", isAgree);
			terminalActionService.responseTerminal(terminalTopic, action, object, busiTerminalAction.getTerminalSn(), "");
		}
	}

	@Override
	public Boolean mqttServerRestart(Long id) {
		//保存输出内容的容器
	    MqttBridge mqttBridge = MqttBridgeCache.getInstance().getMqttBridgeById(id);
	    if(null != mqttBridge && null != mqttBridge.getBusiMqtt()) 
	    {
	    	 BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();
			LOGGER.info("===================>BusiMqtt" + busiMqtt.toString());
				Thread thread = new Thread(new Runnable() 
				{
					
					@Override
					public void run() 
					{
						try {
							 
							 if(!SshConnectionRemoteServer.isLogined())
				             {
								 SshConnectionRemoteServer.sshRemoteCallLogin(busiMqtt.getIp(), busiMqtt.getServerUserName(), busiMqtt.getServerPassword(), busiMqtt.getServerPort());
								 LOGGER.info("===================>连接成功FMQ服务" + busiMqtt.getIp());
								 
				             }
							 
							 String isStop = SshConnectionRemoteServer.execCommand("cd " + busiMqtt.getMqttStartupPath() + " && " + "./emqx stop");
							 LOGGER.info("===================>FMQ服务已停止 " + isStop);
							 if(isStop.contains("ok")) {
								 String execCommand = SshConnectionRemoteServer.execCommand("cd " + busiMqtt.getMqttStartupPath() + " && " + "./emqx start");
								 if(StringUtils.isNotEmpty(execCommand) && execCommand.contains("started successfully")) {
									 LOGGER.info("===================> FMQ服务已经重新启动" + execCommand);
								 }
							 }
						} 
						catch (Exception e) 
						{
							throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "连接服务异常!");
							
						}
					}
				}, "========> 启动mqtt服务线程");
						
				thread.start();
	    }
	   
		return true;
	}

	@Override
	public void terminalActionDealResult(TerminalAction terminalAction) {
		BusiTerminal busiTerminal = busiTerminalService.selectBusiTerminalById(terminalAction.getId());
		if (null != busiTerminal) {
			String clientId = busiTerminal.getSn();
			String action = terminalAction.getAction();
			String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
			JSONObject object = new JSONObject();
			object.put(MqttConfigConstant.CLIENTID, clientId);
			object.put(MqttConfigConstant.TOPIC, terminalTopic);
			if(action.equals(TerminalTopic.TERMINAL_POWEROFF)) {
				object.put(MqttConfigConstant.ACTION, TerminalTopic.TERMINAL_POWEROFF);
			}else if(action.equals(TerminalTopic.TERMINAL_REBOOT)) {
				object.put(MqttConfigConstant.ACTION, TerminalTopic.TERMINAL_REBOOT);
			}else if (action.equals(TerminalTopic.TERMINAL_RESET)) {
				object.put(MqttConfigConstant.ACTION, TerminalTopic.TERMINAL_RESET);
			}else if (action.equals(TerminalTopic.OPEN_PRESENTATION)) {
				object.put(MqttConfigConstant.ACTION, TerminalTopic.OPEN_PRESENTATION);
			}else if (action.equals(TerminalTopic.CLOSE_PRESENTATION)) {
				object.put(MqttConfigConstant.ACTION, TerminalTopic.CLOSE_PRESENTATION);
			}
			object.put(MqttConfigConstant.JSON_DATA_STR, null);
			PublisMessage.getInstance().publishTopicMsg(terminalTopic, clientId, object.toString(), false);
		}
	}

	@Override
	public void terminalInfoModify(Long id, String terminalName) {
		BusiTerminal busiTerminal = busiTerminalService.selectBusiTerminalById(id);
		if (null != busiTerminal) {
			String clientId = busiTerminal.getSn();
			String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
			JSONObject object = new JSONObject();
			JSONObject dataObj = new JSONObject();
			object.put(MqttConfigConstant.CLIENTID, clientId);
			object.put(MqttConfigConstant.ACTION, TerminalTopic.MODIFY_TERMINAL_INFO);
			
			dataObj.put(MqttConfigConstant.NAME, terminalName);
			object.put(MqttConfigConstant.JSON_DATA_STR, dataObj);
			
			PublisMessage.getInstance().publishTopicMsg(terminalTopic, clientId, object.toString(), false);
		}
	}

	@Override
	public void cameraControl(Long id, String direction, Boolean isFar) {
		BusiTerminal busiTerminal = busiTerminalService.selectBusiTerminalById(id);
		if (null != busiTerminal) {
			String clientId = busiTerminal.getSn();
			String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
			JSONObject object = new JSONObject();
			JSONObject dataObj = new JSONObject();
			object.put(MqttConfigConstant.CLIENTID, clientId);
			object.put(MqttConfigConstant.ACTION, TerminalTopic.CAMERA_CONTROL);
			if(isFar) {
				object.put(CameraControlParams.CAMERA_CONTROL_MODE, CameraControlParams.FAREND);
			}else {
				object.put(CameraControlParams.CAMERA_CONTROL_MODE, CameraControlParams.LOCAL);
			}
			
			dataObj.put(CameraControlParams.PANTILT_CONTROL_CMD, direction);
			object.put(MqttConfigConstant.JSON_DATA_STR, dataObj);
			
			PublisMessage.getInstance().publishTopicMsg(terminalTopic, clientId, object.toString(), false);
		}
	}

	@Override
	public void cameraControlStop(Long id, Boolean isFar) {
		BusiTerminal busiTerminal = busiTerminalService.selectBusiTerminalById(id);
		if (null != busiTerminal) {
			String clientId = busiTerminal.getSn();
			String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
			JSONObject object = new JSONObject();
			JSONObject dataObj = new JSONObject();
			object.put(MqttConfigConstant.CLIENTID, clientId);
			object.put(MqttConfigConstant.ACTION, TerminalTopic.CAMERA_CONTROL);
			if(isFar) {
				dataObj.put(CameraControlParams.PANTILT_CONTROL_CMD, CameraControlParams.STOP);
				object.put(CameraControlParams.CAMERA_CONTROL_MODE, CameraControlParams.FAREND);
			}else {
				dataObj.put(CameraControlParams.PANTILT_CONTROL_CMD, CameraControlParams.SET_PANTILT_STOP);
				object.put(CameraControlParams.CAMERA_CONTROL_MODE, CameraControlParams.LOCAL);
			}
			
			object.put(MqttConfigConstant.JSON_DATA_STR, dataObj);
			
			PublisMessage.getInstance().publishTopicMsg(terminalTopic, clientId, object.toString(), false);
		}
	}

	@Override
	public String terminalRemoteUpgrade(Long[] ids) {
		String responseStr = null;
		if(null != ids && ids.length > 0) {
			Map<String, BusiTerminalUpgrade> busiTerminalUpgradeMap = new HashMap<>();
			List<BusiTerminalUpgrade> busiTerminalUpgradeList = busiTerminalUpgradeMapper.selectBusiTerminalUpgradeList(new BusiTerminalUpgrade());
			for (BusiTerminalUpgrade busiTerminalUpgrade: busiTerminalUpgradeList) {
				busiTerminalUpgradeMap.put(busiTerminalUpgrade.getTerminalType(), busiTerminalUpgrade);
			}
			for (Long id : ids) {
				LOGGER.info("APP更新推送。ID：" + id);
				BusiTerminal busiTerminal = TerminalCache.getInstance().get(id);

				if(null != busiTerminal) {
					boolean hasAppUpdate = false;
					BusiTerminalUpgrade busiTerminalUpgrade = busiTerminalUpgradeMap.get(busiTerminal.getAppType());
					if (busiTerminalUpgrade != null) {
						if (StringUtils.isEmpty(busiTerminal.getAppVersionCode()) || busiTerminalUpgrade.getVersionNum().length() > busiTerminal.getAppVersionCode().length() || busiTerminalUpgrade.getVersionNum().compareTo(busiTerminal.getAppVersionCode()) > 0) {
							if (busiTerminal.getMqttOnlineStatus() != null && TerminalOnlineStatus.ONLINE.getValue() == busiTerminal.getMqttOnlineStatus()) {
								hasAppUpdate = true;
							}
						}
					}
					if (hasAppUpdate) {
						String clientId = busiTerminal.getSn();
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("clientId",clientId);
						jsonObject.put("action",TerminalTopic.TERMINAL_UPGRADE);
						Map<String, String> map = new HashMap<>();
						map.put("serverUrl", busiTerminalUpgrade.getServerUrl());
						map.put("versionName",busiTerminalUpgrade.getVersionName());
						map.put("versionNum",busiTerminalUpgrade.getVersionNum());
						map.put("description", busiTerminalUpgrade.getVersionDescription());
						map.put("checkNow","true");
						jsonObject.put("data",map);
						String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
						PublisMessage.getInstance().publishTopicMsg(terminalTopic, clientId, jsonObject.toString(), false);
						if (ids.length == 1) {
							responseStr = "终端升级命令已下发，终端空闲后会提示升级。新版本为：" + busiTerminalUpgrade.getVersionName() + "!";
						}
					} else {
						if (ids.length == 1) {
							responseStr = "终端已为最新版本无需升级!";
						}
					}
				}
			}
			if (ids.length > 1) {
				responseStr = "终端升级命令已下发，终端空闲后会提示升级。";
			}
		}
		return responseStr;
	}

	@Override
	public void terminalCollectLogs(Long id) throws MalformedObjectNameException {
		if(null != id) {
//			BusiTerminal busiTerminal = busiTerminalService.selectBusiTerminalById(id);
			BusiTerminal busiTerminal = TerminalCache.getInstance().get(id);
			if(null != busiTerminal) {
				String clientId = busiTerminal.getSn();
				String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
				JSONObject jsonObject = new JSONObject();
				JSONObject dataObj = new JSONObject();
				jsonObject.put(MqttConfigConstant.CLIENTID, clientId);
				jsonObject.put(MqttConfigConstant.ACTION, TerminalTopic.TERMINAL_COLLECT_LOGS);
				dataObj.put(TerminalUpgradeParams.TERMINAL_ID, busiTerminal.getId());
//				int serverPort = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getServerPort();
				MonitorMqttServeAndUser monitorMqttServeAndUser = new MonitorMqttServeAndUser();
				String fmcRootUrl = ExternalConfigCache.getInstance().getFmcRootUrl();
				String fmcRootUrlExternal = ExternalConfigCache.getInstance().getFmcRootUrlExternal();
				if (StringUtils.isNotEmpty(fmcRootUrlExternal)) {
					if (fmcRootUrlExternal.contains(busiTerminal.getConnectIp())) {
						fmcRootUrl = fmcRootUrlExternal;
					}
				}
				if (StringUtils.isEmpty(fmcRootUrl)) {
					Set<String> fcmsystemIP = monitorMqttServeAndUser.fcmsystemIP();
					fmcRootUrl = MqttConfigConstant.HTTP + fcmsystemIP.iterator().next() + ":8899";
				}
				if (StringUtils.isNotEmpty(contextPath)) {
					fmcRootUrl += contextPath;
				}

				String interfaceAddr = fmcRootUrl + "/busi/terminalAction/logsUploadMulti";
				dataObj.put(TerminalUpgradeParams.INTERFACE_URL, interfaceAddr);
				jsonObject.put(MqttConfigConstant.JSON_DATA_STR, dataObj);
				PublisMessage.getInstance().publishTopicMsg(terminalTopic, clientId, jsonObject.toString(), false);
			}
		}
		
	}
	
	private String getLinuxLocalIp() {
	      String ip = "";
	      try {
	          for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
	              NetworkInterface intf = en.nextElement();
	              String name = intf.getName();
	              if (!name.contains("docker") && !name.contains("lo")) {
	                  for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
	                      InetAddress inetAddress = enumIpAddr.nextElement();
	                      if (!inetAddress.isLoopbackAddress()) {
	                          String ipaddress = inetAddress.getHostAddress().toString();
	                          if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
	                              ip = ipaddress;
	                          }
	                      }
	                  }
	              }
	          }
	      } catch (SocketException ex) {
	          ip = "127.0.0.1";
	          ex.printStackTrace();
	      }
	     return ip;
	  }
	
	public String getLocalPort() throws MalformedObjectNameException {
		MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
		Set<ObjectName> queryNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
		String port = queryNames.iterator().next().getKeyProperty("port");
		return port;
	}
	
//	public String getLocalIP() {
//		InetAddress addr = null;
//		try {
//			addr = InetAddress.getLocalHost();
//	
//		} catch (Exception e) {
//		e.printStackTrace();
//		}
//	
//		byte[] ipAddr = addr.getAddress();
//		String ipAddrStr = "";
//		for (int i = 0; i < ipAddr.length; i++) {
//			if (i > 0) {
//			ipAddrStr += ".";
//			}
//	
//			ipAddrStr += ipAddr[i] & 0xFF;
//		}
//		return ipAddrStr;
//	}

	@Override
	public void saveTerminalLogInfo(String mac, String filePath, String fileName) {
		if(StringUtils.isNotEmpty(mac)) {
			BusiTerminal busiTerminal = new BusiTerminal();
			busiTerminal.setSn(mac);
			List<BusiTerminal> busiTerminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
			if(null != busiTerminalList && busiTerminalList.size() > 0) {
				String clientId = busiTerminalList.get(0).getSn();
				BusiTerminalLog busiTerminalLog = new BusiTerminalLog();
				busiTerminalLog.setSn(clientId);
				File file = new File(filePath + fileName);
				List<BusiTerminalLog> terminalLogList = busiTerminalLogService.selectBusiTerminalLogList(busiTerminalLog);
				if(null != terminalLogList && terminalLogList.size() > 0) {
					terminalLogList.get(0).setUpdateTime(new Date());
					terminalLogList.get(0).setLogFileName(fileName);
					terminalLogList.get(0).setLogFilePath(filePath);
					terminalLogList.get(0).setLogSize(file.length());
					terminalLogList.get(0).setTerminalId(busiTerminalList.get(0).getId());
					busiTerminalLogService.updateBusiTerminalLog(terminalLogList.get(0));
				} else {
					BusiTerminal terminal = busiTerminalList.get(0);
					busiTerminalLog.setCreateTime(new Date());
					busiTerminalLog.setLogFileName(fileName);
					busiTerminalLog.setLogFilePath(filePath);
					busiTerminalLog.setSn(mac);
					busiTerminalLog.setTerminalId(terminal.getId());
					busiTerminalLog.setLogSize(file.length());
					busiTerminalLogService.insertBusiTerminalLog(busiTerminalLog);
				}
			}
		}
		
	}

	@Override
	public void hostControlCamera(String clientId, String conferenceNum, String controlParam, String controlCmd) {
		String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
		String action = TerminalTopic.CAMERA_CONTROL;
		JSONObject object = new JSONObject();
		JSONObject obj = new JSONObject();
		object.put(MqttConfigConstant.CLIENTID, clientId);
		object.put(MqttConfigConstant.ACTION, action);
		obj.put(InstantMeetingParam.CONFERENCENUM, conferenceNum);
		obj.put(CameraControlParams.CAMERA_CONTROL_MODE, CameraControlParams.LOCAL);
		if(controlCmd.equals(CameraControlParams.PANTILT_CONTROL_CMD)) {
			obj.put(CameraControlParams.PANTILT_CONTROL_CMD, controlParam);
		} else if (controlCmd.equals(CameraControlParams.FOCUS_CONTROL_CMD)) {
			obj.put(CameraControlParams.FOCUS_CONTROL_CMD, controlParam);
		}else {
			obj.put(CameraControlParams.ZOOM_CONTROL_CMD, controlParam);
		}
		
		object.put(MqttConfigConstant.JSON_DATA_STR, obj);
		PublisMessage.getInstance().publishTopicMsg(terminalTopic, clientId, object.toString(), false);
	}

	@Override
	@FreeSwitchTransaction
	public void bindNoRegisterAccount(BusiTerminal busiTerminal) {
		String msgId = "";
		List<BusiTerminal> busiTerminals = new ArrayList<BusiTerminal>();
		if(null != busiTerminal) {
			BusiRegisterTerminal registerTerminal = new BusiRegisterTerminal();
			if(StringUtils.isNotEmpty(busiTerminal.getSn())) {
				registerTerminal.setMac(busiTerminal.getSn());
			}
			
			List<BusiRegisterTerminal> registerTerminalList = busiRegisterTerminalService.selectBusiRegisterTerminalList(registerTerminal);
			if(null != registerTerminalList && registerTerminalList.size() > 0) {
				BusiRegisterTerminal busiRegisterTerminal = registerTerminalList.get(0);
				if(TerminalType.isFSBC(busiTerminal.getType()) ) {
					
					//Fsbc类型
					busiTerminal = this.dealFsbcType(busiTerminal);
				}else if(TerminalType.isFCMSIP(busiTerminal.getType())){
					
					//Fcm类型
					busiTerminal = this.dealFcmType(busiTerminal);
				} else if (TerminalType.isZJ(busiTerminal.getType())) {

					//zj
					busiTerminal = this.dealZjType(busiTerminal);
				} else if (TerminalType.isSMCSIP(busiTerminal.getType())) {

					//smc
					busiTerminal = this.dealSmcType(busiTerminal);
				} else if (TerminalType.isSMC2SIP(busiTerminal.getType())) {

					//smc2
					busiTerminal = this.dealSmc2Type(busiTerminal);
				} else if (TerminalType.isHwCloud(busiTerminal.getType())) {

					//hwCloud
					busiTerminal = this.dealHwCloudType(busiTerminal);
				}

				BusiTerminal terminal = new BusiTerminal();
				terminal.setConnectIp(busiRegisterTerminal.getConnectIp());
				terminal.setCredential(busiTerminal.getCredential());
				terminal.setType(busiTerminal.getType());
				List<BusiTerminal> terminalList = busiTerminalMapper.selectBusiTerminalList(terminal);
				LOGGER.info("=========================>表里面是否有值" + terminalList.toString());
				if(null != terminalList && terminalList.size() > 0) {
					BusiTerminal busiTerminal2 = terminalList.get(0);
					Assert.isTrue(StringUtils.isEmpty(busiTerminal2.getSn()) && busiTerminal2.getCredential().equals(busiTerminal.getCredential()), "该账号在已存在，请勿重复添加");
					busiTerminal2.setUpdateTime(new Date());
					busiTerminal2.setIp(busiTerminal.getIp());
					busiTerminal2.setIntranetIp(busiTerminal.getIntranetIp());
					busiTerminal2.setSn(busiRegisterTerminal.getMac());
					busiTerminal2.setMqttOnlineStatus(busiTerminal.getMqttOnlineStatus());
					busiTerminal2.setAppVersionCode(busiRegisterTerminal.getAppVersionCode());
					busiTerminal2.setAppVersionName(busiRegisterTerminal.getAppVersionName());
					busiTerminal2.setConnectIp(busiRegisterTerminal.getConnectIp());
					AppType appType = AppType.convertByType(busiRegisterTerminal.getTerminalType());
					if (appType != null) {
						String oldAppType = busiTerminal2.getAppType();
						busiTerminal2.setAppType(appType.getCode());
						TerminalCache.getInstance().updateAppTypeTerminalMap(oldAppType, busiTerminal2);
					}
//					busiTerminalService.updateBusiTerminal(busiTerminal2);
					busiRegisterTerminalService.updateBusiTerminalStatus(busiTerminal2);
					busiRegisterTerminal.setTerminalId(busiTerminal2.getId());
					busiTerminals.add(busiTerminal2);
				}else {
					busiTerminal.setSn(busiRegisterTerminal.getMac());
					busiTerminal.setAppVersionCode(busiRegisterTerminal.getAppVersionCode());
					busiTerminal.setAppVersionName(busiRegisterTerminal.getAppVersionName());
					busiTerminal.setConnectIp(busiRegisterTerminal.getConnectIp());
					AppType appType = AppType.convertByType(busiRegisterTerminal.getTerminalType());
					if (appType != null) {
						String oldAppType = busiTerminal.getAppType();
						busiTerminal.setAppType(appType.getCode());
						TerminalCache.getInstance().updateAppTypeTerminalMap(oldAppType, busiTerminal);
					}
//					busiTerminalService.insertBusiTerminal(busiTerminal);
					busiRegisterTerminalService.insertBusiTerminalData(busiTerminal);
					busiRegisterTerminal.setTerminalId(busiTerminal.getId());
					busiTerminals.add(busiTerminal);
				}
				
				busiRegisterTerminal.setUpdateTime(new Date());
				busiRegisterTerminal.setCredential(busiTerminal.getCredential());
				busiRegisterTerminal.setIsRelated(MqttConfigConstant.SUCCESS.toString());
				
				int j = busiRegisterTerminalMapper.updateBusiRegisterTerminal(busiRegisterTerminal);
				if(j > 0) {
					if(TerminalType.isFSBC(busiTerminal.getType()) ) {
						
						//Fsbc类型
						TerminalSipAccount.getInstance().vhdTermminalGetSipAccount(busiTerminals.get(0), msgId, busiRegisterTerminal.getMac());
					}else if(TerminalType.isFCMSIP(busiTerminal.getType())){
						
						//Fcm类型
						TerminalSipAccount.getInstance().terminalGetSipAccount(msgId, busiTerminals.get(0));
					} else if (TerminalType.isZJ(busiTerminal.getType())) {

						//zj
						TerminalSipAccount.getInstance().zjTerminalGetSipAccount(busiTerminals.get(0), msgId, busiRegisterTerminal.getMac());
					} else if (TerminalType.isSMCSIP(busiTerminal.getType())) {

						// smc
						TerminalSipAccount.getInstance().smcTerminalGetSipAccount(busiTerminals.get(0), "", busiRegisterTerminal.getMac());
					} else if (TerminalType.isSMC2SIP(busiTerminal.getType())) {

						// smc2
						TerminalSipAccount.getInstance().smc2TerminalGetSipAccount(busiTerminals.get(0), "", busiRegisterTerminal.getMac());
					} else if (TerminalType.isHwCloud(busiTerminal.getType())) {

						// hwCloud
						TerminalSipAccount.getInstance().hwCloudTerminalGetSipAccount(busiTerminals.get(0), "", busiRegisterTerminal.getMac());
					}
				}
			}
		}
	}


	private BusiTerminal dealFcmType(BusiTerminal busiTerminal) {
		if(null != DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId())) {
			FcmBridge fcmBridge = null;
			BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
			if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
				if (busiTerminal.getFsServerId() != null) {
					fcmBridge = FcmBridgeCache.getInstance().get(busiTerminal.getFsServerId());
				}
				if (fcmBridge == null) {
					FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(fsd.getServerId());
					if (fcmBridgeCluster != null) {
						List<FcmBridge> fcmBridges = fcmBridgeCluster.getFcmBridges();
						// 由于使用固定用户信息数据库，任意一个FCM即可
						fcmBridge = fcmBridges.get(0);
					}
				}
			} else {
				fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
			}
			BusiFreeSwitch busiFreeSwitch = fcmBridge.getBusiFreeSwitch();
			busiTerminal.setIp(busiFreeSwitch.getIp());
		}else {
			 throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "请检查该租户是否绑定FCM服务!");
		}
		return busiTerminal;
	}

	private BusiTerminal dealFsbcType(BusiTerminal busiTerminal) {
		if(null != DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId())) {
			FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId()).getFsbcServerId());
			BusiFsbcRegistrationServer fsbcRegistrationServer = fsbcBridge.getBusiFsbcRegistrationServer();
			busiTerminal.setIp(fsbcRegistrationServer.getCallIp());
		}else {
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "请检查该租户是否绑定FSBC服务!");
		}
		return busiTerminal;
	}

	private BusiTerminal dealZjType(BusiTerminal busiTerminal) {
		if(null != DeptMcuZjMappingCache.getInstance().getBindMcu(busiTerminal.getDeptId())) {
			McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(DeptMcuZjMappingCache.getInstance().getBindMcu(busiTerminal.getDeptId()).getMcuId());
			BusiMcuZj busiMcuZj = mcuZjBridge.getBusiMcuZj();
			busiTerminal.setIp(busiMcuZj.getIp());
		}else {
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "请检查该租户是否绑定ZJ服务!");
		}
		return busiTerminal;
	}

	private BusiTerminal dealSmcType(BusiTerminal busiTerminal) {
		if(null != DeptSmc3MappingCache.getInstance().getBindSmc3(busiTerminal.getDeptId())) {
			Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().get(DeptSmc3MappingCache.getInstance().getBindSmc3(busiTerminal.getDeptId()).getMcuId());
			BusiMcuSmc3 busiMcuSmc3 = smc3Bridge.getBusiSMC();
			busiTerminal.setIp(busiMcuSmc3.getIp());
		}else {
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "请检查该租户是否绑定SMC3服务!");
		}
		return busiTerminal;
	}

	private BusiTerminal dealSmc2Type(BusiTerminal busiTerminal) {
		if(null != DeptSmc2MappingCache.getInstance().getBindSmc(busiTerminal.getDeptId())) {
			Smc2Bridge smc2Bridge = Smc2BridgeCache.getInstance().get(DeptSmc2MappingCache.getInstance().getBindSmc(busiTerminal.getDeptId()).getMcuId());
			BusiMcuSmc2 busiMcuSmc2 = smc2Bridge.getBusiSmc2();
			busiTerminal.setIp(busiMcuSmc2.getIp());
		}else {
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "请检查该租户是否绑定SMC2服务!");
		}
		return busiTerminal;
	}

	private BusiTerminal dealHwCloudType(BusiTerminal busiTerminal) {
		if(null != DeptHwcloudMappingCache.getInstance().getBindSmc(busiTerminal.getDeptId())) {
			HwcloudBridge hwcloudBridge = HwcloudBridgeCache.getInstance().get(DeptHwcloudMappingCache.getInstance().getBindSmc(busiTerminal.getDeptId()).getMcuId());
			BusiMcuHwcloud busiMcuHwcloud = hwcloudBridge.getBusiHwcloud();
			busiTerminal.setIp(busiMcuHwcloud.getCallIp());
		}else {
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "请检查该租户是否绑定华为云会议服务!");
		}
		return busiTerminal;
	}

	@Override
	public void inviteTerminalIntoConference(Long templateId, String sn) {
		String conferenceNumber = null;
		String conferencePassword = null;
		String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + sn;
		String action = TerminalTopic.INVITE_CONFERENCE;
		String videoConference = videoConferenceSDKService.getVideoConference(sn, 0, 100);
		if(StringUtils.isNotEmpty(videoConference)) {
			JSONObject object = JSONObject.parseObject(videoConference);
			JSONArray array = object.getJSONArray("records");
			if(null != array) {
				for (int i = 0; i < array.size(); i++) {
					JSONObject obj = (JSONObject) array.get(i);
					if(null != obj) {
						Long templateId1 = (Long)obj.getLong("templateId");
						if(templateId1.equals(templateId)) {
							JSONObject paramObj = obj.getJSONObject("params");
							JSONObject detailObj = paramObj.getJSONObject("templateDetails");
							JSONObject conferenceObj = detailObj.getJSONObject("templateConference");
							if(null != conferenceObj) {
								conferenceNumber = (String)conferenceObj.getString("conferenceNumber");
								conferencePassword = (String)conferenceObj.getString("conferencePassword");
								if(StringUtils.isEmpty(conferencePassword)) {
									conferencePassword = "";
								}
								break;
							}
							
						}
					}
				}
			}
		}
		
		JSONObject object = new JSONObject();
		object.put(InstantMeetingParam.CONFERENCENUM, conferenceNumber);
		object.put(InstantMeetingParam.PASSWORD, conferencePassword);
		terminalActionService.responseTerminal(terminalTopic, action, object, sn, "");
	}

	@Override
	public void hostSetConferenceBanner(BannerParams bannerParams) {
		String clientId = bannerParams.getSn();
		String action =  TerminalTopic.SET_BANNER;
		String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(MqttConfigConstant.CLIENTID, clientId);
		jsonObject.put(MqttConfigConstant.ACTION, action);
		jsonObject.put(MqttConfigConstant.JSON_DATA_STR, JSON.toJSON(bannerParams));
		PublisMessage.getInstance().publishTopicMsg(terminalTopic, clientId, jsonObject.toString(), false);
	}

	@Override
	public void hostSetConferenceScrollBanner(BannerParams bannerParams) {
		String clientId = bannerParams.getSn();
		String action =  TerminalTopic.SET_BANNER;
		String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(MqttConfigConstant.CLIENTID, clientId);
		jsonObject.put(MqttConfigConstant.ACTION, action);
		jsonObject.put(MqttConfigConstant.JSON_DATA_STR, JSON.toJSON(bannerParams));
		PublisMessage.getInstance().publishTopicMsg(terminalTopic, clientId, jsonObject.toString(), false);
	}

	@Override
	public int saveTerminalExcelFile(MultipartFile file, Long deptId) {
		Boolean isSuccess = false;
		int isZero = 0;
		Boolean fileType = this.checkFile(file);
		if(!fileType) {
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "请检查，上传的是否是EXCEL文件!");
		}
		
		//解析终端信息terminal文件
		try {
			List<BusiTerminal> busiTerminals = this.analysisFile(file, deptId);
			if(null != busiTerminals && busiTerminals.size() > 0) {
				busiTerminalService.insertBusiTerminals(busiTerminals);
				
				isSuccess =  true;
			}
		} catch (IOException e) {
			LOGGER.error("解析终端EXCEL文件失败!" , e);
		}
		
		if(isSuccess) {
			isZero = 1;
		}
		
		return isZero;
	}

	private List<BusiTerminal> analysisFile(MultipartFile file, Long deptId) throws IOException {
		
		//获取workbook对象
        Workbook workbook = null;
        String filename = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        
        try {
        	//根据后缀名是否excel文件
            if(filename.endsWith("xls")){
            	
                //2003
                workbook = new HSSFWorkbook(inputStream);
            }else if(filename.endsWith("xlsx")){
            	
                //2007
                workbook = new XSSFWorkbook(inputStream);
            }
		} catch (Exception e) {
			LOGGER.error("解析终端EXCEL文件异常!" , e);
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
			}
		}

        
        List<BusiTerminal> arrayList = new ArrayList<BusiTerminal>();
        if(workbook != null){
        	
            //循环sheet,现在是单sheet
            for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
            	
                //获取第一个sheet
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(null == sheet){
                    return arrayList;
                }

                //获取当前sheet开始行和结束行
                int firstRowNum = sheet.getFirstRowNum();
                int lastRowNum = sheet.getLastRowNum();
                
                //循环开始，除了前一行
                for(int rowNum = firstRowNum + 1;rowNum <= lastRowNum;rowNum++){
                	BusiTerminal busiTerminal = new BusiTerminal();
                	
                    //获取当前行
                    Row row = sheet.getRow(rowNum);
                    
                    //获取当前行的开始列和结束列
                    short firstCellNum = row.getFirstCellNum();
                    short lastCellNum = row.getLastCellNum();
                    
                    LoginUser loginUser = SecurityUtils.getLoginUser();
                    if(null == deptId) {
                    	deptId = loginUser.getUser().getDeptId();
                    }
                    
                    busiTerminal.setDeptId(deptId);
                    busiTerminal.setBusinessFieldType(100);
                    DataFormatter dataFormatter = new DataFormatter();
                   
                    //循环当前行
                    for(int cellNum = firstCellNum ; cellNum < lastCellNum ; cellNum++){
                        Cell cell = row.getCell(cellNum);
                        String cellValue = dataFormatter.formatCellValue(cell);
                        if(null != cell && cellValue.length() > 0) {
                        	busiTerminal = this.getCellValue(cell , cellNum , rowNum, busiTerminal);
                        }
                    }
                    
                    arrayList.add(busiTerminal);
                }
            }
        }
        
        return arrayList;
	}

	private BusiTerminal getCellValue(Cell cell, int cellNum, int rowNum, BusiTerminal busiTerminal) {
		String cellValue = null;
		//判断数据的类型
        switch (cell.getCellType()) {
            case NUMERIC: //数字0
                cellValue = new BigDecimal(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
                break;
            case STRING: //字符串1
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case BLANK: //空值
                cellValue = "";
                break;
            case ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
		
        //返回終端信息对象
        busiTerminal = this.terminalObjectInfo(cell, cellNum, rowNum, busiTerminal, cellValue);
        
        return busiTerminal;
	}

	private BusiTerminal terminalObjectInfo(Cell cell, int cellNum, int rowNum, BusiTerminal busiTerminal,String cellValue) {
        if(StringUtils.isEmpty(cellValue) && (cellNum == 0 || cellNum == 1 || cellNum == 4 )){
            throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "第"+(rowNum+1)+"行,第"+(cellNum+1)+"列为空!");
        }else {
        	switch (cellNum) {
            case 0: 
            	busiTerminal.setName(cellValue);
                break;
            case 1: 
            	String[] typeSp = cellValue.split(" ");
            	if(typeSp.length > 1) {
            		busiTerminal.setType(Integer.valueOf(typeSp[0]));
            	}else {
            		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "第"+(rowNum+1)+"行,第"+(cellNum+1)+"类型格式不正确!");
    			}
                break;
            case 2: 
            	if(StringUtils.isNotEmpty(cellValue)) {
            		busiTerminal.setCredential(new BigDecimal(cellValue).stripTrailingZeros().toPlainString());
            	}
            case 3: 
            	if(StringUtils.isNotEmpty(cellValue) && java.util.regex.Pattern.compile("^[+-]?[\\d]+([.][\\d]*)?([Ee][+-]?[\\d]+)?$").matcher(cellValue).matches()) {
            		busiTerminal.setPassword(new BigDecimal(cellValue).stripTrailingZeros().toPlainString());
            	}
            	else {
            		busiTerminal.setPassword(cellValue);
				}
                break;
            case 4: 
            	String[] conferenceSp = cellValue.split(" ");
            	if(conferenceSp.length > 1) {
            		busiTerminal.setAttendType(Integer.valueOf(conferenceSp[0]));
            	}else {
            		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "第"+(rowNum+1)+"行,第"+(cellNum+1)+"入会类型格式不正确!");
    			}
                break;
            case 5:
				String delSpace = busiTerminalService.delSpace(cellValue);
				busiTerminal.setSn(delSpace);
                break;
            case 6: 
            	if(TerminalType.isFCMSIP(busiTerminal.getType())) {
					FcmBridge fcmBridge = null;
					BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
					if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
						if (busiTerminal.getFsServerId() != null) {
							fcmBridge = FcmBridgeCache.getInstance().get(busiTerminal.getFsServerId());
						}
						if (fcmBridge == null) {
							FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(fsd.getServerId());
							if (fcmBridgeCluster != null) {
								List<FcmBridge> fcmBridges = fcmBridgeCluster.getFcmBridges();
								// 由于使用固定用户信息数据库，任意一个FCM即可
								fcmBridge = fcmBridges.get(0);
							}
						}
					} else {
						fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
					}
					BusiFreeSwitch busiFreeSwitch = fcmBridge.getBusiFreeSwitch();
					busiTerminal.setIp(busiFreeSwitch.getIp());
					busiTerminal.setIntranetIp(cellValue);
    			}else if(TerminalType.isFSBC(busiTerminal.getType())) {
					FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId()).getFsbcServerId());
					BusiFsbcRegistrationServer fsbcRegistrationServer = fsbcBridge.getBusiFsbcRegistrationServer();
					busiTerminal.setIp(fsbcRegistrationServer.getCallIp());
					busiTerminal.setIntranetIp(cellValue);
				} else if (TerminalType.isZJ(busiTerminal.getType())) {
            		McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(DeptMcuZjMappingCache.getInstance().getBindMcu(busiTerminal.getDeptId()).getMcuId());
            		BusiMcuZj busiMcuZj = mcuZjBridge.getBusiMcuZj();
            		busiTerminal.setIp(busiMcuZj.getIp());
            		busiTerminal.setIntranetIp(cellValue);
    			}else {
    				busiTerminal.setIp(cellValue);
				}
                break;
            case 7: 
//            	busiTerminal.setNumber(cellValue);
            	if (!StringUtil.isEmpty(cellValue)) {
            		busiTerminal.setNumber(new BigDecimal(cellValue).stripTrailingZeros().toPlainString());
				}
            	busiTerminal.setNumber(cellValue);
                break;
            case 8: 
            	busiTerminal.setCameraIp(cellValue);
            case 9:
				busiTerminal.setRemarks(cellValue);
				break;
            case 10:
				if (TerminalType.isZJ(busiTerminal.getType())) {
					busiTerminal.setExpiredDate(DateUtil.convertDateByString(cellValue, "yyyy/M/d"));
				}
				break;
            default:
                break;
        	}
		}
		return busiTerminal;
	}

	private Boolean checkFile(MultipartFile file) {
		
		//检查文件是否为空
        boolean empty = file.isEmpty();
        if(empty || file == null){
            return  false;
        }
        
        //检查文件是否是excel类型文件
        String filename = file.getOriginalFilename();
        if(!filename.endsWith("xls") && !filename.endsWith("xlsx")){
            return false;
        }
        return true;
	}

	/**
	 * 导出终端列表
	 * @param id
	 * @return
	 */
	@Override
	public List<ExcelTerminalOut> selectExcel(long id)
	{
		List<ExcelTerminalOut> listExcel = new ArrayList<>();
		ExcelTerminalOut excelTerminalOuts = excelTerminalOutList(busiTerminalMapper.selectBusiTerminalById(id));
		listExcel.add(excelTerminalOuts);

		return listExcel;
	}

	public ExcelTerminalOut excelTerminalOutList(BusiTerminal busiTerminal){
		List<ExcelTerminalOut> listExcel = new ArrayList<>();
		ExcelTerminalOut excelTerminalOut = new ExcelTerminalOut();
		excelTerminalOut.setCameraIp(busiTerminal.getCameraIp());
		excelTerminalOut.setNumber(busiTerminal.getNumber());
		excelTerminalOut.setIp(busiTerminal.getIp());
		excelTerminalOut.setSn(busiTerminal.getSn());
		excelTerminalOut.setAttendType(this.toText(busiTerminal.getAttendType()));
		excelTerminalOut.setPassword(busiTerminal.getPassword());
		excelTerminalOut.setCredential(busiTerminal.getCredential());
		excelTerminalOut.setType(busiTerminal.getType()+" "+TerminalType.convert(busiTerminal.getType()).getDisplayName());
		excelTerminalOut.setName(busiTerminal.getName());
		excelTerminalOut.setRemarks(busiTerminal.getRemarks());
		excelTerminalOut.setAppVersionCode(busiTerminal.getAppVersionCode());
		excelTerminalOut.setAppVersionName(busiTerminal.getAppVersionName());
		listExcel.add(excelTerminalOut);
		return excelTerminalOut;
	}

	//入会方式判断
	public String toText(int attendType){
		switch (attendType){
			case  1:
				return "1 被叫";
			case  2:
				return "2 手动主叫";
			case  3:
				return "3 自动主叫";
			case  10:
				return "10 直播";
		}
		return null;
	}

	/**
	 * 邀请或移除终端直播进入直播或者会议
	 * 0===无状态
	 * 1===直播
	 * 2===会议中
	 * @param mac
	 * @param conferenceId
	 * @param status
	 * @param conferenceNumber
	 * @param conferenceName
	 * @return
	 */
	@Override
	public int isInviteLiveTerminal(String mac,String conferenceId,String status,String conferenceNumber,String  conferenceName) {

		ConferenceContext conferenceContext1 = ConferenceContextCache.getInstance().getByConferenceId(conferenceId);
		//Assert.isTrue(!conferenceContext1.equals(null),"会议或者直播不存在");

		if (!conferenceContext1.equals(null)){
			String terminalTopic = "terminal/" + mac;
			String action = "liveTerminal";
			JSONObject jsonObject = new JSONObject();
//			List<String> stringList = new ArrayList<>();
//			try {
//				BusiLiveDept busiLiveDept = LiveDeptCache.getInstance().get(conferenceContext1.getDeptId());
//				List<BusiLive> streamUrlList = iMqttService.getStreamUrlList(conferenceContext1.getDeptId(), conferenceContext1.getConferenceNumber(), busiLiveDept);
//				if (streamUrlList != null && streamUrlList.size() > 0) {
//					for (BusiLive busiLive : streamUrlList) {
//						if (busiLive.getDomainName() != null && busiLive.getDomainName().length() > 0) {
//							busiLive.setIp(busiLive.getDomainName());
//						}
//						String url = busiLive.getProtocolType() + "://" + busiLive.getIp() + "/" + busiLive.getUriPath() + "/" + conferenceContext1.getConferenceNumber();
//						stringList.add(url);
//					}
//				}
//			} catch (Exception e) {
//
//			}
			List<String> stringList = conferenceContext1.getStreamUrlList();

			switch (status){
				case "0":
					//String action = "liveTerminal";
					//JSONObject jsonObject = new JSONObject();
					jsonObject.put("status",status);
					jsonObject.put("conferenceNum",conferenceNumber);
					jsonObject.put("conferenceName",conferenceContext1.getName());
					ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, mac, "");
					LOGGER.info("直播终端=="+mac+"==状态0==未进入直播和会议");
					break;
				case "1":
					//String terminalTopic = "terminal/" + mac;
					if (conferenceContext1.isStreaming()) {
						int nextInt = 0;
						String streamingUrl = conferenceContext1.getStreamingUrl();
						if (stringList.size() > 0) {
							nextInt = new Random().nextInt(stringList.size());
							streamingUrl = stringList.get(nextInt);
						}
						jsonObject.put("conferenceNum", conferenceNumber);
						jsonObject.put("liveUrl", conferenceContext1.getIsAutoCreateStreamUrl() == 1 ? streamingUrl : conferenceContext1.getStreamingUrl());
						jsonObject.put("status", status);
						jsonObject.put("conferenceName", conferenceContext1.getName());
						ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, mac, "");
						LOGGER.info("直播终端==" + mac + "==状态1==进入直播");
					} else {
						throw new SystemException("未开启直播，不能邀请终端进入直播！");
					}
					break;
				case "2":
					jsonObject.put("conferenceName",conferenceName);
					if (conferenceContext1.getConferencePassword() != null) {
						jsonObject.put("conferencePassword",conferenceContext1.getConferencePassword());
					}
					jsonObject.put("conferenceNum",conferenceNumber);
					jsonObject.put("conferenceName",conferenceContext1.getName());
					jsonObject.put("status",status);
					ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, mac, "");
					LOGGER.info("直播终端=="+mac+"==状态1==进入会议"+conferenceNumber);
					break;
				default:
					break;
			}
		}
		return 0;
	}

	/**
	 * 终端一键升级
	 * @return
	 */
	@Override
	public String terminalRemoteUpgradeAll(long id) {
		AppUpgradeTask appUpgradeTask = new AppUpgradeTask(String.valueOf(id), 60000);
		taskService.addTask(appUpgradeTask);

		String responseStr = "终端升级命令将在数分钟内下发。如要立即下发，请前往终端列表中操作！";
		return responseStr;
	}

}
