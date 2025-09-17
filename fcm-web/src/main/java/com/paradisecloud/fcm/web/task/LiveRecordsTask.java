package com.paradisecloud.fcm.web.task;

import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mqtt.interfaces.ITerminalActionService;
import com.paradisecloud.fcm.web.cache.LiveBroadcastCache;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 */
public class LiveRecordsTask extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiveRecordsTask.class);

    private Long id;

    public LiveRecordsTask(String idStr, long delayInMilliseconds, Long id) {
        super(idStr, delayInMilliseconds);
        this.id = id;
    }


    @Override
    public void run() {
        LOGGER.info("直播回放。ID:" + getId());
        BusiLiveBroadcastMapper busiLiveBroadcastMapper = BeanFactory.getBean(BusiLiveBroadcastMapper.class);
        BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
        BusiRecordsMapper busiRecordsMapper = BeanFactory.getBean(BusiRecordsMapper.class);
        BusiRecordSettingMapper busiRecordSettingMapper = BeanFactory.getBean(BusiRecordSettingMapper.class);
        BusiLiveRecordsMapper busiLiveRecordsMapper = BeanFactory.getBean(BusiLiveRecordsMapper.class);

        BusiLiveBroadcast liveBroadcast = LiveBroadcastCache.getInstance().get(id);
        if (liveBroadcast != null) {
            Integer type = liveBroadcast.getType();
            // 会议直播
            if (type == 1) {
                Integer historyConferenceId = liveBroadcast.getHistoryConferenceId();
                if (historyConferenceId != null) {
                    BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(Long.valueOf(historyConferenceId));
                    if (busiHistoryConference != null) {
                        String coSpace = busiHistoryConference.getCoSpace();
                        BusiRecords busiRecords = new BusiRecords();
                        busiRecords.setCoSpaceId(coSpace);
                        List<BusiRecords> busiRecordsTemp = busiRecordsMapper.selectBusiRecordsList(busiRecords);
                        if (busiRecordsTemp != null && busiRecordsTemp.size() > 0) {
                            for (BusiRecords records : busiRecordsTemp) {
                                if (records != null) {

                                    BusiRecordSetting recordSetting = new BusiRecordSetting();
                                    recordSetting.setStatus(YesOrNo.YES.getValue());
                                    recordSetting.setDeptId(records.getDeptId());
                                    List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(recordSetting);
                                    if (busiRecordSettings == null || busiRecordSettings.isEmpty()) {
                                        continue;
                                    }

                                    BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
                                    busiTemplateConference.setConferenceNumber(records.getConferenceNumber());
                                    String comUrl = busiRecordSettings.size() > 0 ? busiRecordSettings.get(0).getUrl() : "";
                                    if (comUrl.lastIndexOf("/") < comUrl.length() - 1) {
                                        comUrl += "/" + coSpace;
                                    } else {
                                        comUrl += coSpace;
                                    }
                                    String url = comUrl + "/" + busiRecords.getRealName();

                                    BusiLiveRecords busiLiveRecords = new BusiLiveRecords();
                                    busiLiveRecords.setLiveId(liveBroadcast.getId());
                                    busiLiveRecords.setFileUrl(url);
                                    busiLiveRecords.setFileName(records.getFileName());
                                    busiLiveRecords.setCreateTime(new Date());
                                    busiLiveRecords.setCreateBy(liveBroadcast.getCreateBy());
                                    int i = busiLiveRecordsMapper.insertBusiLiveRecords(busiLiveRecords);
                                    if (i > 0) {

                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // 普通直播

            }
        }
    }

    private static String getSavePath() {
        String os = System.getProperty("os.name");
        if (os.contains("indows")) {
            return PathUtil.getRootPath();
        } else {
            return "/mnt/nfs/spaces";
        }
    }
}
