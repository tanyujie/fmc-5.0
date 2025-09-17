package com.paradisecloud.fcm.ops.cloud.mqtt.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author zyz
 *
 */
public class MqttThreadPool 
{
	
	private static final ExecutorService CONNECT_PUBLISH_EXE = Executors.newFixedThreadPool(1000,new ThreadFactory() 
	{
		
		private int indx;
		
		@Override
		public Thread newThread(Runnable r) 
		{
			indx++;
			return new Thread(r, "MqttThreadPool ----- connectPublishExecutor ----- [" + 1000 + "] ----- Thread ---- " + indx);
		}
	});
	
	
	public static void exec(Runnable run)
    {
		CONNECT_PUBLISH_EXE.execute(run);
    }
	
	
	public static boolean isShutDown()
    {
		return CONNECT_PUBLISH_EXE.isShutdown();
    }
}
