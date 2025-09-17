//package com.paradisecloud.fcm.mqtt.scheduler;
//
//import java.io.IOException;
//import java.util.List;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.http.HttpResponse;
//import org.apache.http.entity.ContentType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
//import com.paradisecloud.fcm.dao.model.BusiMqtt;
//import com.paradisecloud.fcm.dao.model.BusiMqttDept;
//import com.paradisecloud.fcm.dao.model.BusiTerminal;
//import com.paradisecloud.fcm.mqtt.cache.MqttBridgeCache;
//import com.paradisecloud.fcm.mqtt.cache.MqttDeptMappingCache;
//import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
//import com.paradisecloud.fcm.mqtt.interfaces.IBusiRegisterTerminalService;
//import com.paradisecloud.fcm.mqtt.model.MqttBridge;
//import com.paradisecloud.fcm.terminal.cache.TerminalCache;
//import com.sinhy.http.HttpObjectCreator;
//import com.sinhy.http.HttpRequester;
//import com.sinhy.http.HttpResponseProcessorAdapter;
//import com.sinhy.utils.ThreadUtils;
//
//@Component
//public class MonitorMqttUser extends Thread implements InitializingBean {
//	
//	private Logger logger = LoggerFactory.getLogger(getClass());
//	
//	@Autowired
//	private IBusiRegisterTerminalService busiRegisterTerminalService;
//	
//	private HttpRequester httpRequester = HttpObjectCreator.getInstance().createHttpRequester(MqttConfigConstant.MQTT_BACK_NAME, MqttConfigConstant.MQTT_BACK_PASSWORD, false);
//	
//	@Override
//    public void run()
//    {
//        logger.info("===========> 监测FMQ用户的在线状态！");
//        ThreadUtils.sleep(15 * 1000);
//        while (true)
//        {
//            try
//            {
//            	List<BusiTerminal> originalTerminals = TerminalCache.getInstance().getCopiedAllValues();
//            	if(null != originalTerminals && originalTerminals.size() > 0) {
//            		for (BusiTerminal busiTerminal : originalTerminals) {
//						if(StringUtils.isNotEmpty(busiTerminal.getSn())) {
//							logger.info("================>" + busiTerminal.getSn());
//							
//							//更新mqtt用户状态
//							this.updateMqttUserStatus(busiTerminal);
//						}
//					}
//            	}
//            }
//            catch (Throwable e)
//            {
//                logger.error("查询FMQ的用户出现问题！", e);
//            }
//            finally 
//            {
//                ThreadUtils.sleep(5000);
//            }
//        }
//    }
//	
//	
//	public void updateMqttUserStatus(BusiTerminal busiTerminal) {
//
//		Long deptId = busiTerminal.getDeptId();
//		BusiMqttDept bindMqttNode = MqttDeptMappingCache.getInstance().getBindMqttNode(deptId);
//		if(null != bindMqttNode) {
//			MqttBridge mqttBridge = MqttBridgeCache.getInstance().get(bindMqttNode.getMqttId());
//			if(null != mqttBridge) {
//				BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();
//				Integer managementPort = busiMqtt.getManagementPort();
//				String httpUrl = MqttConfigConstant.HTTP + busiMqtt.getIp() + MqttConfigConstant.COLON + managementPort + MqttConfigConstant.API_AND_VERSION;
//				String connUrl = httpUrl + "/clients/" + busiTerminal.getSn();
//				
//				httpRequester.get(connUrl, new HttpResponseProcessorAdapter() {
//					
//					@Override
//					public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
//						try {
//							
////							logger.info("++++++++++++++++++++++++++++++++>>> connUrl" + connUrl);
//		                    String nodeData = getBodyContent(httpResponse);
////		                    logger.info("++++++++++++++++++++++++++++++++>>> nodeData" + nodeData.toString());
//		                    if(StringUtils.isNotEmpty(nodeData)) 
//		    				{	
//		    					JSONObject jsonObject = (JSONObject) JSONObject.parse(nodeData);
//		                    	String data = jsonObject.getString(MqttConfigConstant.JSON_DATA_STR);
//		    					JSONArray array = (JSONArray) JSONArray.parse(data);
//		    					int onlineStatus = TerminalOnlineStatus.OFFLINE.getValue();
//		    					if(null != array && array.size() > 0) {
//	    							JSONObject jsonObj = (JSONObject)array.get(0);
//	    							Boolean connect = jsonObj.getBoolean("connected");
//	    							if(connect) {
//	    								onlineStatus = TerminalOnlineStatus.ONLINE.getValue();
//	    							}else {
//	    								onlineStatus = TerminalOnlineStatus.OFFLINE.getValue();
//									}
//		    					}
//		    					
//		    					if(onlineStatus != busiTerminal.getMqttOnlineStatus()) {
//		    						busiTerminal.setMqttOnlineStatus(onlineStatus);
//		    						busiRegisterTerminalService.updateBusiTerminalStatus(busiTerminal);
//		    					}
//		    				}
//						} catch (Exception e) {
//							logger.error("更新FMQ用户状态失败！", e);
//						}
//					}
//
//				});
//			}
//		}else {
//			logger.error("租户未绑定FMQ服务！");
//		}
//	}
//	
////	private void updateBusiTerminalStatus(BusiTerminal busiTerminal) {
////		busiTerminal.setUpdateTime(new Date());
////        
////        if (busiTerminal.getDeptId() == null)
////        {
////            throw new SystemException(1000102, "请选择部门");
////        }
////        
////        if (!TerminalType.isFSBC(busiTerminal.getType()) && !TerminalType.isFCMSIP(busiTerminal.getType()) && !RegExpUtils.isIP(busiTerminal.getIp()))
////        {
////            throw new SystemException(1000103, "IP格式不正确");
////        }
////        
////        Assert.notNull(busiTerminal.getBusinessFieldType(), "终端业务领域类型businessFieldType不能为空！");
////        Assert.notNull(busiTerminal.getAttendType(), "终端入会类型不能为空！");
////        AttendType.convert(busiTerminal.getAttendType());
////        
////        
////        BusiTerminal ot = busiTerminalMapper.selectBusiTerminalById(busiTerminal.getId());
////        if (TerminalType.isFSBC(ot.getType()) && !TerminalType.isFSBC(busiTerminal.getType()))
////        {
////            throw new SystemException(1009894, "FSBC终端不能修改类型，若是要修改，请删除后再新增！");
////        }
////        
////        if (TerminalType.isFSBC(busiTerminal.getType()))
////        {
////            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()), "FSBC账号不能为空");
////            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()), "FSBC密码不能为空");
////            Assert.isTrue(ot.getDeptId().equals(busiTerminal.getDeptId()), "FSBC终端账号不支持切换部门，请删除重建");
////            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPattern.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "FSBC-SIP账号必须为4-10位数字组成！");
////            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPattern.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "FSBC密码必须为1-16位字母、数字和下划线组成！");
////            
////            if (!(ot.getCredential().equals(busiTerminal.getCredential()) && ot.getPassword().equals(busiTerminal.getPassword())))
////            {
////                BusiFsbcServerDept fsd = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
////                Assert.notNull(fsd, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前租户未绑定FSBC服务器，请联系管理员配置您的FSBC服务器！");
////                
////                BusiTerminal con = new BusiTerminal();
////                con.setFsbcServerId(fsd.getFsbcServerId());
////                con.setCredential(busiTerminal.getCredential());
////                List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
////                Assert.isTrue(!ObjectUtils.isEmpty(ts), "该账号已不存在，请删了重新添加");
////                
////                FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(fsd.getFsbcServerId());
////                RestResponse restResponse = fsbcBridge.getCredentialInvoker().update(FsbcCredential.newCredential().name(ot.getCredential()).newName(busiTerminal.getCredential()).password(busiTerminal.getPassword()));
////                Assert.isTrue(restResponse.isSuccess() || restResponse.getMessage().equals("NewName already exist, choose another NewName"), "FSBC账号已存在，请选择其它名字！");
////                busiTerminal.setFsbcServerId(fsd.getFsbcServerId());
////            }
////        }
////        // FCM 类型
////        else if (TerminalType.isFCMSIP(busiTerminal.getType()))
////        {
////            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()), "FCM-SIP账号不能为空");
////            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()), "FCM-SIP密码不能为空");
////            Assert.isTrue(ot.getDeptId().equals(busiTerminal.getDeptId()), "FCM-SIP终端账号不支持切换部门，请删除重建");
////            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPattern.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "FCM-SIP账号必须为4-10位数字组成！");
////            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPattern.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "FCM-SIP密码必须为1-16位字母、数字和下划线组成！");
////            
////            BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
////            Assert.notNull(fsd, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前未绑定FCM服务器，请联系管理员配置您的FCM服务器！");
////            
//////            FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
//////            Assert.isTrue(fcmBridge.updateFreeSwitchUser(busiTerminal.getCredential(), busiTerminal.getPassword()) == FcmConfigConstant.SUCCESS, "修改FCM-SIP账号失败");
////            busiTerminal.setFsServerId(fsd.getServerId());
////        }
////        
////        int c = busiTerminalMapper.updateBusiTerminal(busiTerminal);
////        if (c > 0)
////        {
////            TerminalCache.getInstance().remove(ot.getId());
////            TerminalCache.getInstance().put(busiTerminal.getId(), busiTerminalMapper.selectBusiTerminalById(busiTerminal.getId()));
////        }
////	}
//
//
//	@Override
//	public void afterPropertiesSet() throws Exception {
//		this.start();
//	}
//
//}
