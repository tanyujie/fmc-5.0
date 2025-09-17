package com.paradisecloud.fcm.web.controller.mobile;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiCallLegProfileService;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 入会方案配置，控制参会者进入会议的方案Controller
 * 
 * @author lilinhai
 * @date 2021-01-26
 */
@RestController
@RequestMapping("/mobile/callLegProfile")
@Tag(name = "入会方案配置，控制参会者进入会议的方案")
public class MobileCallLegProfileController extends BaseController
{
    @Autowired
    private IBusiCallLegProfileService busiCallLegProfileService;


    @PutMapping("/presentationSetting/{conferenceId}/presentationContributionAllowed/{enable}")
    @Operation(summary = "辅流设置")
    public RestResponse presentationSettingAll(@PathVariable String conferenceId,@PathVariable String enable)
    {
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getByConferenceId(conferenceId);
        CallLegProfile callLegProfile = conferenceContext.getCallLegProfile();
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(conferenceContext.getDeptId());
        List<NameValuePair> nameValuePairs=new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("presentationContributionAllowed", enable));
        busiCallLegProfileService.updateCallLegProfile(fmeBridge,callLegProfile.getId(),nameValuePairs);
        return success();
    }



}
