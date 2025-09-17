package com.paradisecloud.fcm.terminal.fs.impls;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.dao.mapper.BusiFreeSwitchClusterMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchClusterMap;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.terminal.fs.common.SshRemoteServerOperateOrdinary;
import com.paradisecloud.fcm.terminal.fs.db.FreeSwitchDatabaseManager;
import com.paradisecloud.fcm.terminal.fs.server.FreeSwitchServerManager;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.freeswitch.esl.client.transport.message.EslMessage;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.mapper.BusiFreeSwitchDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiFreeSwitchMapper;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.common.FileConvert;
import com.paradisecloud.fcm.terminal.fs.common.NumberDeal;
import com.paradisecloud.fcm.terminal.fs.common.SshRemoteServerOperate;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfig;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import com.paradisecloud.fcm.terminal.fs.constant.FcmTipConstant;
import com.paradisecloud.fcm.terminal.fs.interfaces.IBusiFreeSwitchService;
import com.paradisecloud.fcm.terminal.fs.model.BusiFcmConfig;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FreeSwitchUser;
import com.paradisecloud.fcm.terminal.fs.model.NfsConfig;
import com.paradisecloud.fcm.terminal.fs.model.TurnServerConf;
import com.paradisecloud.fcm.terminal.fs.model.XmlEntity;
import com.sinhy.exception.SystemException;
import com.sinhy.utils.RegExpUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

/**
 * 服务器资源信息Service业务层处理
 * 
 * @author zyz
 * @date 2021-09-02
 */
@Service("busiFreeSwitchService")
public class BusiFreeSwitchServiceImpl implements IBusiFreeSwitchService 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(BusiFreeSwitchServiceImpl.class);
	
    @Autowired
    private BusiFreeSwitchMapper busiFreeSwitchMapper;
    
    @Autowired
    private BusiFreeSwitchDeptMapper busiFreeSwitchDeptMapper;

	@Resource
	private BusiFreeSwitchClusterMapMapper busiFreeSwitchClusterMapMapper;
    
    @Autowired 
    private Environment environment;
    
    @Value("${application.home}")
	private String projectPath;
    
    private static Map<String, Object> map = new HashMap<String, Object>();

    /**
     * 查询服务器资源信息
     * 
     * @param id 服务器资源信息ID
     * @return 服务器资源信息
     */
    @Override
    public BusiFreeSwitch selectBusiFreeSwitchById(Long id)
    {
        return busiFreeSwitchMapper.selectBusiFreeSwitchById(id);
    }

    /**
     * 查询服务器资源信息列表
     * 
     * @param busiFreeSwitch 服务器资源信息
     * @return 服务器资源信息
     */
    @Override
    public List<BusiFreeSwitch> selectBusiFreeSwitchList(BusiFreeSwitch busiFreeSwitch)
    {
        return busiFreeSwitchMapper.selectBusiFreeSwitchList(busiFreeSwitch);
    }

    /**
     * 新增服务器资源信息
     * 
     * @param busiFreeSwitch 服务器资源信息
     * @return 结果
     */
    @Override
    public int insertBusiFreeSwitch(BusiFreeSwitch busiFreeSwitch)
    {
    	if(StringUtils.isEmpty(busiFreeSwitch.getIp())) 
    	{
    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, FcmTipConstant.SERVER_IP_TIP);
    	}
    	
    	if(StringUtils.isEmpty(busiFreeSwitch.getName())) 
    	{
    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, FcmTipConstant.SERVER_NAME);
    	}
    	
    	if(!RegExpUtils.isIP(busiFreeSwitch.getIp())) 
    	{
    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ONE_TH, FcmTipConstant.IP_TIP);
    	}
    	
    	if(StringUtils.isEmpty(busiFreeSwitch.getUserName())) 
    	{
    		busiFreeSwitch.setUserName(FcmConfigConstant.SERVER_DEFAULT_USER_NAME);
    	}
    	
    	if(StringUtils.isEmpty(busiFreeSwitch.getPassword())) 
    	{
    		busiFreeSwitch.setPassword(FcmConfigConstant.DEFAULT_PASSWORD);
    	}
    	
    	if(null == busiFreeSwitch.getPort()) 
    	{
    		busiFreeSwitch.setPort(FcmConfigConstant.DEFAULT_SERVER_PORT);
    	}
    	
        busiFreeSwitch.setCreateTime(new Date());
        int freeSwitch = busiFreeSwitchMapper.insertBusiFreeSwitch(busiFreeSwitch);
        if(freeSwitch > 0) {
        	FcmBridgeCache.getInstance().update(new FcmBridge(busiFreeSwitch));
			// 添加free switch数据库连接池
			FreeSwitchDatabaseManager.getInstance().addConnectionIp(busiFreeSwitch.getIp());
			// 添加free switch服务器连接
			FreeSwitchServerManager.getInstance().addConnectionIp(busiFreeSwitch.getIp(), busiFreeSwitch);
        }
        return freeSwitch;
    }

    /**
     * 修改服务器资源信息
     * 
     * @param busiFreeSwitch 服务器资源信息
     * @return 结果
     */
    @Override
    public int updateBusiFreeSwitch(BusiFreeSwitch busiFreeSwitch)
    {
        busiFreeSwitch.setUpdateTime(new Date());
        int updateBusiFreeSwitch = busiFreeSwitchMapper.updateBusiFreeSwitch(busiFreeSwitch);

        if (updateBusiFreeSwitch > 0) {
            FcmBridgeCache.getInstance().update(new FcmBridge(busiFreeSwitch));
			// 初始化free switch数据库连接池
			FreeSwitchDatabaseManager.getInstance().addConnectionIp(busiFreeSwitch.getIp());
			// 添加free switch服务器连接
			FreeSwitchServerManager.getInstance().addConnectionIp(busiFreeSwitch.getIp(), busiFreeSwitch);
//            if (!StringUtils.isEmpty(fcmDate)){
           //this.updateFcmServerDate(busiFreeSwitch,fcmDate);
//        	}
     	}
        return updateBusiFreeSwitch;
    }

    private Boolean updateFcmServerDate(BusiFreeSwitch busiFreeSwitch,String fcmDate){
    	Boolean isEmpty = false;
    	
		//判断输入值是否合法,Fcm服务器是否存在可连接
		if (!StringUtils.isEmpty(String.valueOf(busiFreeSwitchMapper.selectBusiFreeSwitchById(busiFreeSwitch.getId())))){
			//连接服务器
			//发送指令
			try
			{
				SshRemoteServerOperate.getInstance().sshRemoteCallLogin(busiFreeSwitch.getIp(),busiFreeSwitch.getUserName(),busiFreeSwitch.getPassword(),busiFreeSwitch.getPort());
				if (SshRemoteServerOperate.getInstance().isLogined()){
					//修改Fcm服务器时间年月日时分秒
					String fileName = SshRemoteServerOperate.getInstance().execCommand("sudo date -s"+"'"+fcmDate+"'");
					if(StringUtils.isNotEmpty(fileName)) {
						isEmpty = true;
					}
					
					SshRemoteServerOperate.getInstance().closeSession();
				}
			}
			catch (Exception e)
			{
				LOGGER.error("=============>>>date -s指令失败" ,e );
				return isEmpty;
			}
		}
		return isEmpty;
}

    /**
     * 批量删除服务器资源信息
     * 
     * @param ids 需要删除的服务器资源信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiFreeSwitchByIds(Long[] ids)
    {
		for (int i = 0; i < ids.length; i++) {
			deleteBusiFreeSwitchById(ids[i]);
		}
        return ids.length;
    }

    /**
     * 删除服务器资源信息信息
     * 
     * @param id 服务器资源信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiFreeSwitchById(Long id)
    {
		FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(id);
		if (fcmBridge != null)
		{
			BusiFreeSwitchDept con1 = new BusiFreeSwitchDept();
			con1.setServerId(id);
			con1.setFcmType(FcmType.SINGLE_NODE.getValue());
			List<BusiFreeSwitchDept> fds = busiFreeSwitchDeptMapper.selectBusiFreeSwitchDeptList(con1);
			if (!ObjectUtils.isEmpty(fds))
			{
				throw new SystemException(1000016, "该FCM正在被租户使用，不能删除！");
			}

			BusiFreeSwitchClusterMap con2 = new BusiFreeSwitchClusterMap();
			con2.setFreeSwitchId(id);
			List<BusiFreeSwitchClusterMap> fcms = busiFreeSwitchClusterMapMapper.selectBusiFreeSwitchClusterMapList(con2);
			if (!ObjectUtils.isEmpty(fcms))
			{
				throw new SystemException(1000016, "该FCM正在集群中，无法删除，请先从集群中剔除，再删除！");
			}
			FcmBridgeCache.getInstance().remove(fcmBridge.getBusiFreeSwitch().getId());
		}

		try {
			int del = busiFreeSwitchMapper.deleteBusiFreeSwitchById(id);
		} catch (Exception e) {
			throw new SystemException(1000016, fcmBridge.getBusiFreeSwitch().getName() + "删除失败！");
		}

		FcmBridgeCache.getInstance().remove(id);
		FreeSwitchDatabaseManager.getInstance().removeConnectionIp(fcmBridge.getBusiFreeSwitch().getIp());
		FreeSwitchServerManager.getInstance().removeConnectionIp(fcmBridge.getBusiFreeSwitch().getIp());

		return 1;
    }

	@Override
	public List<ModelBean> getServerConfigurationInfo() {
		List<ModelBean> modelBeans = new ArrayList<ModelBean>();
		BusiFreeSwitch busiFreeSwitch = new BusiFreeSwitch();
		List<BusiFreeSwitch> busiFreeSwitchList = busiFreeSwitchMapper.selectBusiFreeSwitchList(busiFreeSwitch);
		for (BusiFreeSwitch freeSwitch : busiFreeSwitchList) {
			if (freeSwitch.getCallPort() == null) {
				freeSwitch.setCallPort(5060);
			}
			ModelBean mdb = new ModelBean(freeSwitch);
			int status = FcmBridgeCache.getInstance().get(freeSwitch.getId()).isAvailable() ? 1 : 2;
			mdb.put("status", status);
			mdb.remove("createTime");
			
			BusiFreeSwitchDept busiFreeSwitchDept = new BusiFreeSwitchDept();
			busiFreeSwitchDept.setServerId(freeSwitch.getId());
			List<BusiFreeSwitchDept> busiFreeSwitchDeptList = busiFreeSwitchDeptMapper.selectBusiFreeSwitchDeptList(busiFreeSwitchDept);
			if(null != busiFreeSwitchDeptList && busiFreeSwitchDeptList.size() > 0) {
				mdb.put("bindTenantCount", busiFreeSwitchDeptList.size());
			}else {
				mdb.put("bindTenantCount", 0);
			}
			modelBeans.add(mdb);
		}
		return modelBeans;
	}

	@Override
	public Map<String, Object> getFcmConfigFileInfo(BusiFreeSwitchDept busiFreeSwitchDept) {
		Map<String, Object> freeSwitchFileMaps = new HashMap<String, Object>();
		
		//获取部门绑定的服务器ip
		BusiFreeSwitch busiFreeSwitch = this.getDeptBindServerInfo(busiFreeSwitchDept);
		
		if(null != busiFreeSwitch) {
			 freeSwitchFileMaps = this.getFreeSwitchFileMaps(busiFreeSwitch);
		}
		
		return freeSwitchFileMaps;
	}
	
	/**
	 * @param busiFreeSwitchDept
	 * @return
	 */
	public BusiFreeSwitch getDeptBindServerInfo(BusiFreeSwitchDept busiFreeSwitchDept) {
		if(null != busiFreeSwitchDept) {
			List<BusiFreeSwitchDept> switchDeptList = busiFreeSwitchDeptMapper.selectBusiFreeSwitchDeptList(busiFreeSwitchDept);
			if(null != switchDeptList && switchDeptList.size() > 0) {
				BusiFreeSwitch busiFreeSwitch = busiFreeSwitchMapper.selectBusiFreeSwitchById(switchDeptList.get(0).getServerId());
				return busiFreeSwitch;
			}
		}
		return null;
	}

	private Map<String, Object> getFreeSwitchFileMaps(BusiFreeSwitch busiFreeSwitch) 
    {

		Map<String, Object> varMaps = new HashMap<String, Object>();
		
		Integer serverPort = busiFreeSwitch.getPort();
		String userName = busiFreeSwitch.getUserName();
		String password = busiFreeSwitch.getPassword();
		String ipAddr = busiFreeSwitch.getIp();
		
		//执行vars.xml
		varMaps = this.execVarsXml(userName, ipAddr, serverPort, password, varMaps);
		
		//执行verto.conf.xml
		varMaps = this.execVertoConfXml(userName, ipAddr, serverPort, password, varMaps);
		
		return varMaps;
	}
	
	/**
	 * 执行verto.conf.xml
	 * @param userName
	 * @param ipAddr
	 * @param serverPort
	 * @param password
	 * @param varMaps
	 * @return
	 */
	private Map<String, Object> execVertoConfXml(String userName, String ipAddr, Integer serverPort, String password,
			Map<String, Object> varMaps) {
		try 
		{
			
			//获取服务器上的相关文件
			String fileContent = this.getServerRelatedFile(ipAddr, userName, password, serverPort, FcmConfigConstant.VERTO_CONF_XML);
	        
	        varMaps = this.vertoConvertJson(fileContent, varMaps);
		} 
		catch (Exception e) 
		{
			throw new SystemException(100334, "获取FS的verto.conf.xml文件失败!");
		}
		return varMaps;
	}
	
	/**
	 * 执行vars.xml
	 * @param serverPort 
	 * @param ipAddr 
	 * @param userName 
	 * @param password 
	 * @param varMaps 
	 */
	private Map<String, Object> execVarsXml(String userName, String ipAddr, Integer serverPort, String password, Map<String, Object> varMaps) {
		try 
		{
			
			//获取服务器上的相关文件
			String varsStr = this.getServerRelatedFile(ipAddr, userName, password, serverPort, FcmConfigConstant.VARS_XML);
	        
	        varMaps = this.xmlStrConvertJson(varsStr, varMaps);
	        
		} 
		catch (Exception e) 
		{
			throw new SystemException(100334, "获取FS的vars.xml文件失败!");
		}
		return varMaps;
	}
	
	/**
	 * 获取服务器上的相关文件
	 * @param ipAddr
	 * @param userName
	 * @param password
	 * @param serverPort
	 * @param vertoConfXml
	 * @return
	 */
	private String getServerRelatedFile(String ipAddr, String userName, String password, Integer serverPort,String xmlName) 
	{
		String fileStr = null;
		try 
		{
			SshRemoteServerOperate.getInstance().sshRemoteCallLogin(ipAddr, userName, password, serverPort);
			fileStr = SshRemoteServerOperate.getInstance().execCommand("cat ".concat(FcmConfigConstant.XML_FILE_PATH + xmlName));
		} 
		catch (Exception e) 
		{
			throw new SystemException(100334, "获取FS的 "+xmlName+" 文件失败!");
		}
		
		return fileStr;
	}
	
	private Map<String, Object> vertoConvertJson(String xmlStr, Map<String, Object> varMaps) 
	{
		if(StringUtils.isNotEmpty(xmlStr))
		{
			String jsonStr = XML.toJSONObject(xmlStr).toString();
			JSONObject jsonObj = (JSONObject) JSONObject.parse(jsonStr);
			JSONObject object = jsonObj.getJSONObject(FcmConfig.CONFIGURATION);
			JSONObject objectPro = object.getJSONObject(FcmConfig.PROFILES);
			JSONArray jsonArray = objectPro.getJSONArray(FcmConfig.PROFILE);
			
			if(null != jsonArray && jsonArray.size() > 0) 
			{
				JSONArray jsonArrayParam = jsonArray.getJSONObject(0).getJSONArray(FcmConfig.PARAM);
				for (int i = 0; i < jsonArrayParam.size(); i++) 
				{
					boolean isSecure = jsonArrayParam.getJSONObject(i).containsKey(FcmConfig.SECURE);
					String ipName = jsonArrayParam.getJSONObject(i).getString(FcmConfig.NAME);
					String localIp = jsonArrayParam.getJSONObject(i).getString(FcmConfig.VALUE);
					String[] splitPort = localIp.split(FcmConfigConstant.COLON);
					if(null != splitPort && splitPort.length > 1) 
					{
						if(FcmConfig.BIND_LOCAL.equals(ipName) && isSecure) 
						{
							varMaps.put(FcmConfig.VERTOPORT_WSS, splitPort[1]);
						}
						else 
						{
							varMaps.put(FcmConfig.VERTOPORT_WS, splitPort[1]);
						}
					}
					
				}
			}
		}
		return varMaps;
	}
	
	/**
	 * 把xml的字符串转换为json
	 * @param xmlStr
	 * @param varMaps 
	 */
	public Map<String, Object> xmlStrConvertJson(String xmlStr, Map<String, Object> varMaps) 
	{
		if(StringUtils.isNotEmpty(xmlStr))
		{
			String jsonStr = XML.toJSONObject(xmlStr).toString();
			JSONObject jsonObj = (JSONObject) JSONObject.parse(jsonStr);
			JSONObject object = jsonObj.getJSONObject(FcmConfig.INCLUDE);
			JSONArray jsonArray = object.getJSONArray(FcmConfig.X_PRE_PROCESS);
			
			//数组类型的递归处理
			varMaps = this.arrayRecursion(jsonArray, varMaps);
			
		}
		return varMaps;
	}
	
	/**
	 * 数组类型的递归处理
	 * @param jsonArray
	 * @param varMaps
	 * @return
	 */
	private Map<String, Object> arrayRecursion(JSONArray jsonArray, Map<String, Object> varMaps) 
	{
		if (null != jsonArray && jsonArray.size() > 0) 
		{
			for (int i = 0; i < jsonArray.size(); i++) 
			{
				Object obj = jsonArray.get(i);
				if (obj instanceof JSONObject)
				{
					String dataStr = ((JSONObject) obj).getString(FcmConfig.DATA);
					if (StringUtils.isNotEmpty(dataStr)) 
					{
						String[] varSplit = dataStr.split(FcmConfig.EQUAL);
						if(null != varSplit && varSplit.length > 1) 
						{
							String name = varSplit[0];
							String value = varSplit[1];
							switch (name) {
							case FcmConfig.DEFAULT_PASSWORD:
								varMaps.put(FcmConfig.DEFAULT_PASSWORD_KEY, value);
								break;
							case FcmConfig.USER_EXTERNAL_IP:
								varMaps.put(FcmConfig.USER_EXTERNAL_IP_KEY, value);
								break;
							case FcmConfig.USER_LOCAL_IP:
								varMaps.put(FcmConfig.USER_LOCAL_IP_KEY, value);
								break;
							case FcmConfig.INTERNAL_SIP_PORT:
								varMaps.put(FcmConfig.INTERNAL_SIP_PORT_KEY, value);
								break;
							case FcmConfig.USER_PUBLIC_IP:
								varMaps.put(FcmConfig.USER_PUBLIC_IP_KEY, value);
								break;
							default:
								break;
							}
						}
					}
				}
				else 
				{
					JSONArray array = ((JSONArray) obj);
					varMaps = arrayRecursion(array, varMaps);
				}
					
			}
		}
		return varMaps;
	}

	@Override
	public int operateFcmConfig(BusiFcmConfig busiFcmConfig) {

		if(StringUtils.isEmpty(busiFcmConfig.getDefaultPassword())) 
		{
			new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, "默认密码不能为空!");
		}
		
		if(StringUtils.isEmpty(busiFcmConfig.getInternalSipPort())) 
		{
			new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, "fcmPort不能空!");
		}
		
		if(StringUtils.isEmpty(busiFcmConfig.getUserExternalIp())) 
		{
			new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, "userExteranlIp不能为空!");
		}
		
		if(!RegExpUtils.isIP(busiFcmConfig.getUserExternalIp())) 
    	{
			new SystemException(FcmConfigConstant.EXCEPTION_ONE_ONE_TH, FcmTipConstant.IP_TIP);
    	}
		
		if(StringUtils.isEmpty(busiFcmConfig.getUserLocalIp())) 
		{
			new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, "userLocalIp不能为空!");
		}
		
		if(!RegExpUtils.isIP(busiFcmConfig.getUserLocalIp())) 
    	{
			new SystemException(FcmConfigConstant.EXCEPTION_ONE_ONE_TH, FcmTipConstant.IP_TIP);
    	}
		
		if(StringUtils.isEmpty(busiFcmConfig.getVertoPortWs())) 
		{
			new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, "vertoPortWs不能为空!");
		}
		
		if(StringUtils.isEmpty(busiFcmConfig.getVertoPortWss())) 
		{
			new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, "vertoPortWss不能为空!");
		}
		
		try 
		{
			
			XmlEntity xmlEntity = new XmlEntity();
			xmlEntity.setXmlStr(busiFcmConfig.getWssPem());
			xmlEntity.setFileName(FcmConfig.WSS_PEM);
			
			//更新服务器上的文件
			this.updateServerXmlFile(busiFcmConfig);
			
			//上传文件到服务器
//			this.uploadXmlFileStr(xmlEntity);
		} 
		catch (Exception e) 
		{
			throw new SystemException(100334, "上传FS配置文件失败!");
		}
		
		return FcmConfigConstant.SUCCESS;
	}
	
	private void updateServerXmlFile(BusiFcmConfig busiFcmConfig) 
	{
		BusiFreeSwitch busiFreeSwitch = busiFreeSwitchMapper.selectBusiFreeSwitchById(busiFcmConfig.getId());
		if(null != busiFreeSwitch) {
			Integer serverPort = busiFreeSwitch.getPort();
			String userName = busiFreeSwitch.getUserName();
			String password = busiFreeSwitch.getPassword();
			String ipAddr = busiFreeSwitch.getIp();
			
			try {
				//处理前端端修改了vars.xml文件的一系列操作
				this.dealFrontEndUpdateXmlFile(ipAddr, userName, password, serverPort, FcmConfigConstant.VARS_XML, busiFcmConfig);
				
				//处理verto.conf.xml
				this.dealVertoConfXml(ipAddr, userName, password, serverPort, FcmConfigConstant.VERTO_CONF_XML, busiFcmConfig);
			} 
			catch (Exception e) 
			{
				throw new SystemException(100334, "更新FS配置文件失败!");
			}
		}
	}
	
	public int uploadXmlFileStr(XmlEntity xmlEntity) 
	{
		int fcmFlg = FcmConfigConstant.ZERO;
		//获取项目的相对路径
		String path = projectPath + FcmConfig.XML_PARENT + System.currentTimeMillis();
		try 
		{
			if(StringUtils.isNotEmpty(path)) 
			{
				String fileName = xmlEntity.getFileName();
				String xmlStr = xmlEntity.getXmlStr();
				boolean isSuccess = FileConvert.getInstance().string2File(xmlStr, path, xmlEntity.getFileName());
				if(isSuccess)
				{	
					String updateFilePath = null;
					if(fileName.equals(FcmConfig.WSS_PEM)) 
					{
						updateFilePath = FcmConfigConstant.WSS_PEM_PATH;
					}
					else 
					{
						if(fileName.equals(FcmConfig.DEFAULT_XML) || fileName.equals(FcmConfig.PUBLIC_XML)) 
						{
							updateFilePath = FcmConfigConstant.DEFAULT_PUBLIC_PATH;
						}
						
						if(fileName.equals(FcmConfig.DISTRIBUTOR_CONF_XML)) 
						{
							updateFilePath = FcmConfigConstant.XML_FILE_PATH + FcmConfigConstant.AUTOLOAD_CONFIGS;
						}
						
						BusiFcmConfig busiFcmConfig = new BusiFcmConfig();
						busiFcmConfig.setId(xmlEntity.getId());
						//与服务器的连接
						this.connectServerFs(busiFcmConfig);
					}
					
					//在把文件上传到服务器上
					SshRemoteServerOperate.getInstance().uploadFile(updateFilePath + fileName, path + FcmConfigConstant.SLASH + fileName);
					FileConvert.getInstance().deleteAllFiles(new File(path));
				}
			}
			fcmFlg = FcmConfigConstant.SUCCESS;
			SshRemoteServerOperate.getInstance().closeSession();
		} 
		catch (Exception e) 
		{
			fcmFlg = FcmConfigConstant.ZERO;
			throw new SystemException(100334, "上传FS配置文件失败!");
		}
		
		return fcmFlg;
	}
	
	/**
	 * 处理前端端修改了vars.xml文件的一系列操作
	 * @param ipAddr
	 * @param userName
	 * @param password
	 * @param serverPort
	 * @param varsXml
	 * @param busiFcmConfig 
	 */
	private void dealFrontEndUpdateXmlFile(String ipAddr, String userName, String password, Integer serverPort,String xmlName, BusiFcmConfig busiFcmConfig) {
		String varsFile = this.getServerRelatedFile(ipAddr, userName, password, serverPort, xmlName);
		if(StringUtils.isNotEmpty(varsFile)) 
		{
			//修改服务器上xml的属性
			String xmlFileCon = this.editServerXmlFile(varsFile, busiFcmConfig, xmlName);
			
			//获取项目的相对路径
			String path = projectPath + FcmConfig.XML_PARENT + System.currentTimeMillis();
			if(StringUtils.isNotEmpty(path)) 
			{
				//生成xml文件，在项目的指定目录
				boolean isSuccess = FileConvert.getInstance().string2File(xmlFileCon, path, xmlName);
				if(isSuccess)
				{
					//在把文件上传到服务器上
					SshRemoteServerOperate.getInstance().uploadFile(FcmConfigConstant.XML_FILE_PATH + xmlName, path + FcmConfigConstant.SLASH + xmlName);
					FileConvert.getInstance().deleteAllFiles(new File(path));
				}
			}
		}
		
	}
	
	/**
	 * 处理verto.conf.xml
	 * @param ipAddr
	 * @param userName
	 * @param password
	 * @param serverPort
	 * @param vertoConfXml
	 * @param busiFcmConfig
	 */
	private void dealVertoConfXml(String ipAddr, String userName, String password, Integer serverPort,
			String vertoConfXml, BusiFcmConfig busiFcmConfig) 
	{
		this.dealFrontEndUpdateXmlFile(ipAddr, userName, password, serverPort, vertoConfXml, busiFcmConfig);
	}
	
	/**
	 * 修改服务器上xml的属性
	 * @param varsFile
	 * @param busiFcmConfig
	 * @param xmlName 
	 * @return 
	 */
	private String editServerXmlFile(String varsFile, BusiFcmConfig busiFcmConfig, String xmlName) 
	{
		Document document = null;
		try 
		{
			
			//读取XML文件，获得document对象
			document = DocumentHelper.parseText(varsFile);
			
			//获得某个节点的属性对象
	        Element rootElem = document.getRootElement();
	        
	        //循环子节点
	        List<Element> elements =rootElem.elements();
	        if(null != elements && elements.size() > 0) 
	        {
	       	 for(Element element : elements) 
	       	 { 
	       		 if (xmlName.contains(FcmConfigConstant.DISTRIBUTOR_CONF_XML)) 
	       		 {
	       			//处理distributor.conf.xml文件
	       			element = this.getChildNodes(element, busiFcmConfig);
				 }
	       		 
	       		if (xmlName.contains(FcmConfigConstant.VERTO_CONF_XML)) 
	       		 {
	       			//处理verto.conf.xml文件
	       			element = this.getVertoConfXmlNodes(rootElem.element(FcmConfig.PROFILES).element(FcmConfig.PROFILE), busiFcmConfig);
				 }
	       		
	       		 if(xmlName.contains(FcmConfigConstant.VARS_XML)) 
	       		 {
	       			 //处理vars.xml的节点
	       			 element =  this.getVarsXmlNodes(element, busiFcmConfig);
	       		 }
	       		
	       	   }
	       }
		} 
		catch (DocumentException e) 
		{
			throw new SystemException(100334, "修改FS服务的 "+xmlName+" 文件失败!");
		}
        return document.asXML();
	}
	
	/**获取项目的相对路径
	 * @return
	 */
//	public String getProjectPath() 
//	{
//		String property = System.getProperty(FcmConfig.CATALOG);
//		String path = property + FcmConfig.XML_PARENT;
//		return path;
//	}
	
	private Element getChildNodes(Element element, BusiFcmConfig busiFcmConfig) 
	{
		Iterator<Node> it = element.nodeIterator();
		while (it.hasNext())
		{
			Node node = it.next();
			if (node instanceof Element)
			{
				Element ele = (Element)node;
				String name = ele.getName();
				if(!name.equals(FcmConfig.NODE)) 
				{
					 getChildNodes(ele, busiFcmConfig);
				}
			}
		}
		return element;
	}
	
	/**
	 * 处理verto.conf.xml文件
	 * @param element
	 * @param busiFcmConfig
	 * @return
	 */
	private Element getVertoConfXmlNodes(Element element, BusiFcmConfig busiFcmConfig) 
	{
		Boolean comeFlg = false;
		Iterator<Node> it = element.nodeIterator();
		while (it.hasNext())
		{
			Node node = it.next();
			if (node instanceof Element)
			{
				Element ele = (Element)node;
				String name = ele.getName();
				if(!name.equals(FcmConfig.PARAM)) 
				{
					getChildNodes(ele, busiFcmConfig);
				}
				else 
				{
					List<Attribute> attributes = ele.attributes();
					for(Attribute attr1 : attributes)
	       		 	{
						 String attrVal1 = attr1.getValue();
						 String conAttrName1 = attr1.getName();
						 if(attrVal1.contains(FcmConfig.LOCAL_IP_V4)) 
						 {
							 if (!comeFlg) 
							 {
								 ele.addAttribute(conAttrName1, FcmConfig.LOCAL_IP_V4 + busiFcmConfig.getVertoPortWs());
							 }
							 else 
							 {
								 ele.addAttribute(conAttrName1, FcmConfig.LOCAL_IP_V4 + busiFcmConfig.getVertoPortWss());
							 }
							 comeFlg = true;
						 }
						 
	       		 	}
				}
			}
		}
		return element;
	}
	
	/**
	 * 与服务器的连接
	 * @param busiFreeSwitchDept 
	 */
	public Boolean connectServer(BusiFreeSwitchDept busiFreeSwitchDept) {
		Boolean isConnect = false;
		BusiFreeSwitch busiFreeSwitch = this.getDeptBindServerInfo(busiFreeSwitchDept);
		if(null != busiFreeSwitch) {
			Integer serverPort = busiFreeSwitch.getPort();
			String userName = busiFreeSwitch.getUserName();
			String password = busiFreeSwitch.getPassword();
			String ipAddr = busiFreeSwitch.getIp();
			
			try 
			{
				SshRemoteServerOperate.getInstance().sshRemoteCallLogin(ipAddr, userName, password, serverPort);
				isConnect = true;
			} 
			catch (Exception e) 
			{
				isConnect = false;
				throw new SystemException(100334, "连接FS服务失败!");
			}
		}
	
		return isConnect;
	}
	
	/**
	 * 处理vars.xml的节点
	 * @param element
	 * @param busiFcmConfig
	 * @return
	 */
	private Element getVarsXmlNodes(Element element, BusiFcmConfig busiFcmConfig) {
		 List<Attribute> attributes =element.attributes();
   		 for(Attribute attr : attributes)
   		 {
   			 	 String conTxt = attr.getValue();
	             String conAttrName = attr.getName();
	             if(conTxt.contains(FcmConfig.DEFAULT_PASSWORD + FcmConfig.EQUAL)) {
	            	 element.addAttribute(conAttrName, FcmConfig.DEFAULT_PASSWORD + FcmConfig.EQUAL+ busiFcmConfig.getDefaultPassword());
	             }
	             
	             if (conTxt.contains(FcmConfig.USER_EXTERNAL_IP + FcmConfig.EQUAL)) 
	             {
	            	 element.addAttribute(conAttrName, FcmConfig.USER_EXTERNAL_IP + FcmConfig.EQUAL+ busiFcmConfig.getUserExternalIp());
				 }
	             
	             if (conTxt.contains(FcmConfig.USER_LOCAL_IP + FcmConfig.EQUAL)) 
	             {
	            	 element.addAttribute(conAttrName, FcmConfig.USER_LOCAL_IP + FcmConfig.EQUAL+ busiFcmConfig.getUserLocalIp());
				 }
	             
	             if (conTxt.contains(FcmConfig.INTERNAL_SIP_PORT + FcmConfig.EQUAL)) 
	             {
	            	 element.addAttribute(conAttrName, FcmConfig.INTERNAL_SIP_PORT + FcmConfig.EQUAL+ busiFcmConfig.getInternalSipPort());
				 }
   		   }
		return element;
	}

	@Override
	public Map<String, Object> getCutrnConfigData(BusiFreeSwitchDept busiFreeSwitchDept) {
		Map<String,Object> map = new HashMap<String, Object>();
		Map<String,Object> turnMap = new HashMap<String, Object>();
		
		//与服务器的连接
		Boolean isConnect = this.connectServer(busiFreeSwitchDept);
		if(isConnect) 
		{
			try {
				String fileStr = SshRemoteServerOperate.getInstance().execCommand("cat ".concat(FcmConfig.TURN_SERVER_PATH));
				
				//处理turnServer.Conf文件
				map = this.dealTurnServerConfFile(fileStr,map);
				 
				//处理turnServer.conf文件
				 turnMap = this.dealTurnServerConf(map, turnMap);
			} catch (IOException e) {
				throw new SystemException(100334, "查看turnServer.Conf文件失败!");
			}
		}
		
		return turnMap;
	}
	
	/**
	 * 处理turnServer.Conf文件
	 * @param fileStr
	 * @param map
	 * @return
	 */
	public Map<String, Object> dealTurnServerConfFile(String fileStr, Map<String, Object> map) {
		if(!(Objects.isNull(fileStr) || fileStr.isEmpty())) {
			 Arrays.stream(fileStr.split(FcmConfig.NEW_LINE)).filter(kv -> kv.contains(FcmConfig.EQUAL)).map(kv -> kv.split(FcmConfig.EQUAL)).forEach(array -> map.put(array[0], array[1]));
		 } 
		return map;
	}
	
	public Map<String, Object> dealTurnServerConf(Map<String, Object> map, Map<String, Object> turnMap) {
		 for(Entry<String, Object> entry : map.entrySet())
		 {
			 String value = (String) entry.getValue();
			 
			 switch (entry.getKey()) {
				case FcmConfig.LISTENING_DEVICE:
					turnMap.put(FcmConfig.LISTENING_DEVICE_KEY, value);
					break;
				case FcmConfig.LISTENING_PORT:
					turnMap.put(FcmConfig.LISTENING_PORT_KEY, value);
					break;
				case FcmConfig.REALM:
					turnMap.put(FcmConfig.REALM_KEY, value);
					break;
				case FcmConfig.RELAY_IP:
					turnMap.put(FcmConfig.RELAY_IP_KEY, value);
					break;
				case FcmConfig.MIN_PORT:
					turnMap.put(FcmConfig.MIN_PORT_KEY, value);
					break;
				case FcmConfig.MAX_PORT:
					turnMap.put(FcmConfig.MAX_PORT_KEY, value);
					break;
				case FcmConfig.USER:
					turnMap.put(FcmConfig.USER, value);
					break;
				case FcmConfig.EXTERNAL_IP:
					turnMap.put(FcmConfig.EXTERNAL_IP_KEY, value);
					break;
				default:
					break;
			}
			 
		 }
		return turnMap;
	}

	@Override
	public int updateCutrnConfigData(TurnServerConf turnServerConf) {
		int curFlg = FcmConfigConstant.ZERO;
		Map<String,Object> serverMap = new HashMap<String, Object>();
		
		if(StringUtils.isNotEmpty(turnServerConf.getRealm()) && !RegExpUtils.isIP(turnServerConf.getRealm())) 
    	{
			new SystemException(FcmConfigConstant.EXCEPTION_ONE_ONE_TH, FcmTipConstant.IP_TIP);
    	}
		
		if(StringUtils.isNotEmpty(turnServerConf.getExternalIp()) && !RegExpUtils.isIP(turnServerConf.getExternalIp()))
    	{
			new SystemException(FcmConfigConstant.EXCEPTION_ONE_ONE_TH, FcmTipConstant.IP_TIP);
    	}
		
		if(StringUtils.isNotEmpty(turnServerConf.getRelayIp()) && !RegExpUtils.isIP(turnServerConf.getRelayIp()))
    	{
			new SystemException(FcmConfigConstant.EXCEPTION_ONE_ONE_TH, FcmTipConstant.IP_TIP);
    	}
		
		BusiFcmConfig busiFcmConfig = new BusiFcmConfig();
		busiFcmConfig.setId(turnServerConf.getId());
		//与服务器的连接
		Boolean isConnect = this.connectServerFs(busiFcmConfig);
		if(isConnect) 
		{
			try {
				String turnServerStr = SshRemoteServerOperate.getInstance().execCommand("cat ".concat(FcmConfig.TURN_SERVER_PATH));
				
				//处理turnServer.Conf文件
				serverMap = this.dealTurnServerConfFile(turnServerStr,serverMap);
				 
				//处理更新turnServer.conf文件
				serverMap = this.dealUpdateTurnServerConf(serverMap, turnServerConf);
				
				//处理serverMap，拼成字符串
				String turnStr = this.serverMapDataSpliceContent(serverMap);
				
				//获取项目的相对路径
				String path = projectPath + FcmConfig.XML_PARENT + System.currentTimeMillis();
				if(StringUtils.isNotEmpty(path)) 
				{
					//生成xml文件，在项目的指定目录
					boolean isSuccess = FileConvert.getInstance().string2File(turnStr, path, FcmConfig.TURN_SERVER_CONF);
					if(isSuccess)
					{
						//在把文件上传到服务器上
						SshRemoteServerOperate.getInstance().uploadFile(FcmConfig.TURN_SERVER_PATH, path + FcmConfigConstant.SLASH + FcmConfig.TURN_SERVER_CONF);
						FileConvert.getInstance().deleteAllFiles(new File(path));
						curFlg = FcmConfigConstant.SUCCESS;
					}
				}
				
			} catch (IOException e) {
				curFlg = FcmConfigConstant.ZERO;
				throw new SystemException(100334, "更新FS文件失败!");
			}
		}
		
		return curFlg;
	}
	
	private Map<String, Object> dealUpdateTurnServerConf(Map<String, Object> serverMap, TurnServerConf turnServerConf) {
		for(Entry<String, Object> entry : serverMap.entrySet())
		 {
			 
			 switch (entry.getKey()) {
				case FcmConfig.LISTENING_DEVICE:
					serverMap.put(FcmConfig.LISTENING_DEVICE, turnServerConf.getListeningDevice());
					break;
				case FcmConfig.LISTENING_PORT:
					serverMap.put(FcmConfig.LISTENING_PORT, turnServerConf.getListeningPort());
					break;
				case FcmConfig.REALM:
					serverMap.put(FcmConfig.REALM, turnServerConf.getRealm());
					break;
				case FcmConfig.RELAY_IP:
					serverMap.put(FcmConfig.RELAY_IP, turnServerConf.getRelayIp());
					break;
				case FcmConfig.MIN_PORT:
					serverMap.put(FcmConfig.MIN_PORT, turnServerConf.getMinPort());
					break;
				case FcmConfig.MAX_PORT:
					serverMap.put(FcmConfig.MAX_PORT, turnServerConf.getMaxPort());
					break;
				case FcmConfig.USER:
					serverMap.put(FcmConfig.USER, turnServerConf.getUser());
					break;
				case FcmConfig.EXTERNAL_IP:
					serverMap.put(FcmConfig.EXTERNAL_IP, turnServerConf.getExternalIp());
					break;
				default:
					break;
			}
			 
		 }
		return serverMap;
	}
	
	/**
	 * 处理serverMap，拼成字符串
	 * @param serverMap
	 * @return
	 */
	private String serverMapDataSpliceContent(Map<String, Object> serverMap) {
		StringBuffer buffer = new StringBuffer();
		for(Entry<String, Object> entry : serverMap.entrySet())
		 {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			
			buffer.append(key + FcmConfig.EQUAL + value + FcmConfig.NEW_LINE);
		 }
		return buffer.toString();
	}

	@Override
	public String getDefaultPullicXml(String xmlType, Long id) {
		String varsStr = null;
		
		//获取部门绑定的服务器ip
		BusiFreeSwitch busiFreeSwitch = busiFreeSwitchMapper.selectBusiFreeSwitchById(id);
		if(null != busiFreeSwitch) {
			Integer serverPort = busiFreeSwitch.getPort();
			String userName = busiFreeSwitch.getUserName();
			String password = busiFreeSwitch.getPassword();
			String ipAddr = busiFreeSwitch.getIp();
			
			String xmlFileName = null;
			
			if(xmlType.equals(FcmConfigConstant.DEFAULT_XML)) 
			{
				xmlFileName = FcmConfigConstant.DEFAULT_XML;
			}
			else if (xmlType.equals(FcmConfigConstant.PUBLIC_XML)) 
			{
				xmlFileName = FcmConfigConstant.PUBLIC_XML;
			}
			else 
			{
				xmlFileName = FcmConfigConstant.DISTRIBUTOR_CONF_XML;
			}
			
			//获取服务器上的相关文件
			varsStr = this.getServerRelatedFile(ipAddr, userName, password, serverPort, xmlFileName);
			
			SshRemoteServerOperate.getInstance().closeSession();
		}
		
		return varsStr;
	}

	@Override
	public int sendNfsConfigCommand(NfsConfig nfsConfig) {
		Integer isSuccess = FcmConfigConstant.ZERO;
		if(StringUtils.isEmpty(nfsConfig.getFmeIp())) 
		{
			new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, "fmeIp不能为空!");
		}
		
		if(!RegExpUtils.isIP(nfsConfig.getFmeIp())) 
    	{
			new SystemException(FcmConfigConstant.EXCEPTION_ONE_ONE_TH, FcmTipConstant.IP_TIP);
    	}
		
		if(StringUtils.isEmpty(nfsConfig.getUserName())) 
		{
			nfsConfig.setUserName(FcmConfig.FME_DEFAULT_USER_NAME);
		}
		
		if(StringUtils.isEmpty(nfsConfig.getPassword())) 
		{
			nfsConfig.setPassword(FcmConfig.FME_DEFAULT_PASSWORD);
		}
		
		if(StringUtils.isEmpty(nfsConfig.getPort())) 
		{
			nfsConfig.setPort(FcmConfig.FME_DEFAULT_PORT);
		}
		
		if(StringUtils.isEmpty(nfsConfig.getRecorderNfs())) 
		{
			new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, "nfs录制地址不能为空!");
		}
		
		String[] recordStr = nfsConfig.getRecorderNfs().split(":");
		
		try 
		{
			SshRemoteServerOperate.getInstance().sshRemoteCallLogin(nfsConfig.getFmeIp(), nfsConfig.getUserName(), nfsConfig.getPassword(), Integer.valueOf(nfsConfig.getPort()));
			SshRemoteServerOperate.getInstance().execCommand(FcmConfig.RECORDER_DISABLE);
			SshRemoteServerOperate.getInstance().execCommand(FcmConfig.RECORDER_NFS + nfsConfig.getRecorderNfs());
			String execCommand = SshRemoteServerOperate.getInstance().execCommand(FcmConfig.RECORDER);
			if(null != recordStr && recordStr.length > 1) 
			{
				if(execCommand.toString().contains(recordStr[0]))
				{
					isSuccess = FcmConfigConstant.SUCCESS;
					SshRemoteServerOperate.getInstance().closeSession();
				}
			}
		} 
		catch (Exception e) 
		{
			isSuccess = FcmConfigConstant.ZERO;
			throw new SystemException(100334, "更新FS文件失败!");
		}
		return isSuccess;
	}

	@Override
	public int restartFreeSwitchServer(Long id) {
		Integer isSuccess = FcmConfigConstant.ZERO;
		BusiFcmConfig busiFcmConfig = new BusiFcmConfig();
		busiFcmConfig.setId(id);
		
		//与服务器的连接
		Boolean isConnect = this.connectServerFs(busiFcmConfig);
		if(isConnect) 
		{
			try {
				String execCommand = SshRemoteServerOperate.getInstance().execCommand(FcmConfig.RESTART_FREESWITCH_SERVICE);
				isSuccess = FcmConfigConstant.SUCCESS;
				LOGGER.info(execCommand);
			} catch (IOException e) {
				isSuccess = FcmConfigConstant.ZERO;
				throw new SystemException(100334, "重启FS服务失败!");
			}
		}
		return isSuccess;
	}

	@Override
	public Boolean restartFreeSwitchListen(Long id) {
		Boolean isSuccess = false;
		Map<String, Object> varMaps = new HashMap<String, Object>();
		
		try {
			if(map.isEmpty()) {
				String fileStr = SshRemoteServerOperate.getInstance().execCommand("cat ".concat(FcmConfigConstant.XML_FILE_PATH + FcmConfigConstant.VARS_XML));
				varMaps = this.xmlStrConvertJson(fileStr, varMaps);
				String internalSipPort = (String) varMaps.get(FcmConfig.INTERNAL_SIP_PORT_KEY);
				map.put(FcmConfig.INTERNAL_SIP_PORT_KEY, internalSipPort);
			}
			
			String restartIsSuccess = SshRemoteServerOperate.getInstance().execCommand("netstat -an | grep " + map.get(FcmConfig.INTERNAL_SIP_PORT_KEY));
			if(StringUtils.isNotEmpty(restartIsSuccess)) 
			{
				isSuccess = true;
				map.clear();
				SshRemoteServerOperate.getInstance().closeSession();
			}
			else 
			{
				isSuccess = false;
			}
		} catch (IOException e) {
			isSuccess = false;
			throw new SystemException(100334, "监听FS服务重启失败!");
		}
		return isSuccess;
	}

	@Override
	public List<FreeSwitchUser> freeSwitchAllUser(Long deptId) {
		List<FreeSwitchUser> switchUsers = new ArrayList<FreeSwitchUser>();
		BusiFreeSwitchDept busiFreeSwitchDept = new BusiFreeSwitchDept();
		busiFreeSwitchDept.setDeptId(deptId);
		
		//与服务器的连接
		Boolean isConnect = this.connectServer(busiFreeSwitchDept);
		if(isConnect) 
		{
			try {
				String fileName = SshRemoteServerOperate.getInstance().execCommand("cd " + FcmConfig.FREE_SWITCH_USER + "&& ls -lv" );
				String[] fileSplit = fileName.split(FcmConfig.NEW_LINE);
				int length = fileSplit.length;
				if(null != fileSplit && length > 0) 
				{
					for (int i = 0; i < length; i++) {
						String userFileName = fileSplit[i];
						if(!userFileName.contains("total")) {
							String[] fileNum = userFileName.split("\\.");
							if(null != fileNum && fileNum.length > 0) 
							{
								String userId = fileNum[0].substring(fileNum[0].lastIndexOf(" ")+1);
								if(NumberDeal.getInstance().isNumberic(userId)) 
								{
									FreeSwitchUser switchUser = new FreeSwitchUser();
									switchUser.setId((long) (i));
									switchUser.setUserId(userId);
									switchUsers.add(switchUser);
									
									//获取freeSwitch下具体用户的信息
//									this.getUserInfo(userFileName);
								}
								
							}
						}
					}
				}
			} catch (IOException e) {
				throw new SystemException(100334, "获取FS全部的注册用户异常!");
			}
		}
		
		SshRemoteServerOperate.getInstance().closeSession();
		return switchUsers;
	}

	@Override
	public Map<String, Object> getFcmConfigInfoById(BusiFcmConfig busiFcmConfig) {
		Map<String, Object> freeSwitchFileMaps = new HashMap<String, Object>();
	
		if(null != busiFcmConfig) {
			BusiFreeSwitch busiFreeSwitch = busiFreeSwitchMapper.selectBusiFreeSwitchById(busiFcmConfig.getId());
			if(null != busiFreeSwitch) {
				 freeSwitchFileMaps = this.getFreeSwitchFileMaps(busiFreeSwitch);
			}
		}
		
		return freeSwitchFileMaps;
	}

	@Override
	public Map<String, Object> getCutrnConfigDataById(BusiFcmConfig busiFcmConfig) {
		Map<String,Object> map = new HashMap<String, Object>();
		Map<String,Object> turnMap = new HashMap<String, Object>();
		
		//与服务器的连接
		Boolean isConnect = this.connectServerFs(busiFcmConfig);
		if(isConnect) 
		{
			try {
				String fileStr = SshRemoteServerOperate.getInstance().execCommand("cat ".concat(FcmConfig.TURN_SERVER_PATH));
				
				//处理turnServer.Conf文件
				map = this.dealTurnServerConfFile(fileStr,map);
				 
				//处理turnServer.conf文件
				 turnMap = this.dealTurnServerConf(map, turnMap);
			} catch (IOException e) {
				throw new SystemException(100334, "获取FS的turnServer.conf异常!");
			}
		}
		
		return turnMap;
	}

	private Boolean connectServerFs(BusiFcmConfig busiFcmConfig) {
		Boolean isConnect = false;
		BusiFreeSwitch busiFreeSwitch = busiFreeSwitchMapper.selectBusiFreeSwitchById(busiFcmConfig.getId());
		if(null != busiFreeSwitch) {
			Integer serverPort = busiFreeSwitch.getPort();
			String userName = busiFreeSwitch.getUserName();
			String password = busiFreeSwitch.getPassword();
			String ipAddr = busiFreeSwitch.getIp();
			
			try 
			{
				SshRemoteServerOperate.getInstance().sshRemoteCallLogin(ipAddr, userName, password, serverPort);
				isConnect = true;
			} 
			catch (Exception e) 
			{
				isConnect = false;
				throw new SystemException(100334, "连接FS异常!");
			}
		}
	
		return isConnect;
	}

	@Override
	public Map<String, TerminalOnlineStatus> getFsOnlineUser(Long fcmId) {
		Map<String, TerminalOnlineStatus> maps = new HashMap<String, TerminalOnlineStatus>();
		BusiFcmConfig busiFcmConfig = new BusiFcmConfig();
		busiFcmConfig.setId(fcmId);
		
		BusiFreeSwitch busiFreeSwitch = busiFreeSwitchMapper.selectBusiFreeSwitchById(busiFcmConfig.getId());
		if(null != busiFreeSwitch) {
			Integer serverPort = Integer.valueOf(environment.getProperty("fs.port"));
			String password = environment.getProperty("fs.password");
			String ipAddr = busiFreeSwitch.getIp();
			
			try {
				Client client = new Client();
				client.connect(ipAddr, serverPort, password, 5);
				EslMessage response = client.sendSyncApiCommand("list_users", "group default");
				List<String> bodyLines = response.getBodyLines();
				if(null != bodyLines && bodyLines.size() > 0) {
					for (int i = 1; i < bodyLines.size()-1; i++) {
						if(StringUtils.isNotEmpty(bodyLines.get(i))) {
							String[] fsSplit = bodyLines.get(i).split("\\|");
							if(null != fsSplit && fsSplit.length > 0) {
								if("error/user_not_registered".equals(fsSplit[4])) {
									maps.put(fsSplit[0], TerminalOnlineStatus.OFFLINE);
								}else {
									maps.put(fsSplit[0], TerminalOnlineStatus.ONLINE);
								}
							}
						}
					}
				}
				
				
			} catch (InboundConnectionFailure e) {
				throw new SystemException(100334, "连接FS注册用户异常!");
			}
		}
		
		return maps;
	}

	@Override
	public Date getFcmServerDate(Long id) {
		String fileName = null;
		
		//将String转化为Date
		SimpleDateFormat formatter = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
		BusiFreeSwitch busiFreeSwitch = busiFreeSwitchMapper.selectBusiFreeSwitchById(id);
		if(null != busiFreeSwitch) {
			try {
				
				SshRemoteServerOperate.getInstance().sshRemoteCallLogin(busiFreeSwitch.getIp(),busiFreeSwitch.getUserName(),busiFreeSwitch.getPassword(),busiFreeSwitch.getPort());
				if (SshRemoteServerOperate.getInstance().isLogined()){
					fileName = SshRemoteServerOperate.getInstance().execCommand("date -R");
					
					//关闭服务器连接
					SshRemoteServerOperate.getInstance().closeSession();
					return formatter.parse(fileName);
				}
			}catch (Exception e) {
				LOGGER.error("date -R指令失败" ,e);
			}
		}
		return null;
	}

	/**
	 * 根据IP ping 终端
	 *  ping -c 5 命令
	 *
	 * @param ip
	 */
	@Override
	public String pingIp(String ip,long id) {
		String str = null;
		Assert.isTrue(FcmBridgeCache.getInstance().getFcmBridgeMap().containsKey(id),"未找到Fcm");
		BusiFreeSwitch busiFreeSwitch = FcmBridgeCache.getInstance().get(id).getBusiFreeSwitch();
		String pingIp = "ping -c 5 " + ip;
		SshRemoteServerOperateOrdinary sshRemoteServerOperateOrdinary = new SshRemoteServerOperateOrdinary();

		if(busiFreeSwitch != null){
			try {
				sshRemoteServerOperateOrdinary.sshRemoteCallLogin(busiFreeSwitch.getIp(),busiFreeSwitch.getUserName(),busiFreeSwitch.getPassword(),busiFreeSwitch.getPort());

				if (sshRemoteServerOperateOrdinary.isLogined()){
					str = sshRemoteServerOperateOrdinary.execCommand(pingIp);
					System.out.println(str);
					return str;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				sshRemoteServerOperateOrdinary.closeSession();
			}
		}

		return str;
	}

	/**
	 * 查看freeswitch服务是否在线：1在线，2离线
	 */
	public Integer getFcmServerStatus(BusiFreeSwitch freeSwitch){
	   String status = null;
	   if (pingIp(freeSwitch.getIp())){
	      try
	      {
	         SshRemoteServerOperate.getInstance().sshRemoteCallLogin(freeSwitch.getIp(),freeSwitch.getUserName(),freeSwitch.getPassword(),freeSwitch.getPort());
	         if (SshRemoteServerOperate.getInstance().isLogined()){
	            //发送指令
	            status=SshRemoteServerOperate.getInstance().execCommand("ps -e | grep freeswitch");
	         }
	      }
	      catch (Exception e)
	      {
	         LOGGER.error("==========>>>>ps -e | grep freeswitch 指令失败" ,e );
	      }finally {
	    	  SshRemoteServerOperate.getInstance().closeSession();
	      }
	      if (!StringUtils.isEmpty(status)){
	         return 1;
	      }
	   }
	   return 2;
	}

	/**
	 *        ping服务器Ip
	 * @param ip
	 * @return
	 */
	public Boolean pingIp(String ip)
	{
	   if (null == ip || 0 == ip.length()) {
	      return false;
	   }

	   try
	   {
	      boolean reachable = InetAddress.getByName(ip).isReachable(500);//超过3秒
	      if (reachable){
	         return true;
	      }
	      return false;
	   }
	   catch (IOException e)
	   {
	      return false;
	   }
	}
}
