package com.paradisecloud.fcm.mcu.zj.task;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuZjTemplateConference;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmDeleteRoomRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmSearchRoomsRequest;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmDeleteRoomResponse;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmSearchRoomsResponse;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class McuZjDeleteRoomTask extends DelayTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(McuZjDeleteRoomTask.class);

    private McuZjBridge mcuZjBridge;
    private String conferenceNumber;

    public McuZjDeleteRoomTask(String id, long delayInMilliseconds, McuZjBridge mcuZjBridge, String conferenceNumber) {
        super("delete_r_" + id, delayInMilliseconds);
        this.mcuZjBridge = mcuZjBridge;
        this.conferenceNumber = conferenceNumber;
    }

    @Override
    public void run() {
        LOGGER.info("MCU_ZJ终端注册开始。ID:" + getId());

        // 未完待续 改为一次抽取多个
        if (mcuZjBridge != null) {
            if (StringUtils.isNotEmpty(conferenceNumber)) {
                // 删除会议室
                try {
                    CmSearchRoomsRequest cmSearchRoomsRequest = new CmSearchRoomsRequest();
                    String[] filterType = new String[1];
                    filterType[0] = "room_mark";
                    Object[] filterValue = new Object[1];
                    filterValue[0] = conferenceNumber;
                    cmSearchRoomsRequest.setFilter_type(filterType);
                    cmSearchRoomsRequest.setFilter_value(filterValue);
                    CmSearchRoomsResponse cmSearchRoomsResponse = mcuZjBridge.getConferenceManageApi().searchRooms(cmSearchRoomsRequest);
                    if (cmSearchRoomsResponse != null && cmSearchRoomsResponse.getRoom_ids() != null && cmSearchRoomsResponse.getRoom_ids().length > 0) {
                        CmDeleteRoomRequest cmDeleteRoomRequest = new CmDeleteRoomRequest();
                        cmDeleteRoomRequest.setRoom_ids(cmSearchRoomsResponse.getRoom_ids());
                        CmDeleteRoomResponse cmDeleteRoomResponse = mcuZjBridge.getConferenceManageApi().deleteRoom(cmDeleteRoomRequest);
                        if (cmDeleteRoomResponse != null && (cmDeleteRoomResponse.getFail_ids() == null || cmDeleteRoomResponse.getFail_ids().size() == 0)) {
                            BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZjTemplateConferenceMapper.class);
                            BusiMcuZjTemplateConference busiMcuZjTemplateConferenceCon = new BusiMcuZjTemplateConference();
                            busiMcuZjTemplateConferenceCon.setConferenceNumber(Long.valueOf(conferenceNumber));
                            List<BusiMcuZjTemplateConference> busiMcuZjTemplateConferenceList = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceList(busiMcuZjTemplateConferenceCon);
                            if (busiMcuZjTemplateConferenceList != null) {
                                for (BusiMcuZjTemplateConference busiMcuZjTemplateConference : busiMcuZjTemplateConferenceList) {
                                    busiMcuZjTemplateConference.setConferenceCtrlPassword(null);
                                    busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiMcuZjTemplateConference);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }
}
