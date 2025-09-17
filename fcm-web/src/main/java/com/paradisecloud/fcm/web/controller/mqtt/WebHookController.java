package com.paradisecloud.fcm.web.controller.mqtt;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paradisecloud.fcm.mqtt.interfaces.IWebHookService;

/**
 * @author zyz
 *
 */
@RestController
@RequestMapping("/mqtt")
public class WebHookController 
{
	
	public static final Logger LOG = LoggerFactory.getLogger(WebHookController.class);
	
	@Autowired
	private IWebHookService webHookService;
	
	@PostMapping("/webHook")
	public void hook(@RequestBody Map<String, Object> paramsMap) 
	{
//		webHookService.monitorTerminalStatus(paramsMap);
	}

}
