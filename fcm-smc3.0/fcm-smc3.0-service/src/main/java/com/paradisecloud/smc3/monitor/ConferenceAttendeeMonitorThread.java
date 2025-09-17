package com.paradisecloud.smc3.monitor;

import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiPickerMapper;
import com.paradisecloud.fcm.dao.model.BusiPicker;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.ParticipantStatusDto;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import com.paradisecloud.smc3.utils.RandomPicker;
import com.paradisecloud.system.dao.mapper.SysConfigMapper;
import com.paradisecloud.system.dao.mapper.SysDeptMapper;
import com.paradisecloud.system.dao.mapper.SysUserMapper;
import com.paradisecloud.system.dao.model.SysConfig;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.dao.model.SysUser;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;



public class ConferenceAttendeeMonitorThread extends Thread  {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    private List<Integer> deptIds=new ArrayList<>();

    private Map<String,List<String>> selectedMap=new HashMap<>();
    @Override
    public void run() {

        while (true) {
            try {

                BusiPickerMapper busiPickerMapper = BeanFactory.getBean(BusiPickerMapper.class);
                List<BusiPicker> busiPickers = busiPickerMapper.selectBusiPickerList(new BusiPicker());
                if(CollectionUtils.isNotEmpty(busiPickers)){
                    for (BusiPicker busiPicker : busiPickers) {
                        deptIds.add(busiPicker.getDeptId());
                    }
                }
                if(CollectionUtils.isNotEmpty(deptIds)){
                    SysConfigMapper sysConfigMapper = BeanFactory.getBean(SysConfigMapper.class);
                    SysConfig sysConfig_q = new SysConfig();
                    sysConfig_q.setConfigKey("conference.pick.enable");
                    SysConfig sysConfig = sysConfigMapper.selectConfig(sysConfig_q);
                    if(sysConfig!=null&&sysConfig.getConfigValue().equals("1")){
                        dorun();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Threads.sleep(1000);
            }

        }
    }

    private void dorun() {
        Collection<Smc3ConferenceContext> values = Smc3ConferenceContextCache.getInstance().values();
        if (CollectionUtils.isNotEmpty(values)) {
            for (Smc3ConferenceContext conferenceContext : values) {
                if (conferenceContext != null && conferenceContext.isStart()) {
//                    Long createUserId = conferenceContext.getCreateUserId();
//                    if(createUserId!=null){
//                        if(createUserId==1){
//                            continue;
//                        }else {
//
//                        }
//                    }
                    Long deptId1 = conferenceContext.getDeptId();
                    if(deptIds.contains(deptId1.intValue())){
                        List<String> selected_item=new ArrayList<>();
                        List<AttendeeSmc3> attendees = conferenceContext.getAttendees();
                        if (CollectionUtils.isNotEmpty(attendees)) {
                            for (AttendeeSmc3 attendee : attendees) {
                                if(attendee.isMeetingJoined()){
                                    long deptId = attendee.getDeptId();
                                    selected_item.add(attendee.getId());
                                }
                            }
                        }

                        List<AttendeeSmc3> cascadeAttendees = conferenceContext.getCascadeAttendees();
                        if (CollectionUtils.isNotEmpty(cascadeAttendees)) {
                            for (AttendeeSmc3 attendee : cascadeAttendees) {
                                if(attendee.isMeetingJoined()){
                                    selected_item.add(attendee.getId());
                                }
                            }
                        }
                        selectedMap.put(conferenceContext.getId(),selected_item);
                    }
                }
            }

            selectedMap.forEach((k,v)->{
                RandomPicker.pickRandomElement(v, new RandomPicker.Callback() {
                    @Override
                    public void onElementPicked(String element) {
                        logger.info("++++++++++++onElementPicked++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        logger.info("++++++++++++onElementPicked++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        logger.info("++++++++++++onElementPicked++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        logger.info("++++++++++++onElementPicked++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        logger.info("++++++++++++onElementPicked++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        logger.info("++++++++++++onElementPicked++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        logger.info("++++++++++++onElementPicked++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        logger.info("++++++++++++onElementPicked++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        logger.info("++++++++++++onElementPicked++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        List<String> options = new ArrayList<>();
                        options.add("A");
                        options.add("B");

                        setSpeaker(k,element);
//                        setCamera(k,element);

//                        Random random = new Random();
//                        int index = random.nextInt(options.size());
//                        String selectedOption = options.get(index);
//                        if(selectedOption.equals("A")){
//                            setSpeaker(k,element);
//                        }else if(selectedOption.equals("B")){
//                            setCamera(k,element);
//                        }else {
//                        //    setChoose(k,element);
//                        }

                    }

                    @Override
                    public void onNoElementPicked() {
                        logger.info("++++++++++++onNoElementPicked++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    }
                });
            });
        }
    }




    public void setSpeaker(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        String smc3conferenceId = conferenceContext.getSmc3conferenceId();
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        AttendeeSmc3 attendeeById = conferenceContext.getAttendeeById(attendeeId);
        if (attendeeById == null) {
            return;
        }
        List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setId(attendeeById.getParticipantUuid());
        participantStatusDto.setIsQuiet(true);
        participantStatusList.add(participantStatusDto);
        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
            bridge.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(smc3conferenceId, participantStatusList, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } else {
            bridge.getSmcParticipantsInvoker().PATCHParticipantsOnly(smc3conferenceId, participantStatusList, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
        Threads.sleep(4000);
        participantStatusDto.setIsQuiet(false);

        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
            bridge.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(smc3conferenceId, participantStatusList, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } else {
            bridge.getSmcParticipantsInvoker().PATCHParticipantsOnly(smc3conferenceId, participantStatusList, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
    }

    public void setCamera(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        // 打开终端摄像头（打开上行视频）
        AttendeeSmc3 attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {
            List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
            ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
            participantStatusDto.setIsVideoMute(true);
            participantStatusDto.setId(attendee.getSmcParticipant().getGeneralParam().getId());
            Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
            String conferenceIdSmc = conferenceContext.getSmc3conferenceId();
            participantStatusList.add(participantStatusDto);
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceIdSmc, participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceIdSmc, participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }

            Threads.sleep(4000);
            participantStatusDto.setIsVideoMute(false);
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceIdSmc, participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceIdSmc, participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
        }


    }

    public void  setChoose(String conferenceId,String attendeeId){
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc3 attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null) {
            Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
            Random random = new Random();
            List<String> elements = selectedMap.get(conferenceId);
            String chooseeid = elements.get(random.nextInt(elements.size()));
            AttendeeSmc3 attendee_choose = conferenceContext.getAttendeeById(chooseeid);
            SmcParitipantsStateRep.ContentDTO choose = attendee_choose.getSmcParticipant();

            if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                bridgesByDept.getSmcConferencesInvoker().conferencesControlChooseCascade(conferenceId, attendee.getParticipantUuid(), choose.getGeneralParam().getId(), bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }else {
                bridgesByDept.getSmcConferencesInvoker().conferencesControlChoose(conferenceId, attendee.getParticipantUuid(), choose.getGeneralParam().getId(), bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
        }

    }



}