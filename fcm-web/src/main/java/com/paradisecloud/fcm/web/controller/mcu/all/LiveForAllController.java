package com.paradisecloud.fcm.web.controller.mcu.all;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.AppType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.vo.EncryptIdVo;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.AttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContextCache;
import com.paradisecloud.fcm.mqtt.interfaces.ITerminalActionService;
import com.paradisecloud.fcm.mqtt.model.TerminalLive;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.smc2.cache.Smc2Bridge;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.TerminalAttendeeSmc2;
import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.TerminalAttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContextCache;
import com.paradisecloud.fcm.tencent.model.client.TencentConferenceCtrlClient;
import com.paradisecloud.fcm.tencent.model.request.ScreenSharedParticipantRequest;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.McuZteWebSocketMessagePusher;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.attendee.TerminalAttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.TerminalAttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.smc.ConfCtrlOperationType;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import com.zte.m900.request.SendParticipantDualVideoRequest;
import com.zte.m900.response.SendParticipantDualVideoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/busi/mcu/all/terminalAction")
@Tag(name = "终端动作控制器")
@Slf4j
public class LiveForAllController {

    @Resource
    private ITerminalActionService terminalActionService;

    /**
     * 获取直播终端列表
     */
    @Log(title = "终端信息")
    @GetMapping("/liveTerminalList")
    @Operation(summary = "通过会议号获取直播终端消息")
    public RestResponse getLiveTerminal(String conferenceId , @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        List<TerminalLive> terminalLiveList = terminalActionService.liveTerminalList(baseConferenceContext);

        PaginationData<TerminalLive> pd = new PaginationData();
        if (terminalLiveList != null && terminalLiveList.size() > 0) {
            pd.setTotal((new PageInfo(terminalLiveList)).getTotal());
            pd.setSize(pageSize);
            pd.setPage(pageNum);

            int count = 0;
            count = terminalLiveList.size();
            int fromIndex = (pageNum - 1) * pageSize;
            int toIndex = pageNum * pageSize;
            if (toIndex > count) {
                toIndex = count;
            }
            if (!(toIndex > terminalLiveList.size() || fromIndex >= terminalLiveList.size())) {
                pd.setRecords(terminalLiveList.subList(fromIndex, toIndex));
            }else {
                pd.setRecords(null);
            }

        } else {
            return RestResponse.fail(0L, "查询失败", pd);
        }
        return RestResponse.success(0L, "查询成功", pd);
    }

    /**
     * 邀请或移除终端直播进入直播或者会议
     * 0===无状态
     * 1===直播
     * 2===会议中
     * @return
     */
    @Log(title = "邀请或移除直播终端")
    @PostMapping("/isJoinLiveTerminal")
    @Operation(summary = "通过会议好获取直播终端消息", description = "修改终端会议直播状态")
    public RestResponse isInviteLiveTerminal(@RequestBody JSONObject params){
        int inviteLiveTerminal = terminalActionService.isInviteLiveTerminal(params.get("mac").toString(),
                params.get("conferenceId").toString(),
                params.get("status").toString()
        );
        return RestResponse.success(inviteLiveTerminal);
    }

    /**
     * 专业终端是否打开辅流
     * @param params
     * @return
     */
    @Log(title = "专业终端是否打开辅流")
    @PostMapping("/isOpenSecondaryStream")
    @Operation(summary = "通过会议好获取直播终端消息", description = "设置终端辅流")
    public RestResponse isOpenSecondaryStream(@RequestBody JSONObject params) {
        String id = params.getString("id");
        boolean isOpen = params.getBoolean("isOpen");
        String conferenceId =(String)params.get("conferenceId");
        String presenter =(String)params.get("presenter");
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(conferenceId);
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuType mcuType = encryptIdVo.getMcuType();
        switch (mcuType) {
            case SMC3 -> {
                Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
                if(Strings.isNotBlank(presenter)){
                    AttendeeSmc3 attendeeSmc3 = conferenceContext.getAttendeeById(presenter);
                    if(attendeeSmc3 != null){
                        if(attendeeSmc3 instanceof TerminalAttendeeSmc3){
                            Long terminalId = attendeeSmc3.getTerminalId();
                            if(terminalId != null){
                                BusiTerminal busiTerminal = TerminalCache.getInstance().get(id);
                                if (busiTerminal != null) {
                                    AppType appType = AppType.convert(busiTerminal.getAppType());
                                    if(appType.isSupportSecondaryStream()){
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if(Strings.isNotBlank(id)){
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(id);
                    if (busiTerminal != null) {
                        AppType appType = AppType.convert(busiTerminal.getAppType());
                        if(appType.isSupportSecondaryStream()){
                            break;
                        }
                    }
                }

                Smc3Bridge smc3Bridge = conferenceContext.getSmc3Bridge();
                JSONObject jsonObject = new JSONObject();
                if(isOpen){
                    jsonObject.put("presenter", presenter);
                }else {
                    jsonObject.put("presenter", "");
                }
                try {
                     smc3Bridge.getSmcConferencesInvoker().conferencesShareControl(conferenceContext.getSmc3conferenceId(), jsonObject.toJSONString(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } catch (Exception e) {
                     log.error("conferencesShareControl error:", e);
                    return RestResponse.fail(e.getMessage());
                }
                return RestResponse.success();
            }
            case SMC2 -> {
                Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);

                if(Strings.isNotBlank(presenter)){
                    AttendeeSmc2 attendee = conferenceContext.getAttendeeById(presenter);
                    if(attendee != null){
                        if(attendee instanceof TerminalAttendeeSmc2){
                            Long terminalId = attendee.getTerminalId();
                            if(terminalId != null){
                                BusiTerminal busiTerminal = TerminalCache.getInstance().get(id);
                                if (busiTerminal != null) {
                                    AppType appType = AppType.convert(busiTerminal.getAppType());
                                    if(appType.isSupportSecondaryStream()){
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if(Strings.isNotBlank(id)){
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(id);
                    if (busiTerminal != null) {
                        AppType appType = AppType.convert(busiTerminal.getAppType());
                        if(appType.isSupportSecondaryStream()){
                            break;
                        }
                    }
                }

                String confId = conferenceContext.getSmc2conferenceId();
                String siteUri ="";
                if(Strings.isNotBlank(presenter)&&isOpen){
                    AttendeeSmc2 attendee = conferenceContext.getAttendeeById(presenter);
                    siteUri=attendee.getRemoteParty();
                    //发送会场辅流。返回结果0成功，非0失败
                    ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                    int resultCode = conferenceServiceEx.setPresentationEx(confId, siteUri, ConfCtrlOperationType.ConfCtrlOpType_Set);
                    if (resultCode == 0) {
                        return RestResponse.success();
                    }else {
                        return RestResponse.fail("会场发送演示失败"+resultCode+"");
                    }
                }else if(Strings.isNotBlank(presenter)&&!isOpen){
                    ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                    AttendeeSmc2 attendee = conferenceContext.getAttendeeById(presenter);
                    siteUri=attendee.getRemoteParty();
                    int resultCode = conferenceServiceEx.setPresentationEx(confId, siteUri, ConfCtrlOperationType.ConfCtrlOpType_Cancel);
                    if (resultCode == 0) {
                        return RestResponse.success();
                    }else {
                        return RestResponse.fail("会场取消演示失败"+resultCode+"");
                    }
                }


            }
            case MCU_TENCENT -> {
                TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);

                if(Strings.isNotBlank(presenter)){
                    AttendeeTencent attendee = conferenceContext.getAttendeeById(presenter);
                    if(attendee != null){
                        if(attendee instanceof TerminalAttendeeTencent){
                            Long terminalId = attendee.getTerminalId();
                            if(terminalId != null){
                                BusiTerminal busiTerminal = TerminalCache.getInstance().get(id);
                                if (busiTerminal != null) {
                                    AppType appType = AppType.convert(busiTerminal.getAppType());
                                    if(appType.isSupportSecondaryStream()){
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if(Strings.isNotBlank(id)){
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(id);
                    if (busiTerminal != null) {
                        AppType appType = AppType.convert(busiTerminal.getAppType());
                        if(appType.isSupportSecondaryStream()){
                            break;
                        }
                    }
                }
                if(isOpen){
                    return RestResponse.fail("不支持开启共享");
                }

                if(!isOpen){
                    TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();
                    AttendeeTencent attendee = conferenceContext.getAttendeeById(presenter);
                    ScreenSharedParticipantRequest request = new ScreenSharedParticipantRequest();
                    request.setMeetingId(conferenceContext.getMeetingId());
                    request.setInstanceid(1);
                    request.setOperatorIdType(4);
                    request.setOperatorId(conferenceContext.getMsopenid());
                    ScreenSharedParticipantRequest.UserDTO usersDTO = new ScreenSharedParticipantRequest.UserDTO();
                    usersDTO.setUuid(attendee.getSmcParticipant().getUuid());
                    usersDTO.setInstanceid(1);
                    request.setUser(usersDTO);
                    try {
                        conferenceCtrlClient.screenSharedClose(request);
                        conferenceContext.setPresentAttendeeId(null);
                        return RestResponse.success();
                    } catch (WemeetSdkException e) {
                        return RestResponse.fail("关闭共享失败"+e.getMessage());
                    }
                }

                break;
            }

            case MCU_HWCLOUD -> {

                HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);

                AttendeeHwcloud attendee = conferenceContext.getAttendeeById(presenter);
                if(attendee!=null){
                    conferenceContext.getHwcloudMeetingBridge().getMeetingControl().share( conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(), conferenceContext.getHwcloudMeetingBridge().getConfID(),isOpen?1:0,attendee.getParticipantUuid());
                }

                return RestResponse.success();
            }

            case MCU_ZTE -> {

                McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
                if(Strings.isNotBlank(presenter)){
                    AttendeeForMcuZte attendeeForMcuZte = conferenceContext.getAttendeeById(presenter);
                    if(attendeeForMcuZte != null){
                        if(attendeeForMcuZte instanceof TerminalAttendeeForMcuZte){
                            Long terminalId = attendeeForMcuZte.getTerminalId();
                            if(terminalId != null){
                                BusiTerminal busiTerminal = TerminalCache.getInstance().get(id);
                                if (busiTerminal != null) {
                                    AppType appType = AppType.convert(busiTerminal.getAppType());
                                    if(appType.isSupportSecondaryStream()){
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if(Strings.isNotBlank(id)){
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(id);
                    if (busiTerminal != null) {
                        AppType appType = AppType.convert(busiTerminal.getAppType());
                        if(appType.isSupportSecondaryStream()){
                            break;
                        }
                    }
                }
                AttendeeForMcuZte attendee = conferenceContext.getAttendeeById(presenter);
                if(attendee!=null){
                    SendParticipantDualVideoRequest sendParticipantDualVideoRequest = new SendParticipantDualVideoRequest();
                    sendParticipantDualVideoRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    sendParticipantDualVideoRequest.setTerminalIdentifier(attendee.getParticipantUuid());
                    sendParticipantDualVideoRequest.setValue(isOpen?1:0);
                    SendParticipantDualVideoResponse sendParticipantDualVideoResponse = conferenceContext.getConferenceControlApi().sendParticipantDualVideo(sendParticipantDualVideoRequest);
                    if(sendParticipantDualVideoResponse!=null&& CommonResponse.STATUS_OK.equals(sendParticipantDualVideoResponse.getStatus())){
                        attendee.setPresentStatus_enable(isOpen?1:2);
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, attendee.getName() + "已"+(isOpen?"允许发送辅流":"禁止发送辅流"));
                        return RestResponse.success();
                    }else {
                        return RestResponse.fail("开启或关闭共享辅流失败");
                    }
                }
                return RestResponse.success();
            }
        }
        if (StringUtils.isEmpty(id)) {
            return RestResponse.fail("该类型终端不支持从会控开启或关闭共享辅流！请从终端开启或关闭辅流。");
        }

        boolean isOpenSecondaryStream = terminalActionService.isOpenSecondaryStream(params.get("conferenceId").toString(), Long.parseLong(id), isOpen);
        return RestResponse.success(isOpenSecondaryStream);
    }
}
