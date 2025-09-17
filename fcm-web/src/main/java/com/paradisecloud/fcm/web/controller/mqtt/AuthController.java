package com.paradisecloud.fcm.web.controller.mqtt;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paradisecloud.fcm.mqtt.interfaces.IEmqxBrokerHealthService;
import com.paradisecloud.fcm.mqtt.interfaces.ITerminalActionService;


/**
 * @author zyz
 *
 */
@RestController
@RequestMapping("/mqtt")
public class AuthController 
{
	
	public static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
	
	@Autowired
	private IEmqxBrokerHealthService  emqxBrokerHealthService;
	
	@Autowired
	private ITerminalActionService  terminalActionService;
	
	/**
	 * 连接mqtt服务器
	 * @param username
	 * @param password
	 * @param clientid
	 * @param brokerUrl
	 * @return
	 */
//	@PostMapping("/connectBroker")
//	public RestResponse connectBroker(@RequestParam("username") String username, 
//							  		  @RequestParam("password") String password, 
//							  		  @RequestParam("clientid") String clientid,
//							  		  @RequestParam("brokerUrl") String brokerUrl) 
//	{
//		Boolean isConnect = terminalActionService.connectMqttServer(username, password, clientid, brokerUrl);
//		return isConnect ? RestResponse.success() : RestResponse.fail();
//	}
	
	/**
	 * 发布主题
	 * @param publishTopic
	 * @param clientid
	 * @param conferenceNum
	 */
//	@PostMapping("/publishMsg")
//	public void publishMsg(@RequestParam("publishTopic") String publishTopic, 
//						  @RequestParam("clientid") String clientid,
//						  @RequestParam("ipAddr") String ipAddr,
//						  @RequestParam("payloadMsg") String payloadMsg) 
//	{
//		terminalActionService.publishTopicMsg(publishTopic, clientid, payloadMsg, false);
//	}
	
	/**
	 * 获取mqtt服务器的健康状况
	 * @param brokerUrl
	 */
	@PostMapping("/emqxHealth")
	public List<Map<String, Object>> getEmqxHealth(HttpServletResponse response) throws UnknownHostException, IOException
	{
		List<Map<String, Object>> dealEmqxBrokerHealthDatas = emqxBrokerHealthService.dealEmqxBrokerHealthData(response);
		return dealEmqxBrokerHealthDatas;
	}
	
	/**
	 * 从mqtt服务节点上删除终端信息
	 * @param clientid
	 * @param brokerUrl
	 */
//	@PostMapping("/deleteTerminal")
//	public void deleteTerminal(@RequestParam("clientid") String clientid,
//							   @RequestParam("brokerUrl") String brokerUrl,
//							   @RequestParam("userName") String userName) 
//	{
//		terminalActionService.deleteBrokerTerminal(clientid, brokerUrl, userName);
//	}
	
	/**
	 * 绑定mqtt服务器节点
	 * @param clientid
	 * @param brokerUrl
	 * @param username
	 * @param password
	 */
//	@PostMapping("/bindBrokerNodePub")
//	public void bindBrokerNodePub(@RequestParam("clientid") String clientid,
//								  @RequestParam("mqttIp") String mqttIp,
//								  @RequestParam("username") String username,
//								  @RequestParam("password") String password) 
//	{
//		terminalActionService.bindBrokerNodeAndPubTopic(clientid, mqttIp, username, password);
//	}
	
	/**
	 * 邀请终端入会或者预约会议
	 * @param clientid
	 * @param meetType
	 */
//	@PostMapping("/terminalJoinMeeting")
//	public void terminalJoinMeetingWay(@RequestParam("meetType") Integer meetType) 
//	{
//		String clientid = "aa123456_22222^bb123456_22222";
//		terminalActionService.terminalJoinMeetingWay(clientid, meetType);
//	}

	/**
	 *向mqtt服务器添加用户
	 * @param clientid
	 * @param meetType
	 */
	@PostMapping("/addTerminalUser")
	public void addConnectMqttUser(@RequestParam("brokerUrl") String brokerUrl,
			  					   @RequestParam("username") String username,
			  					   @RequestParam("password") String password) 
	{
		terminalActionService.addDefaultUserInfo(username, password, brokerUrl);
	}
		
}
