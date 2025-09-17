package com.paradisecloud.fcm.terminal.fs.interfaces;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.terminal.fs.model.BusiFcmConfig;
import com.paradisecloud.fcm.terminal.fs.model.FreeSwitchUser;
import com.paradisecloud.fcm.terminal.fs.model.NfsConfig;
import com.paradisecloud.fcm.terminal.fs.model.TurnServerConf;
import com.paradisecloud.fcm.terminal.fs.model.XmlEntity;

/**
 * 服务器资源信息Service接口
 * 
 * @author zyz
 * @date 2021-09-02
 */
public interface IBusiFreeSwitchService 
{
    /**
     * 查询服务器资源信息
     * 
     * @param id 服务器资源信息ID
     * @return 服务器资源信息
     */
    public BusiFreeSwitch selectBusiFreeSwitchById(Long id);

    /**
     * 查询服务器资源信息列表
     * 
     * @param busiFreeSwitch 服务器资源信息
     * @return 服务器资源信息集合
     */
    public List<BusiFreeSwitch> selectBusiFreeSwitchList(BusiFreeSwitch busiFreeSwitch);

    /**
     * 新增服务器资源信息
     * 
     * @param busiFreeSwitch 服务器资源信息
     * @return 结果
     */
    public int insertBusiFreeSwitch(BusiFreeSwitch busiFreeSwitch);

    /**
     * 修改服务器资源信息
     * 
     * @param busiFreeSwitch 服务器资源信息
     * @return 结果
     */
    public int updateBusiFreeSwitch(BusiFreeSwitch busiFreeSwitch);

    /**
     * 批量删除服务器资源信息
     * 
     * @param ids 需要删除的服务器资源信息ID
     * @return 结果
     */
    public int deleteBusiFreeSwitchByIds(Long[] ids);

    /**
     * 删除服务器资源信息信息
     * 
     * @param id 服务器资源信息ID
     * @return 结果
     */
    public int deleteBusiFreeSwitchById(Long id);
    

	/**
	 * @return
	 */
	public List<ModelBean> getServerConfigurationInfo();
	

	/**
	 * @param busiFreeSwitchDept
	 * @return
	 */
	public Map<String, Object> getFcmConfigFileInfo(BusiFreeSwitchDept busiFreeSwitchDept);
	

	/**
	 * @param busiFreeSwitchDept
	 * @param busiFcmConfig 
	 * @return
	 */
	public int operateFcmConfig(BusiFcmConfig busiFcmConfig);
	

	/**
	 * @param busiFreeSwitchDept 
	 * @return
	 */
	public Map<String, Object> getCutrnConfigData(BusiFreeSwitchDept busiFreeSwitchDept);
	

	/**
	 * @param turnServerConf
	 * @return
	 */
	public int updateCutrnConfigData(TurnServerConf turnServerConf);
	

	/**
	 * @param xmlType
	 * @param deptId 
	 * @return
	 */
	public String getDefaultPullicXml(String xmlType, Long deptId);
	

	/**
	 * @param xmlEntity
	 * @return
	 */
	public int uploadXmlFileStr(XmlEntity xmlEntity);
	

	/**
	 * @param nfsConfig
	 * @return
	 */
	public int sendNfsConfigCommand(NfsConfig nfsConfig);
	

	/**
	 * @param deptId
	 * @return
	 */
	public int restartFreeSwitchServer(Long id);
	

	/**
	 * @param deptId
	 * @return
	 */
	public Boolean restartFreeSwitchListen(Long id);
	

	/**
	 * @param deptId
	 * @return
	 */
	public List<FreeSwitchUser> freeSwitchAllUser(Long deptId);
	
	
	/**
	 * @param busiFreeSwitchDept
	 * @return
	 */
	public BusiFreeSwitch getDeptBindServerInfo(BusiFreeSwitchDept busiFreeSwitchDept);
	
	
	/**
	 * @param xmlStr
	 * @param varMaps
	 * @return
	 */
	public Map<String, Object> xmlStrConvertJson(String xmlStr, Map<String, Object> varMaps);
	
	
	/**
	 * @param fileStr
	 * @param map
	 * @return
	 */
	public Map<String, Object> dealTurnServerConfFile(String fileStr, Map<String, Object> map);
	
	
	/**
	 * @param map
	 * @param turnMap
	 * @return
	 */
	public Map<String, Object> dealTurnServerConf(Map<String, Object> map, Map<String, Object> turnMap);

	/**
	 * @param busiFcmConfig
	 * @return
	 */
	public Map<String, Object> getFcmConfigInfoById(BusiFcmConfig busiFcmConfig);

	/**
	 * @param busiFcmConfig
	 * @return
	 */
	public Map<String, Object> getCutrnConfigDataById(BusiFcmConfig busiFcmConfig);
	
	
	/**
	 * @return
	 */
	public Map<String, TerminalOnlineStatus> getFsOnlineUser(Long fcmId);

	public Date getFcmServerDate(Long id);

	/**
	 * 在 fcm中ping终端Ip
	 */
	String pingIp(String ip,long id);
}
