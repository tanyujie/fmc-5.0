package com.paradisecloud.fcm.web.monitor;

import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiLiveBroadcastMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiLiveBroadcast;
import com.paradisecloud.fcm.dao.model.BusiLiveBroadcastAppointmentMap;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiRecordsService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IAttendeeForMcuZjService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiRecordsForMcuZjService;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IAttendeeSmc2Service;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiRecordsForMcuSmc2Service;
import com.paradisecloud.fcm.web.cache.LiveBroadcastCache;
import com.paradisecloud.fcm.web.service.interfaces.IBusiLiveBroadcastService;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceService;
import com.paradisecloud.fcm.mqtt.cache.AppointmentCache;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiSmc2ConferenceService;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.service.interfaces.IAttendeeSmc3Service;
import com.paradisecloud.smc3.service.interfaces.IBusiRecordsForMcuSmc3Service;
import com.paradisecloud.smc3.service.interfaces.IBusiSmc3ConferenceService;
import com.sinhy.utils.DateUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */

@Component
public class LiveBroadcastThread extends Thread implements InitializingBean {

    @Resource
    private IBusiLiveBroadcastService busiLiveBroadcastService;
    @Resource
    private IBusiMcuZjConferenceService busiMcuZjConferenceService;
    @Resource
    private IBusiSmc3ConferenceService busiSmc3ConferenceService;
    @Resource
    private IBusiSmc2ConferenceService busiSmc2ConferenceService;
    @Resource
    private IBusiConferenceService busiConferenceService;
    @Resource
    private IBusiRecordsForMcuZjService iBusiRecordsForMcuZjService;
    @Resource
    private IBusiRecordsService busiRecordsService;
    @Resource
    private IBusiRecordsForMcuSmc3Service busiRecordsForMcuSmc3Service;
    @Resource
    private IBusiRecordsForMcuSmc2Service busiRecordsForMcuSmc2Service;
    @Resource
    private IAttendeeService attendeeService;
    @Resource
    private IAttendeeForMcuZjService attendeeForMcuZjService;
    @Resource
    private IAttendeeSmc3Service attendeeSmc3Service;
    @Resource
    private IAttendeeSmc2Service attendeeSmc2Service;
    @Resource
    private BusiLiveBroadcastMapper busiLiveBroadcastMapper;

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

            try {
                checkLiveBroadcast();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkLiveBroadcast() {
        Collection<BusiLiveBroadcast> values = LiveBroadcastCache.getInstance().values();
        for (BusiLiveBroadcast busiLiveBroadcast : values) {
            Integer status = busiLiveBroadcast.getStatus();
            Integer isStart = busiLiveBroadcast.getIsStart();
            if (status == null) {
                continue;
            }
            if (status == 1) {
                Date startTime = null;
                Date endTime = null;
                Date curDate = new Date();
                startTime = DateUtils.convertToDate(busiLiveBroadcast.getStartTime());
                endTime = DateUtils.convertToDate(busiLiveBroadcast.getEndTime());

                if (isStart == null || isStart == 2) {
                    // 开始直播
                    if (curDate.after(startTime) && curDate.before(endTime)) {
                        try {
                            busiLiveBroadcast.setIsStart(1);
                            int i = busiLiveBroadcastMapper.updateBusiLiveBroadcast(busiLiveBroadcast);
                            if (i > 0) {
                                LiveBroadcastCache.getInstance().add(busiLiveBroadcast);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    // 结束直播
                    long timeDiff = endTime.getTime() - curDate.getTime();
                    if (timeDiff < 10 * 60 * 1000) {
                        long min = timeDiff / (60 * 1000);
                        long s = (timeDiff / 1000) % 60;

                    }

                    if (curDate.after(endTime)) {
                        try {
                            busiLiveBroadcastService.endLive(busiLiveBroadcast.getId(), EndReasonsType.AUTO_END);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (isStart == null || isStart == 1) {
                    Integer type = busiLiveBroadcast.getType();
                    Integer terminalId = busiLiveBroadcast.getTerminalId();
                    Integer meetingFileId = busiLiveBroadcast.getMeetingFileId();
                    Long id = busiLiveBroadcast.getId();
                    Integer playbackEnabled = busiLiveBroadcast.getPlaybackEnabled();
                    if (type == 1) {
                        BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMap = LiveBroadcastCache.getInstance().getMapById(id);
                        String mcuType = busiLiveBroadcastAppointmentMap.getMcuType();
                        Long appointmentId = busiLiveBroadcastAppointmentMap.getAppointmentId();
                        String generateKey = EncryptIdUtil.generateKey(appointmentId, mcuType);
                        BusiConferenceAppointment busiConferenceAppointment = AppointmentCache.getInstance().get(generateKey);
                        if (busiConferenceAppointment != null) {
                            Long templateId = busiConferenceAppointment.getTemplateId();
                            String contextKey = EncryptIdUtil.generateContextKey(templateId, mcuType);
                            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                            if (baseConferenceContext != null) {
                                String encryptId = EncryptIdUtil.generateEncryptId(contextKey);
                                BaseAttendee masterAttendee = baseConferenceContext.getMasterAttendee();
                                if (masterAttendee == null) {
                                    if (terminalId != null) {
                                        BaseAttendee attendeeByTerminalId = baseConferenceContext.getAttendeeByTerminalId(Long.valueOf(terminalId));
                                        String attendeeId = attendeeByTerminalId.getId();
                                        if (baseConferenceContext instanceof ConferenceContext) {
                                            attendeeService.changeMaster(encryptId, attendeeId);
                                        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
                                            attendeeSmc3Service.changeMaster(encryptId, attendeeId);
                                        } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
                                            attendeeSmc2Service.changeMaster(encryptId, attendeeId);
                                        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
                                            attendeeForMcuZjService.changeMaster(encryptId, attendeeId);
                                        }
                                    } else if (meetingFileId != null) {
                                        List attendees = baseConferenceContext.getAttendees();
                                        if (attendees != null && attendees.size() > 0) {
                                            Object attendeeObj = attendees.get(0);
                                            if (attendeeObj != null) {
                                                if (baseConferenceContext instanceof ConferenceContext) {
                                                    Attendee attendee = (Attendee) attendeeObj;
                                                    String attendeeId = attendee.getId();
                                                    attendeeService.changeMaster(encryptId, attendeeId);
                                                } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
                                                    AttendeeSmc3 attendee = (AttendeeSmc3) attendeeObj;
                                                    String attendeeId = attendee.getId();
                                                    attendeeSmc3Service.changeMaster(encryptId, attendeeId);
                                                } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
                                                    AttendeeSmc2 attendee = (AttendeeSmc2) attendeeObj;
                                                    String attendeeId = attendee.getId();
                                                    attendeeSmc2Service.changeMaster(encryptId, attendeeId);
                                                } else if (baseConferenceContext instanceof McuZjConferenceContext) {
                                                    AttendeeForMcuZj attendee = (AttendeeForMcuZj) attendeeObj;
                                                    String attendeeId = attendee.getId();
                                                    attendeeForMcuZjService.changeMaster(encryptId, attendeeId);
                                                }
                                            }
                                        }
                                    }
                                }
                                if (masterAttendee != null) {
                                    Integer streamingEnabled = baseConferenceContext.getStreamingEnabled();
                                    if (streamingEnabled == 2) {
                                        String streamingUrl = busiLiveBroadcast.getStreamUrl();
                                        if (baseConferenceContext instanceof ConferenceContext) {
                                            busiConferenceService.stream(encryptId, true, streamingUrl);
                                        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
                                            busiSmc3ConferenceService.stream(encryptId, true, streamingUrl);
                                        } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
                                            busiSmc2ConferenceService.stream(encryptId, true, streamingUrl);
                                        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
                                            busiMcuZjConferenceService.stream(encryptId, true, streamingUrl);
                                        }
                                    }

                                    Integer recordingEnabled = baseConferenceContext.getRecordingEnabled();
                                    String decryptHexToString = AesEnsUtils.getAesEncryptor().decryptHexToString(encryptId);
                                    if (recordingEnabled == 2 && playbackEnabled == 1) {
                                        if (baseConferenceContext instanceof ConferenceContext) {
                                            busiRecordsService.updateBusiRecords(true, decryptHexToString);
                                        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
                                            iBusiRecordsForMcuZjService.updateBusiRecords(true, decryptHexToString);
                                        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
                                            busiRecordsForMcuSmc3Service.updateBusiRecords(true, decryptHexToString);
                                        }  else if (baseConferenceContext instanceof Smc2ConferenceContext) {
                                            busiRecordsForMcuSmc2Service.updateBusiRecords(true, decryptHexToString);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
