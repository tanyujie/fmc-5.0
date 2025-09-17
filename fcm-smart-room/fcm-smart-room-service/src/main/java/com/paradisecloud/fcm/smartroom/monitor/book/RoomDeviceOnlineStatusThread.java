package com.paradisecloud.fcm.smartroom.monitor.book;

import com.paradisecloud.fcm.common.enumer.LotDeviceType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDeviceMapper;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDevice;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDeviceCache;
import com.sinhy.spring.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author admin
 */
@Component
public class RoomDeviceOnlineStatusThread extends Thread implements InitializingBean {

    private BusiSmartRoomDeviceMapper busiSmartRoomDeviceMapper;

    @Override
    public void run() {
        try {
            sleep(60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            if (isInterrupted()) {
                return;
            }

            if (busiSmartRoomDeviceMapper == null) {
                busiSmartRoomDeviceMapper = BeanFactory.getBean(BusiSmartRoomDeviceMapper.class);
            }

            try {
                Collection<BusiSmartRoomDevice> values = SmartRoomDeviceCache.getInstance().values();
                for (BusiSmartRoomDevice busiSmartRoomDevice : values) {
                    if (busiSmartRoomDevice != null && busiSmartRoomDevice.getLotId() != null && busiSmartRoomDevice.getLotChannel() != null && busiSmartRoomDevice.getLotDeviceType() == LotDeviceType.POWER_SEQUENCER.getCode()) {
                        process(busiSmartRoomDevice);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void process(BusiSmartRoomDevice busiSmartRoomDevice) {
        try {
            Long lotDeviceOnlineTime = SmartRoomDeviceCache.getInstance().getLotDeviceOnlineTime(busiSmartRoomDevice.getLotId(), busiSmartRoomDevice.getLotChannel());
            long currentTimeMillis = System.currentTimeMillis();
            Integer onlineStatus = busiSmartRoomDevice.getOnlineStatus();
            if (lotDeviceOnlineTime != null) {
                if (currentTimeMillis - lotDeviceOnlineTime <= 30 * 1000) {
                    if (onlineStatus == null) {
                        busiSmartRoomDevice.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                        SmartRoomDeviceCache.getInstance().add(busiSmartRoomDevice);
                        busiSmartRoomDeviceMapper.updateBusiSmartRoomDevice(busiSmartRoomDevice);
                    } else if (onlineStatus == TerminalOnlineStatus.OFFLINE.getValue()) {
                        busiSmartRoomDevice.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                        SmartRoomDeviceCache.getInstance().add(busiSmartRoomDevice);
                        busiSmartRoomDeviceMapper.updateBusiSmartRoomDevice(busiSmartRoomDevice);
                    }
                } else {
                    if (onlineStatus == null) {
                        busiSmartRoomDevice.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                        SmartRoomDeviceCache.getInstance().add(busiSmartRoomDevice);
                        busiSmartRoomDeviceMapper.updateBusiSmartRoomDevice(busiSmartRoomDevice);
                    } else if (onlineStatus == TerminalOnlineStatus.ONLINE.getValue()) {
                        busiSmartRoomDevice.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                        SmartRoomDeviceCache.getInstance().add(busiSmartRoomDevice);
                        busiSmartRoomDeviceMapper.updateBusiSmartRoomDevice(busiSmartRoomDevice);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
