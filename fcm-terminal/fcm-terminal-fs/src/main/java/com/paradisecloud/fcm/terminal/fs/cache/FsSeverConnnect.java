package com.paradisecloud.fcm.terminal.fs.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.freeswitch.esl.client.transport.message.EslMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import com.paradisecloud.fcm.terminal.fs.util.EnvironmentUtil;
import com.sinhy.exception.SystemException;

public class FsSeverConnnect {
	 
//	 private static Client client = new Client();
	 
	 private static Integer serverPort = Integer.valueOf(EnvironmentUtil.searchByKey("fs.port"));
	 
	 private static String password = EnvironmentUtil.searchByKey("fs.password");
	 
	 private static Logger logger = LoggerFactory.getLogger(FsSeverConnnect.class);
	 
//	 private static final IFreeSwitchUserService service = (IFreeSwitchUserService) SpringContextUtil.getBean("freeSwitchUserService");
	 
	 private static class FsSeverConnnectHolder {  
		 
	       private static final FsSeverConnnect INSTANCE = new FsSeverConnnect();  
	 }  
	 
     private FsSeverConnnect(){
    	 
     }  
     
     public static final FsSeverConnnect getInstance() {  
        return FsSeverConnnectHolder.INSTANCE; 
     }  
     
     public Map<String, TerminalOnlineStatus> getFsTerminalOnlineData(BusiFreeSwitch busiFreeSwitch) {
    	 Map<String, TerminalOnlineStatus> maps = new HashMap<String, TerminalOnlineStatus>();
    	 Client client = null;
    	 try {
				client = new Client();
				logger.info("========================> 客户端实例" + client.toString());
				logger.info("========================> 连接状态1" + client.canSend());
				client.connect(busiFreeSwitch.getIp(), serverPort, password, 5);
				
				logger.info("========================> 连接状态2" + client.canSend());
				EslMessage response = client.sendSyncApiCommand("list_users", "group default");
				List<String> bodyLines = response.getBodyLines();
//				logger.info("========================> bodyLines信息" + bodyLines.toString());
				if(null != bodyLines && bodyLines.size() > 0) {
					for (int i = 1; i < bodyLines.size()-1; i++) {
						if(StringUtils.isNotEmpty(bodyLines.get(i))) {
							String[] fsSplit = bodyLines.get(i).split("\\|");
							if(null != fsSplit && fsSplit.length > 0) {
								if("error/user_not_registered".equals(fsSplit[4])) {
									maps.put(fsSplit[0], TerminalOnlineStatus.OFFLINE);
								} else {
									maps.put(fsSplit[0], TerminalOnlineStatus.ONLINE);
								}
							}
						}
					}
				}
			
			} catch (InboundConnectionFailure e) {
				throw new SystemException(FcmConfigConstant.EXCEPTION_ONE_th_th_F, "FREESWITCH发送获取用户数据异常!");
			}finally {
				try {
					if(null != client) {
						client.close();
					}
				} catch (Exception e2) {
					logger.error("关闭freeSwitch连接异常" , e2);
				}
				
			}
    	
 		return maps;
 	}
}
