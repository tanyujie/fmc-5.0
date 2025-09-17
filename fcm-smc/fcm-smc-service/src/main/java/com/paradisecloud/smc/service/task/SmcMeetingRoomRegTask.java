package com.paradisecloud.smc.service.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.MeetingRoomCreateReq;
import com.paradisecloud.com.fcm.smc.modle.MeetingRoomResponse;
import com.paradisecloud.com.fcm.smc.modle.SMcipProtocolType;
import com.paradisecloud.com.fcm.smc.modle.TerminalParam;
import com.paradisecloud.com.fcm.smc.modle.response.UserInfoRep;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.smc.service.SmcUserService;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * @author nj
 * @date 2023/5/22 10:33
 */
public class SmcMeetingRoomRegTask extends SmcDelayTask{

    public static final String FALSE = "false";
    public static final String KBIT_S = "1920 Kbit/s";
    public static final String VHD = "vhd";
    public static final String PUBLIC = "PUBLIC";
    private String name;
    private BusiTerminal busiTerminal;
    private String uri;

    private SmcBridge smcBridge;
    private SmcUserService smcUserService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SmcMeetingRoomRegTask.class);


    public SmcMeetingRoomRegTask(String id,String name,String uri, long delayInMilliseconds, SmcBridge smcBridge, SmcUserService smcUserService,BusiTerminal busiTerminal) {
        super(id, delayInMilliseconds);
        this.smcBridge = smcBridge;
        this.smcUserService = smcUserService;
        this.name=name;
        this.uri=uri;
        this.busiTerminal=busiTerminal;
    }

    @Override
    public void run() {



        String result = smcBridge.getSmcMeetingroomsInvoker().getMeetingRoomsByName(name, smcBridge.getSmcportalTokenInvoker().getSystemHeaders());
        LOGGER.info("外部会议室名称查询结果:"+result);
        if(Objects.equals(result, FALSE)){

            MeetingRoomCreateReq meetingRoomReq = new MeetingRoomCreateReq();
            meetingRoomReq.setName(name);
            meetingRoomReq.setProvisionEua("true");

            UserInfoRep userInfo = smcUserService.getUserInfo();
            String id = userInfo.getAccount().getOrganization().getId();
            meetingRoomReq.setOrganizationId(id);

            TerminalParam terminalParam = new TerminalParam();


            terminalParam.setTerminalType(VHD);
            terminalParam.setMiddleUri(uri);
            terminalParam.setRate(KBIT_S);
            terminalParam.setSecurityLevel(PUBLIC);
            terminalParam.setIpProtocolType(SMcipProtocolType.AUTO.name());
            meetingRoomReq.setTerminalParam(terminalParam);
            String res = smcBridge.getSmcMeetingroomsInvoker().creatMeetingrooms(JSONObject.toJSONString(meetingRoomReq), smcBridge.getSmcportalTokenInvoker().getSystemHeaders());

            try {
                MeetingRoomResponse meetingRoomResponse = JSON.parseObject(res, MeetingRoomResponse.class);
                if(meetingRoomResponse!=null){
                    Map<String, Object> params=new HashMap<>();
                    params.put("room_id",meetingRoomResponse.getId());
                    busiTerminal.setBusinessProperties(params);
                    BusiTerminalMapper bean = BeanFactory.getBean(BusiTerminalMapper.class);
                    bean.updateBusiTerminal(busiTerminal);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LOGGER.info("外部会议室添加结果:"+res);
        }
    }
}
