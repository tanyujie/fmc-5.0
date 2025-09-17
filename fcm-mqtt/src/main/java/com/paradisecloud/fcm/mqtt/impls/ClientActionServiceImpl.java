package com.paradisecloud.fcm.mqtt.impls;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.PurchaseType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.model.BusiClient;
import com.paradisecloud.fcm.dao.model.BusiClientResource;
import com.paradisecloud.fcm.dao.model.vo.BusiClientResourceVo;
import com.paradisecloud.fcm.dao.model.vo.BusiClientVo;
import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.constant.DeviceAction;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.interfaces.IClientActionService;
import com.paradisecloud.fcm.ops.cloud.asr.EncryptUtil;
import com.paradisecloud.fcm.ops.cloud.cache.ClientCache;
import com.paradisecloud.fcm.ops.cloud.interfaces.IBusiClientResourceService;
import com.paradisecloud.fcm.ops.cloud.interfaces.IBusiClientService;
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
public class ClientActionServiceImpl implements IClientActionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientActionServiceImpl.class);

    @Resource
    private TokenService tokenService;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private IBusiClientService busiClientService;
    @Resource
    private IBusiClientResourceService busiClientResourceService;

    @Override
    public void register(JSONObject jsonS, String clientId) {
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX_CLIENT + clientId;
        String action = DeviceAction.REGISTER;
        JSONObject jsonObject = new JSONObject();
        if (jsonS != null) {
            String ip = jsonS.getString(MqttConfigConstant.IP);
            String appType = jsonS.getString(MqttConfigConstant.TERMINAL_TYPE);
            String versionCode = jsonS.getString(MqttConfigConstant.versionCode);
            String versionName = jsonS.getString(MqttConfigConstant.versionName);
            String connectIp = jsonS.getString(MqttConfigConstant.CONNECT_IP);
            String licenseStatus = jsonS.getString("licenseStatus");
            String licenseDate = jsonS.getString("licenseDate");
            String email = jsonS.getString("email");
            String phoneNumber = jsonS.getString("phoneNumber");

//            AppType appTypeTemp = AppType.convertByType(appType);
            BusiClient busiClient = ClientCache.getInstance().getBySn(clientId);
            if (busiClient != null) {
                boolean needUpdate = false;
                BusiClientVo busiClientVo = new BusiClientVo();
                BeanUtils.copyProperties(busiClient, busiClientVo);
                if (!versionCode.equals(busiClient.getAppVersionCode())
                        || !versionName.equals(busiClient.getAppVersionName())
                        || !connectIp.equals(busiClient.getConnectIp())
                        || !ip.equals(busiClient.getIp())) {
                    busiClientVo.setAppVersionCode(versionCode);
                    busiClientVo.setAppVersionName(versionName);
                    busiClientVo.setConnectIp(connectIp);
                    busiClientVo.setIp(ip);
                    needUpdate = true;
                }
                List<BusiClientResourceVo> resourceList = new ArrayList<>();
                BusiClientResourceVo busiClientResourceCon = new BusiClientResourceVo();
                busiClientResourceCon.setSn(busiClient.getSn());
                List<BusiClientResource> busiClientResources = busiClientResourceService.selectBusiClientResourceList(busiClientResourceCon);
                for (BusiClientResource busiClientResource : busiClientResources) {
                    BusiClientResourceVo busiClientResourceVo = new BusiClientResourceVo();
                    BeanUtils.copyProperties(busiClientResource, busiClientResourceVo);
                    busiClientResourceVo.setClientId(busiClient.getId());
                    if (StringUtils.isNotEmpty(busiClientResource.getPurchaseType())) {
                        PurchaseType purchaseType = PurchaseType.convert(busiClientResource.getPurchaseType());
                        if (purchaseType != null) {
                            busiClientResourceVo.setPurchaseTypeName(purchaseType.getName());
                            busiClientResourceVo.setPurchaseTypeAlias(purchaseType.getName());
                        }
                    }
                    if (StringUtils.isNotEmpty(busiClientResource.getMcuType())) {
                        McuType mcuType = McuType.convert(busiClientResource.getMcuType());
                        if (mcuType != null) {
                            busiClientResourceVo.setMcuTypeName(mcuType.getName());
                            busiClientResourceVo.setMcuTypeAlias(mcuType.getAlias());
                        }
                    }
                    resourceList.add(busiClientResourceVo);
                }
                jsonObject.put("resources", resourceList);
                String token = null;
                if (busiClient.getUserId() != null) {
                    SysUser sysUser = BeanFactory.getBean(ISysUserService.class).selectUserById(busiClient.getUserId());
                    if (sysUser != null) {
                        if ((email == null && sysUser.getEmail() != null) || (email != null && !email.equals(sysUser.getEmail()))) {
                            busiClientVo.setEmail(email);
                            needUpdate = true;
                        }
                        if ((phoneNumber == null && sysUser.getPhonenumber() != null) || (phoneNumber != null && !phoneNumber.equals(sysUser.getPhonenumber()))) {
                            busiClientVo.setPhoneNumber(phoneNumber);
                            needUpdate = true;
                        }
                        token = loginNoCode(sysUser.getUserName(), sysUser.getPassword());
                        jsonObject.put("cloudToken", token);
                    } else {
                        busiClientVo.setEmail(email);
                        busiClientVo.setPhoneNumber(phoneNumber);
                        needUpdate = true;
                    }
                } else {
                    busiClientVo.setEmail(email);
                    busiClientVo.setPhoneNumber(phoneNumber);
                    needUpdate = true;
                }
                Date expiredDate = null;
                if ("1".equals(licenseStatus)) {
                    expiredDate = new Date(Long.valueOf(licenseDate));
                } else if ("2".equals(licenseStatus)) {
                    expiredDate = DateUtil.convertDateByString("9999-12-31 00:00:00", "yyyy-MM-dd HH:mm:ss");
                }
                busiClientVo.setExpiredDate(expiredDate);
                if (needUpdate) {
                    busiClientVo.setRegister(true);
                    busiClientVo.setMqttOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                    busiClientVo.setLastOnlineTime(new Date());
                    busiClientVo.setAutoUpdate(true);
                    busiClientService.updateBusiClient(busiClientVo);
                }
                if (StringUtils.isEmpty(token)) {
                    busiClient = ClientCache.getInstance().getBySn(clientId);
                    if (busiClient.getUserId() != null) {
                        SysUser sysUser = BeanFactory.getBean(ISysUserService.class).selectUserById(busiClient.getUserId());
                        token = loginNoCode(sysUser.getUserName(), sysUser.getPassword());
                        jsonObject.put("cloudToken", token);
                    }
                }
                if (StringUtils.isNotEmpty(token)) {
                    jsonObject.put("registered", true);
                } else {
                    jsonObject.put("registered", false);
                }
                busiClient = ClientCache.getInstance().getBySn(clientId);
                busiClient.getParams().put("licenseStatus", licenseStatus);
                busiClient.getParams().put("licenseDate", licenseDate);
            } else {
                BusiClientVo busiClientVo = new BusiClientVo();
                busiClientVo.setSn(clientId);
                String name = "client_" + System.currentTimeMillis();
                Random random = new Random();
                for (int i = 0; i < 3; i++) {
                    name += random.nextInt(9);
                }
                busiClientVo.setName(name);
                busiClientVo.setAppVersionCode(versionCode);
                busiClientVo.setAppVersionName(versionName);
                busiClientVo.setIp(ip);
                busiClientVo.setConnectIp(connectIp);
                busiClientVo.setCreateTime(new Date());
                busiClientVo.setEmail(email);
                busiClientVo.setPhoneNumber(phoneNumber);
                busiClientVo.setRegister(true);
                busiClientVo.setMqttOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                busiClientVo.setLastOnlineTime(new Date());
                Date expiredDate = null;
                if ("1".equals(licenseStatus)) {
                    expiredDate = new Date(Long.valueOf(licenseDate));
                } else if ("2".equals(licenseStatus)) {
                    expiredDate = DateUtil.convertDateByString("9999-12-31 00:00:00", "yyyy-MM-dd HH:mm:ss");
                }
                busiClientVo.setExpiredDate(expiredDate);
                busiClientService.insertBusiClient(busiClientVo);
                busiClient = ClientCache.getInstance().getBySn(clientId);
                List<BusiClientResourceVo> resourceList = new ArrayList<>();
                BusiClientResourceVo busiClientResourceCon = new BusiClientResourceVo();
                busiClientResourceCon.setSn(busiClient.getSn());
                List<BusiClientResource> busiClientResources = busiClientResourceService.selectBusiClientResourceList(busiClientResourceCon);
                for (BusiClientResource busiClientResource : busiClientResources) {
                    BusiClientResourceVo busiClientResourceVo = new BusiClientResourceVo();
                    BeanUtils.copyProperties(busiClientResource, busiClientResourceVo);
                    busiClientResourceVo.setClientId(busiClient.getId());
                    if (StringUtils.isNotEmpty(busiClientResource.getPurchaseType())) {
                        PurchaseType purchaseType = PurchaseType.convert(busiClientResource.getPurchaseType());
                        if (purchaseType != null) {
                            busiClientResourceVo.setPurchaseTypeName(purchaseType.getName());
                            busiClientResourceVo.setPurchaseTypeAlias(purchaseType.getName());
                        }
                    }
                    if (StringUtils.isNotEmpty(busiClientResource.getMcuType())) {
                        McuType mcuType = McuType.convert(busiClientResource.getMcuType());
                        if (mcuType != null) {
                            busiClientResourceVo.setMcuTypeName(mcuType.getName());
                            busiClientResourceVo.setMcuTypeAlias(mcuType.getAlias());
                        }
                    }
                    resourceList.add(busiClientResourceVo);
                }
                jsonObject.put("resources", resourceList);
                String token = null;
                if (busiClient.getUserId() != null) {
                    SysUser sysUser = BeanFactory.getBean(ISysUserService.class).selectUserById(busiClient.getUserId());
                    token = loginNoCode(sysUser.getUserName(), sysUser.getPassword());
                    jsonObject.put("cloudToken", token);
                }
                if (StringUtils.isNotEmpty(token)) {
                    jsonObject.put("registered", true);
                } else {
                    jsonObject.put("registered", false);
                }
                busiClient = ClientCache.getInstance().getBySn(clientId);
                busiClient.getParams().put("licenseStatus", licenseStatus);
                busiClient.getParams().put("licenseDate", licenseDate);
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
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX_CLIENT + clientId;
        String action = DeviceAction.REGISTER;
        JSONObject jsonObject = new JSONObject();
        BusiClient busiClient = ClientCache.getInstance().getBySn(clientId);
        if (busiClient != null) {
            if (busiClient.getMqttOnlineStatus() != TerminalOnlineStatus.ONLINE.getValue()) {
                return;
            }
            List<BusiClientResourceVo> resourceList = new ArrayList<>();
            BusiClientResourceVo busiClientResourceCon = new BusiClientResourceVo();
            busiClientResourceCon.setSn(busiClient.getSn());
            List<BusiClientResource> busiClientResources = busiClientResourceService.selectBusiClientResourceList(busiClientResourceCon);
            for (BusiClientResource busiClientResource : busiClientResources) {
                BusiClientResourceVo busiClientResourceVo = new BusiClientResourceVo();
                BeanUtils.copyProperties(busiClientResource, busiClientResourceVo);
                busiClientResourceVo.setClientId(busiClient.getId());
                if (StringUtils.isNotEmpty(busiClientResource.getPurchaseType())) {
                    PurchaseType purchaseType = PurchaseType.convert(busiClientResource.getPurchaseType());
                    if (purchaseType != null) {
                        busiClientResourceVo.setPurchaseTypeName(purchaseType.getName());
                        busiClientResourceVo.setPurchaseTypeAlias(purchaseType.getName());
                    }
                }
                if (StringUtils.isNotEmpty(busiClientResource.getMcuType())) {
                    McuType mcuType = McuType.convert(busiClientResource.getMcuType());
                    if (mcuType != null) {
                        busiClientResourceVo.setMcuTypeName(mcuType.getName());
                        busiClientResourceVo.setMcuTypeAlias(mcuType.getAlias());
                    }
                }
                resourceList.add(busiClientResourceVo);
            }
            jsonObject.put("resources", resourceList);
            String token = null;
            if (busiClient.getUserId() != null) {
                SysUser sysUser = BeanFactory.getBean(ISysUserService.class).selectUserById(busiClient.getUserId());
                if (sysUser != null) {
                    token = loginNoCode(sysUser.getUserName(), sysUser.getPassword());
                    jsonObject.put("cloudToken", token);
                }
            }
            if (StringUtils.isEmpty(token)) {
                busiClient = ClientCache.getInstance().getBySn(clientId);
                if (busiClient.getUserId() != null) {
                    SysUser sysUser = BeanFactory.getBean(ISysUserService.class).selectUserById(busiClient.getUserId());
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

    /**
     * 推送客户端字幕信息
     *
     * @param clientId
     */
    @Override
    public void asrSign(String clientId) {
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX_CLIENT + clientId;
        String action = DeviceAction.ASR_SIGN;
        JSONObject jsonObject = new JSONObject();
        String appId = ExternalConfigCache.getInstance().getAsrAppId();
        String keySecret = ExternalConfigCache.getInstance().getAsrKeySecret();
        String ts = System.currentTimeMillis() / 1000 + "";
        String asrSign = "";
        if (StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(keySecret)) {
            try {
                asrSign = EncryptUtil.HmacSHA1Encrypt(EncryptUtil.MD5(appId + ts), keySecret);
            } catch (Exception e) {
            }
        }
        jsonObject.put("appId", appId);
        jsonObject.put("ts", ts);
        jsonObject.put("asrSign", asrSign);
        ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, clientId, "");
    }

    /**
     * 更新客户端字license
     *
     * @param clientId
     * @param license
     */
    @Override
    public void updateLicense(String clientId, String license) {
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX_CLIENT + clientId;
        String action = DeviceAction.UPDATE_LICENSE;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("license", license);
        ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, clientId, "");
    }

}
