//package com.paradisecloud.fcm.web.compositive;
//
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.paradisecloud.com.fcm.smc.modle.ParticipantReqDto;
//import com.paradisecloud.com.fcm.smc.modle.ParticipantRspDto;
//import com.paradisecloud.com.fcm.smc.modle.ParticipantStatus;
//import com.paradisecloud.com.fcm.smc.modle.SmcConferenceContext;
//import com.paradisecloud.com.fcm.smc.modle.mix.ConferenceControllerRequest;
//import com.paradisecloud.com.fcm.smc.modle.mix.CreateParticipantsReq;
//import com.paradisecloud.com.fcm.smc.modle.request.ConferenceStatusRequest;
//import com.paradisecloud.common.core.controller.BaseController;
//import com.paradisecloud.common.core.model.RestResponse;
//import com.paradisecloud.common.model.ModelBean;
//import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
//import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
//import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
//import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
//import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
//import com.paradisecloud.fcm.fme.service.interfaces.IBusiFmeDeptService;
//import com.paradisecloud.fcm.web.utils.AuthenticationUtil;
//import com.paradisecloud.smc.service.SmcConferenceService;
//import com.paradisecloud.smc.service.SmcParticipantsService;
//import io.swagger.v3.oas.annotations.Operation;
//import org.springframework.util.Assert;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import javax.validation.Valid;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
///**
// * @author nj
// * @date 2022/8/26 9:09
// */
//@RestController
//@RequestMapping("/smc/compositive/participants")
//public class CompositiveParticipantsController extends BaseController {
//
//    @Resource
//    private IAttendeeService attendeeService;
//    @Resource
//    private IBusiFmeDeptService iBusiFmeDeptService;
//    @Resource
//    private SmcConferenceService smcConferenceService;
//    @Resource
//    private SmcParticipantsService smcParticipantsService;
//
//    /**
//     * 参会者重呼
//     */
//    @PostMapping("/recall")
//    @Operation(summary = "参会者重呼")
//    public RestResponse recall(@RequestBody ConferenceControllerRequest conferenceControllerRequest)
//    {
//        Long deptId = AuthenticationUtil.getDeptId();
//        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
//        String participantId = conferenceControllerRequest.getParticipantId();
//        String conferenceId = conferenceControllerRequest.getConferenceId();
//        if(modelBean.isEmpty()){
//            //fme
//            attendeeService.recall(conferenceId, participantId);
//
//        } else {
//            ParticipantStatus participantStatus = new ParticipantStatus();
//            participantStatus.setIsOnline(true);
//            participantStatus.setId(participantId);
//            smcParticipantsService.changeParticipantStatusOnly(conferenceId, participantId, participantStatus);
//        }
//
//        return success();
//    }
//
//
//    /**
//     * 邀请与会者
//     *
//     * @param createParticipantsReq
//     */
//    @PostMapping("/add/participants")
//    public void addParticipants(@RequestBody CreateParticipantsReq createParticipantsReq) {
//        Long deptId = AuthenticationUtil.getDeptId();
//        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
//        if (modelBean.isEmpty()) {
//            //fme
//            String conferenceId = createParticipantsReq.getConferenceId();
//            Assert.isTrue(conferenceId != null, "会议ID不能为空");
//            List<ParticipantReqDto> participants = createParticipantsReq.getParticipants();
//            Assert.isTrue(participants != null, "与会者不能为空");
//            participants.forEach(p -> {
//                JSONObject jsonObj = new JSONObject();
//                jsonObj.put("name", p.getName());
//                jsonObj.put("uri", p.getUri());
//                jsonObj.put("conferenceId", conferenceId);
//                attendeeService.invite(conferenceId, jsonObj);
//            });
//
//        } else {
//            smcParticipantsService.addParticipants(createParticipantsReq);
//        }
//    }
//
//
//    /**
//     * 挂断
//     *
//     * @param conferenceControllerRequest
//     */
//    @PutMapping("/hangup")
//    public RestResponse hangUpParticipants(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {
//        Long deptId = AuthenticationUtil.getDeptId();
//        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
//        String participantId = conferenceControllerRequest.getParticipantId();
//        String conferenceId = conferenceControllerRequest.getConferenceId();
//        if(modelBean.isEmpty()){
//            //fme
//            attendeeService.hangUp(conferenceId, participantId);
//
//        }else {
//            ParticipantStatus participantStatus=new ParticipantStatus();
//            participantStatus.setIsOnline(false);
//            participantStatus.setId(participantId);
//            smcParticipantsService.changeParticipantStatusOnly(conferenceId,participantId,participantStatus);
//        }
//        return success();
//    }
//
//    /**
//     * 移除与会者
//     * @param conferenceControllerRequest
//     */
//    @DeleteMapping("/delete")
//    public RestResponse delete(@RequestBody  ConferenceControllerRequest conferenceControllerRequest){
//        Long deptId = AuthenticationUtil.getDeptId();
//        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
//        String participantId = conferenceControllerRequest.getParticipantId();
//        String conferenceId = conferenceControllerRequest.getConferenceId();
//        if(modelBean.isEmpty()){
//            //fme
//            attendeeService.remove(conferenceId, participantId);
//        }else {
//
//            List<String> participantIds = new ArrayList<>();
//            participantIds.add(participantId);
//            smcParticipantsService.delete(conferenceId,participantIds);
//        }
//
//        return success();
//    }
//
//    /**
//     * 摄像头开启
//     * @param conferenceControllerRequest
//     */
//    @PatchMapping("/videoMuteOpenOne")
//    public RestResponse setVideoMuteOPenOne(@RequestBody  ConferenceControllerRequest conferenceControllerRequest){
//        Long deptId = AuthenticationUtil.getDeptId();
//        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
//        String participantId = conferenceControllerRequest.getParticipantId();
//        String conferenceId = conferenceControllerRequest.getConferenceId();
//        if(modelBean.isEmpty()){
//            //fme
//
//                attendeeService.openCamera(conferenceId, participantId);
//        }else {
//            ParticipantStatus participantStatus=new ParticipantStatus();
//            participantStatus.setIsVideoMute(true);
//            participantStatus.setId(participantId);
//            smcParticipantsService.changeParticipantStatusOnly(conferenceId,participantId, participantStatus);
//        }
//        return success();
//    }
//
//    /**
//     * 摄像头开启
//     * @param conferenceControllerRequest
//     */
//    @PatchMapping("/videoMuteCloseOne")
//    public RestResponse videoMuteCloseOne(@RequestBody  ConferenceControllerRequest conferenceControllerRequest){
//        Long deptId = AuthenticationUtil.getDeptId();
//        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
//        String participantId = conferenceControllerRequest.getParticipantId();
//        String conferenceId = conferenceControllerRequest.getConferenceId();
//        if(modelBean.isEmpty()){
//            //fme
//                attendeeService.closeCamera(conferenceId, participantId);
//        }else {
//            ParticipantStatus participantStatus=new ParticipantStatus();
//            participantStatus.setIsVideoMute(false);
//            participantStatus.setId(participantId);
//            smcParticipantsService.changeParticipantStatusOnly(conferenceId,participantId, participantStatus);
//        }
//        return success();
//    }
//
//    /**
//     * 麦克风 静音
//     * @param conferenceControllerRequest
//     */
//    @PatchMapping("/muteCloseOne")
//    public RestResponse muteCloseOne(@RequestBody  ConferenceControllerRequest conferenceControllerRequest){
//        Long deptId = AuthenticationUtil.getDeptId();
//        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
//        String participantId = conferenceControllerRequest.getParticipantId();
//        String conferenceId = conferenceControllerRequest.getConferenceId();
//        if(modelBean.isEmpty()){
//            //fme
//            attendeeService.closeMixing(conferenceId, participantId);
//        }else {
//            ParticipantStatus participantStatus=new ParticipantStatus();
//            participantStatus.setIsMute(true);
//            participantStatus.setId(participantId);
//            smcParticipantsService.changeParticipantStatusOnly(conferenceId,participantId, participantStatus);
//        }
//        return success();
//    }
//
//    /**
//     * 麦克风 打开
//     * @param conferenceControllerRequest
//     */
//    @PatchMapping("/muteOpenOne")
//    public RestResponse muteOpenOne(@RequestBody  ConferenceControllerRequest conferenceControllerRequest){
//        Long deptId = AuthenticationUtil.getDeptId();
//        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
//        String participantId = conferenceControllerRequest.getParticipantId();
//        String conferenceId = conferenceControllerRequest.getConferenceId();
//        if(modelBean.isEmpty()){
//            //fme
//            attendeeService.openMixing(conferenceId, participantId);
//        }else {
//            ParticipantStatus participantStatus=new ParticipantStatus();
//            participantStatus.setIsMute(false);
//            participantStatus.setId(participantId);
//            smcParticipantsService.changeParticipantStatusOnly(conferenceId,participantId, participantStatus);
//        }
//        return success();
//    }
//
//    /**
//     *打开扬声器
//     * @param conferenceControllerRequest
//     */
//    @PatchMapping("/quietOpenOne")
//    public RestResponse quietOpenOne(@RequestBody  ConferenceControllerRequest conferenceControllerRequest){
//        Long deptId = AuthenticationUtil.getDeptId();
//        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
//        String participantId = conferenceControllerRequest.getParticipantId();
//        String conferenceId = conferenceControllerRequest.getConferenceId();
//        if(modelBean.isEmpty()){
//            //fme
//            attendeeService.openMixing(conferenceId, participantId);
//        }else {
//            ParticipantStatus participantStatus=new ParticipantStatus();
//            participantStatus.setIsQuiet(true);
//            participantStatus.setId(participantId);
//            smcParticipantsService.changeParticipantStatusOnly(conferenceId,participantId, participantStatus);
//        }
//        return success();
//    }
//
//    /**
//     * 关闭扬声器
//     * @param conferenceControllerRequest
//     */
//    @PatchMapping("/quietCloseOne")
//    public RestResponse quietCloseOne(@RequestBody  ConferenceControllerRequest conferenceControllerRequest){
//        Long deptId = AuthenticationUtil.getDeptId();
//        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
//        String participantId = conferenceControllerRequest.getParticipantId();
//        String conferenceId = conferenceControllerRequest.getConferenceId();
//        if(modelBean.isEmpty()){
//            //fme
//            attendeeService.closeMixing(conferenceId, participantId);
//        } else {
//            ParticipantStatus participantStatus = new ParticipantStatus();
//            participantStatus.setIsQuiet(false);
//            participantStatus.setId(participantId);
//            smcParticipantsService.changeParticipantStatusOnly(conferenceId, participantId, participantStatus);
//        }
//        return success();
//    }
//
////    /**
////     * 点名
////     */
////    @PostMapping("/callTheRoll")
////    @Operation(summary = "点名")
////    public RestResponse callTheRoll(@Valid @RequestBody ConferenceControllerRequest callTheRollRequest)
////    {
////
////        Long deptId = AuthenticationUtil.getDeptId();
////        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
////        String participantId = callTheRollRequest.getParticipantId();
////        String conferenceId = callTheRollRequest.getConferenceId();
////        if(modelBean.isEmpty()){
////            //fme
////            attendeeService.callTheRoll(conferenceId, participantId);
////        }else {
////            ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
////            conferenceStatusRequest.setSpokesman(participantId);
////            smcConferenceService.statusControl( conferenceId,conferenceStatusRequest);
////        }
////        return success();
////    }
//
//    /**
//     * 点名
//     */
//    @PostMapping("/callTheRoll")
//    @Operation(summary = "点名")
//    public RestResponse callTheRoll(@Valid @RequestBody ConferenceControllerRequest callTheRollRequest) {
//
//        Long deptId = AuthenticationUtil.getDeptId();
//        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
//        String participantId = callTheRollRequest.getParticipantId();
//        String conferenceId = callTheRollRequest.getConferenceId();
//        if(modelBean.isEmpty()){
//            //fme
//            attendeeService.callTheRoll(conferenceId, participantId);
//        } else {
//            //确保主会场为smc会议id
//            String smcConferenceInfo = smcConferenceService.getSmcConferenceInfoById(conferenceId);
//            SmcConferenceContext smcConferenceContext = JSON.parseObject(smcConferenceInfo, SmcConferenceContext.class);
//
//            String accessCode = smcConferenceContext.getMultiConferenceService().getAccessCode();
//            // String uri = accessCode + "@" + SmcBridgeCache.getInstance().getSmcBridgeByDeptId(AuthenticationUtil.getDeptId()).getScUrl();
//            //找到与会者 固定模板会议号
//            List<ParticipantRspDto> participants = smcConferenceContext.getParticipants();
//            ParticipantRspDto participantRspDto = participants.stream().filter(p -> Objects.equals(p.getId(), participantId)).findFirst().get();
//            String conferenceNumber = participantRspDto.getUri().substring(0, participants.indexOf("@"));
//            String conferenceIdFme = conferenceNumber == null ? null : AesEnsUtils.getAesEncryptor().encryptToHex(String.valueOf(conferenceNumber));
//            //在fme的participantId;
//            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(conferenceIdFme);
//            List<Attendee> attendees = conferenceContext.getAttendees();
//            Attendee attendee = attendees.stream().filter(p -> Objects.equals(p.getName(), (accessCode))).findAny().get();
//            attendeeService.changeMaster(conferenceIdFme, attendee.getId());
//            attendeeService.callTheRoll(conferenceIdFme, participantId);
//
//            ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
//            conferenceStatusRequest.setSpokesman(participantRspDto.getId());
//            smcConferenceService.statusControl(conferenceId, conferenceStatusRequest);
//        }
//        return success();
//    }
//
//
//    private Boolean isParticipantId(ConferenceControllerRequest callTheRollRequest) {
//        String participantId = callTheRollRequest.getParticipantId();
//        String conferenceId = callTheRollRequest.getConferenceId();
//        List<ParticipantRspDto> participants = getParticipants(conferenceId);
//        if (CollectionUtils.isEmpty(participants)) {
//            return false;
//        }
//        Optional<ParticipantRspDto> first = participants.stream().filter(p -> Objects.equals(p.getId(), participantId)).findFirst();
//        if (first.isPresent()) {
//            return true;
//        }
//        return false;
//    }
//
//
//    /**根据FME ParticipantId找到会议
//     * @param participantId
//     * @param conferenceId  smc会议号码
//     */
//    public ConferenceContext getConferenceContext(String participantId, String conferenceId) {
//        List<ParticipantRspDto> participants = getParticipants(conferenceId);
//        Optional<ParticipantRspDto> any = participants.stream().filter(p -> p.getUri().contains("@")).findAny();
//        if (any.isPresent()) {
//            List<ParticipantRspDto> participantRspDtos = any.stream().collect(Collectors.toList());
//            for (ParticipantRspDto participantRspDto : participantRspDtos) {
//                String conferenceNumber = participantRspDto.getUri().substring(0, participants.indexOf("@"));
//                String conferenceIdFme = conferenceNumber == null ? null : AesEnsUtils.getAesEncryptor().encryptToHex(conferenceNumber);
//                //在fme的participantId;
//                ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(conferenceIdFme);
//                if (!Objects.isNull(conferenceContext)) {
//                    List<Attendee> attendees = conferenceContext.getAttendees();
//                    Optional<Attendee> attendeeOptional = attendees.stream().filter(p -> Objects.equals(p.getId(), participantId)).findAny();
//                    if(attendeeOptional.isPresent()){
//                        return conferenceContext;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    /**根据conferenceId 找到FME会议列表
//     * @param conferenceId  smc会议号码
//     */
//    public List<ConferenceContext> getConferenceContextList(String conferenceId) {
//        List<ConferenceContext> conferenceContextList = new ArrayList<>();
//        List<ParticipantRspDto> participants = getParticipants(conferenceId);
//        Optional<ParticipantRspDto> any = participants.stream().filter(p -> p.getUri().contains("@")).findAny();
//        if (any.isPresent()) {
//            List<ParticipantRspDto> participantRspDtos = any.stream().collect(Collectors.toList());
//            for (ParticipantRspDto participantRspDto : participantRspDtos) {
//                String conferenceNumber = participantRspDto.getUri().substring(0, participants.indexOf("@"));
//                String conferenceIdFme = conferenceNumber == null ? null : AesEnsUtils.getAesEncryptor().encryptToHex(conferenceNumber);
//                ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(conferenceIdFme);
//                if (!Objects.isNull(conferenceContext)) {
//                    conferenceContextList.add(conferenceContext);
//                }
//            }
//        }
//        return conferenceContextList;
//    }
//
//    /**
//     * 获取smc会议号
//     * @param conferenceId
//     * @return
//     */
//    public String getAccessCode(String conferenceId){
//        String smcConferenceInfo = smcConferenceService.getSmcConferenceInfoById(conferenceId);
//        SmcConferenceContext smcConferenceContext = JSON.parseObject(smcConferenceInfo, SmcConferenceContext.class);
//        String accessCode = smcConferenceContext.getMultiConferenceService().getAccessCode();
//        return accessCode;
//    }
//
//    /**
//     * 获取主场ID
//     * @param conferenceId
//     * @return
//     */
//    public  List<Attendee> getMasterAttendee(String conferenceId) {
//        List<ConferenceContext> conferenceContextList = getConferenceContextList(conferenceId);
//        if(!CollectionUtils.isEmpty(conferenceContextList)){
//            for (ConferenceContext conferenceContext : conferenceContextList) {
//                //if()  todo
//            }
//        }
//        return null;
//    }
//
//
//    public List<ParticipantRspDto> getParticipants(String conferenceId) {
//        String smcConferenceInfo = smcConferenceService.getSmcConferenceInfoById(conferenceId);
//        SmcConferenceContext smcConferenceContext = JSON.parseObject(smcConferenceInfo, SmcConferenceContext.class);
//        return smcConferenceContext.getParticipants();
//    }
//
//
//
//    /**
//     * 点名
//     */
//    @PostMapping("/callTheRoll2")
//    @Operation(summary = "点名")
//    public RestResponse callTheRoll2(@Valid @RequestBody ConferenceControllerRequest callTheRollRequest) {
//        String participantId = callTheRollRequest.getParticipantId();
//        String conferenceId = callTheRollRequest.getConferenceId();
//
//        Long deptId = AuthenticationUtil.getDeptId();
//        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
//
//        if(!modelBean.isEmpty()){
//            //fme
//            attendeeService.callTheRoll(conferenceId, participantId);
//        } else {
//            //确保主会场为smc会议id
//            String smcConferenceInfo = smcConferenceService.getSmcConferenceInfoById(conferenceId);
//            SmcConferenceContext smcConferenceContext = JSON.parseObject(smcConferenceInfo, SmcConferenceContext.class);
//
//            String accessCode = smcConferenceContext.getMultiConferenceService().getAccessCode();
//            // String uri = accessCode + "@" + SmcBridgeCache.getInstance().getSmcBridgeByDeptId(AuthenticationUtil.getDeptId()).getScUrl();
//            //找到与会者 固定模板会议号
//            List<ParticipantRspDto> participants = smcConferenceContext.getParticipants();
//            ParticipantRspDto participantRspDto = participants.stream().filter(p -> Objects.equals(p.getId(), participantId)).findFirst().get();
//            String conferenceNumber = participantRspDto.getUri().substring(0, participants.indexOf("@"));
//            String conferenceIdFme = conferenceNumber == null ? null : AesEnsUtils.getAesEncryptor().encryptToHex(String.valueOf(conferenceNumber));
//            //在fme的participantId;
//            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(conferenceIdFme);
//            List<Attendee> attendees = conferenceContext.getAttendees();
//            Attendee attendee = attendees.stream().filter(p -> Objects.equals(p.getName(), (accessCode))).findAny().get();
//            attendeeService.changeMaster(conferenceIdFme, attendee.getId());
//            attendeeService.callTheRoll(conferenceIdFme, participantId);
//
//            ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
//            conferenceStatusRequest.setSpokesman(participantRspDto.getId());
//            smcConferenceService.statusControl(conferenceId, conferenceStatusRequest);
//        }
//        return success();
//    }
//
//
//
//}
