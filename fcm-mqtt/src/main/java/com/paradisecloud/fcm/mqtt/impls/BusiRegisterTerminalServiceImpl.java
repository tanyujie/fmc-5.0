package com.paradisecloud.fcm.mqtt.impls;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.zj.cache.DeptMcuZjMappingCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmAddUsrRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmGetUsrInfoRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmModUsrRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmSearchUsrRequest;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmAddUsrResponse;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmGetUsrInfoResponse;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmModUsrResponse;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmSearchUsrResponse;
import com.paradisecloud.fcm.mcu.zj.task.DelayTaskService;
import com.paradisecloud.fcm.terminal.fs.db.FreeSwitchTransaction;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.mapper.BusiRegisterTerminalMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.common.TerminalSipAccount;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.constant.TerminalTopic;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiRegisterTerminalService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.DeptFsbcMappingCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcCredential;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import com.sinhy.utils.RegExpUtils;

import javax.annotation.Resource;

/**
 * 需绑定fs账号的终端Service业务层处理
 * 
 * @author zyz
 * @date 2021-11-04
 */
@Transactional
@Service("busiRegisterTerminalService")
public class BusiRegisterTerminalServiceImpl implements IBusiRegisterTerminalService 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(BusiRegisterTerminalServiceImpl.class);
	
    @Resource
    private BusiRegisterTerminalMapper busiRegisterTerminalMapper;
    
    @Resource
    private BusiTerminalMapper busiTerminalMapper;
    
    @Resource
    private IBusiTerminalService busiTerminalService;

    @Resource
    private DelayTaskService delayTaskService;
    
	/**
     * fsbc账号样板
     */
    private Pattern numberPattern = Pattern.compile("^[1-9]\\d{3,9}$");
    
    /**
     * fsbc密码样板
     */
    private Pattern passwordPattern = Pattern.compile("^\\w{1,16}$");

	/**
	 * zj账号样板
	 */
	private Pattern numberPatternZj = Pattern.compile("^[1-9]\\d{3,9}$");

	/**
	 * zj密码样板
	 */
	private Pattern passwordPatternZj = Pattern.compile("^\\d{4,6}$");

    /**
     * 查询需绑定fs账号的终端
     * 
     * @param id 需绑定fs账号的终端ID
     * @return 需绑定fs账号的终端
     */
    @Override
    public BusiRegisterTerminal selectBusiRegisterTerminalById(Long id)
    {
        return busiRegisterTerminalMapper.selectBusiRegisterTerminalById(id);
    }

    /**
     * 查询需绑定fs账号的终端列表
     * 
     * @param busiRegisterTerminal 需绑定fs账号的终端
     * @return 需绑定fs账号的终端
     */
    @Override
    public List<BusiRegisterTerminal> selectBusiRegisterTerminalList(BusiRegisterTerminal busiRegisterTerminal)
    {
        return busiRegisterTerminalMapper.selectBusiRegisterTerminalByCodeAndMac(busiRegisterTerminal);
    }

    /**
     * 新增需绑定fs账号的终端
     * 
     * @param busiRegisterTerminal 需绑定fs账号的终端
     * @return 结果
     */
    @Override
    public int insertBusiRegisterTerminal(BusiRegisterTerminal busiRegisterTerminal)
    {
        busiRegisterTerminal.setCreateTime(new Date());
        return busiRegisterTerminalMapper.insertBusiRegisterTerminal(busiRegisterTerminal);
    }

    /**
     * 修改需绑定fs账号的终端
     * 
     * @param busiRegisterTerminal 需绑定fs账号的终端
     * @return 结果
     */
    @Override
    @FreeSwitchTransaction
    public int updateBusiRegisterTerminal(BusiRegisterTerminal busiRegisterTerminal)
    {
    	int terminal = 0;
    	if(null != busiRegisterTerminal) {
    		BusiRegisterTerminal registerTerminal = busiRegisterTerminalMapper.selectBusiRegisterTerminalById(busiRegisterTerminal.getId());
    		if(null != registerTerminal) {
    			registerTerminal.setUpdateTime(new Date());
    			registerTerminal.setTerminalId(busiRegisterTerminal.getTerminalId());
    			terminal = busiRegisterTerminalMapper.updateBusiRegisterTerminal(registerTerminal);
    			if(terminal > 0) {
    				BusiTerminal busiTerminal = busiTerminalMapper.selectBusiTerminalById(busiRegisterTerminal.getTerminalId());
    				if(null != busiTerminal) {
    					busiTerminal.setUpdateTime(new Date());
    					busiTerminal.setSn(registerTerminal.getSn());
    					int bTerminal = busiTerminalService.updateBusiTerminal(busiTerminal);
    					if(bTerminal > 0) {
    						TerminalSipAccount.getInstance().terminalGetSipAccount(busiTerminal.getId().toString(), busiTerminal);
    					}
    				}
    			}
    		}
    	}
        return terminal;
    }
    
    /**
     * 批量删除需绑定fs账号的终端
     * 
     * @param ids 需要删除的需绑定fs账号的终端ID
     * @return 结果
     */
    @Override
    public int deleteBusiRegisterTerminalByIds(Long[] ids)
    {
        return busiRegisterTerminalMapper.deleteBusiRegisterTerminalByIds(ids);
    }

    /**
     * 删除需绑定fs账号的终端信息
     * 
     * @param id 需绑定fs账号的终端ID
     * @return 结果
     */
    @Override
    public int deleteBusiRegisterTerminalById(Long id)
    {
        return busiRegisterTerminalMapper.deleteBusiRegisterTerminalById(id);
    }
    
    @Override
	public void sipRegister(JSONObject jsonS, String clientId, String messageId) {
		if(null != jsonS) {
			JSONObject jObjs = new JSONObject();
			String action = TerminalTopic.SIP_REGISTER;
			String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + clientId;
			String sn = (String)jsonS.getString(MqttConfigConstant.SN);
			String ip = (String)jsonS.getString(MqttConfigConstant.IP);
			String connectIp = jsonS.getString(MqttConfigConstant.CONNECT_IP);
			String code = (String)jsonS.getString(MqttConfigConstant.CODE);
			String mac = (String)jsonS.getString(MqttConfigConstant.MAC);
			String type = (String)jsonS.getString(MqttConfigConstant.TERMINAL_TYPE);
			String credential = (String)jsonS.getString("credential");
			String versionCode = (String) jsonS.getString(MqttConfigConstant.versionCode);
			String versionName = jsonS.getString(MqttConfigConstant.versionName);

			LOGGER.info("===============》sipRegister ###" + jsonS.toString());

			if(StringUtils.isNotEmpty(mac)) {
				BusiTerminal busiTerminal = TerminalCache.getInstance().getBySn(mac);
				if (busiTerminal != null) {
					LOGGER.info("===============》sipRegister ### 注册终端表已存在");
					this.updateBusiTerminalVersion(busiTerminal, versionCode, versionName ,type);

					jObjs.put("deptId", busiTerminal.getDeptId());
					jObjs.put("type", busiTerminal.getType());
					if((StringUtils.isNotEmpty(ip) && !ip.equals(busiTerminal.getIntranetIp()))
							|| (StringUtils.isNotEmpty(mac) && !mac.equals(busiTerminal.getSn()))
							|| (StringUtils.isNotEmpty(connectIp) && !connectIp.equals(busiTerminal.getConnectIp()))) {
						busiTerminal.setIntranetIp(ip);
						busiTerminal.setSn(mac);
						busiTerminal.setUpdateTime(new Date());
						busiTerminal.setConnectIp(connectIp);
						this.updateBusiTerminalStatus(busiTerminal);
					}

					if (TerminalType.isFSBC(busiTerminal.getType())) {
						//Fsbc类型
						TerminalSipAccount.getInstance().vhdTermminalGetSipAccount(busiTerminal, "", mac);
					} else if (TerminalType.isFCMSIP(busiTerminal.getType())) {
						//Fcm类型
						TerminalSipAccount.getInstance().setTopBoxGetSipAccount("", busiTerminal);
					} else if (TerminalType.isZJ(busiTerminal.getType())) {
						//zj
						TerminalSipAccount.getInstance().zjTerminalGetSipAccount(busiTerminal, "", mac);
					} else if (TerminalType.isSMCSIP(busiTerminal.getType())) {
						// smc
						TerminalSipAccount.getInstance().smcTerminalGetSipAccount(busiTerminal, "", mac);
					} else if (TerminalType.isSMC2SIP(busiTerminal.getType())) {
						// smc2
						TerminalSipAccount.getInstance().smc2TerminalGetSipAccount(busiTerminal, "", mac);
					} else if (TerminalType.isHwCloud(busiTerminal.getType())) {
						// hwCloud
						TerminalSipAccount.getInstance().hwCloudTerminalGetSipAccount(busiTerminal, "", mac);
					}
					ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jObjs, clientId, messageId);
					return;
				}
			}

			BusiRegisterTerminal busiRegisterTerminal = new BusiRegisterTerminal();
			busiRegisterTerminal.setMac(mac);
			List<BusiRegisterTerminal> registerTerminals = busiRegisterTerminalMapper.selectBusiRegisterTerminalList(busiRegisterTerminal);
			busiRegisterTerminal.setIp(ip);
			busiRegisterTerminal.setSn(sn);
			busiRegisterTerminal.setCode(code);
			busiRegisterTerminal.setTerminalType(type);
			busiRegisterTerminal.setCredential(credential);
			busiRegisterTerminal.setIsRelated(MqttConfigConstant.ZERO.toString());
			busiRegisterTerminal.setCreateTime(new Date());
			busiRegisterTerminal.setAppVersionCode(versionCode);
			busiRegisterTerminal.setAppVersionName(versionName);
			busiRegisterTerminal.setConnectIp(connectIp);

			LOGGER.info("===============》sipRegister ### busiRegisterTerminal" + busiRegisterTerminal.toString());
			
			if(null != registerTerminals && registerTerminals.size() > 0) {
				Long registerId = registerTerminals.get(0).getId();
				Long terminalId = registerTerminals.get(0).getTerminalId();

				LOGGER.info("===============》sipRegister ### registerId" + registerId);
				LOGGER.info("===============》sipRegister ### terminalId" + terminalId);
				jObjs.put(MqttConfigConstant.ID, registerId);
				if(null != terminalId) {
					BusiTerminal bTerminal = TerminalCache.getInstance().get(terminalId);
					if(null != bTerminal) {
						jObjs.put("deptId", bTerminal.getDeptId());
						jObjs.put("type", bTerminal.getType());
						if(!mac.equals(bTerminal.getSn()) || (StringUtils.isNotEmpty(ip) && !ip.equals(bTerminal.getIntranetIp())) || (StringUtils.isNotEmpty(connectIp) && !connectIp.equals(bTerminal.getConnectIp()))) {
							bTerminal.setIntranetIp(ip);
							bTerminal.setSn(mac);
							bTerminal.setUpdateTime(new Date());
							bTerminal.setConnectIp(connectIp);
							this.updateBusiTerminalStatus(bTerminal);
						}
						this.updateBusiTerminalVersion(bTerminal, versionCode, versionName ,type);

						if (TerminalType.isFSBC(bTerminal.getType())) {
							//Fsbc类型
							TerminalSipAccount.getInstance().vhdTermminalGetSipAccount(bTerminal, "", busiRegisterTerminal.getMac());
						} else if (TerminalType.isFCMSIP(bTerminal.getType())) {
							//Fcm类型
							TerminalSipAccount.getInstance().setTopBoxGetSipAccount("", bTerminal);
						} else if (TerminalType.isZJ(bTerminal.getType())) {
							//zj
							TerminalSipAccount.getInstance().zjTerminalGetSipAccount(bTerminal, "", busiRegisterTerminal.getMac());
						}
						ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jObjs, clientId, messageId);
						return;
					}
				} else {
					if(StringUtils.isNotEmpty(credential)) {
						BusiTerminal terminal = new BusiTerminal();
						terminal.setCredential(credential);
						List<BusiTerminal> busiTerminalList = busiTerminalMapper.selectBusiTerminalList(terminal);
						if(null != busiTerminalList && busiTerminalList.size() > 0) {
							BusiTerminal busiTerminal1 = busiTerminalList.get(0);
							
							BusiRegisterTerminal registerTerminal = new BusiRegisterTerminal();
							registerTerminal.setCredential(busiTerminal1.getCredential());
							registerTerminal.setTerminalId(busiTerminal1.getId());
							List<BusiRegisterTerminal> registerTers = busiRegisterTerminalMapper.selectBusiRegisterTerminalList(registerTerminal);
							if (null != registerTers && registerTers.size() > 0) {
								jObjs.put("isBind", true);
								jObjs.put("credential", busiRegisterTerminal.getCredential());
							} else {
								jObjs.put("deptId", busiTerminal1.getDeptId());
								registerTerminals.get(0).setCredential(credential);

								//会控以手动增加了账号，直接关联
								this.fcmAddFcmSipAccount(busiTerminal1, registerTerminals.get(0));
							}
							jObjs.put("type", busiTerminal1.getType());

							this.updateBusiTerminalVersion(busiTerminal1, versionCode, versionName ,type);

							if (TerminalType.isFSBC(busiTerminal1.getType())) {
								//Fsbc类型
								TerminalSipAccount.getInstance().vhdTermminalGetSipAccount(busiTerminal1, "", busiRegisterTerminal.getMac());
							} else if (TerminalType.isFCMSIP(busiTerminal1.getType())) {
								//Fcm类型
								TerminalSipAccount.getInstance().setTopBoxGetSipAccount("", busiTerminal1);
							} else if (TerminalType.isZJ(busiTerminal1.getType())) {
								//zj
								TerminalSipAccount.getInstance().zjTerminalGetSipAccount(busiTerminal1, "", busiRegisterTerminal.getMac());
							} else if (TerminalType.isSMCSIP(busiTerminal1.getType())) {
								//smc
								TerminalSipAccount.getInstance().smcTerminalGetSipAccount(busiTerminal1, "", busiRegisterTerminal.getMac());
							} else if (TerminalType.isSMC2SIP(busiTerminal1.getType())) {
								//smc2
								TerminalSipAccount.getInstance().smc2TerminalGetSipAccount(busiTerminal1, "", busiRegisterTerminal.getMac());
							} else if (TerminalType.isHwCloud(busiTerminal1.getType())) {
								//hwCloud
								TerminalSipAccount.getInstance().hwCloudTerminalGetSipAccount(busiTerminal1, "", busiRegisterTerminal.getMac());
							}
							ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jObjs, clientId, messageId);
							return;
						}else {
							jObjs.put("invalidNumber", credential);
						}
					}else {
						registerTerminals.get(0).setIp(ip);
						registerTerminals.get(0).setCode(code);
						registerTerminals.get(0).setMac(mac);
						registerTerminals.get(0).setUpdateTime(new Date());
						registerTerminals.get(0).setConnectIp(connectIp);
						//registerTerminals.get(0).set
						int ter = busiRegisterTerminalMapper.updateBusiRegisterTerminal(registerTerminals.get(0));
						if(ter > 0) {
							jObjs.put("deptId", "");
						}
					}
				}
			} else {
				LOGGER.info("===============》sipRegister ###" + jObjs.toString());
				//sip账号为空的处理
				jObjs = this.putRegisterTerminalInfo(jObjs, credential, mac, busiRegisterTerminal);
			}

			LOGGER.info("===============》sipRegister ###" + jObjs.toString());
			ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jObjs, clientId, messageId);
		}
	}

	private void updateBusiTerminalVersion(BusiTerminal busiTerminal, String versionCode, String versionName, String type) {
		if (StringUtils.isNotEmpty(versionCode) && StringUtils.isNotEmpty(versionName)
				&& (!versionCode.equals(busiTerminal.getAppVersionCode()) || !versionName.equals(busiTerminal.getAppVersionName()))) {
			busiTerminal = TerminalCache.getInstance().get(busiTerminal.getId());
			if (busiTerminal != null) {
				busiTerminal.setAppVersionCode(versionCode);
				busiTerminal.setAppVersionName(versionName);
				AppType appType = AppType.convertByType(type);
				if (appType != null) {
					String oldAppType = busiTerminal.getAppType();
					busiTerminal.setAppType(appType.getCode());
					TerminalCache.getInstance().updateAppTypeTerminalMap(oldAppType, busiTerminal);
				}
				busiTerminalMapper.updateBusiTerminal(busiTerminal);
			}
		}
	}
    
    public void updateBusiTerminalStatus(BusiTerminal busiTerminal) {
		busiTerminal.setUpdateTime(new Date());
        
        if (busiTerminal.getDeptId() == null)
        {
            throw new SystemException(1000102, "请选择部门");
        }
        
        if (!TerminalType.isFSBC(busiTerminal.getType()) && !TerminalType.isFCMSIP(busiTerminal.getType()) && !RegExpUtils.isIP(busiTerminal.getIp()))
        {
            throw new SystemException(1000103, "IP格式不正确");
        }
        
        Assert.notNull(busiTerminal.getBusinessFieldType(), "终端业务领域类型businessFieldType不能为空！");
        Assert.notNull(busiTerminal.getAttendType(), "终端入会类型不能为空！");
        AttendType.convert(busiTerminal.getAttendType());
        
        
        BusiTerminal ot = busiTerminalMapper.selectBusiTerminalById(busiTerminal.getId());
        if (TerminalType.isFSBC(ot.getType()) && !TerminalType.isFSBC(busiTerminal.getType()) || TerminalType.isFCMSIP(ot.getType()) && !TerminalType.isFCMSIP(busiTerminal.getType()) || TerminalType.isZJ(ot.getType()) && !TerminalType.isZJ(busiTerminal.getType()))
        {
            throw new SystemException(1009894, "该终端不能修改类型，若是要修改，请删除后再新增！");
        }
        
        if (TerminalType.isFSBC(busiTerminal.getType()))
        {
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()), "FSBC账号不能为空");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()), "FSBC密码不能为空");
            Assert.isTrue(ot.getDeptId().equals(busiTerminal.getDeptId()), "FSBC终端账号不支持切换部门，请删除重建");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPattern.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "FSBC-SIP账号必须为4-10位数字组成！");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPattern.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "FSBC密码必须为1-16位字母、数字和下划线组成！");
            
            if (!(ot.getCredential().equals(busiTerminal.getCredential()) && ot.getPassword().equals(busiTerminal.getPassword())))
            {
                BusiFsbcServerDept fsd = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                Assert.notNull(fsd, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前租户未绑定FSBC服务器，请联系管理员配置您的FSBC服务器！");
                
                BusiTerminal con = new BusiTerminal();
                con.setFsbcServerId(fsd.getFsbcServerId());
                con.setCredential(busiTerminal.getCredential());
                List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
                Assert.isTrue(!ObjectUtils.isEmpty(ts), "该账号已不存在，请删了重新添加");
                
                FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(fsd.getFsbcServerId());
                RestResponse restResponse = fsbcBridge.getCredentialInvoker().update(FsbcCredential.newCredential().name(ot.getCredential()).newName(busiTerminal.getCredential()).password(busiTerminal.getPassword()));
                Assert.isTrue(restResponse.isSuccess() || restResponse.getMessage().equals("NewName already exist, choose another NewName"), "FSBC账号已存在，请选择其它名字！");
                busiTerminal.setFsbcServerId(fsd.getFsbcServerId());
            }
        }
        // FCM 类型
        else if (TerminalType.isFCMSIP(busiTerminal.getType()))
        {
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()), "FCM-SIP账号不能为空");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()), "FCM-SIP密码不能为空");
            Assert.isTrue(ot.getDeptId().equals(busiTerminal.getDeptId()), "FCM-SIP终端账号不支持切换部门，请删除重建");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPattern.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "FCM-SIP账号必须为4-10位数字组成！");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPattern.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "FCM-SIP密码必须为1-16位字母、数字和下划线组成！");
            
            BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
            Assert.notNull(fsd, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前未绑定FCM服务器，请联系管理员配置您的FCM服务器！");
            if (FcmType.CLUSTER.getValue() == fsd.getFcmType()) {
//				busiTerminal.setFsServerId(fsd.getServerId());// 集群时不插入
			} else {
            	busiTerminal.setFsServerId(fsd.getServerId());
			}
        }
		// ZJ 类型
		else if (TerminalType.isZJ(busiTerminal.getType())) {
			Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()), "ZJ账号不能为空");
			Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()), "ZJ密码不能为空");
			Assert.isTrue(ot.getDeptId().equals(busiTerminal.getDeptId()), "ZJ终端账号不支持切换部门，请删除重建");
			Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPatternZj.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "ZJ账号必须为4-10位数字组成！");
			Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPatternZj.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "ZJ账户密码必须为4-6位数字！");
			List<McuZjBridge> mcuZjBridgeList = McuZjBridgeCache.getInstance().getMcuZjBridgesByDept(busiTerminal.getDeptId());
			McuZjBridge mcuZjBridge = null;
			if (mcuZjBridgeList != null && mcuZjBridgeList.size() > 0) {
				mcuZjBridge = mcuZjBridgeList.get(0);
			}
			Assert.notNull(mcuZjBridge, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前未绑定ZJ服务器，请联系管理员配置您的ZJ服务器！");

			busiTerminal.setFsServerId(mcuZjBridge.getBusiMcuZj().getId());
			busiTerminal.setIp(mcuZjBridge.getBusiMcuZj().getIp());

			BusiTerminal busiTerminalExist = TerminalCache.getInstance().get(busiTerminal.getId());
			String terminalNum = StringUtils.leftPad(busiTerminalExist.getTerminalNum().toString(), 5, "0");
			CmSearchUsrRequest cmSearchUsrRequest = new CmSearchUsrRequest();
			String[] filterType = new String[1];
			filterType[0] = "usr_mark";
			Object[] filterValue = new Object[1];
			filterValue[0] = terminalNum;
			cmSearchUsrRequest.setFilter_type(filterType);
			cmSearchUsrRequest.setFilter_value(filterValue);
			CmSearchUsrResponse cmSearchUsrResponse = mcuZjBridge.getConferenceManageApi().searchUsr(cmSearchUsrRequest);
			if (cmSearchUsrResponse != null && cmSearchUsrResponse.getUsr_ids().length > 0) {
				// 存在
				Integer[] usrIds = cmSearchUsrResponse.getUsr_ids();
				CmGetUsrInfoRequest cmGetUsrInfoRequest = new CmGetUsrInfoRequest();
				cmGetUsrInfoRequest.setUsr_ids(cmSearchUsrResponse.getUsr_ids());
				Integer[] lastModifyDtms = new Integer[usrIds.length];
				for (int i = 0; i < usrIds.length; i++) {
					lastModifyDtms[i] = 0;
				}
				cmGetUsrInfoRequest.setLast_modify_dtms(lastModifyDtms);
				CmGetUsrInfoResponse cmGetUsrInfoResponse = mcuZjBridge.getConferenceManageApi().getUsrInfo(cmGetUsrInfoRequest);
				if (cmGetUsrInfoResponse != null && cmGetUsrInfoResponse.getUsr_ids().length > 0) {
					for (int i = 0; i < cmGetUsrInfoResponse.getUsr_ids().length; i++) {
						Integer usrId = cmGetUsrInfoResponse.getUsr_ids()[i];
						String nickName = cmGetUsrInfoResponse.getNick_names()[i];
						if (!nickName.equals(busiTerminal.getName())) {
							CmModUsrRequest cmModUsrRequest = new CmModUsrRequest();
							cmModUsrRequest.setUsr_id(usrId);
							cmModUsrRequest.setNick_name(busiTerminal.getName());
							CmModUsrResponse cmModUsrResponse = mcuZjBridge.getConferenceManageApi().modifyUsr(cmModUsrRequest);
							if (cmModUsrResponse != null) {
							}
						}
					}
				}
			}
		}
        
        int c = busiTerminalMapper.updateBusiTerminal(busiTerminal);
        if (c > 0)
        {
            TerminalCache.getInstance().remove(ot.getId());
            TerminalCache.getInstance().put(busiTerminal.getId(), busiTerminalMapper.selectBusiTerminalById(busiTerminal.getId()));
        }
	}

	private JSONObject putRegisterTerminalInfo(JSONObject jObjs, String credential, String mac, BusiRegisterTerminal busiRegisterTerminal) {
		BusiTerminal busiTerminal = new BusiTerminal();
		if(StringUtils.isNotEmpty(credential)) {
			busiTerminal.setCredential(credential);
		} else {
			busiTerminal.setSn(mac);
		}
		
		List<BusiTerminal> terminals = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
		if(null != terminals && terminals.size() > 0) {
				busiRegisterTerminal.setTerminalId(terminals.get(0).getId());
				busiRegisterTerminal.setIsRelated(MqttConfigConstant.SUCCESS.toString());
				busiRegisterTerminal.setCredential(terminals.get(0).getCredential());
				
				BusiRegisterTerminal registerTerminal = new BusiRegisterTerminal();
				registerTerminal.setCredential(terminals.get(0).getCredential());
				registerTerminal.setTerminalId(terminals.get(0).getId());
				List<BusiRegisterTerminal> registerTers = busiRegisterTerminalMapper.selectBusiRegisterTerminalList(registerTerminal);
				if(null != registerTers && registerTers.size() > 0) {
						jObjs.put("isBind", true);
						jObjs.put("credential", busiRegisterTerminal.getCredential());
				}else {
					BusiTerminal terminal = terminals.get(0);
//					if(StringUtils.isEmpty(terminal.getSn())) {
						if(TerminalType.isFSBC(terminal.getType()) ) {
							
							//Fsbc类型
							terminal = this.dealFsbcType(terminal);
						}else if(TerminalType.isFCMSIP(terminal.getType())){
							
							//Fcm类型
							terminal = this.dealFcmType(terminal);
						} else if (TerminalType.isZJ(terminal.getType())) {

							//zj
							terminal = this.dealZjType(terminal);
						}

						terminal.setSn(mac);
						terminal.setUpdateTime(new Date());
						terminal.setIntranetIp(busiRegisterTerminal.getIp());
//						busiTerminalService.updateBusiTerminal(terminal);
						this.updateBusiTerminalStatus(terminal);
						
						//更新待注册数据
						jObjs = this.updateRegisterTermail(busiRegisterTerminal , jObjs);
						jObjs.put("type", terminals.get(0).getType());
						jObjs.put("deptId", terminal.getDeptId());
						jObjs.put("deptName", SysDeptCache.getInstance().get(terminal.getDeptId()).getDeptName());
//					}
				}
		} else {
			busiRegisterTerminal.setIsRelated(MqttConfigConstant.ZERO.toString());
			jObjs.put("deptId", "");
			
			LOGGER.info("===============》批量删除22222" + jObjs.toString());
			
			//更新待注册数据
			jObjs = this.updateRegisterTermail(busiRegisterTerminal , jObjs);
			
			LOGGER.info("===============》批量删除44444" + jObjs.toString());
		}
		
		return jObjs;
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

	private BusiTerminal dealFsbcType(BusiTerminal busiTerminal) {
		if(null != DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId())) {
			FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId()).getFsbcServerId());
			BusiFsbcRegistrationServer fsbcRegistrationServer = fsbcBridge.getBusiFsbcRegistrationServer();
			busiTerminal.setIp(fsbcRegistrationServer.getCallIp());
		}else {
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "请检查该租户是否绑定FSBC服务!");
		}
		return busiTerminal;
	}

	private BusiTerminal dealZjType(BusiTerminal busiTerminal) {
		if(null != DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId())) {
			McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(DeptMcuZjMappingCache.getInstance().getBindMcu(busiTerminal.getDeptId()).getMcuId());
			BusiMcuZj busiMcuZj = mcuZjBridge.getBusiMcuZj();
			busiTerminal.setIp(busiMcuZj.getIp());
		}else {
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "请检查该租户是否绑定FSBC服务!");
		}
		return busiTerminal;
	}

	private JSONObject updateRegisterTermail(BusiRegisterTerminal busiRegisterTerminal, JSONObject jObjs) {
		int terminal = busiRegisterTerminalMapper.insertBusiRegisterTerminal(busiRegisterTerminal);
		LOGGER.info("===============》批量删除55555" + busiRegisterTerminal.toString());
		if(terminal > 0) {
			jObjs.put(MqttConfigConstant.ID, busiRegisterTerminal.getId());
			LOGGER.info("===============》批量删除33333" + jObjs.toString());
		}
		return jObjs;
	}

	private void fcmAddFcmSipAccount(BusiTerminal busiTerminal1, BusiRegisterTerminal busiRegisterTerminal) {
		busiRegisterTerminal.setTerminalId(busiTerminal1.getId());
		busiRegisterTerminal.setIsRelated(MqttConfigConstant.SUCCESS.toString());
		busiRegisterTerminal.setUpdateTime(new Date());
		int reg = busiRegisterTerminalMapper.updateBusiRegisterTerminal(busiRegisterTerminal);
		if(reg > 0) {
			busiTerminal1.setSn(busiRegisterTerminal.getMac());
			busiTerminal1.setIntranetIp(busiRegisterTerminal.getIp());
			busiTerminal1.setUpdateTime(new Date());
			
			if(TerminalType.isFCMSIP(busiTerminal1.getType())) {
				FcmBridge fcmBridge = null;
				BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(busiTerminal1.getDeptId());
				if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
					if (busiTerminal1.getFsServerId() != null) {
						fcmBridge = FcmBridgeCache.getInstance().get(busiTerminal1.getFsServerId());
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
				busiTerminal1.setIp(busiFreeSwitch.getIp());
			}else if(TerminalType.isFSBC(busiTerminal1.getType())) {
				FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(DeptFsbcMappingCache.getInstance().get(busiTerminal1.getDeptId()).getFsbcServerId());
				BusiFsbcRegistrationServer fsbcRegistrationServer = fsbcBridge.getBusiFsbcRegistrationServer();
				busiTerminal1.setIp(fsbcRegistrationServer.getCallIp());
			} else if (TerminalType.isZJ(busiTerminal1.getType())) {
				McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(DeptMcuZjMappingCache.getInstance().getBindMcu(busiTerminal1.getDeptId()).getMcuId());
				BusiMcuZj busiMcuZj = mcuZjBridge.getBusiMcuZj();
				busiTerminal1.setIp(busiMcuZj.getIp());
			}
			
//			busiTerminalService.updateBusiTerminal(busiTerminal1);
			this.updateBusiTerminalStatus(busiTerminal1);
		}
		
	}

	@Override
	public void insertBusiTerminalData(BusiTerminal busiTerminal) {
		if (busiTerminal.getDeptId() == null)
        {
            throw new SystemException(1000102, "请选择部门");
        }
        
        if (!TerminalType.isFSBC(busiTerminal.getType()) && !TerminalType.isFCMSIP(busiTerminal.getType()) &&!TerminalType.isZJ(busiTerminal.getType()) && !RegExpUtils.isIP(busiTerminal.getIp()))
        {
            throw new SystemException(1000103, "IP格式不正确");
        }
        
        Assert.notNull(busiTerminal.getBusinessFieldType(), "终端业务领域类型businessFieldType不能为空！");
        Assert.notNull(busiTerminal.getAttendType(), "终端入会类型不能为空！");
        AttendType.convert(busiTerminal.getAttendType());
        
        LoginUser loginUser = SecurityUtils.getLoginUser();
        busiTerminal.setCreateUserId(loginUser.getUser().getUserId());
        busiTerminal.setCreateUserName(loginUser.getUser().getUserName());
        
        busiTerminal.setCreateTime(new Date());
        busiTerminal.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
        busiTerminal.setMqttOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
        if (TerminalType.isFSBC(busiTerminal.getType()))
        {
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPattern.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "FSBC-SIP账号必须为4-10位数字组成！");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPattern.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "FSBC密码必须为1-16位字母、数字和下划线组成！");
            BusiFsbcServerDept fsd = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
            Assert.notNull(fsd, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前未绑定FSBC服务器，请联系管理员配置您的FSBC服务器！");
            
            BusiTerminal con = new BusiTerminal();
            con.setFsbcServerId(fsd.getFsbcServerId());
            con.setCredential(busiTerminal.getCredential());
            List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
            Assert.isTrue(ObjectUtils.isEmpty(ts), "该账号已存在，请勿重复添加");
            
            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(fsd.getFsbcServerId());
            
            // 创建
            RestResponse restResponse = fsbcBridge.getCredentialInvoker().create(FsbcCredential.newCredential().name(busiTerminal.getCredential()).password(busiTerminal.getPassword()));
            Assert.isTrue(restResponse.isSuccess() || restResponse.getMessage().equals("Credential name already exists"), "新增终端FSBC账号失败");
            busiTerminal.setFsbcServerId(fsd.getFsbcServerId());
        }
        // FCM 类型
        else if (TerminalType.isFCMSIP(busiTerminal.getType()))
        {
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPattern.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "FCM-SIP账号必须为4-10位数字组成！");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPattern.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "FCM-SIP账户密码必须为1-16位字母、数字和下划线组成！");
            BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
            Assert.notNull(fsd, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前未绑定FCM服务器，请联系管理员配置您的FCM服务器！");

            BusiTerminal con = new BusiTerminal();
            con.setType(TerminalType.FCM_SIP.getId());
            con.setCredential(busiTerminal.getCredential());
            List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
            Assert.isTrue(ObjectUtils.isEmpty(ts), "该账号已存在，请勿重复添加");
            Assert.isTrue(busiTerminalService.isFcm(busiTerminal.getDeptId(),busiTerminal.getCredential()),"该部门未分配FCM号段,请联系管理员分配！");

			if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
				FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(fsd.getServerId());
				if (fcmBridgeCluster != null) {
					List<FcmBridge> fcmBridges = fcmBridgeCluster.getFcmBridges();
					// 由于使用固定用户信息数据库，任意一个FCM即可
					FcmBridge fcmBridge = fcmBridges.get(0);
					Assert.isTrue(fcmBridge.addFreeSwitchUser(busiTerminal.getCredential(), busiTerminal.getPassword()) == FcmConfigConstant.SUCCESS, "添加FCM-SIP账号失败");
					busiTerminal.setFsServerId(fcmBridge.getBusiFreeSwitch().getId());// 集群插入时随机插入一个FCM的id
					busiTerminal.setIp(fcmBridge.getBusiFreeSwitch().getIp());
				} else {
					Assert.isTrue(0 == FcmConfigConstant.SUCCESS, "添加FCM-SIP账号失败");
				}
			} else {
				FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
				Assert.isTrue(fcmBridge.addFreeSwitchUser(busiTerminal.getCredential(), busiTerminal.getPassword()) == FcmConfigConstant.SUCCESS, "添加FCM-SIP账号失败");
				busiTerminal.setFsServerId(fsd.getServerId());
				busiTerminal.setIp(fcmBridge.getBusiFreeSwitch().getIp());
			}
        }
		// ZJ 类型
		else if (TerminalType.isZJ(busiTerminal.getType())) {

			Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPatternZj.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "ZJ账号必须为4-10位数字组成！");
			Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPatternZj.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "ZJ账户密码必须为4-6位数字！");
			List<McuZjBridge> mcuZjBridgeList = McuZjBridgeCache.getInstance().getMcuZjBridgesByDept(busiTerminal.getDeptId());
			McuZjBridge mcuZjBridge = null;
			if (mcuZjBridgeList != null && mcuZjBridgeList.size() > 0) {
				mcuZjBridge = mcuZjBridgeList.get(0);
			}
			Assert.notNull(mcuZjBridge, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前未绑定ZJ服务器，请联系管理员配置您的ZJ服务器！");

			{
				BusiTerminal con = new BusiTerminal();
				con.setType(TerminalType.ZJ_H323.getId());
				con.setCredential(busiTerminal.getCredential());
				List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
				Assert.isTrue(ObjectUtils.isEmpty(ts), "该账号已存在，请勿重复添加");
			}
			{
				BusiTerminal con = new BusiTerminal();
				con.setType(TerminalType.ZJ_SIP.getId());
				con.setCredential(busiTerminal.getCredential());
				List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
				Assert.isTrue(ObjectUtils.isEmpty(ts), "该账号已存在，请勿重复添加");
			}
			try {
				Integer newTerminalNum = busiTerminalMapper.getAvailableTerminalNumForZj();
				if (newTerminalNum == null) {
					newTerminalNum = 50001;
				}
				if (newTerminalNum >= 100000) {
					throw new Exception("MCU资源耗尽。ZJ终端数超过50000");//ZJ终端50000-99999
				}
				String terminalNumStr = StringUtils.leftPad(newTerminalNum.toString(), 5, "0");
				String userMark = terminalNumStr;
				String nickName = busiTerminal.getName();
				CmAddUsrRequest cmAddUsrRequest = CmAddUsrRequest.buildDefaultRequestForAddEps();
				cmAddUsrRequest.setNick_name(nickName);
				cmAddUsrRequest.setLogin_id(busiTerminal.getCredential());
				cmAddUsrRequest.setLogin_pwd(busiTerminal.getPassword());
				cmAddUsrRequest.setUsr_mark(userMark);
				List<Integer> belongToDepartments = new ArrayList<>();
				belongToDepartments.add(mcuZjBridge.getTopDepartmentId());// 总部
				cmAddUsrRequest.setBelong_to_departments(belongToDepartments);
				if (TerminalType.ZJ_H323.getId() == busiTerminal.getType()) {
					cmAddUsrRequest.setPtotocol_type(1);//  1 H323 协议， 2 SIP 协议， 3 多流协议， 5：RTSP 协议， 默认为多流协议
				} else {
					cmAddUsrRequest.setPtotocol_type(2);//  1 H323 协议， 2 SIP 协议， 3 多流协议， 5：RTSP 协议， 默认为多流协议
				}
				cmAddUsrRequest.setOption("endpoint");
				cmAddUsrRequest.setIs_endpoint(1);
				CmAddUsrResponse cmAddUsrResponse = mcuZjBridge.getConferenceManageApi().addUsr(cmAddUsrRequest);
				if (cmAddUsrResponse != null && cmAddUsrResponse.getResult().contains("success")) {
					busiTerminal.setTerminalNum(newTerminalNum);
					busiTerminal.setZjServerId(mcuZjBridge.getBusiMcuZj().getId());
					busiTerminal.setZjUserId(Long.valueOf(cmAddUsrResponse.getUsr_id()));//存储zj的用户id
					busiTerminal.setIp(mcuZjBridge.getBusiMcuZj().getIp());
				} else {
					Assert.isTrue(false, "添加ZJ账号失败！");
				}
			} catch (Exception e) {
				Assert.isTrue(false, "添加ZJ终端失败！");
			}
		}
        else {
			BusiTerminal con = new BusiTerminal();
			con.setIp(busiTerminal.getIp());
			if (!ObjectUtils.isEmpty(busiTerminal.getNumber())) {
				con.setNumber(busiTerminal.getNumber());
			}
			List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalListForIpTerminal(con.getIp(), con.getNumber());
			Assert.isTrue(ObjectUtils.isEmpty(ts), "该终端在本部门或者其它部门已存在，请勿重复添加");
		}
        
        int c = 0;
        try
        {
            c = busiTerminalMapper.insertBusiTerminal(busiTerminal);
            if (c > 0)
            {
                TerminalCache.getInstance().put(busiTerminal.getId(), busiTerminal);
            }
        }
        catch (Exception e)
        {
            throw new SystemException("该账号已存在，请勿重复添加", e);
        }
    }

}
