package com.paradisecloud.fcm.fme.conference.task;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantImgMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipantImg;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConferenceTakeSnapshotTask extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ConferenceTakeSnapshotTask.class);
    private final String conferenceId;

    private final String conferenceNumber;

    public ConferenceTakeSnapshotTask(String id, long delayInMilliseconds, String conferenceId, String conferenceNumber) {
        this.conferenceId = conferenceId;
        this.conferenceNumber = conferenceNumber;


    }

    private static void takeImage(ConferenceContext conferenceContext, List<BusiHistoryParticipantImg> imgList, Attendee attendee, int chairman) {
        BusiHistoryParticipantImg busiHistoryParticipantImg = new BusiHistoryParticipantImg();
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(attendee);
        Participant participant = fmeBridge.getDataCache().getParticipantByUuid(attendee.getParticipantUuid());
        CallLeg callLeg = participant.getCallLeg();

        busiHistoryParticipantImg.setCoSpace(conferenceContext.getCoSpaceId());
        busiHistoryParticipantImg.setCallId(attendee.getCallId());
        busiHistoryParticipantImg.setCreateTime(new Date());
        busiHistoryParticipantImg.setCallLegId(callLeg.getId());
        busiHistoryParticipantImg.setRemoteParty(attendee.getRemoteParty());
        busiHistoryParticipantImg.setName(attendee.getName());
        busiHistoryParticipantImg.setChairman(chairman);
        busiHistoryParticipantImg.setHistoryId(conferenceContext.getHistoryConference().getId());
        if (attendee.getMeetingJoinedTime() != 0) {
            busiHistoryParticipantImg.setJoinTime(new Date(attendee.getMeetingJoinedTime()));
        }
        if (callLeg != null) {
            String snapshot = null;
            try {
                snapshot = fmeBridge.getCallLegInvoker().takeSnapshot(callLeg.getId(), "rx", 360);
            } catch (Exception e) {
                logger.info(e.getMessage());
            }

            busiHistoryParticipantImg.setImageBase64(snapshot);
            busiHistoryParticipantImg.setImgTime(new Date());
        }
        imgList.add(busiHistoryParticipantImg);
    }

    private static void executeTask(String des, String conferenceNumber, String conferenceId, ConferenceContext conferenceContext) {
        if (conferenceContext == null || conferenceContext.isEnd()) {
            return;
        }
        BusiHistoryParticipantImgMapper busiHistoryParticipantImgMapper = BeanFactory.getBean(BusiHistoryParticipantImgMapper.class);
        //获取快照
        logger.info("抓取快照>>>>>>>>>>>>>>>>>>>>>>>会议号:" + conferenceNumber + "会议ID:" + conferenceId);
        List<BusiHistoryParticipantImg> imgList = new ArrayList<>();
        List<Attendee> attendees = conferenceContext.getAttendees();

        for (Attendee attendee : attendees) {
            if (attendee.isMeetingJoined()) {
                takeImage(conferenceContext, imgList, attendee, 0);
            }
        }
        Attendee masterAttendee = conferenceContext.getMasterAttendee();
        if (masterAttendee != null && masterAttendee.isMeetingJoined()) {
            takeImage(conferenceContext, imgList, masterAttendee, 1);
        }

        List<Attendee> masterAttendees = conferenceContext.getMasterAttendees();
        for (Attendee attendee : masterAttendees) {
            if (attendee.isMeetingJoined()) {
                takeImage(conferenceContext, imgList, attendee, 0);
            }
        }

        if (CollectionUtils.isNotEmpty(imgList)) {
            busiHistoryParticipantImgMapper.batchInsertBusiHistoryParticipantImg(imgList);
        }
    }

    @Override
    public void run() {
        try {
            logger.info("快照任务启动>>>>>>>>>>>>>>>>>>>>>>>会议号:" + conferenceNumber + "会议ID:" + conferenceId);
            String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
            if (baseConferenceContext == null) {
                return;
            }
            ConferenceContext conferenceContext = (ConferenceContext) baseConferenceContext;

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);


            long[] DELAYS = {5,30,60}; // 延迟时间（分钟）
            try {
                for (long delay : DELAYS) {
                    executor.schedule(() -> {
                        try {
                            executeTask(delay + "分钟后执行的任务", conferenceNumber, conferenceId, conferenceContext);
                        } catch (Exception e) {
                            // 使用日志记录错误
                            logger.info("Error executing task: " + e.getMessage());
                        }
                    }, delay, TimeUnit.MINUTES); // 设置延迟时间
                }
            } finally {
                executor.shutdown(); // 关闭调度器
                try {
                    if (!executor.awaitTermination(62, TimeUnit.MINUTES)) {
                        executor.shutdownNow(); // 强制关闭
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow(); // 处理中断
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
