package com.paradisecloud.fcm.fme.conference.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiPacketMapper;
import com.paradisecloud.fcm.dao.model.BusiPacket;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.RtspCallAttendeeProcessor;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.InvitedAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.RemotePartyAttendeesMap;
import com.paradisecloud.fcm.fme.model.response.participant.ParticipantInfoResponse;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.packet.PacketEndPoint;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author nj
 * @date 2024/7/10 14:17
 */
public class PacketConferenceTask extends Task {


    private static final Logger logger = LoggerFactory.getLogger(PacketConferenceTask.class);
    private final String conferenceId;

    private final Set<String> ipSet = new HashSet<>();

    private final HashMap<String, BaseAttendee> baseAttendeeHashMap = new HashMap<>();

    public PacketConferenceTask(String id, long delayInMilliseconds, String conferenceId) {
        super("packet_conference_t_i" + id, delayInMilliseconds);
        this.conferenceId = conferenceId;
    }

    @Override
    public void run() {
        BusiPacketMapper busiPacketMapper = BeanFactory.getBean(BusiPacketMapper.class);

        List<BusiPacket> busiPackets = busiPacketMapper.selectBusiPacketList(new BusiPacket());
        if (CollectionUtils.isEmpty(busiPackets)) {
            logger.info("抓包服务器不存在");
            return;
        }
        BusiPacket busiPacket = busiPackets.get(0);

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);

        String packetConferenceId = baseConferenceContext.getPacketConferenceId();
        if (Strings.isNotBlank(packetConferenceId)) {
            String contextKey_P = EncryptIdUtil.parasToContextKey(packetConferenceId);
            BaseConferenceContext baseConferenceContext_P = AllConferenceContextCache.getInstance().get(contextKey_P);
            ConferenceContext pConferenceContext = (ConferenceContext) baseConferenceContext_P;

            while (true) {

                try {
                    if (baseConferenceContext == null || baseConferenceContext.isEnd() || pConferenceContext == null || pConferenceContext.isEnd()) {
                        //删除终端

                        RestTemplate restTemplate = new RestTemplate();
                        ResponseEntity<String> allEntity = restTemplate.getForEntity("http://" + busiPacket.getIp() + "/endpoints?cmd=getsnifferpeer", String.class);
                        String bodyAll = allEntity.getBody();
                        List<PacketEndPoint> packetEndPointsAll = JSONArray.parseArray(bodyAll, PacketEndPoint.class);
                        List<Integer> delIds = new ArrayList<>();
                        for (PacketEndPoint point : packetEndPointsAll) {
                            if (ipSet.contains(point.getIp())) {
                                delIds.add(point.getId());
                            }
                        }
                        if (CollectionUtils.isNotEmpty(delIds)) {
                            restTemplate.postForEntity("http://" + busiPacket.getIp() + "/endpoints?cmd=delpeers", delIds, String.class);
                        }
                        break;
                    }
                    ipSet.clear();
                    List attendees = baseConferenceContext.getAttendees();
                    for (Object obj : attendees) {
                        BaseAttendee attendee = (BaseAttendee) obj;
                        if(attendee.isMeetingJoined()){
                            String ip = attendee.getIp();
                            if (ip != null && !ipSet.contains(ip)) {
                                baseAttendeeHashMap.put(ip, attendee);
                                ipSet.add(ip);
                            }
                        }

                    }
                    List masterAttendees = baseConferenceContext.getMasterAttendees();
                    for (Object obj : masterAttendees) {
                        BaseAttendee attendee = (BaseAttendee) obj;
                        if(attendee.isMeetingJoined()){
                            String ip = attendee.getIp();
                            if (ip != null && !ipSet.contains(ip)) {
                                baseAttendeeHashMap.put(ip, attendee);
                                ipSet.add(ip);
                            }
                        }

                    }
                    BaseAttendee masterAttendee = baseConferenceContext.getMasterAttendee();
                    if (masterAttendee != null&&masterAttendee.isMeetingJoined()) {
                        String ip = masterAttendee.getIp();
                        if (ip != null) {
                            ipSet.add(ip);
                        }
                    }



                    if (attendees.size() > 0) {
                        //拉取电视墙列表
                        RestTemplate restTemplate = new RestTemplate();

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                        MultiValueMap<String, String> formData = new org.springframework.util.LinkedMultiValueMap<>();
                        formData.put("username", Collections.singletonList(busiPacket.getUsername()));
                        formData.put("password", Collections.singletonList(busiPacket.getPassword()));
                        String url = "http://" + busiPacket.getIp() + "/signin";
                        // 创建HttpEntity对象
                        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
                        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);


                        if (response != null) {
                            int statusCodeValue = response.getStatusCodeValue();
                            logger.info(busiPacket.getIp() + "login signin :{}", statusCodeValue);
                            RestTemplate restTemplate2 = new RestTemplate();
                            //获取所有列表
                            //http://ip/endpoints?cmd=getsnifferpeer
                            ResponseEntity<String> forEntity = restTemplate2.getForEntity("http://" + busiPacket.getIp() + "/endpoints?cmd=getsnifferpeer", String.class);

                            if (forEntity != null) {
                                int getsnifferpeer_statusCodeValue = forEntity.getStatusCodeValue();
                                logger.info(busiPacket.getIp() + "endpoints?cmd=getsnifferpeer :{}", getsnifferpeer_statusCodeValue);

                                HashSet<String> endpoints = new HashSet<>();
                                String body1 = forEntity.getBody();
                                List<PacketEndPoint> packetEndPoints = JSONArray.parseArray(body1, PacketEndPoint.class);
                                for (PacketEndPoint point : packetEndPoints) {
                                    String ip = point.getIp();
                                    endpoints.add(ip);
                                }
                                if (endpoints.size() == 0) {
                                    //添加终端  http://ip/endpoints/add
                                    for (String s : ipSet) {
                                        addPonit(busiPacket, restTemplate, s);
                                    }
                                } else {
                                    Set<String> difference = new HashSet<>(ipSet);
                                    difference.removeAll(endpoints);
                                    if (difference.size() > 0) {
                                        for (String s : difference) {
                                            addPonit(busiPacket, restTemplate, s);
                                        }
                                    }
                                }

                                //获取所有列表：
                                ResponseEntity<String> allEntity = restTemplate2.getForEntity("http://" + busiPacket.getIp() + "/endpoints?cmd=getsnifferpeer", String.class);
                                String bodyAll = allEntity.getBody();
                                List<PacketEndPoint> packetEndPointsAll = JSONArray.parseArray(bodyAll, PacketEndPoint.class);

                                for (PacketEndPoint point : packetEndPointsAll) {
                                    if (ipSet.contains(point.getIp())) {
                                        BaseAttendee attendee_base = baseAttendeeHashMap.get(point.getIp());
                                        if (attendee_base != null) {
                                            List<Attendee> attendees_packet = pConferenceContext.getAttendees();
                                            if(CollectionUtils.isEmpty(attendees_packet)){
                                                doRtspcall(attendee_base.getId(), contextKey_P, pConferenceContext, point);
                                            }else {
                                                boolean isExist = false;
                                                for (Attendee attendee : attendees_packet) {
                                                    String remoteParty = attendee.getRemoteParty();
                                                    if(remoteParty.contains(attendee_base.getId())){
                                                        isExist=true;
                                                        if(!attendee.isMeetingJoined()){
                                                            doRtspcall(attendee_base.getId(), contextKey_P, pConferenceContext, point);
                                                        }
                                                        break;
                                                    }
                                                }
                                                if(!isExist){
                                                    doRtspcall(attendee_base.getId(), contextKey_P, pConferenceContext, point);
                                                }
                                            }
                                        }

                                    }
                                }


                                List<Attendee> attendees_packet = pConferenceContext.getAttendees();
                                for (PacketEndPoint point : packetEndPointsAll) {
                                    if (ipSet.contains(point.getIp())) {
                                        //再次检查
                                        if (CollectionUtils.isNotEmpty(attendees_packet)) {
                                            for (Attendee attendee : attendees_packet) {
                                                logger.info("P-ConferenceContext-attendee-NAME:"+ attendee.getName() + "-ID:" + attendee.getId() + "-isMeetingJoined:" + attendee.isMeetingJoined() + "-contextKey:" + attendee.getContextKey()+ "-ip:" + point.getIp());
                                                if (attendee != null && !attendee.isMeetingJoined()) {
                                                    new RtspCallAttendeeProcessor(attendee.getContextKey(), attendee.getId(), point.getUrl1()).process();
                                                }
                                            }
                                        }
                                    }
                                }

                            }

                        }

                    }
                } catch (Exception e) {
                    logger.error("抓包服务异常：" + e.getMessage());
                    break;
                } finally {
                    Threads.sleep(2000);
                }


            }


        }


    }

    private void doRtspcall(String id, String contextKey_P, ConferenceContext pConferenceContext, PacketEndPoint point) {
        InvitedAttendee ia = new InvitedAttendee();
        ia.setConferenceNumber(pConferenceContext.getConferenceNumber());
        ia.setId(id);
      //  ia.setIp("10.0.66.110");
        ia.setName(baseAttendeeHashMap.get(point.getIp()).getName());
        ia.setRemoteParty(point.getUrl1());
        ia.setContextKey(contextKey_P);
        ia.setWeight(1);
        ia.setDeptId(pConferenceContext.getDeptId());
        new RtspCallAttendeeProcessor(ia, point.getUrl1()).process();

    }

    private void addPonit(BusiPacket busiPacket, RestTemplate restTemplate, String s) {

        JSONObject jsonObject_add = new JSONObject();
        jsonObject_add.put("ip", s);
        jsonObject_add.put("name", s);
        jsonObject_add.put("codec", "g722");
        jsonObject_add.put("channel_layout", "mono");
        jsonObject_add.put("sample_format", "s16");
        jsonObject_add.put("sample_rate", 48000);
        jsonObject_add.put("codec_option", "latm");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> formaddEntity = new HttpEntity<>(JSON.toJSONString(jsonObject_add), headers);
        restTemplate.postForEntity("http://" + busiPacket.getIp() + "/endpoints?cmd=add", formaddEntity, String.class);

    }
}
