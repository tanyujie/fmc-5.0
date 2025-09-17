package com.paradisecloud.fcm.wvp.gb28181.core;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.wvp.gb28181.WvpBridge;
import com.paradisecloud.fcm.wvp.gb28181.reponse.WvpCommonResponse;
import com.paradisecloud.fcm.wvp.gb28181.reponse.WvpLoginResponse;
import com.paradisecloud.fcm.wvp.gb28181.request.WvpLoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class WvpKeepAliveThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(WvpKeepAliveThread.class);

    private final WvpBridge wvpBridge;

    public WvpKeepAliveThread(String name, WvpBridge wvpBridge) {
        super(name);
        this.wvpBridge = wvpBridge;
    }

    @Override
    public void run() {

        while (true) {
            try {
                if (isInterrupted()) {
                    return;
                }

                long currentTimeMillis = System.currentTimeMillis();
                if (wvpBridge.getLastUpdateTime() == 0 || StringUtils.isEmpty(wvpBridge.getAccessToken()) || currentTimeMillis - wvpBridge.getLastUpdateTime() > 30*1000) {
                    // 登录
                    WvpLoginRequest wvpLoginRequest = new WvpLoginRequest();
                    wvpLoginRequest.setUserName("admin");
                    wvpLoginRequest.setPassword("admin");
                    WvpLoginResponse wvpLoginResponse = wvpBridge.getWvpControllApi().login(wvpLoginRequest);
                    if (wvpLoginResponse != null && wvpLoginResponse.getCode() == 0) {
                        wvpBridge.setAccessToken(wvpLoginResponse.getData().getAccessToken());
                        LOGGER.info("=============WVP 登录成功,accessToken：" + wvpLoginResponse.getData().getAccessToken());
                    } else {
                        LOGGER.info("=============WVP 登录失败！");
                        wvpBridge.setAccessToken(null);
                    }

                }
                LOGGER.info("=============WVP 已经登录！");
                if (StringUtils.isNotEmpty(wvpBridge.getAccessToken())) {

                    if (StringUtils.isEmpty(wvpBridge.getAccessToken())) {
                        break;
                    }
                    currentTimeMillis = System.currentTimeMillis();
                    if (wvpBridge.getLastUpdateTime() == 0 || currentTimeMillis - wvpBridge.getLastUpdateTime() > 30*1000) {

                        WvpCommonResponse response = wvpBridge.getWvpControllApi().devices();
                        if (response != null && response.getCode() == 0) {
                            wvpBridge.setLastUpdateTime(currentTimeMillis);
                        } else {
                            LOGGER.info("=============WVP 登录失效！");
                            wvpBridge.setLastUpdateTime(0);
                        }
                    }
                }

                sleep(1000);

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

    }


}
