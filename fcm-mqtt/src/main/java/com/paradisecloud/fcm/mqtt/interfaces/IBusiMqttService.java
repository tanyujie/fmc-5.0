package com.paradisecloud.fcm.mqtt.interfaces;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMqtt;
import com.paradisecloud.fcm.mqtt.model.MqttBridge;

/**
 * mqtt配置接口
 * 
 * @author zyz
 * @date 2021-07-21
 */
public interface IBusiMqttService 
{
    /**
             * 查询具体的mqtt配置信息
     * 
     * @param id 
     * @return BusiMqtt
     */
    public BusiMqtt selectBusiMqttById(Long id);

    /**
             * 查询mqtt配置信息列表
     * 
     * @param busiMqtt
     * @return List<BusiMqtt>
     */
    public List<BusiMqtt> selectBusiMqttList(BusiMqtt busiMqtt);

    /**
             * 新增mqtt配置信息
     * 
     * @param busiMqtt
     * @return void
     */
    public BusiMqtt insertBusiMqtt(BusiMqtt busiMqtt);

    /**
             * 修改mqtt配置信息
     * 
     * @param busiMqtt
     * @return int
     */
    public void updateBusiMqtt(BusiMqtt busiMqtt);

    /**
     * 批量删除mqtt配置信息
     * 
     * @param ids 
     * @return int
     */
    public int deleteBusiMqttByIds(Long[] ids);

    /**
     * 删除mqtt配置信息
     * 
     * @param id 
     * @return int
     */
    public void deleteBusiMqttById(Long id);
    
    
    /**
     * 初始化mqttBridge的数据
     * @param mqttBridge
     */
    public void initBusiMqtt(MqttBridge mqttBridge);
    

	/**
	 * mqtt信息的list的列表
	 * @param response 
	 * @param request 
	 * @return
	 */
	public List<ModelBean> getMqttConfigurationInfo(HttpServletResponse response) throws UnknownHostException, IOException;
	
	
	/**
	 * ping  ip
	 * @param ip
	 * @return
	 */
//	public Boolean pingIpAndPort(String ip, Integer port) throws UnknownHostException, IOException;
	

	/**
	 * @param id 
	 * @return
	 */
	public Boolean restartMqttListen(Long id);
	

	/**
	 * @param busiMqtt
	 * @return
	 */
	public Boolean nameIsRepeat(BusiMqtt busiMqtt);
	
	/**
	 * @param ip
	 * @return
	 */
//	public Boolean pingIp(String ip);
	
	/**
	 * @return
	 */
//	public String getProjectPath();
	
	public void publishTopicMsg(String terminalTopic, String clientId, String msg, boolean flag);
	
}
