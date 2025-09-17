package com.paradisecloud.fcm.mqtt.impls;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.MessageUtils;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.PurchaseType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.BusiOpsResourceVo;
import com.paradisecloud.fcm.dao.model.vo.BusiOpsVo;
import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.constant.DeviceAction;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.interfaces.IOpsActionService;
import com.paradisecloud.fcm.ops.cloud.cache.OpsCache;
import com.paradisecloud.fcm.ops.cloud.interfaces.IBusiOpsResourceService;
import com.paradisecloud.fcm.ops.cloud.interfaces.IBusiOpsService;
import com.paradisecloud.framework.manager.AsyncManager;
import com.paradisecloud.framework.manager.factory.AsyncFactory;
import com.paradisecloud.framework.web.service.TokenService;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.service.ISysUserService;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class OpsActionServiceImpl implements IOpsActionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpsActionServiceImpl.class);

    @Resource
    private TokenService tokenService;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private IBusiOpsService busiOpsService;
    @Resource
    private IBusiOpsResourceService busiOpsResourceService;

    @Override
    public void register(JSONObject jsonS, String clientId) {
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX_OPS + clientId;
        String action = DeviceAction.REGISTER;
        JSONObject jsonObject = new JSONObject();
        if (jsonS != null) {
            String ip = jsonS.getString(MqttConfigConstant.IP);
            String appType = jsonS.getString(MqttConfigConstant.TERMINAL_TYPE);
            String versionCode = jsonS.getString(MqttConfigConstant.versionCode);
            String versionName = jsonS.getString(MqttConfigConstant.versionName);
            String connectIp = jsonS.getString(MqttConfigConstant.CONNECT_IP);
            String email = jsonS.getString("email");
            String phoneNumber = jsonS.getString("phoneNumber");

//            AppType appTypeTemp = AppType.convertByType(appType);
            BusiOps busiOps = OpsCache.getInstance().getBySn(clientId);
            if (busiOps != null) {
                boolean needUpdate = false;
                BusiOpsVo busiOpsVo = new BusiOpsVo();
                BeanUtils.copyProperties(busiOps, busiOpsVo);
                if (!versionCode.equals(busiOps.getAppVersionCode())
                        || !versionName.equals(busiOps.getAppVersionName())
                        || !connectIp.equals(busiOps.getConnectIp())
                        || !ip.equals(busiOps.getIp())) {
                    busiOpsVo.setAppVersionCode(versionCode);
                    busiOpsVo.setAppVersionName(versionName);
                    busiOpsVo.setConnectIp(connectIp);
                    busiOpsVo.setIp(ip);
                    needUpdate = true;
                }
                List<BusiOpsResourceVo> resourceList = new ArrayList<>();
                BusiOpsResourceVo busiOpsResourceCon = new BusiOpsResourceVo();
                busiOpsResourceCon.setSn(busiOps.getSn());
                List<BusiOpsResource> busiOpsResources = busiOpsResourceService.selectBusiOpsResourceList(busiOpsResourceCon);
                for (BusiOpsResource busiOpsResource : busiOpsResources) {
                    BusiOpsResourceVo busiOpsResourceVo = new BusiOpsResourceVo();
                    BeanUtils.copyProperties(busiOpsResource, busiOpsResourceVo);
                    busiOpsResourceVo.setOpsId(busiOps.getId());
                    if (StringUtils.isNotEmpty(busiOpsResource.getPurchaseType())) {
                        PurchaseType purchaseType = PurchaseType.convert(busiOpsResource.getPurchaseType());
                        if (purchaseType != null) {
                            busiOpsResourceVo.setPurchaseTypeName(purchaseType.getName());
                            busiOpsResourceVo.setPurchaseTypeAlias(purchaseType.getName());
                        }
                    }
                    if (StringUtils.isNotEmpty(busiOpsResource.getMcuType())) {
                        McuType mcuType = McuType.convert(busiOpsResource.getMcuType());
                        if (mcuType != null) {
                            busiOpsResourceVo.setMcuTypeName(mcuType.getName());
                            busiOpsResourceVo.setMcuTypeAlias(mcuType.getAlias());
                        }
                    }
                    resourceList.add(busiOpsResourceVo);
                }
                jsonObject.put("resources", resourceList);
                String token = null;
                if (busiOps.getUserId() != null) {
                    SysUser sysUser = BeanFactory.getBean(ISysUserService.class).selectUserById(busiOps.getUserId());
                    if (sysUser != null) {
                        if ((email == null && sysUser.getEmail() != null) || (email != null && !email.equals(sysUser.getEmail()))) {
                            busiOpsVo.setEmail(email);
                            needUpdate = true;
                        }
                        if ((phoneNumber == null && sysUser.getPhonenumber() != null) || (phoneNumber != null && !phoneNumber.equals(sysUser.getPhonenumber()))) {
                            busiOpsVo.setPhoneNumber(phoneNumber);
                            needUpdate = true;
                        }
                        token = loginNoCode(sysUser.getUserName(), sysUser.getPassword());
                        jsonObject.put("cloudToken", token);
                    } else {
                        busiOpsVo.setEmail(email);
                        busiOpsVo.setPhoneNumber(phoneNumber);
                        needUpdate = true;
                    }
                } else {
                    busiOpsVo.setEmail(email);
                    busiOpsVo.setPhoneNumber(phoneNumber);
                    needUpdate = true;
                }
                if (needUpdate) {
                    busiOpsVo.setRegister(true);
                    busiOpsVo.setMqttOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                    busiOpsVo.setLastOnlineTime(new Date());
                    busiOpsVo.setAutoUpdate(true);
                    busiOpsService.updateBusiOps(busiOpsVo);
                }
                if (StringUtils.isEmpty(token)) {
                    busiOps = OpsCache.getInstance().getBySn(clientId);
                    if (busiOps.getUserId() != null) {
                        SysUser sysUser = BeanFactory.getBean(ISysUserService.class).selectUserById(busiOps.getUserId());
                        token = loginNoCode(sysUser.getUserName(), sysUser.getPassword());
                        jsonObject.put("cloudToken", token);
                    }
                }
                if (StringUtils.isNotEmpty(token)) {
                    jsonObject.put("registered", true);
                } else {
                    jsonObject.put("registered", false);
                }
            } else {
                BusiOpsVo busiOpsVo = new BusiOpsVo();
                busiOpsVo.setSn(clientId);
                String name = "ops_" + System.currentTimeMillis();
                Random random = new Random();
                for (int i = 0; i < 3; i++) {
                    name += random.nextInt(9);
                }
                busiOpsVo.setName(name);
                busiOpsVo.setAppVersionCode(versionCode);
                busiOpsVo.setAppVersionName(versionName);
                busiOpsVo.setIp(ip);
                busiOpsVo.setConnectIp(connectIp);
                busiOpsVo.setCreateTime(new Date());
                busiOpsVo.setEmail(email);
                busiOpsVo.setPhoneNumber(phoneNumber);
                busiOpsVo.setRegister(true);
                busiOpsVo.setMqttOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                busiOpsVo.setLastOnlineTime(new Date());
                busiOpsService.insertBusiOps(busiOpsVo);
                busiOps = OpsCache.getInstance().getBySn(clientId);
                List<BusiOpsResourceVo> resourceList = new ArrayList<>();
                BusiOpsResourceVo busiOpsResourceCon = new BusiOpsResourceVo();
                busiOpsResourceCon.setSn(busiOps.getSn());
                List<BusiOpsResource> busiOpsResources = busiOpsResourceService.selectBusiOpsResourceList(busiOpsResourceCon);
                for (BusiOpsResource busiOpsResource : busiOpsResources) {
                    BusiOpsResourceVo busiOpsResourceVo = new BusiOpsResourceVo();
                    BeanUtils.copyProperties(busiOpsResource, busiOpsResourceVo);
                    busiOpsResourceVo.setOpsId(busiOps.getId());
                    if (StringUtils.isNotEmpty(busiOpsResource.getPurchaseType())) {
                        PurchaseType purchaseType = PurchaseType.convert(busiOpsResource.getPurchaseType());
                        if (purchaseType != null) {
                            busiOpsResourceVo.setPurchaseTypeName(purchaseType.getName());
                            busiOpsResourceVo.setPurchaseTypeAlias(purchaseType.getName());
                        }
                    }
                    if (StringUtils.isNotEmpty(busiOpsResource.getMcuType())) {
                        McuType mcuType = McuType.convert(busiOpsResource.getMcuType());
                        if (mcuType != null) {
                            busiOpsResourceVo.setMcuTypeName(mcuType.getName());
                            busiOpsResourceVo.setMcuTypeAlias(mcuType.getAlias());
                        }
                    }
                    resourceList.add(busiOpsResourceVo);
                }
                jsonObject.put("resources", resourceList);
                String token = null;
                if (busiOps.getUserId() != null) {
                    SysUser sysUser = BeanFactory.getBean(ISysUserService.class).selectUserById(busiOps.getUserId());
                    token = loginNoCode(sysUser.getUserName(), sysUser.getPassword());
                    jsonObject.put("cloudToken", token);
                }
                if (StringUtils.isNotEmpty(token)) {
                    jsonObject.put("registered", true);
                } else {
                    jsonObject.put("registered", false);
                }
            }
            ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, clientId, "");
        } else {
            ResponseTerminal.getInstance().responseTerminalFailed(terminalTopic, action, jsonObject, clientId, "");
        }

    }

    public String loginNoCode(String username, String password) {
        Authentication authentication = null;

        try {
            authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, "tty@2021"));
        } catch (Exception var9) {
            return "";
        }

//        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, "Success", MessageUtils.message("user.login.success", new Object[0]), new Object[0]));
        LoginUser loginUser = (LoginUser)authentication.getPrincipal();
        return this.tokenService.createTokenForServer(loginUser);
    }

    @Override
    public void pushRegister(String clientId) {
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX_OPS + clientId;
        String action = DeviceAction.REGISTER;
        JSONObject jsonObject = new JSONObject();
        BusiOps busiOps = OpsCache.getInstance().getBySn(clientId);
        if (busiOps != null) {
            if (busiOps.getMqttOnlineStatus() != TerminalOnlineStatus.ONLINE.getValue()) {
                return;
            }
            List<BusiOpsResourceVo> resourceList = new ArrayList<>();
            BusiOpsResourceVo busiOpsResourceCon = new BusiOpsResourceVo();
            busiOpsResourceCon.setSn(busiOps.getSn());
            List<BusiOpsResource> busiOpsResources = busiOpsResourceService.selectBusiOpsResourceList(busiOpsResourceCon);
            for (BusiOpsResource busiOpsResource : busiOpsResources) {
                BusiOpsResourceVo busiOpsResourceVo = new BusiOpsResourceVo();
                BeanUtils.copyProperties(busiOpsResource, busiOpsResourceVo);
                busiOpsResourceVo.setOpsId(busiOps.getId());
                if (StringUtils.isNotEmpty(busiOpsResource.getPurchaseType())) {
                    PurchaseType purchaseType = PurchaseType.convert(busiOpsResource.getPurchaseType());
                    if (purchaseType != null) {
                        busiOpsResourceVo.setPurchaseTypeName(purchaseType.getName());
                        busiOpsResourceVo.setPurchaseTypeAlias(purchaseType.getName());
                    }
                }
                if (StringUtils.isNotEmpty(busiOpsResource.getMcuType())) {
                    McuType mcuType = McuType.convert(busiOpsResource.getMcuType());
                    if (mcuType != null) {
                        busiOpsResourceVo.setMcuTypeName(mcuType.getName());
                        busiOpsResourceVo.setMcuTypeAlias(mcuType.getAlias());
                    }
                }
                resourceList.add(busiOpsResourceVo);
            }
            jsonObject.put("resources", resourceList);
            String token = null;
            if (busiOps.getUserId() != null) {
                SysUser sysUser = BeanFactory.getBean(ISysUserService.class).selectUserById(busiOps.getUserId());
                if (sysUser != null) {
                    token = loginNoCode(sysUser.getUserName(), sysUser.getPassword());
                    jsonObject.put("cloudToken", token);
                }
            }
            if (StringUtils.isEmpty(token)) {
                busiOps = OpsCache.getInstance().getBySn(clientId);
                if (busiOps.getUserId() != null) {
                    SysUser sysUser = BeanFactory.getBean(ISysUserService.class).selectUserById(busiOps.getUserId());
                    token = loginNoCode(sysUser.getUserName(), sysUser.getPassword());
                    jsonObject.put("cloudToken", token);
                }
            }
            if (StringUtils.isNotEmpty(token)) {
                jsonObject.put("registered", true);
            } else {
                jsonObject.put("registered", false);
            }
        }
        ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, clientId, "");
    }

}
