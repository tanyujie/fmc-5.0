package com.paradisecloud.fcm.web.controller.mqtt;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author zyz
 *
 */
@RestController
@RequestMapping("/resource")
public class RuleEngineController 
{
	
	@RequestMapping("/process")
	public void process(@RequestBody Map<String, Object> paramsMap) 
	{
		for (Map.Entry<String, Object> entry : paramsMap.entrySet()) 
		{
		    System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}
}
