package com.paradisecloud.fcm.web.controller.mcu.all;

import com.huaweicloud.sdk.meeting.v1.MeetingClient;
import com.huaweicloud.sdk.meeting.v1.model.ShowCorpResourceResponse;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMcuZjResourceTemplate;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridgeCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.MeetingCorpDir;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.model.SourceTemplate;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjResourceTemplateService;
import com.paradisecloud.fcm.zte.cache.McuZteBridgeCache;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridge;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridgeCollection;
import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.zte.m900.request.GetConferenceTempletListRequest;
import com.zte.m900.response.GetConferenceTempletListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * MCU资源模板信息Controller
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/busi/mcu/all/resource")
@Tag(name = "MCU资源模板控制层")
public class BusiAllResourceTemplateController extends BaseController
{

    @Resource
    private IBusiMcuZjResourceTemplateService busiMcuZjResourceTemplateService;
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键查找单个记录</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "获取MCU资源模板表（不分页）")
    @GetMapping("/list")
    public RestResponse list(BusiMcuZjResourceTemplate busiMcuZjResourceTemplate)
    {
        List<BusiMcuZjResourceTemplate> busiMcuZjResourceTemplateList = busiMcuZjResourceTemplateService.selectBusiMcuZjResourceTemplateList(busiMcuZjResourceTemplate);
        List<ModelBean> modelBeanList = new ArrayList<>();
        for (BusiMcuZjResourceTemplate busiMcuZjResourceTemplateTemp : busiMcuZjResourceTemplateList) {
            if (busiMcuZjResourceTemplateTemp.getMaxGuestMosic() == 2) {
                busiMcuZjResourceTemplateTemp.setMaxGuestMosic(busiMcuZjResourceTemplateTemp.getMaxGuestMosic() - 1);
                busiMcuZjResourceTemplateTemp.setMaxMosic(busiMcuZjResourceTemplateTemp.getMaxMosic() - 1);
            }
            if (busiMcuZjResourceTemplateTemp.getSingleView() != 1) {
                busiMcuZjResourceTemplateTemp.setMaxMosic(busiMcuZjResourceTemplateTemp.getMaxMosic() - busiMcuZjResourceTemplateTemp.getMaxChairMosic());
            }
            ModelBean modelBean = new ModelBean(busiMcuZjResourceTemplateTemp);
            int mrCount = 0;
            float evaluationResourceCount = 0;
            McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(busiMcuZjResourceTemplate.getMcuZjServerId());
            if (mcuZjBridge != null) {
                SourceTemplate sourceTemplate = mcuZjBridge.getSourceTemplate(busiMcuZjResourceTemplateTemp.getName());
                if (sourceTemplate != null) {
                    mrCount = sourceTemplate.getMr_count();
                    evaluationResourceCount = sourceTemplate.getEvaluationResourceCount();
                }
            }
            modelBean.put("mrCount", mrCount);
            modelBean.put("evaluationResourceCount", evaluationResourceCount);

            modelBeanList.add(modelBean);
        }
        return success(modelBeanList);
    }
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">创建会议桥[bridgeHost]</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "单个会议桥记录新增：记录的属性和属性值放到请求body中封装为json格式", description = "新增资源模板")
    @PostMapping("")
    public RestResponse save(@RequestBody BusiMcuZjResourceTemplate busiMcuZjResourceTemplate)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiMcuZjResourceTemplate.setCreateUserId(loginUser.getUser().getUserId());
        }
        busiMcuZjResourceTemplateService.insertBusiMcuZjResourceTemplate(busiMcuZjResourceTemplate);
        return success(busiMcuZjResourceTemplate);
    }

    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">设置默认会议资源模板[bridgeHost]</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "设置默认会议资源模板：记录的属性和属性值放到请求body中封装为json格式", description = "设置默认会议资源模板")
    @PostMapping("/setDefault")
    public RestResponse setDefault(@RequestBody BusiMcuZjResourceTemplate busiMcuZjResourceTemplate)
    {
        if (busiMcuZjResourceTemplate.getId() == null) {
            Assert.isTrue(false, "id不能为空！");
        }
        try {
            busiMcuZjResourceTemplateService.setDefaultBusiMcuZjResourceTemplate(busiMcuZjResourceTemplate.getId());
        } catch (Exception e) {
            return fail(-1, "设置默认会议资源模板失败！模板或已不存在。");
        }
        return success();
    }
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键ID删除一个实体</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键ID删除单个记录：id放到rest地址上占位符处", description = "删除资源模板")
    @DeleteMapping("/{id}")
    public RestResponse delete(@PathVariable Long id)
    {
        busiMcuZjResourceTemplateService.deleteBusiMcuZjResourceTemplateById(id);
        return success("删除资源模板成功, id: " + id);
    }


    @Operation(summary = "根据APPID获取企业资源", description = "查询企业资源")
    @GetMapping("/{appId}")
    public RestResponse getHwcloudCropRes(@PathVariable String appId)
    {
        HwcloudBridge bridgesByAppId = HwcloudBridgeCache.getInstance().getBridgesByAppId(appId);
        MeetingClient meetingClient = bridgesByAppId.getMeetingClient();
        MeetingCorpDir meetingCorpDir = new MeetingCorpDir(meetingClient);
        ShowCorpResourceResponse showCorpResourceResponse = meetingCorpDir.showCorpResource();
        return success(showCorpResourceResponse);
    }


    @Operation(summary = "指定账号获取其权限范围内的会议模板", description = "查询模板资源")
    @GetMapping("/conferenceTempletList/{deptId}")
    public RestResponse getConferenceTempletList(@PathVariable Long deptId)
    {
        McuZteBridgeCollection availableMcuZteBridgesByDept = McuZteBridgeCache.getInstance().getAvailableMcuZteBridgesByDept(deptId);
        if(availableMcuZteBridgesByDept!=null){
            McuZteBridge masterMcuZteBridge = availableMcuZteBridgesByDept.getMasterMcuZteBridge();
            GetConferenceTempletListRequest request = new GetConferenceTempletListRequest();
            request.setAccount(masterMcuZteBridge.getBusiMcuZte().getUsername());
            GetConferenceTempletListResponse conferenceTempletListResponse = masterMcuZteBridge.getConferenceManageApi().getConferenceTempletList(request);
            if(conferenceTempletListResponse!=null&& CommonResponse.STATUS_OK.equals(conferenceTempletListResponse.getResult())){
                return success(conferenceTempletListResponse.getConferenceTemplet());
            }
        }
        return RestResponse.fail("查询模板资源失败");
    }



}
