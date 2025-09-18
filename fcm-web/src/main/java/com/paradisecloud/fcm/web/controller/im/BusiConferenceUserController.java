package com.paradisecloud.fcm.web.controller.im;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.im.service.IBusiConferenceSignInService;
import com.paradisecloud.im.service.IBusiConferenceUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/im/conference/user")
@Tag(name = "会议模板的用户")
public class BusiConferenceUserController extends BaseController {
    @Resource
    private IBusiConferenceUserService conferenceUserService;
}
