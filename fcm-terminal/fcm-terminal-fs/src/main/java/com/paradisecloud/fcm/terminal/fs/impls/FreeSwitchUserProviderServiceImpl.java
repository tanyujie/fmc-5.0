package com.paradisecloud.fcm.terminal.fs.impls;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.terminal.fs.common.FileConvert;
import com.paradisecloud.fcm.terminal.fs.common.NumberDeal;
import com.paradisecloud.fcm.terminal.fs.common.SshRemoteServerOperate;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfig;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import com.paradisecloud.fcm.terminal.fs.interfaces.IFreeSwitchUserProviderService;
import com.paradisecloud.fcm.terminal.fs.model.FreeSwitchUser;
import com.sinhy.exception.SystemException;

/**
 * freeSwitchService业务层处理
 * 
 * @author zyz
 * @date 2021-08-17
 */
@Transactional
@Service
public class FreeSwitchUserProviderServiceImpl implements IFreeSwitchUserProviderService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FreeSwitchUserProviderServiceImpl.class);

    /**
     * 查询freeSwitch
     * 
     * @param id freeSwitchID
     * @return freeSwitch
     */
    @Override
    public FreeSwitchUser selectFreeSwitchUserById(String id)
    {
    	//id代表userId
    	String xmlName = id + FcmConfig.DOTTED_XML;
		File file = new File(FcmConfig.FREE_SWITCH_USER + xmlName);
		String fileContent = FileConvert.getInstance().file2String(file, FcmConfig.ENCODING);
        return this.getSpecificUserInfo(fileContent);
    }

    
    private FreeSwitchUser getSpecificUserInfo(String fileContent) {
    	FreeSwitchUser freeSwitchUser = new FreeSwitchUser();
		if(StringUtils.isNotEmpty(fileContent))
		{
			String jsonStr = XML.toJSONObject(fileContent).toString();
			JSONObject jsonObj = (JSONObject) JSONObject.parse(jsonStr);
			
			JSONObject object = jsonObj.getJSONObject(FcmConfig.INCLUDE);
			JSONObject object2 = object.getJSONObject(FcmConfig.USER);
			String userId = (String)object2.getString(FcmConfig.AT_ID);
			JSONObject paraObj = object2.getJSONObject(FcmConfig.PARAMS);
			JSONArray jsonArray = paraObj.getJSONArray(FcmConfig.PARAM);
			freeSwitchUser.setUserId(userId);
			
			if(null != jsonArray && jsonArray.size() > 0) 
			{
				for (int i = 0; i < jsonArray.size(); i++) 
				{
					JSONObject paramObj = (JSONObject)jsonArray.get(i);
					String password = paramObj.getString(FcmConfig.NAME);
					if(password.equals(FcmConfig.PASSWORD)) 
					{
						String userPassword = paramObj.getString(FcmConfig.VALUE);
						freeSwitchUser.setPassword(userPassword);
						break;
					}
				}
			}
		}
		return freeSwitchUser;
	}
    
    /**
     * 查询freeSwitch列表
     * 
     * @param freeSwitchUser freeSwitch
     * @return freeSwitch
     */
    @Override
    public List<FreeSwitchUser> selectFreeSwitchUserList(FreeSwitchUser freeSwitchUser)
    {
    	List<FreeSwitchUser> switchUsers = new ArrayList<FreeSwitchUser>();
    	List<Integer> fileNum = new ArrayList<Integer>();
    	File[] allFile = FileConvert.getInstance().getAllFile(FcmConfig.FREE_SWITCH_USER);
    	if(allFile.length > 0) {
    		int length = allFile.length;
    		for (int i = 0; i < length; i++) {
    			String[] userArr = allFile[i].getName().split("\\" + FcmConfig.DOTTED);
    			if(userArr.length > 1) {
    				fileNum.add(Integer.valueOf(userArr[0]));
    			}
			}
    		
    		Collections.sort(fileNum);
    		
    		int size = fileNum.size();
    		for (int x = 0; x < size; x++) {
        		FreeSwitchUser switchUser = new FreeSwitchUser();
    			switchUser.setId((long) (x+1));
    			switchUser.setUserId(String.valueOf(fileNum.get(x)));
    			switchUsers.add(switchUser);
			}

    	}
        return switchUsers;
    }

    /**
     * 新增freeSwitch
     * 
     * @param freeSwitchUser freeSwitch
     * @return 结果
     */
    @Override
    public int insertFreeSwitchUser(FreeSwitchUser freeSwitchUser)
    {
    	int insert = FcmConfigConstant.ZERO;
    	try 
    	{
    		if(null != freeSwitchUser) 
        	{
    			String filePath = System.getProperty(FcmConfig.CATALOG) + FcmConfig.TEMPLATE_1000_PATH;
				String freeSwitchTemplateStr = FileConvert.getInstance().file2String(new File(filePath), FcmConfig.ENCODING);
				if(StringUtils.isNotEmpty(freeSwitchTemplateStr)) 
				{
					if(freeSwitchUser.getUserId().contains(FcmConfig.SPLIT_LINE)) 
					{
						String[] splitUserId = freeSwitchUser.getUserId().split(FcmConfig.SPLIT_LINE);
						if(null != splitUserId && splitUserId.length > 0) 
						{
							Integer beginUserId = Integer.valueOf(splitUserId[0]);
							Integer endUserId =  Integer.valueOf(splitUserId[1])+1;
							for (int i = beginUserId; i < endUserId; i++) {
								FreeSwitchUser switchUser = new FreeSwitchUser();
								switchUser.setUserId(String.valueOf(i));
								switchUser.setPassword(freeSwitchUser.getPassword());
								
								//批量添加freeSwitch用户
								this.batchAddFreeSwitchUser(i, freeSwitchTemplateStr, switchUser);
							}
						}
					}
					else 
					{
						this.batchAddFreeSwitchUser(Integer.valueOf(freeSwitchUser.getUserId()), freeSwitchTemplateStr, freeSwitchUser);
					}
					
					insert = FcmConfigConstant.SUCCESS;
				}
        	}
		} 
    	catch (Exception e) 
    	{
    		insert = FcmConfigConstant.ZERO;
    		throw new SystemException(100334, "增加FS用户异常!");
		}
    	
        return insert;
    }

    /**
     * 批量添加freeSwitch用户
     * @param i
     * @param freeSwitchTemplateStr
     * @param freeSwitchUser
     */
    private void batchAddFreeSwitchUser(int i, String freeSwitchTemplateStr, FreeSwitchUser freeSwitchUser) {

		//根据userId生成用户xml文件
		String generateUserXml = this.byUserIdGenerateUserXml(i, freeSwitchTemplateStr, freeSwitchUser);
		
		String xmlName = i + FcmConfig.DOTTED_XML;
			
		//生成xml文件，在项目的指定目录
		FileConvert.getInstance().string2File(generateUserXml, FcmConfig.FREE_SWITCH_USER, xmlName);
	}

	/**
     * 根据userId生成用户xml文件
     * @param i
     * @param freeSwitchTemplateStr
     * @param freeSwitchUser
     * @return 
     */
    private String byUserIdGenerateUserXml(int i, String freeSwitchTemplateStr, FreeSwitchUser freeSwitchUser) {
    	Document document = null;
		try
		{
			//读取XML文件，获得document对象
			document = DocumentHelper.parseText(freeSwitchTemplateStr);
			
			//获得某个节点的属性对象
	        Element rootElem = document.getRootElement();
	        
	        //循环子节点
	        List<Element> elements =rootElem.elements();
	        if(null != elements && elements.size() > 0) 
	        {
		       	 for(Element element : elements) 
		       	 {
		       		getUserChildNodes(element, freeSwitchUser);
		       	 }
	        }
		} 
		catch (Exception e) 
		{
			throw new SystemException(100334, "生成FS用户异常!");
		}
		
		return document.asXML();
	}

	private Element getUserChildNodes(Element element, FreeSwitchUser freeSwitchUser) {
		Boolean isPassword = false;
		String eleName = element.getName();
		Iterator<Node> it = element.nodeIterator();
		if(eleName.equals(FcmConfig.USER)) 
		{
			element.addAttribute(FcmConfig.ID, freeSwitchUser.getUserId());
		}
		
		while (it.hasNext())
		{
			Node node = it.next();
			if (node instanceof Element)
			{
				Element ele = (Element)node;
				String name = ele.getName();
				if(name.equals(FcmConfig.PARAMS)) 
				{
					getUserChildNodes(ele, freeSwitchUser);
				}
				else if (name.equals(FcmConfig.VARIABLES)) 
				{
					getUserChildNodes(ele, freeSwitchUser);
				}
				else
				{
					List<String> filedTypes = new ArrayList<String>();
					List<Attribute> attributes = ele.attributes();
					for(Attribute attr : attributes)
			   		 {
			   			  String freeTxt = attr.getValue();
				          String freeName = attr.getName();
				          if(freeTxt.equals(FcmConfig.PASSWORD) 
				        		  || freeTxt.equals(FcmConfig.VM_PASSWORD) 
				        		  || freeTxt.equals(FcmConfig.ACCOUNTCODE)
				        		  || freeTxt.equals(FcmConfig.EFFECTIVE_CALLER_ID_NAME)
				        		  || freeTxt.equals(FcmConfig.EFFECTIVE_CALLER_ID_NUMBER)) 
				          {
				        	  isPassword = true;
				        	  filedTypes.add(freeTxt);
				          }
				          
				          if(freeName.equals(FcmConfig.VALUE_NAME) && isPassword) 
			        	  {
				        	  if(freeTxt.contains(FcmConfig.EXTENSION)) 
				        	  {
				        		  ele.addAttribute(freeName, FcmConfig.EXTENSION + " " + freeSwitchUser.getUserId());
				        	  }
				        	  else 
				        	  {
				        		  if(filedTypes.contains(FcmConfig.PASSWORD)) {
				        			  ele.addAttribute(freeName, freeSwitchUser.getPassword());
				        		  }
				        		  else 
				        		  {
				        			  ele.addAttribute(freeName, freeSwitchUser.getUserId());
								  }
				        		  filedTypes.clear();
							  }
			        		  isPassword = false;
			        		  break;
			        	  }
			   		 }
				}
			}
		}
		return element;
	}

	/**
     * 修改freeSwitch
     * 
     * @param freeSwitchUser freeSwitch
     * @return 结果
     */
    @Override
    public int updateFreeSwitchUser(FreeSwitchUser freeSwitchUser)
    {
    	int update = FcmConfigConstant.ZERO;
    	try 
    	{
			if(null != freeSwitchUser) 
			{
				String xmlName = freeSwitchUser.getUserId() + FcmConfig.DOTTED_XML;
				File file = new File(FcmConfig.FREE_SWITCH_USER + xmlName);
				String freeSwitchInfo = FileConvert.getInstance().file2String(file, FcmConfig.ENCODING);
				
				if(StringUtils.isNotEmpty(freeSwitchInfo)) 
				{
					//处理freeSwitch下的user信息
					String userXml = this.dealFreeSwitchUserXml(freeSwitchInfo, freeSwitchUser);
					FileConvert.getInstance().deleteFile(file);
					
					//生成xml文件，在项目的指定目录
					FileConvert.getInstance().string2File(userXml, FcmConfig.FREE_SWITCH_USER, xmlName);
				}
				
				update = FcmConfigConstant.SUCCESS;
			}
		} 
    	catch (Exception e) 
    	{
    		update = FcmConfigConstant.ZERO;
    		throw new SystemException(100334, "更新FS用户异常!");
		}
    	
        return update;
    }

    /**
             * 处理freeSwitch下的user信息
     * @param freeSwitchInfo
     * @param freeSwitchUser 
     * @return 
     */
    private String dealFreeSwitchUserXml(String freeSwitchInfo, FreeSwitchUser freeSwitchUser) {
    	Document document = null;
		try
		{
			//读取XML文件，获得document对象
			document = DocumentHelper.parseText(freeSwitchInfo);
			
			//获得某个节点的属性对象
	        Element rootElem = document.getRootElement();
	        
	        //循环子节点
	        List<Element> elements =rootElem.elements();
	        if(null != elements && elements.size() > 0) 
	        {
		       	 for(Element element : elements) 
		       	 {
		       		getChildNodes(element, freeSwitchUser);
		       	 }
	        }
		} 
		catch (Exception e) 
		{
			throw new SystemException(100334, "xml文件异常!");
		}
		
		return document.asXML();
	}
    
    private Element getChildNodes(Element element, FreeSwitchUser freeSwitchUser) 
	{
    	Boolean isPassword = false;
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
					getChildNodes(ele, freeSwitchUser);
				}
				else
				{
					List<Attribute> attributes = ele.attributes();
					for(Attribute attr : attributes)
			   		 {
			   			  String freeTxt = attr.getValue();
				          String freeName = attr.getName();
				          if(freeTxt.equals(FcmConfig.PASSWORD)) 
				          {
				        	  isPassword = true;
				          }
				          
				          if(freeName.equals(FcmConfig.VALUE) && isPassword) 
			        	  {
				        	  ele.addAttribute(freeName, freeSwitchUser.getPassword());
			        		  isPassword = false;
			        		  break;
			        	  }
			   		 }
				}
			}
		}
		return element;
	}

	/**
     * 批量删除freeSwitch
     * 
     * @param ids 需要删除的freeSwitchID
     * @return 结果
     */
    @Override
    public int deleteFreeSwitchUserByIds(Long[] ids)
    {
    	int del = FcmConfigConstant.ZERO;
    	try 
    	{

			for (int i = 0; i < ids.length; i++) 
			{
				LOGGER.info(FcmConfig.FREE_SWITCH_USER + ids[i] + FcmConfig.DOTTED_XML);
				FileConvert.getInstance().deleteFile(new File(FcmConfig.FREE_SWITCH_USER + ids[i] + FcmConfig.DOTTED_XML));
			}
			del = FcmConfigConstant.SUCCESS;
		} 
    	catch (Exception e) 
    	{
    		del = FcmConfigConstant.ZERO;
    		throw new SystemException(100334, "批量删除xml文件异常!");
		}
    	
    	SshRemoteServerOperate.getInstance().closeSession();
        return del;
    }

	@Override
	public int getFreeSwitchUserInfo() {
		//与服务器的连接
//		Boolean isConnect = busiFcmConfigService.connectServer();
		Boolean isConnect = false;
		int g = FcmConfigConstant.ZERO;
		if(isConnect) 
		{
			try {
				String fileName = SshRemoteServerOperate.getInstance().execCommand("cd " + FcmConfig.FREE_SWITCH_USER + "&& ls -a" );
				String[] fileSplit = fileName.split(FcmConfig.NEW_LINE);
				if(null != fileSplit && fileSplit.length > 0) 
				{
					for (int i = 0; i < fileSplit.length; i++) {
						String userFileName = fileSplit[i];
						String[] fileNum = userFileName.split("\\.");
						if(null != fileNum && fileNum.length > 0) 
						{
							if(NumberDeal.getInstance().isNumberic(fileNum[0])) 
							{
								//获取freeSwitch下具体用户的信息
								this.getUserInfo(userFileName);
							}
							
						}
					}
					g = FcmConfigConstant.SUCCESS;
				}
			} catch (IOException e) {
				g = FcmConfigConstant.ZERO;
				throw new SystemException(100334, "获取FS的全部用户异常!");
			}
		}
		
		SshRemoteServerOperate.getInstance().closeSession();
		return g;
	}

	private void getUserInfo(String userFileName) {
		try {
			String fileStr = SshRemoteServerOperate.getInstance().execCommand("cat ".concat(FcmConfig.FREE_SWITCH_USER + FcmConfigConstant.SLASH + userFileName));
		
			if(StringUtils.isNotEmpty(fileStr))
			{
				FreeSwitchUser freeSwitchUser = new FreeSwitchUser();
				String jsonStr = XML.toJSONObject(fileStr).toString();
				
				JSONObject jsonObj = (JSONObject) JSONObject.parse(jsonStr);
				JSONObject object = jsonObj.getJSONObject(FcmConfig.INCLUDE);
				JSONObject object2 = object.getJSONObject(FcmConfig.USER);
				String userId = (String)object2.getString(FcmConfig.AT_ID);
				JSONObject paraObj = object2.getJSONObject(FcmConfig.PARAMS);
				JSONArray jsonArray = paraObj.getJSONArray(FcmConfig.PARAM);
				freeSwitchUser.setUserId(userId);
				if(null != jsonArray && jsonArray.size() > 0) 
				{
					for (int i = 0; i < jsonArray.size(); i++) 
					{
						JSONObject paramObj = (JSONObject)jsonArray.get(i);
						String password = paramObj.getString(FcmConfig.NAME);
						if(password.equals(FcmConfig.PASSWORD)) 
						{
							
							String userPassword = paramObj.getString(FcmConfig.VALUE);
							freeSwitchUser.setPassword(userPassword);
							break;
						}
					}
				}
			}
		} 
		catch (IOException e) 
		{
			throw new SystemException(100334, "获取FS的用户信息异常!");
		}
		
	}

	@Override
	public List<String> userIdIsRepeat(String userId) {
		List<String> userIdLists = new ArrayList<String>();
		if(StringUtils.isNotEmpty(userId)) 
		{
			if(userId.contains(FcmConfig.SPLIT_LINE))
			{
				String[] userSplit = userId.split(FcmConfig.SPLIT_LINE);
				if(null != userSplit && userSplit.length > 0) 
				{
					Integer val1 = Integer.valueOf(userSplit[0]);
					Integer val2 = Integer.valueOf(userSplit[1])+1;
					for (int i = val1; i < val2; i++) 
					{
						//检查userId,重复的放入list中
						userIdLists = this.repeatUserIdPutList(String.valueOf(i), userIdLists);
					}
				}
			}
			else 
			{
				//检查userId,重复的放入list中
				userIdLists = this.repeatUserIdPutList(userId, userIdLists);
			}
		}
		return userIdLists;
	}

	/**
	 * 检查userId,重复的放入list中
	 * @param num
	 * @param userIdLists
	 * @return
	 */
	private List<String> repeatUserIdPutList(String userId, List<String> userIdLists) {
		String xmlName = userId + FcmConfig.DOTTED_XML;
		File file = new File(FcmConfig.FREE_SWITCH_USER + xmlName);
		if(file.exists()) {
			userIdLists.add(userId);
		}
		return userIdLists;
	}
}
