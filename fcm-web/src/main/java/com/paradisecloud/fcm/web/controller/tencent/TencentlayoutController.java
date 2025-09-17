package com.paradisecloud.fcm.web.controller.tencent;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.tencent.cache.TencentBridge;
import com.paradisecloud.fcm.tencent.cache.TencentBridgeCache;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContextCache;
import com.paradisecloud.fcm.tencent.model.client.TencentLayoutClient;
import com.paradisecloud.fcm.tencent.model.reponse.AddMeetingLayoutReponse;
import com.paradisecloud.fcm.tencent.model.reponse.LayoutTemplatesResponse;
import com.paradisecloud.fcm.tencent.model.reponse.MeetingAdvancedLayoutResponse;
import com.paradisecloud.fcm.tencent.model.reponse.MeetingBackgroundResponse;
import com.paradisecloud.fcm.tencent.model.request.layout.*;
import com.paradisecloud.system.utils.SecurityUtils;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2023/7/14 14:57
 */
@RestController
@RequestMapping("/tencent/layout")
@Slf4j
public class TencentlayoutController extends BaseController {

    /**
     * 查询账户布局模板列表
     */
    @GetMapping("/templates")
    @Operation(summary = "查询账户布局模板列表")
    public RestResponse layoutTemplates() {
        Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
        TencentBridge tencentBridge = null;
        if (deptId == null) {
            Map<Long, TencentBridge> tencentBridgeMap = TencentBridgeCache.getInstance().getTencentBridgeMap();
            for (TencentBridge value : tencentBridgeMap.values()) {
                if (value.isAvailable()) {
                    tencentBridge = value;
                    break;
                }
            }
        } else {
            tencentBridge = TencentBridgeCache.getInstance().getAvailableBridgesByDept(deptId);
        }
        TencentLayoutClient layout_client = tencentBridge.getLAYOUT_CLIENT();
        QueryLayoutTemplatesAllRequest request = new QueryLayoutTemplatesAllRequest();
        try {
            LayoutTemplatesResponse layoutTemplatesResponse = layout_client.queryLayoutTemplates(request);
            return RestResponse.success(layoutTemplatesResponse);
        } catch (WemeetSdkException e) {
           logger.info(e.getMessage());
        }

        return RestResponse.fail();
    }

    /**
     * 查询账户布局模板列表
     */
    @GetMapping("/templates/{conferenceId}")
    @Operation(summary = "查询账户布局模板列表")
    public RestResponse layoutTemplates(@PathVariable String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }
        TencentBridge tencentBridge = tencentConferenceContext.getTencentBridge();
        TencentLayoutClient layout_client = tencentBridge.getLAYOUT_CLIENT();
        QueryLayoutTemplatesAllRequest request = new QueryLayoutTemplatesAllRequest();
        try {
            LayoutTemplatesResponse layoutTemplatesResponse = layout_client.queryLayoutTemplates(request);
            return RestResponse.success(layoutTemplatesResponse);
        } catch (WemeetSdkException e) {
            logger.info(e.getMessage());
        }

        return RestResponse.fail();
    }

    @GetMapping("/advancedLayouts/{conferenceId}")
    @Operation(summary = "查询会议布局列表(高级)")
    public RestResponse queryAdvancedLayouts(@PathVariable String conferenceId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }

        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();
        QueryMeetingLayoutListAdvancedRequest request = new QueryMeetingLayoutListAdvancedRequest();
        request.setInstanceid(1);
        request.setOperatorIdType(1);
        request.setMeetingId(tencentConferenceContext.getMeetingId());
        request.setOperatorId(tencentConferenceContext.getTencentUser());
        try {
            MeetingAdvancedLayoutResponse meetingAdvancedLayoutResponse = layout_client.queryMeetingLayoutsAdvanced(request);
            return RestResponse.success(meetingAdvancedLayoutResponse);
        } catch (WemeetSdkException e) {
           log.info(e.getMessage());
        }
        return RestResponse.fail();
    }

    @GetMapping("/meetingLayouts/{meetingId}")
    @Operation(summary = "查询会议布局列表")
    public RestResponse queryMeetingLayouts(@PathVariable String conferenceId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }
        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();
        QueryMeetingLayoutListRequest request = new QueryMeetingLayoutListRequest();
        request.setInstanceid(1);
        request.setMeetingId(tencentConferenceContext.getMeetingId());
        request.setUserid(tencentConferenceContext.getTencentUser());
        try {
            MeetingAdvancedLayoutResponse meetingAdvancedLayoutResponse = layout_client.queryMeetingLayouts(request);
            return RestResponse.success(meetingAdvancedLayoutResponse);
        } catch (WemeetSdkException e) {
           log.info(e.getMessage());
        }
        return RestResponse.fail();
    }


    @PostMapping("/add/meetingLayouts/{conferenceId}")
    @Operation(summary = "添加会议布局")
    public RestResponse addMeetingLayouts(@PathVariable String conferenceId, @RequestBody AddMeetingLayoutRequest addMeetingLayoutRequest) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }
        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();
        addMeetingLayoutRequest.setInstanceid(1);
        addMeetingLayoutRequest.setMeetingId(tencentConferenceContext.getMeetingId());
        addMeetingLayoutRequest.setUserid(tencentConferenceContext.getTencentUser());
        AddMeetingLayoutRequest request = new AddMeetingLayoutRequest();
        request.setInstanceid(1);
        request.setMeetingId(tencentConferenceContext.getMeetingId());
        request.setUserid(tencentConferenceContext.getTencentUser());
        request.setLayoutList(addMeetingLayoutRequest.getLayoutList());
        try {
            AddMeetingLayoutReponse addMeetingLayoutReponse = layout_client.addMeetingLayout(request);
            return RestResponse.success(addMeetingLayoutReponse);
        } catch (WemeetSdkException e) {
           log.info(e.getMessage());
            return RestResponse.fail(e.getMessage());
        }
    }


    /**
     * 对当前会议添加高级自定义布局，支持批量添加。
     * 用户座次设置需设置参会成员。
     * 单个会议最多允许添加20个布局。
     * 目前暂不支持 OAuth2.0 鉴权访问。
     * 目前仅会应用于 H.323/SIP 终端。
     *
     * @param conferenceId
     * @param addMeetingAdancedLayoutRequest
     * @return
     */
    @PostMapping("/add/advancedMeetingLayouts/{conferenceId}")
    @Operation(summary = "添加自定义布局")
    public RestResponse addAdvancedMeetingLayouts(@PathVariable String conferenceId, @RequestBody AddMeetingAdancedLayoutRequest addMeetingAdancedLayoutRequest) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }
        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();
        addMeetingAdancedLayoutRequest.setInstanceid(1);
        addMeetingAdancedLayoutRequest.setMeetingId(tencentConferenceContext.getMeetingId());
        AddMeetingAdancedLayoutRequest request = new AddMeetingAdancedLayoutRequest();
        request.setInstanceid(1);
        request.setMeetingId(tencentConferenceContext.getMeetingId());
        request.setOperatorId(tencentConferenceContext.getMsopenid());
        request.setOperatorIdType(4);
        request.setLayoutList(addMeetingAdancedLayoutRequest.getLayoutList());
        try {
            layout_client.addAdvancedLayout(request);
            return RestResponse.success();
        } catch (WemeetSdkException e) {
           log.info(e.getMessage());
        }
        return RestResponse.fail();
    }


    @PutMapping("/change/meetingLayouts/{conferenceId}")
    @Operation(summary = "修改会议布局")
    public RestResponse changeMeetingLayouts(@PathVariable String conferenceId, @RequestBody ChangeMeetingLayoutRequest changeMeetingLayoutRequest) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }
        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();
        changeMeetingLayoutRequest.setInstanceid(1);
        changeMeetingLayoutRequest.setMeetingId(tencentConferenceContext.getMeetingId());
        ChangeMeetingLayoutRequest request = new ChangeMeetingLayoutRequest();
        request.setInstanceid(1);
        request.setMeetingId(tencentConferenceContext.getMeetingId());
        request.setLayoutId(changeMeetingLayoutRequest.getLayoutId());
        request.setUserid(tencentConferenceContext.getTencentUser());
        request.setPageList(changeMeetingLayoutRequest.getPageList());
        try {
            layout_client.changeMeetingLayout(request);
            return RestResponse.success();
        } catch (WemeetSdkException e) {
           log.info(e.getMessage());
        }
        return RestResponse.fail();
    }

    /**
     * 对会议中的布局进行修改，注意修改的是布局定义。
     * 若修改的会议布局正被会议使用，新布局会自动应用到会议。
     * 若修改的会议布局正在被用户使用，新布局不会自动应用到用户。
     * 接口仅支持全量更新，不支持部分字段单独更新。
     * 目前暂不支持 OAuth2.0 鉴权访问。
     * 目前仅会应用于 H.323/SIP 终端。
     * @param conferenceId
     * @param changeMeetingLayoutRequest
     * @return
     */
    @PutMapping("/change/advancedMeetingLayouts/{conferenceId}")
    @Operation(summary = "修改单个布局")
    public RestResponse changeADMeetingLayouts(@PathVariable String conferenceId, @RequestBody ChangeAdancedMeetingLayoutRequest changeMeetingLayoutRequest) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }
        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();
        changeMeetingLayoutRequest.setInstanceid(1);
        changeMeetingLayoutRequest.setMeetingId(tencentConferenceContext.getMeetingId());
        ChangeAdancedMeetingLayoutRequest request = new ChangeAdancedMeetingLayoutRequest();
        request.setInstanceid(1);
        request.setMeetingId(tencentConferenceContext.getMeetingId());
        request.setLayoutId(changeMeetingLayoutRequest.getLayoutId());
        request.setOperatorIdType(4);
        request.setOperatorId(tencentConferenceContext.getMsopenid());
        List<ChangeAdancedMeetingLayoutRequest.PageListDTO> pageList = changeMeetingLayoutRequest.getPageList();
        request.setPageList(pageList);

        try {
            layout_client.changeAdMeetingLayout(request);
            return RestResponse.success();
        } catch (WemeetSdkException e) {
           log.info(e.getMessage());
        }
        return RestResponse.fail();
    }

    @DeleteMapping("/delete/meetingLayouts/{conferenceId}/{layoutId}")
    @Operation(summary = "删除会议布局")
    public RestResponse deleteMeetingLayouts(@PathVariable String conferenceId, @PathVariable String layoutId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }

        DeleteMeetingLayoutRequest deleteMeetingLayoutRequest = new DeleteMeetingLayoutRequest();
        deleteMeetingLayoutRequest.setMeeting_id(tencentConferenceContext.getMeetingId());
        deleteMeetingLayoutRequest.setLayout_id(layoutId);
        deleteMeetingLayoutRequest.setUserid(tencentConferenceContext.getTencentUser());
        deleteMeetingLayoutRequest.setInstanceid(1);
        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();

        try {
            layout_client.deleteMeetingLayout(deleteMeetingLayoutRequest);
            return RestResponse.success();
        } catch (WemeetSdkException e) {
           log.info(e.getMessage());
        }
        return RestResponse.fail();
    }


    @PostMapping("/delete/meetingLayouts/batch/{conferenceId}")
    @Operation(summary = "批量删除会议布局")
    public RestResponse deleteMeetingLayouts(@PathVariable String conferenceId,@RequestBody List<String> layoutIdList) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }

        DeleteMeetingLayoutBatchRequest deleteMeetingLayoutBatchRequest = new DeleteMeetingLayoutBatchRequest();
        deleteMeetingLayoutBatchRequest.setMeeting_id(tencentConferenceContext.getMeetingId());
        deleteMeetingLayoutBatchRequest.setUserid(tencentConferenceContext.getTencentUser());
        deleteMeetingLayoutBatchRequest.setInstanceid(1);
        deleteMeetingLayoutBatchRequest.setLayout_id_list(layoutIdList);
        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();

        try {
            layout_client.deleteMeetingLayoutBatch(deleteMeetingLayoutBatchRequest);
            return RestResponse.success();
        } catch (WemeetSdkException e) {
           log.info(e.getMessage());
        }
        return RestResponse.fail();
    }

    /**
     * 根据布局 ID 批量删除布局。
     * 正在被应用的布局无法删除，请先设置成其他布局或恢复成默认原始布局后再行删除。
     * 接口不做布局是否存在的校验，删除不存在的布局不会有提示。
     * 目前暂不支持 OAuth2.0 鉴权访问。
     * 目前仅会应用于 H.323/SIP 终端。
     * 批量删除布局(删除自定义布局)
     * @param conferenceId
     * @param layoutIdList
     * @return
     */
    @PostMapping("/delete/meetingLayouts/advanced/batch/{conferenceId}")
    @Operation(summary = "批量删除布局")
    public RestResponse deleteAdvancedMeetingLayouts(@PathVariable String conferenceId,@RequestBody List<String> layoutIdList) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }

        DeleteMeetingLayoutADBatchRequest deleteAdBatch = new DeleteMeetingLayoutADBatchRequest();
        deleteAdBatch.setMeeting_id(tencentConferenceContext.getMeetingId());
        deleteAdBatch.setOperator_id(tencentConferenceContext.getTencentUser());
        deleteAdBatch.setOperator_id_type(1);
        deleteAdBatch.setInstanceid(1);
        deleteAdBatch.setLayout_id_list(layoutIdList);
        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();

        try {
            layout_client.deleteMeetingLayoutADBatch(deleteAdBatch);
            return RestResponse.success();
        } catch (WemeetSdkException e) {
           log.info(e.getMessage());
        }
        return RestResponse.fail();
    }

    /**
     * 应用布局
     * 将会议中的高级自定义布局应用到指定成员或者整个会议。
     * 恢复指定成员或整个会议的默认布局。
     * 目前暂不支持 OAuth2.0 鉴权访问。
     * 目前仅会应用于 H.323/SIP 终端。
     * @param conferenceId
     * @param layoutId
     * @return
     */
    @PutMapping("/apply/meetingLayouts/{conferenceId}/{layoutId}")
    @Operation(summary = "应用布局")
    public RestResponse addMeetingLayouts(@PathVariable String conferenceId,@PathVariable(required = false) String layoutId,@RequestBody(required = false) List<ApplyingLayoutRequest.UserListMsopenIdDto> user_list) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }

        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();

        ApplyingLayoutRequest request = new ApplyingLayoutRequest();
        request.setInstanceid(1);
        request.setMeetingId(tencentConferenceContext.getMeetingId());
        request.setOperatorIdType(1);
        request.setOperatorId(tencentConferenceContext.getTencentUser());
        if(Strings.isNotBlank(layoutId)){
            request.setLayout_id(layoutId);
        }else {
            request.setLayout_id("");
        }
        if(!CollectionUtils.isEmpty(user_list)){
            request.setUser_list(user_list);
        }
        try {
            layout_client.applyingLayout(request);
            return RestResponse.success();
        } catch (WemeetSdkException e) {
           log.info(e.getMessage());
        }
        return RestResponse.fail();
    }

    @PutMapping("/apply/meetingLayouts/{conferenceId}")
    @Operation(summary = "取消应用布局")
    public RestResponse applyLayout(@PathVariable String conferenceId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }

        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();

        ApplyingLayoutRequest request = new ApplyingLayoutRequest();
        request.setInstanceid(1);
        request.setMeetingId(tencentConferenceContext.getMeetingId());
        request.setOperatorIdType(1);
        request.setOperatorId(tencentConferenceContext.getTencentUser());
        request.setLayout_id("");
        try {
            layout_client.applyingLayout(request);
            return RestResponse.success();
        } catch (WemeetSdkException e) {
            log.info(e.getMessage());
        }
        return RestResponse.fail();
    }


    /**
     * 对成功预定的会议添加会议背景，支持多个背景图片的添加。一场会议最多添加7个背景，且仅支持不超过10MB大小的 PNG 格式图片，分辨率最小为1920*1080。添加成功返回新增的会议背景信息。目前暂不支持 OAuth2.0 鉴权访问。
     * 背景图片上传方式为异步上传，您可以通过订阅 素材上传结果 获得上传结果通知
     * @param conferenceId
     * @param image_list
     * @return
     */
    @PostMapping("/add/backgrounds/{conferenceId}")
    @Operation(summary = "添加会议背景")
    public RestResponse addbackgrounds(@PathVariable String conferenceId,@RequestBody List<AddMeetingBackgrouds.ImageListDTO> image_list) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }
        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();

        AddMeetingBackgrouds request = new AddMeetingBackgrouds();
        request.setInstanceid(1);
        request.setMeetingId(tencentConferenceContext.getMeetingId());
        request.setImage_list(image_list);
        request.setUserid(tencentConferenceContext.getTencentUser());
        request.setDefault_image_order(1);
        try {
            layout_client.addMeetingBackground(request);
            return RestResponse.success();
        } catch (WemeetSdkException e) {
           log.info(e.getMessage());
        }
        return RestResponse.fail();
    }


    /**
     * 设置会议默认背景
     * @param conferenceId
     * @return
     */
    @PutMapping("/set/defaultBackgrounds/{conferenceId}")
    @Operation(summary = "设置会议默认背景")
    public RestResponse setDefaultBackgrounds(@PathVariable String conferenceId,@RequestBody(required = false) String selected_background_id) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }
        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();

        SetingDefaultBackgrounds request = new SetingDefaultBackgrounds();
        request.setInstanceid(1);
        request.setMeetingId(tencentConferenceContext.getMeetingId());
        request.setUserid(tencentConferenceContext.getTencentUser());
        request.setSelected_background_id(selected_background_id);
        try {
            layout_client.addDefaultGrounds(request);
            return RestResponse.success();
        } catch (WemeetSdkException e) {
           log.info(e.getMessage());
        }
        return RestResponse.fail();
    }

    /**
     * 根据背景 ID 删除单个会议背景。正在被会议应用的背景无法删除，请先设置成其他背景或恢复成会议的默认黑色背景后再行删除。目前暂不支持 OAuth2.0 鉴权访问。
     * @param conferenceId
     * @param backgroundId
     * @return
     */
    @DeleteMapping("/delete/backgrounds/{conferenceId}/{backgroundId}")
    @Operation(summary = "删除会议背景")
    public RestResponse deleteBackgrounds(@PathVariable String conferenceId,@PathVariable String backgroundId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }
        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();

        DeleteBackgrounds request = new DeleteBackgrounds();
        request.setInstanceid(1);
        request.setMeetingId(tencentConferenceContext.getMeetingId());
        request.setUserid(tencentConferenceContext.getTencentUser());
        request.setBackground_id(backgroundId);
        try {
            layout_client.deleteBackgrounds(request);
            return RestResponse.success();
        } catch (WemeetSdkException e) {
           log.info(e.getMessage());
        }
        return RestResponse.fail();
    }


    @DeleteMapping("/delete/backgrounds/batch/{conferenceId}")
    @Operation(summary = "批量删除会议背景")
    public RestResponse deleteBatchBackgrounds(@PathVariable String conferenceId,@RequestBody List<String> background_id_list) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }
        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();

        DeleteBackgroundsBatch request = new DeleteBackgroundsBatch();
        request.setInstanceid(1);
        request.setMeeting_id(tencentConferenceContext.getMeetingId());
        request.setUserid(tencentConferenceContext.getTencentUser());
        request.setBackground_id_list(background_id_list);
        try {
            layout_client.deleteBackgroundBatch(request);
            return RestResponse.success();
        } catch (WemeetSdkException e) {
           log.info(e.getMessage());
        }
        return RestResponse.fail();
    }

    /**
     * 查询会议背景列表
     * 根据会议 ID 返回会议背景列表信息，目前暂不支持 OAuth2.0 鉴权访问
     * @param conferenceId
     * @return
     */
    @GetMapping("/query/backgrounds/{conferenceId}")
    @Operation(summary = "查询会议背景列表")
    public RestResponse queryBackgrounds(@PathVariable String conferenceId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (tencentConferenceContext == null) {
            return null;
        }
        TencentLayoutClient layout_client = tencentConferenceContext.getTencentBridge().getLAYOUT_CLIENT();

        QueryBackGroundsList request = new QueryBackGroundsList();
        request.setInstanceid(1);
        request.setMeetingId(tencentConferenceContext.getMeetingId());
        request.setUserid(tencentConferenceContext.getTencentUser());
        try {
            MeetingBackgroundResponse meetingBackgroundResponse = layout_client.queryBackgroundsList(request);
            return RestResponse.success(meetingBackgroundResponse);
        } catch (WemeetSdkException e) {
           log.info(e.getMessage());
        }
        return RestResponse.fail();
    }

}
