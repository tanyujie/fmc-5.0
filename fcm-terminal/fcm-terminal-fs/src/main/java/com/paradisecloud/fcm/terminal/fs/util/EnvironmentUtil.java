package com.paradisecloud.fcm.terminal.fs.util;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentUtil implements EnvironmentAware {
	
	private static Environment environment;
	
	@Override
	public void setEnvironment(Environment environment) {
		EnvironmentUtil.environment = environment;
		
	}
	
	public static String searchByKey(String key) {
		return EnvironmentUtil.environment.getProperty(key);
	}

}
