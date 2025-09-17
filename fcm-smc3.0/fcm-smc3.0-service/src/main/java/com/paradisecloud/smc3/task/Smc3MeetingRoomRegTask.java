package com.paradisecloud.smc3.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.response.QueryAREASResponse;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.model.MeetingRoomCreateReq;
import com.paradisecloud.smc3.model.MeetingRoomResponse;
import com.paradisecloud.smc3.model.SMcipProtocolType;
import com.paradisecloud.smc3.model.TerminalParam;
import com.paradisecloud.smc3.model.response.UserInfoRep;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class Smc3MeetingRoomRegTask extends Smc3DelayTask {


    private static final Logger LOGGER = LoggerFactory.getLogger(Smc3MeetingRoomRegTask.class);

    public static final String FALSE = "false";
    public static final String KBIT_S = "1920 Kbit/s";
    public static final String VHD = "vhd";
    public static final String PUBLIC = "PUBLIC";

    private BusiTerminal busiTerminal;

    private Long deptId;
    private String uri;

    public Smc3MeetingRoomRegTask(String id, String uri, long delayInMilliseconds,Long deptId,BusiTerminal busiTerminal) {
        super(id, delayInMilliseconds);
        this.busiTerminal=busiTerminal;
        this.deptId=deptId;
        this.uri=uri;
    }

    @Override
    public void run() {

        Smc3Bridge smcBridge = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);
        String result = smcBridge.getSmcMeetingroomsInvoker().getMeetingRoomsByName(busiTerminal.getName(), smcBridge.getSmcportalTokenInvoker().getSystemHeaders());
        LOGGER.info("外部会议室名称查询结果:"+result);
        if(Objects.equals(result, FALSE)){
            MeetingRoomCreateReq meetingRoomReq = new MeetingRoomCreateReq();
            meetingRoomReq.setName(busiTerminal.getName());
            meetingRoomReq.setProvisionEua("true");
            UserInfoRep userInfo = getUserInfo(smcBridge);
            String id = userInfo.getAccount().getOrganization().getId();
            meetingRoomReq.setOrganizationId(id);
            String s = smcBridge.getSmcMeetingroomsInvoker().queryAreaId(smcBridge.getSmcportalTokenInvoker().getSystemHeaders());
            List<QueryAREASResponse> queryAREASResponses = JSONArray.parseArray(s, QueryAREASResponse.class);
            if(CollectionUtils.isNotEmpty(queryAREASResponses)){
                for (QueryAREASResponse queryAREASRespons : queryAREASResponses) {
                   if(queryAREASRespons.getParent()==null){
                       meetingRoomReq.setAreaId(queryAREASRespons.getId());
                       break;
                   }
                }
            }

            Map<String, Object> businessProperties = busiTerminal.getBusinessProperties();
            if (businessProperties == null) {
                businessProperties = new HashMap<>();
            }
            if (businessProperties.containsKey("areaId")) {
                meetingRoomReq.setAreaId((String) businessProperties.get("areaId"));
            }
            if (businessProperties.containsKey("organizationId")) {
                meetingRoomReq.setOrganizationId((String) businessProperties.get("organizationId"));
            }

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
                if(meetingRoomResponse != null){
                    businessProperties.put("room_id",meetingRoomResponse.getId());
                    busiTerminal.setBusinessProperties(businessProperties);
                    BusiTerminalMapper bean = BeanFactory.getBean(BusiTerminalMapper.class);
                    bean.updateBusiTerminal(busiTerminal);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LOGGER.info("外部会议室添加结果:"+res);
        }else {
            LOGGER.info("外部会议室添加失败(名称重复):"+busiTerminal.getName());
        }
    }

    public UserInfoRep getUserInfo(Smc3Bridge bridge) {
        String userInfo = bridge.getSmcUserInvoker().getUserInfo(bridge.getSmcportalTokenInvoker().getUserName(), bridge.getSmcportalTokenInvoker().getSystemHeaders());
        UserInfoRep userInfoRep = JSON.parseObject(userInfo, UserInfoRep.class);
        return  userInfoRep;
    }



}
