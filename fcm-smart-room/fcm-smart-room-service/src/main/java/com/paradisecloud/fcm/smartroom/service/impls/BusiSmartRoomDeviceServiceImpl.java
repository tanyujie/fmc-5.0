package com.paradisecloud.fcm.smartroom.service.impls;

import java.util.*;

import com.paradisecloud.fcm.common.enumer.DeviceType;
import com.paradisecloud.fcm.common.enumer.LotDeviceType;
import com.paradisecloud.fcm.common.enumer.PowerSequencerModel;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.utils.Converter;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDeviceMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDeviceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomLotMapper;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDevice;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDeviceMap;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomLot;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomDeviceVo;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDeviceCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomLotCache;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeviceService;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 会议室设备Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-02-02
 */
@Service
public class BusiSmartRoomDeviceServiceImpl implements IBusiSmartRoomDeviceService
{
    @Resource
    private BusiSmartRoomDeviceMapper busiSmartRoomDeviceMapper;
    @Resource
    private BusiSmartRoomDeviceMapMapper busiSmartRoomDeviceMapMapper;
    @Resource
    private BusiSmartRoomLotMapper busiSmartRoomLotMapper;
    @Resource
    private IMqttService mqttService;

    /**
     * 查询会议室设备
     * 
     * @param id 会议室设备ID
     * @return 会议室设备
     */
    @Override
    public BusiSmartRoomDevice selectBusiSmartRoomDeviceById(Long id)
    {
        return busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceById(id);
    }

    /**
     * 查询会议室设备列表
     * 
     * @param busiSmartRoomDevice 会议室设备
     * @return 会议室设备
     */
    @Override
    public List<BusiSmartRoomDevice> selectBusiSmartRoomDeviceList(BusiSmartRoomDeviceVo busiSmartRoomDevice)
    {
        String searchKey = busiSmartRoomDevice.getSearchKey();
        busiSmartRoomDevice.setDeviceName(searchKey);
        return busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceList(busiSmartRoomDevice);
    }

    /**
     * 新增会议室设备
     * 
     * @param busiSmartRoomDevice 会议室设备
     * @return 结果
     */
    @Override
    public int insertBusiSmartRoomDevice(BusiSmartRoomDevice busiSmartRoomDevice)
    {
        Assert.notNull(busiSmartRoomDevice.getDeviceName(), "设备名不能为空！");
        Assert.notNull(busiSmartRoomDevice.getDeviceType(), "设备类型不能为空！");
        if (busiSmartRoomDevice.getDeviceType() == DeviceType.LOT_DEVICE.getCode()) {
            busiSmartRoomDevice.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
            Assert.notNull(busiSmartRoomDevice.getLotId(), "物联网设备需要绑定物联网关！");
            Assert.notNull(busiSmartRoomDevice.getLotChannel(), "物联网设备需要填写使用的物联网关通道！");
            Assert.notNull(busiSmartRoomDevice.getLotDeviceType(), "物联网设备需要填写物联网设备类型！");
            Assert.notNull(busiSmartRoomDevice.getDeviceModel(), "物联网设备需要填写物联网设备型号！");
            BusiSmartRoomLot busiSmartRoomLot = busiSmartRoomLotMapper.selectBusiSmartRoomLotById(busiSmartRoomDevice.getLotId());
            Assert.notNull(busiSmartRoomLot, "需要绑定物联网关不存在！");
            BusiSmartRoomDevice busiSmartRoomDeviceTemp = new BusiSmartRoomDevice();
            busiSmartRoomDeviceTemp.setLotId(busiSmartRoomDevice.getLotId());
            busiSmartRoomDeviceTemp.setLotChannel(busiSmartRoomDevice.getLotChannel());
            List<BusiSmartRoomDevice> busiSmartRoomDeviceList = busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceList(busiSmartRoomDeviceTemp);
            if (busiSmartRoomDeviceList != null && busiSmartRoomDeviceList.size() > 0) {
                Assert.isTrue(false, "物联网设备填写的物联网关通道已被使用！");
            }
            if (LotDeviceType.POWER_SEQUENCER.getCode() == busiSmartRoomDevice.getLotDeviceType()) {
                PowerSequencerModel powerSequencerModel = PowerSequencerModel.convert(busiSmartRoomDevice.getDeviceModel());
                if (powerSequencerModel != null) {
                    List<Map<String, Object>> channelList = new ArrayList<>();
                    Map<Integer, Map<String, Object>> channelMap = new HashMap<>();
                    Map<String, Object> detailsIn = busiSmartRoomDevice.getDetails();
                    if (detailsIn != null) {
                        Object channelListObj = detailsIn.get("channelList");
                        if (channelListObj != null) {
                            List<Map<String, Object>> channelListTemp = (List<Map<String, Object>>) channelListObj;
                            if (channelListTemp.size() > 0) {
                                for (Map<String, Object> map : channelListTemp) {
                                    Object channelObj = map.get("channel");
                                    if (channelObj != null) {
                                        Integer channel = (Integer) channelObj;
                                        if (channel <= powerSequencerModel.getChannels()) {
                                            channelMap.put(channel, map);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // 新增时，有无channelList都需要补全details
                    for (int channel = 1; channel <= powerSequencerModel.getChannels(); channel++) {
                        Map<String, Object> map = channelMap.get(channel);
                        if (map == null) {
                            map = new HashMap<>();
                            map.put("channel", channel);
                        }
                        String name = "";
                        Object nameObj = map.get("name");
                        if (nameObj != null) {
                            name = (String) nameObj;
                        }
                        if (StringUtils.isEmpty(name)) {
                            map.put("name", "通道" + channel);
                        }
                        map.put("powerStatus", 0);
                        channelList.add(map);
                    }
                    Map<String, Object> details = new HashMap<>();
                    details.put("type", powerSequencerModel.getType());
                    details.put("channels", powerSequencerModel.getChannels());
                    details.put("brandAlias", powerSequencerModel.getBrandAlias());
                    details.put("pubTopic", busiSmartRoomLot.getPubTopic() + "/" + busiSmartRoomDevice.getLotChannel() + "/pub");
                    details.put("subTopic", busiSmartRoomLot.getSubTopic() + "/" + busiSmartRoomDevice.getLotChannel() + "/sub");
                    details.put("channelList", channelList);
                    busiSmartRoomDevice.setDetails(details);
                }
            }
        }
        busiSmartRoomDevice.setCreateTime(new Date());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiSmartRoomDevice.setCreateBy(loginUser.getUsername());
        }
        int i = busiSmartRoomDeviceMapper.insertBusiSmartRoomDevice(busiSmartRoomDevice);
        if (i > 0) {
            SmartRoomDeviceCache.getInstance().add(busiSmartRoomDevice);
        }
        return i;
    }

    /**
     * 修改会议室设备
     * 
     * @param busiSmartRoomDevice 会议室设备
     * @return 结果
     */
    @Override
    public int updateBusiSmartRoomDevice(BusiSmartRoomDevice busiSmartRoomDevice)
    {
        Assert.notNull(busiSmartRoomDevice.getDeviceName(), "设备名不能为空！");
        BusiSmartRoomDevice busiSmartRoomDeviceExist = busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceById(busiSmartRoomDevice.getId());
        Assert.notNull(busiSmartRoomDeviceExist, "该设备不存在！");
        BusiSmartRoomDevice busiSmartRoomDeviceUpdate = new BusiSmartRoomDevice();
        busiSmartRoomDeviceUpdate.setId(busiSmartRoomDevice.getId());
        busiSmartRoomDeviceUpdate.setUpdateTime(new Date());
        busiSmartRoomDeviceUpdate.setRemark(busiSmartRoomDevice.getRemark());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiSmartRoomDeviceUpdate.setCreateBy(loginUser.getUsername());
        }
        busiSmartRoomDeviceUpdate.setDeviceName(busiSmartRoomDevice.getDeviceName());
        busiSmartRoomDeviceUpdate.setHardwareVersion(busiSmartRoomDevice.getHardwareVersion());
        busiSmartRoomDeviceUpdate.setSoftwareVersion(busiSmartRoomDevice.getSoftwareVersion());
        busiSmartRoomDeviceUpdate.setDetails(busiSmartRoomDeviceExist.getDetails());
        if (busiSmartRoomDeviceExist.getDeviceType() == DeviceType.LOT_DEVICE.getCode()) {
            BusiSmartRoomLot busiSmartRoomLot = busiSmartRoomLotMapper.selectBusiSmartRoomLotById(busiSmartRoomDeviceExist.getLotId());
            Assert.notNull(busiSmartRoomLot, "需要绑定物联网关不存在！");
            BusiSmartRoomDevice busiSmartRoomDeviceTemp = new BusiSmartRoomDevice();
            busiSmartRoomDeviceTemp.setLotId(busiSmartRoomDevice.getLotId());
            busiSmartRoomDeviceTemp.setLotChannel(busiSmartRoomDevice.getLotChannel());
            List<BusiSmartRoomDevice> busiSmartRoomDeviceList = busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceList(busiSmartRoomDeviceTemp);
            if (busiSmartRoomDeviceList != null && busiSmartRoomDeviceList.size() > 0) {
                BusiSmartRoomDevice busiSmartRoomDeviceT = busiSmartRoomDeviceList.get(0);
                if (!busiSmartRoomDeviceT.getId().equals(busiSmartRoomDevice.getId())) {
                    Assert.isTrue(false, "物联网设备填写的物联网关通道已被使用！");
                }
            }
            if (LotDeviceType.POWER_SEQUENCER.getCode() == busiSmartRoomDevice.getLotDeviceType()) {
                PowerSequencerModel powerSequencerModel = PowerSequencerModel.convert(busiSmartRoomDeviceExist.getDeviceModel());
                if (powerSequencerModel != null) {
                    List<Map<String, Object>> channelList = new ArrayList<>();
                    Map<Integer, Map<String, Object>> channelMap = new HashMap<>();
                    Map<Integer, Map<String, Object>> channelExistMap = new HashMap<>();
                    Map<String, Object> detailsIn = busiSmartRoomDevice.getDetails();
                    if (detailsIn != null) {
                        Object channelListObj = detailsIn.get("channelList");
                        if (channelListObj != null) {
                            List<Map<String, Object>> channelListTemp = (List<Map<String, Object>>) channelListObj;
                            // 更新时，没有channelList则不更新details
                            if (channelListTemp.size() > 0) {
                                // 寄存数据
                                Map<String, Object> detailsExist = busiSmartRoomDeviceExist.getDetails();
                                if (detailsExist != null) {
                                    Object channelListExistObj = detailsExist.get("channelList");
                                    if (channelListExistObj != null) {
                                        List<Map<String, Object>> channelListExist = (List<Map<String, Object>>) channelListExistObj;
                                        if (channelListExist.size() > 0) {
                                            for (Map<String, Object> map : channelListExist) {
                                                Object channelObj = map.get("channel");
                                                if (channelObj != null) {
                                                    Integer channel = (Integer) channelObj;
                                                    if (channel <= powerSequencerModel.getChannels()) {
                                                        channelExistMap.put(channel, map);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                for (Map<String, Object> map : channelListTemp) {
                                    Object channelObj = map.get("channel");
                                    if (channelObj != null) {
                                        Integer channel = (Integer) channelObj;
                                        if (channel <= powerSequencerModel.getChannels()) {
                                            channelMap.put(channel, map);
                                        }
                                    }
                                }
                                for (int channel = 1; channel <= powerSequencerModel.getChannels(); channel++) {
                                    Map<String, Object> map = channelMap.get(channel);
                                    if (map == null) {
                                        map = new HashMap<>();
                                        map.put("channel", channel);
                                    }
                                    String name = "";
                                    int powerStatus = 0;
                                    Object nameObj = map.get("name");
                                    if (nameObj != null) {
                                        name = (String) nameObj;
                                    }
                                    Map<String, Object> existMap = channelExistMap.get(channel);
                                    if (existMap != null) {
                                        Object powerStatusObj = existMap.get("powerStatus");
                                        if (powerStatusObj != null) {
                                            powerStatus = (int) powerStatusObj;
                                        }
                                    }
                                    if (StringUtils.isEmpty(name)) {
                                        map.put("name", "通道" + channel);
                                    }
                                    map.put("powerStatus", powerStatus);
                                    channelList.add(map);
                                }
                                Map<String, Object> details = new HashMap<>();
                                details.put("type", powerSequencerModel.getType());
                                details.put("channels", powerSequencerModel.getChannels());
                                details.put("brandAlias", powerSequencerModel.getBrandAlias());
                                details.put("pubTopic", busiSmartRoomLot.getPubTopic() + "/" + busiSmartRoomDevice.getLotChannel() + "/pub");
                                details.put("subTopic", busiSmartRoomLot.getSubTopic() + "/" + busiSmartRoomDevice.getLotChannel() + "/sub");
                                details.put("channelList", channelList);
                                busiSmartRoomDeviceUpdate.setDetails(details);
                            }
                        }
                    }
                }
            }
        } else {
            busiSmartRoomDeviceUpdate.setBrand(busiSmartRoomDevice.getBrand());
            busiSmartRoomDeviceUpdate.setDeviceModel(busiSmartRoomDevice.getDeviceModel());
            busiSmartRoomDeviceUpdate.setDeviceClassify(busiSmartRoomDevice.getDeviceClassify());
        }
        int i = busiSmartRoomDeviceMapper.updateBusiSmartRoomDevice(busiSmartRoomDeviceUpdate);
        if (i > 0) {
            BusiSmartRoomDevice busiSmartRoomDeviceTemp = busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceById(busiSmartRoomDeviceUpdate.getId());
            if (busiSmartRoomDeviceTemp != null && busiSmartRoomDeviceTemp.getId() != null) {
                SmartRoomDeviceCache.getInstance().add(busiSmartRoomDeviceTemp);
            }
        }
        return i;
    }

    /**
     * 批量删除会议室设备
     * 
     * @param ids 需要删除的会议室设备ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomDeviceByIds(Long[] ids)
    {
        int row = 0;
        for (Long id : ids) {
            int i = deleteBusiSmartRoomDeviceById(id);
            row += i;
        }
        return row;
    }

    /**
     * 删除会议室设备信息
     * 
     * @param id 会议室设备ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomDeviceById(Long id)
    {
        BusiSmartRoomDevice busiSmartRoomDeviceExist = busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceById(id);
        int i =  busiSmartRoomDeviceMapper.deleteBusiSmartRoomDeviceById(id);
        if (i > 0) {
            BusiSmartRoomDeviceMap busiSmartRoomDeviceMapCon = new BusiSmartRoomDeviceMap();
            busiSmartRoomDeviceMapCon.setDeviceId(id);
            busiSmartRoomDeviceMapCon.setDeviceType(busiSmartRoomDeviceExist.getDeviceType());
            List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList = busiSmartRoomDeviceMapMapper.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMapCon);
            for (BusiSmartRoomDeviceMap busiSmartRoomDeviceMap : busiSmartRoomDeviceMapList) {
                busiSmartRoomDeviceMapMapper.deleteBusiSmartRoomDeviceMapById(busiSmartRoomDeviceMap.getId());
            }
            SmartRoomDeviceCache.getInstance().remove(id);
        }
        return i;
    }

    /**
     * 查询未绑定的设备列表
     *
     * @return
     */
    @Override
    public List<BusiSmartRoomDevice> selectBusiSmartRoomDeviceListForUnbind(BusiSmartRoomDevice busiSmartRoomDevice) {
        return busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceListForUnbind(busiSmartRoomDevice);
    }

    /**
     * 查询未绑定的物联网设备列表
     *
     * @return
     */
    @Override
    public List<BusiSmartRoomDevice> selectBusiSmartRoomDeviceListForUnbindLotDevice() {
        return busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceListForUnbindLotDevice();
    }

    /**
     * 打开设备某通道电源
     *
     * @param id
     * @param channel
     */
    @Override
    public boolean powerOnChannel(Long id, int channel) {
        BusiSmartRoomDevice busiSmartRoomDevice = busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceById(id);
        Assert.notNull(busiSmartRoomDevice, "设备不存在！");
        Assert.isTrue(busiSmartRoomDevice.getOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue(), "设备未正确连接无法操作！");
        BusiSmartRoomLot busiSmartRoomLot = SmartRoomLotCache.getInstance().get(busiSmartRoomDevice.getLotId());
        Assert.notNull(busiSmartRoomDevice, "网关不存在！");
        if (busiSmartRoomDevice.getDeviceType() == DeviceType.LOT_DEVICE.getCode()) {
            if (busiSmartRoomDevice.getLotDeviceType() == LotDeviceType.POWER_SEQUENCER.getCode()) {
                PowerSequencerModel powerSequencerModel = PowerSequencerModel.convert(busiSmartRoomDevice.getDeviceModel());
                if (powerSequencerModel != null) {
                    String powerOnCmd = powerSequencerModel.getPowerOnCmd(channel - 1);
                    if (StringUtils.isNotEmpty(powerOnCmd)) {
                        byte[] bytesMsg = Converter.hexToByteArray(powerOnCmd);
                        String cmdTopic = powerSequencerModel.getCmdTopic(busiSmartRoomLot.getSubTopic(), busiSmartRoomDevice.getLotChannel());
                        mqttService.pushMsg(cmdTopic, bytesMsg);

                        // 寄存数据
                        Map<String, Object> detailsExist = busiSmartRoomDevice.getDetails();
                        if (detailsExist != null) {
                            Object channelListExistObj = detailsExist.get("channelList");
                            if (channelListExistObj != null) {
                                List<Map<String, Object>> channelListExist = (List<Map<String, Object>>) channelListExistObj;
                                if (channelListExist.size() > 0) {
                                    for (Map<String, Object> map : channelListExist) {
                                        Object channelObj = map.get("channel");
                                        if (channelObj != null) {
                                            Integer channelExist = (Integer) channelObj;
                                            if (channel == channelExist) {
                                                map.put("powerStatus", 1);
                                                BusiSmartRoomDevice busiSmartRoomDeviceUpdate = new BusiSmartRoomDevice();
                                                busiSmartRoomDeviceUpdate.setId(busiSmartRoomDevice.getId());
                                                busiSmartRoomDeviceUpdate.setDetails(detailsExist);
                                                busiSmartRoomDeviceMapper.updateBusiSmartRoomDevice(busiSmartRoomDeviceUpdate);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 关闭设备某通道电源
     *
     * @param id
     * @param channel
     */
    @Override
    public boolean powerOffChannel(Long id, int channel) {
        BusiSmartRoomDevice busiSmartRoomDevice = busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceById(id);
        Assert.notNull(busiSmartRoomDevice, "设备不存在！");
        Assert.isTrue(busiSmartRoomDevice.getOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue(), "设备未正确连接无法操作！");
        BusiSmartRoomLot busiSmartRoomLot = SmartRoomLotCache.getInstance().get(busiSmartRoomDevice.getLotId());
        Assert.notNull(busiSmartRoomDevice, "网关不存在！");
        if (busiSmartRoomDevice.getDeviceType() == DeviceType.LOT_DEVICE.getCode()) {
            if (busiSmartRoomDevice.getLotDeviceType() == LotDeviceType.POWER_SEQUENCER.getCode()) {
                PowerSequencerModel powerSequencerModel = PowerSequencerModel.convert(busiSmartRoomDevice.getDeviceModel());
                if (powerSequencerModel != null) {
                    String powerOffCmd = powerSequencerModel.getPowerOffCmd(channel - 1);
                    if (StringUtils.isNotEmpty(powerOffCmd)) {
                        byte[] bytesMsg = Converter.hexToByteArray(powerOffCmd);
                        String cmdTopic = powerSequencerModel.getCmdTopic(busiSmartRoomLot.getSubTopic(), busiSmartRoomDevice.getLotChannel());
                        mqttService.pushMsg(cmdTopic, bytesMsg);

                        // 寄存数据
                        Map<String, Object> detailsExist = busiSmartRoomDevice.getDetails();
                        if (detailsExist != null) {
                            Object channelListExistObj = detailsExist.get("channelList");
                            if (channelListExistObj != null) {
                                List<Map<String, Object>> channelListExist = (List<Map<String, Object>>) channelListExistObj;
                                if (channelListExist.size() > 0) {
                                    for (Map<String, Object> map : channelListExist) {
                                        Object channelObj = map.get("channel");
                                        if (channelObj != null) {
                                            Integer channelExist = (Integer) channelObj;
                                            if (channel == channelExist) {
                                                map.put("powerStatus", 0);
                                                BusiSmartRoomDevice busiSmartRoomDeviceUpdate = new BusiSmartRoomDevice();
                                                busiSmartRoomDeviceUpdate.setId(busiSmartRoomDevice.getId());
                                                busiSmartRoomDeviceUpdate.setDetails(detailsExist);
                                                busiSmartRoomDeviceMapper.updateBusiSmartRoomDevice(busiSmartRoomDeviceUpdate);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
