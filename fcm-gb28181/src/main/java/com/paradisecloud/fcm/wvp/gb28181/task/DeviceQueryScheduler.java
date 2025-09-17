package com.paradisecloud.fcm.wvp.gb28181.task;

import com.paradisecloud.fcm.common.message.terminal.TerminalOnlineStatusMessage;
import com.paradisecloud.fcm.common.message.terminal.TerminalOnlineStatusMessageQueue;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.fcm.wvp.gb28181.WvpBridge;
import com.paradisecloud.fcm.wvp.gb28181.WvpBridgeCache;
import com.paradisecloud.fcm.wvp.gb28181.reponse.WvpDevicesResponse;
import com.paradisecloud.fcm.wvp.gb28181.reponse.WvpPlayStartResponse;
import com.paradisecloud.fcm.wvp.gb28181.service.WvpDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DeviceQueryScheduler {

    @Resource
    private WvpDeviceService wvpDeviceService;

    @Resource
    private IBusiTerminalService busiTerminalService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final int PAGE_SIZE = 15;

    public void startQueryingDevices() {
        final int initialDelay = 0; // 初始延迟
        final int period = 5; // 查询周期（秒）

        scheduler.scheduleAtFixedRate(() -> {
            try {
                // 查询设备总数

                WvpDevicesResponse initialResponse = devices(1, PAGE_SIZE);
                if (initialResponse != null && initialResponse.getCode() == 0) {
                    int totalDevices = initialResponse.getData().getTotal();
                    int totalPages = (int) Math.ceil((double) totalDevices / PAGE_SIZE);

                    // 查询所有页面的设备
                    for (int pageNum = 1; pageNum <= totalPages; pageNum++) {
                        WvpDevicesResponse response = devices(pageNum, PAGE_SIZE);
                        if (response != null && response.getCode() == 0) {
                            List<WvpDevicesResponse.DataDTO.ListDTO> devices = response.getData().getList();
                            devices.forEach(device -> {
                                Boolean onLine = device.getOnLine();

                                String deviceId = device.getDeviceId();
                                WvpPlayStartResponse play = wvpDeviceService.play(deviceId);
                                String rtsp = Optional.ofNullable(play).map(WvpPlayStartResponse::getData).map(WvpPlayStartResponse.DataDTO::getRtsp).get();
                                Object ip = Optional.ofNullable(play).map(WvpPlayStartResponse::getData).map(WvpPlayStartResponse.DataDTO::getIp).get();
                                BusiTerminal busiTermina_query = new BusiTerminal();
                                busiTermina_query.setNumber(deviceId);
                                BusiTerminal busiTerminal = busiTerminalService.selectBusiTerminal(busiTermina_query);
                                if(busiTerminal!=null){
                                    busiTerminal.setOnlineStatus(onLine?1:2);
                                    if(ip!=null){
                                        busiTerminal.setIp((String)ip);
                                    }

                                    busiTerminalService.updateBusiTerminal(busiTerminal);
                                    TerminalOnlineStatusMessageQueue.getInstance().put(new TerminalOnlineStatusMessage(busiTerminal.getId(), onLine?1:2));
                                }

                                BusiTerminal busiTerminalCache = TerminalCache.getInstance().get(busiTerminal.getId());
                                if(busiTerminalCache!=null){
                                    busiTerminalCache.setOnlineStatus(onLine?1:2);
                                    if(Strings.isNotBlank(rtsp)){
                                        busiTerminal.setProtocol(rtsp);
                                        busiTerminalCache.setProtocol(rtsp);
                                    }else {
                                        busiTerminalCache.setOnlineStatus(2);
                                    }
                                }



                            });
                        } else {
                           log.info("Failed to fetch devices: " + response.getMsg());
                        }
                    }
                } else {
                    log.info("Failed to fetch total devices: " + initialResponse.getMsg());
                }
            } catch (Exception e) {
               log.error(e.getMessage());
            }
        }, initialDelay, period, TimeUnit.SECONDS);
    }

    public void stopQuerying() {
        scheduler.shutdown();
    }



    private WvpDevicesResponse devices(int page, int count) {
        WvpBridge wvpBridge = WvpBridgeCache.getInstance().get();
        return wvpBridge.getWvpControllApi().devices(page, count);
    }
}

