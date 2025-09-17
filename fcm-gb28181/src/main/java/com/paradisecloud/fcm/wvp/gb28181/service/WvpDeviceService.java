package com.paradisecloud.fcm.wvp.gb28181.service;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.wvp.gb28181.WvpBridge;
import com.paradisecloud.fcm.wvp.gb28181.WvpBridgeCache;
import com.paradisecloud.fcm.wvp.gb28181.reponse.WvpChannelsResponse;
import com.paradisecloud.fcm.wvp.gb28181.reponse.WvpCommonResponse;
import com.paradisecloud.fcm.wvp.gb28181.reponse.WvpConfigInfoResponse;
import com.paradisecloud.fcm.wvp.gb28181.reponse.WvpPlayStartResponse;
import com.paradisecloud.fcm.wvp.gb28181.request.WvpAddDeviceRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
public class WvpDeviceService {

    public String addDevice(WvpAddDeviceRequest wvpAddDeviceRequest) {

        WvpBridge wvpBridge = WvpBridgeCache.getInstance().get();

        WvpCommonResponse wvpCommonResponse = wvpBridge.getWvpControllApi().addDevice(wvpAddDeviceRequest);
        if (wvpCommonResponse != null && wvpCommonResponse.getCode() == 0) {
            return wvpAddDeviceRequest.getDeviceId();
        } else {
            throw new CustomException("添加设备失败!");
        }

    }

    //查询通道
    public WvpChannelsResponse queryChannels(String deviceId) {
        WvpBridge wvpBridge = WvpBridgeCache.getInstance().get();
        WvpChannelsResponse wvpChannelsResponse = wvpBridge.getWvpControllApi().queryChannels(deviceId);
        return wvpChannelsResponse;

    }

    //开始播放
    public WvpPlayStartResponse play(String deviceId) {
        WvpBridge wvpBridge = WvpBridgeCache.getInstance().get();
        wvpBridge.getWvpControllApi().syncDevice(deviceId);

        WvpChannelsResponse wvpChannelsResponse = wvpBridge.getWvpControllApi().queryChannels(deviceId);
        if (wvpChannelsResponse != null && wvpChannelsResponse.getCode() == 0) {
            List<WvpChannelsResponse.DataDTO.ListDTO> list = wvpChannelsResponse.getData().getList();
            if(!CollectionUtils.isEmpty(list)){
                WvpPlayStartResponse wvpPlayStartResponse = wvpBridge.getWvpControllApi().playStart(deviceId,list.get(0).getChannelId());
                return wvpPlayStartResponse;
            }
        }

        return null;

    }

    public void updateDevice(WvpAddDeviceRequest wvpAddDeviceRequest) {
        WvpBridge wvpBridge = WvpBridgeCache.getInstance().get();
        WvpCommonResponse wvpCommonResponse = wvpBridge.getWvpControllApi().updateDevice(wvpAddDeviceRequest);
        if (wvpCommonResponse == null ||wvpCommonResponse.getCode() != 0) {
            throw new CustomException("更新设备失败!");
        }

    }

    public WvpConfigInfoResponse configInfo( ) {
        WvpBridge wvpBridge = WvpBridgeCache.getInstance().get();
        WvpConfigInfoResponse wvpConfigInfoResponse = wvpBridge.getWvpControllApi().configInfo();
        if (wvpConfigInfoResponse == null ||wvpConfigInfoResponse.getCode() != 0) {
            return null;
        }
        return wvpConfigInfoResponse;
    }
}
