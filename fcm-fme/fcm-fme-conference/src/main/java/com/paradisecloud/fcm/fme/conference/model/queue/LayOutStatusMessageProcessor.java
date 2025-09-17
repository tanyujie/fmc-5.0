package com.paradisecloud.fcm.fme.conference.model.queue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.CellScreenAttendeeOperation;
import com.paradisecloud.fcm.fme.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.conference.interfaces.IDefaultAttendeeOperationPackageService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.fme.model.busi.message.AttendeeStatusMessage;
import com.paradisecloud.fcm.fme.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.fme.model.busi.operation.DefaultViewOperation;
import com.sinhy.model.AsyncBlockingMessageProcessor;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author nj
 * @date 2022/12/19 10:45
 */
@Component
public class LayOutStatusMessageProcessor extends AsyncBlockingMessageProcessor<AttendeeStatusMessage> implements InitializingBean {

    public static final int INT_JOIN = 3;
    public static final int INT_LAYOUT = 2;
    public static final int LAYOUT_BROADCAST = 1;
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * <pre>构造方法</pre>
     *
     * @author lilinhai
     * @since 2021-02-04 17:00
     */
    public LayOutStatusMessageProcessor() {
        super("LayOutStatusMessageProcessor", LayOutStatusMessageQueue.getInstance());
    }

    @Override
    public void process(AttendeeStatusMessage message) {
            try {
                Attendee attendee = message.getAttendee();
                logger.info("message:"+JSONObject.toJSONString(message));
                if (null != attendee) {
                    ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(attendee.getContextKey());
                    if(conferenceContext.getRoundRobin()||conferenceContext.isStartRound()){
                        return;
                    }
                    Object onlineStatus = attendee.getUpdateMap().get("meetingStatus");
                    if (onlineStatus != null || attendee.getUpdateMap().size()>=LAYOUT_BROADCAST){
                       // if (onlineStatus != null || attendee.getUpdateMap().size() == 1||attendee.getUpdateMap().size() == INT_JOIN||attendee.getUpdateMap().size() == INT_LAYOUT) {
                        DefaultViewOperation defaultViewOperation = conferenceContext.getDefaultViewOperation();

                        AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
                        logger.info("attendeeOperation:"+attendeeOperation);
                        if(attendeeOperation!=null){
                            if (attendeeOperation instanceof DefaultAttendeeOperation && !defaultViewOperation.isCancel()) {
//                                SplitScreen splitScreen = conferenceContext.getAttendeeOperation().getSplitScreen();
//                                Integer isFill = defaultViewOperation.getDefaultViewIsFill();
//                                String layout = defaultViewOperation.getDefaultViewLayout();
//                                updateView(conferenceContext, defaultViewOperation, splitScreen, isFill);
                                int repeatedCount = ((DefaultAttendeeOperation) attendeeOperation).getRepeatedCount();
                                ((DefaultAttendeeOperation) attendeeOperation).setRepeatedCount(repeatedCount+1);
                                logger.info("setRepeatedCount:"+repeatedCount);
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                LoggerFactory.getLogger(getClass()).error("Error in process LayOutStatusMessageProcessor", e);
            }

    }

    private void updateView(ConferenceContext conferenceContext, DefaultViewOperation defaultViewOperation, SplitScreen splitScreen, Integer isFill) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("defaultViewLayout", defaultViewOperation.getDefaultViewLayout());
        jsonObject.put("defaultViewIsBroadcast", defaultViewOperation.getDefaultViewIsBroadcast());
        jsonObject.put("defaultViewIsDisplaySelf", defaultViewOperation.getDefaultViewIsDisplaySelf());
        jsonObject.put("defaultViewIsFill", isFill);
        jsonObject.put("pollingInterval", defaultViewOperation.getDefaultViewPollingInterval());



        List<CellScreen> cellScreens = splitScreen.getCellScreens();
        JSONArray jsonArrayCell = new JSONArray();
        JSONArray jsonArrayParticpants = new JSONArray();
        if(!CollectionUtils.isEmpty(cellScreens)){
            for (int i = 0; i < cellScreens.size(); i++) {
                JSONObject job = new JSONObject();
                job.put("cellSequenceNumber", cellScreens.get(i).getSerialNumber());
                CellScreenAttendeeOperation cellScreenAttendeeOperation = cellScreens.get(i).getCellScreenAttendeeOperation();
                if(cellScreenAttendeeOperation==null){
                    job.put("operation", CellScreenAttendeeOperation.CHOOSE_SEE.getValue());
                }else {
                    job.put("operation", cellScreens.get(i).getCellScreenAttendeeOperation().getValue());
                }

                job.put("isFixed", cellScreens.get(i).getIsFixedValue());
                jsonArrayCell.add(job);

                List<Attendee> attendees = cellScreens.get(i).getAttendees();
                if(!CollectionUtils.isEmpty(attendees)){
                    for (int f = 0; f < attendees.size(); f++) {
                        JSONObject jobAttendee = new JSONObject();
                        jobAttendee.put("cellSequenceNumber",cellScreens.get(i).getSerialNumber());
                        jobAttendee.put("participantUuId",attendees.get(f).getParticipantUuid());
                        jobAttendee.put("weight",f+1);
                        jsonArrayParticpants.add(jobAttendee);
                    }

                }

            }

            jsonObject.put("defaultViewCellScreens", jsonArrayCell);
            jsonObject.put("defaultViewPaticipants", jsonArrayParticpants);
        }
        IDefaultAttendeeOperationPackageService defaultAttendeeOperationPackageService = BeanFactory.getBean(IDefaultAttendeeOperationPackageService.class);
        defaultAttendeeOperationPackageService.updateDefaultViewConfigInfo(conferenceContext.getId(), jsonObject);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
