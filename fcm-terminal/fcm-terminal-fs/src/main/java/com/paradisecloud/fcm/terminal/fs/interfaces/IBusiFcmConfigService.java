package com.paradisecloud.fcm.terminal.fs.interfaces;

import java.util.List;
import java.util.Map;

import com.paradisecloud.fcm.terminal.fs.model.BusiFcmConfig;
import com.paradisecloud.fcm.terminal.fs.model.NfsConfig;
import com.paradisecloud.fcm.terminal.fs.model.TurnServerConf;
import com.paradisecloud.fcm.terminal.fs.model.XmlEntity;

/**
 * fcm配置Service接口
 * 
 * @author zyz
 * @date 2021-08-10
 */
public interface IBusiFcmConfigService 
{
    /**
     * 查询fcm配置
     * 
     * @param id fcm配置ID
     * @return fcm配置
     */
    public BusiFcmConfig selectBusiFcmConfigById(Long id);

    /**
     * 查询fcm配置列表
     * 
     * @param busiFcmConfig fcm配置
     * @return fcm配置集合
     */
    public List<BusiFcmConfig> selectBusiFcmConfigList(BusiFcmConfig busiFcmConfig);

    /**
     * 新增fcm配置
     * 
     * @param busiFcmConfig fcm配置
     * @return 结果
     */
    public int insertBusiFcmConfig(BusiFcmConfig busiFcmConfig);

    /**
     * 修改fcm配置
     * 
     * @param busiFcmConfig fcm配置
     * @return 结果
     */
    public int updateBusiFcmConfig(BusiFcmConfig busiFcmConfig);

    /**
     * 批量删除fcm配置
     * 
     * @param ids 需要删除的fcm配置ID
     * @return 结果
     */
    public int deleteBusiFcmConfigByIds(Long[] ids);

    /**
     * 删除fcm配置信息
     * 
     * @param id fcm配置ID
     * @return 结果
     */
    public int deleteBusiFcmConfigById(Long id);
    
    
    /**
	 * 获取FreeSwitchFile的属性
	 * @param id
	 * @return
	 */
	public Map<String, Object> getFreeSwitchFileMaps();

	/**
	 * 
	 * @param busiFcmConfig
	 * @param file
	 * @param request
	 * @return
	 */
	public int operateFcmConfig(BusiFcmConfig busiFcmConfig);

	/**
	 * @param xmlType 
	 * @return
	 */
	public String getDefaultPullicXml(String xmlType);
	
	/**
	 * @param wssCert
	 * @param fileName
	 */
	public Integer uploadXmlFileStr(XmlEntity xmlEntity);

	/**
	 * @return
	 */
	public Map<String,Object> getCutrnConfigData(Long deptId);
	

	/**
	 * @param turnServerConf
	 * @return
	 */
	public int updateCutrnConfigData(TurnServerConf turnServerConf);
	

	/**
	 * @param nfsConfig
	 * @return
	 */
	public int sendNfsConfigCommand(NfsConfig nfsConfig);
	

	/**
	 * @return
	 */
	public int restartFreeSwitchServer(Long deptId);
	

	/**
	 * @return
	 */
	public Boolean connectServer(Long deptId);
	

	/**
	 * @return
	 */
	public String getProjectPath();

	/**
	 * @return
	 */
	public Boolean restartFreeSwitchListen();
	
	
	/**
	 * @return
	 */
	public String currentServerIP();
	

}
