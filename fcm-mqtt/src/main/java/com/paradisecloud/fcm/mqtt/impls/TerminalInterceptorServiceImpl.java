package com.paradisecloud.fcm.mqtt.impls;

import java.util.Date;
import java.util.List;

import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import com.sinhy.exception.SystemException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiRegisterTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiRegisterTerminal;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.common.TerminalSipAccount;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.TerminalTopic;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import com.paradisecloud.fcm.terminal.service.interfaces.ITerminalInterceptor;

@Service
public class TerminalInterceptorServiceImpl implements ITerminalInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TerminalInterceptorServiceImpl.class);
	
	@Autowired
	private BusiRegisterTerminalMapper busiRegisterTerminalMapper;

	@Autowired
	private BusiTerminalMapper busiTerminalMapper;
	 
	@Override
	public void terminalInserted(BusiTerminal busiTerminal) {
		LOGGER.info("=======================> 有无终端的mac地址" + busiTerminal.getSn());
		if(StringUtils.isNotEmpty(busiTerminal.getSn())) {
			this.terminalMacAddrrelation(busiTerminal);
		}
		
//		IBusiTerminalActionService busiTerminalActionService = (IBusiTerminalActionService) SpringContextUtil.getBean("busiTerminalActionService");
//		busiTerminalActionService.bindNoRegisterAccount(busiTerminal);
	}

	public int terminalMacAddrrelation(BusiTerminal busiTerminal) {
		Integer returnFlg = FcmConfigConstant.ZERO;
		BusiRegisterTerminal busiRegisterTerminal = new BusiRegisterTerminal();
		busiRegisterTerminal.setMac(busiTerminal.getSn());
		List<BusiRegisterTerminal> busiRegisterTerminals = busiRegisterTerminalMapper.selectBusiRegisterTerminalList(busiRegisterTerminal);
		if(null != busiRegisterTerminals && busiRegisterTerminals.size() > 0) {
			BusiRegisterTerminal registerTerminal = busiRegisterTerminals.get(0);
			if(null != registerTerminal && null == registerTerminal.getTerminalId() && "0".equals(registerTerminal.getIsRelated())) {
				registerTerminal.setTerminalId(busiTerminal.getId());
				registerTerminal.setIsRelated(FcmConfigConstant.SUCCESS.toString());
				registerTerminal.setCredential(busiTerminal.getCredential());
				registerTerminal.setUpdateTime(new Date());
				
				returnFlg = busiRegisterTerminalMapper.updateBusiRegisterTerminal(registerTerminal);
				if(returnFlg > 0) {
					busiTerminal.setIntranetIp(busiRegisterTerminals.get(0).getIp());
					busiTerminalMapper.updateBusiTerminal(this.dealFcmType(busiTerminal));

					LOGGER.info("==============根据mac地址绑待注册终端==============================");
					if(TerminalType.isFSBC(busiTerminal.getType()) ) {
						
						//Fsbc类型
						TerminalSipAccount.getInstance().vhdTermminalGetSipAccount(busiTerminal, "", busiRegisterTerminal.getSn());
					}else if(TerminalType.isFCMSIP(busiTerminal.getType())){
						
						//Fcm类型
						TerminalSipAccount.getInstance().terminalGetSipAccount("", busiTerminal);
					} else if (TerminalType.isZJ(busiTerminal.getType())) {

						//zj
						TerminalSipAccount.getInstance().zjTerminalGetSipAccount(busiTerminal, "", busiRegisterTerminal.getSn());
					} else if (TerminalType.isSMCSIP(busiTerminal.getType())) {

						//smc
						TerminalSipAccount.getInstance().smcTerminalGetSipAccount(busiTerminal, "", busiRegisterTerminal.getSn());
					} else if (TerminalType.isSMC2SIP(busiTerminal.getType())) {

						//smc2
						TerminalSipAccount.getInstance().smc2TerminalGetSipAccount(busiTerminal, "", busiRegisterTerminal.getSn());
					} else if (TerminalType.isHwCloud(busiTerminal.getType())) {

						//hwCloud
						TerminalSipAccount.getInstance().hwCloudTerminalGetSipAccount(busiTerminal, "", busiRegisterTerminal.getSn());
					}
				}
			}
		}
		return returnFlg;
	}

	private BusiTerminal dealFcmType(BusiTerminal busiTerminal) {
		if(null != DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId())) {
			FcmBridge fcmBridge = null;
			BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
			if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
				if (busiTerminal.getFsServerId() != null) {
					fcmBridge = FcmBridgeCache.getInstance().get(busiTerminal.getFsServerId());
				}
				if (fcmBridge == null) {
					FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(fsd.getServerId());
					if (fcmBridgeCluster != null) {
						List<FcmBridge> fcmBridges = fcmBridgeCluster.getFcmBridges();
						// 由于使用固定用户信息数据库，任意一个FCM即可
						fcmBridge = fcmBridges.get(0);
					}
				}
			} else {
				fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
			}
			BusiFreeSwitch busiFreeSwitch = fcmBridge.getBusiFreeSwitch();
			busiTerminal.setIp(busiFreeSwitch.getIp());
		}else {
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "请检查该租户是否绑定FCM服务!");
		}
		return busiTerminal;
	}
	
	@Override
	public void terminalUpdated(BusiTerminal busiTerminal) {
		String clientId = busiTerminal.getSn();
		if(StringUtils.isNotEmpty(clientId)) {
			String action = TerminalTopic.MODIFY_TERMINAL_INFO;
			String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminal.getSn();
			if(null != busiTerminal) {
				ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, (JSONObject)JSON.toJSON(busiTerminal), clientId, "");
			}
		}
	}

	@Override
	public void terminalRemoved(BusiTerminal busiTerminal) {
		BusiRegisterTerminal registerTerminal = new BusiRegisterTerminal();
		registerTerminal.setMac(busiTerminal.getSn());

		List<BusiRegisterTerminal> registerTerminalList = busiRegisterTerminalMapper.selectBusiRegisterTerminalList(registerTerminal);
		if(null != registerTerminalList && registerTerminalList.size() > 0) {
			int delTerminal = busiRegisterTerminalMapper.deleteBusiRegisterTerminalById(registerTerminalList.get(0).getId());
			if(delTerminal > 0) {
				String clientId = busiTerminal.getSn();
				if(StringUtils.isNotEmpty(clientId)) {
					String action = TerminalTopic.ADMIN_DELETE_TERMINAL;
					String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminal.getSn();
					
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("deleteTerminal", busiTerminal.getId());
					ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic , action, jsonObject , clientId , "");
				}
			}
		}
		
//		String clientId = busiTerminal.getSn();
//		String action = TerminalTopic.ADMIN_DELETE_TERMINAL;
//		String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminal.getSn();
//		if(null != busiTerminal) {
//			if(TerminalType.FSBC_SIP.getId() == busiTerminal.getType()) {
//				BusiRegisterTerminal registerTerminal = new BusiRegisterTerminal();
//				registerTerminal.setTerminalId(busiTerminal.getId());
//				registerTerminal.setIsRelated("1");
//				List<BusiRegisterTerminal> registerTerminalList = busiRegisterTerminalMapper.selectBusiRegisterTerminalList(registerTerminal);
//				if(null != registerTerminalList && registerTerminalList.size() > 0) {
//					busiRegisterTerminalMapper.deleteBusiRegisterTerminalById(registerTerminalList.get(0).getId());
//				}
//			}
			
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("deleteTerminal", busiTerminal.getId());
//			ResponseTerminal.getInstance().responseTerminal(terminalTopic , action, jsonObject , clientId , "");
		}

}
