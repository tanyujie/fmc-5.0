package com.paradisecloud.fcm.web.controller.hwcloud;


import com.huaweicloud.sdk.meeting.v1.model.TokenInfo;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContextCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IHwcloudUserService;
import com.sinhy.utils.Base64Utils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @author nj
 * @date 2022/8/23 10:48
 */

@RestController
@RequestMapping("/hwcloud/crop")
@Tag(name = "华为云企业联系人")
public class HwcloudUserController extends BaseController {

    @Resource
   private IHwcloudUserService hwcloudUserService;


    /**
     * 查询通讯录内部人员
     * @return
     */
    @GetMapping("/users")
    public RestResponse getAbsUsers(@RequestParam Long deptId,
                                    @RequestParam(required = false,defaultValue = "1") String deptCode,
                                    @RequestParam(required = false) String searchKey,
                                    @RequestParam(required = false,defaultValue = "ALL") String searchScope,
                                    @RequestParam(defaultValue = "0") Integer pageIndex,
                                    @RequestParam(defaultValue = "100") Integer pageSize){

        return RestResponse.success(hwcloudUserService.getUsers(deptId,deptCode,searchKey,searchScope,pageIndex,pageSize));
    }


    /**
     * 查询通讯录外部人员
     * @return
     */
    @GetMapping("/externalContacts")
    public RestResponse getExternalContacts(@RequestParam Long deptId,
                                            @RequestParam(required = false) String searchKey,
                                            @RequestParam(required = false,defaultValue = "ALL") String searchScope,
                                            @RequestParam(defaultValue = "0") Integer pageIndex,
                                            @RequestParam(defaultValue = "100") Integer pageSize){
        return RestResponse.success(hwcloudUserService.getExternalContacts(deptId,searchKey,searchScope,pageIndex,pageSize));
    }

    /**
     * 查询硬件终端
     * @return
     */
    @GetMapping("/device")
    public RestResponse getDevices(@RequestParam Long deptId,
                                   @RequestParam(required = false,defaultValue = "1") String deptCode,
                                   @RequestParam(required = false) String searchKey,
                                   @RequestParam(defaultValue = "0") Integer pageIndex,
                                   @RequestParam(defaultValue = "100") Integer pageSize){
        return RestResponse.success(hwcloudUserService.getDevices(deptId,deptCode,searchKey,pageIndex,pageSize));
    }

    /**
     * 查询企业部门
     * @return
     */
    @GetMapping("/dept")
    public RestResponse getCropDept(@RequestParam Long deptId,@RequestParam(defaultValue = "1") String deptCode){
        return RestResponse.success(hwcloudUserService.getCropDept(deptId,deptCode));
    }


    @GetMapping("/qos")
    public RestResponse getCropDept(){

      //  HwcloudConferenceContext hwcloudConferenceContext = HwcloudConferenceContextCache.getInstance().get(conferenceId);

        Collection<HwcloudConferenceContext> values = HwcloudConferenceContextCache.getInstance().values();
        for (HwcloudConferenceContext hwcloudConferenceContext : values) {
            String meetingId = hwcloudConferenceContext.getMeetingId();
            String meetingUUID = hwcloudConferenceContext.getMeetingUUID();

            System.out.println("UUD:"+meetingUUID);

            TokenInfo tokenInfo = hwcloudConferenceContext.getHwcloudMeetingBridge().getTokenInfo();
            String token = tokenInfo.getToken();
            String encode = Base64Utils.encode(token);
            System.out.println("BASE:"+encode);

            String appId = hwcloudConferenceContext.getHwcloudMeetingBridge().getHwcloudbridge().getBusiHwcloud().getAppId();
            return RestResponse.success(encode);
        }


        return RestResponse.success();
    }

}
