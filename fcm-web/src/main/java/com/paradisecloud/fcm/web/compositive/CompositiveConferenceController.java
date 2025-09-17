//package com.paradisecloud.fcm.web.compositive;
//
//import com.alibaba.fastjson.JSON;
//import com.paradisecloud.com.fcm.smc.modle.request.ConferenceStatusRequest;
//import com.paradisecloud.common.core.controller.BaseController;
//import com.paradisecloud.common.core.model.RestResponse;
//import com.paradisecloud.common.model.ModelBean;
//import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
//import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
//import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
//import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
//import com.paradisecloud.fcm.fme.service.interfaces.IBusiFmeDeptService;
//import com.paradisecloud.fcm.web.utils.AuthenticationUtil;
//import com.paradisecloud.smc.service.SmcConferenceService;
//import com.paradisecloud.smc.service.SmcParticipantsService;
//import com.paradisecloud.smc.service.TemplateService;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//
///**
// * @author nj
// * @date 2022/8/25 13:20
// */
////@RestController
////@RequestMapping("/smc/compositive/conference")
//public class CompositiveConferenceController extends BaseController {
//
//
//    @Resource
//    private TemplateService templateService;
//
//    @Resource
//    private IBusiTemplateConferenceService busiTemplateConferenceService;
//
//    @Resource
//    private ITemplateConferenceStartService templateConferenceStartService;
//
//
//    @Resource
//    private IBusiConferenceService busiConferenceService;
//
//    @Resource
//    private SmcConferenceService smcConferenceService;
//
//
//    @Resource
//    private IBusiFmeDeptService iBusiFmeDeptService;
//
//
//    @Resource
//    private SmcParticipantsService smcParticipantsService;
//
//    @Resource
//    private IAttendeeService attendeeService;
//    /**
//     * 模板的 ID 找到会议模板
//     *
//     * @return
//     */
//    @GetMapping("/templates/{id}")
//    public RestResponse getTemplateInfo(@PathVariable Object id) {
//
//        if (id instanceof String) {
//            return RestResponse.success(JSON.parseObject(templateService.getTemplateById(String.valueOf(id)), Object.class));
//        }
//        if (id instanceof Long) {
//            return RestResponse.success(busiTemplateConferenceService.selectBusiTemplateConferenceById(((Long) id)));
//        }
//        return RestResponse.success();
//
//    }
//
////
////    /**
////     * 开始模板会议
////     *
////     * @return
////     */
////    @PostMapping("/templates/start")
////    public RestResponse startConferenceTemplate(@RequestBody StartConferenceRequest startConference) {
////        if (startConference.getTemplateId() != null) {
////            Long cn = templateConferenceStartService.startTemplateConference(startConference.getTemplateId());
////            String cnStr = cn == null ? null : AesEnsUtils.getAesEncryptor().encryptToHex(String.valueOf(cn));
////            return success(cnStr);
////        }
////        if (startConference.getStartConference() != null) {
////            return RestResponse.success(JSON.parseObject(templateService.startConferenceTemplate(startConference.getStartConference()), Object.class));
////        }
////        return RestResponse.success();
////    }
//
//    /**
//     * 结束会议
//     *
//     * @param conferenceId
//     */
//    @DeleteMapping("/delete/{conferenceId}")
//    public void endConference(@PathVariable String conferenceId) {
//        Long deptId = AuthenticationUtil.getDeptId();
//        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
//        if (modelBean.isEmpty()) {
//           smcConferenceService.endConference(conferenceId);
//        }else {
//            busiConferenceService.endConference(conferenceId, 1);
//        }
//    }
//
//
//
//
//
//    /**
//     * 会场一键呼入
//     * @param conferenceId
//     */
//    @PatchMapping("/participants/status/recall")
//    public void changeParticipantsStatus(@RequestParam String conferenceId){
//        Long deptId = AuthenticationUtil.getDeptId();
//        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
//        if(modelBean.isEmpty()){
//            //fme
//            busiConferenceService.reCall(conferenceId);
//        }else {
//            //查询所有与会者
////            String smcConferenceInfo = smcConferenceService.getSmcConferenceInfoById(conferenceId);
////            SmcConferenceContext smcConferenceContext = JSON.parseObject(smcConferenceInfo, SmcConferenceContext.class);
////            List<ParticipantRspDto> participants = smcConferenceContext.getParticipants();
////            for (ParticipantRspDto participant : participants) {
////                participant.setIsOnline(true);
////            }
////            smcParticipantsService.changeParticipantsStatus(conferenceId,participants);
//            ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
//            conferenceStatusRequest.setIsOnline(true);
//            smcConferenceService.statusControl(conferenceId,conferenceStatusRequest);
//
//        }
//    }
//
//    /**
//     * 会场一键静音
//     * @param conferenceId
//     */
//    @PatchMapping("/participants/status/isMute")
//    public void changeParticipantsStatusMute(@RequestParam String conferenceId){
//        Long deptId = AuthenticationUtil.getDeptId();
//        ModelBean modelBean = iBusiFmeDeptService.selectBusiFmeDeptById(deptId);
//        if(modelBean.isEmpty()){
//            //fme
//            busiConferenceService.reCall(conferenceId);
//        }else {
//            smcConferenceService.setMic(conferenceId);
//
//        }
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//}
