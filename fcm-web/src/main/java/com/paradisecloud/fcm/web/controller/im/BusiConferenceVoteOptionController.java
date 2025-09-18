package com.paradisecloud.fcm.web.controller.im;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.im.service.IBusiConferenceVoteOptionService;
import com.paradisecloud.im.service.IBusiConferenceVoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/im/conference/vote/option")
@Tag(name = "会议投票选项")
public class BusiConferenceVoteOptionController extends BaseController {
    @Resource
    private IBusiConferenceVoteOptionService voteOptionService;
}
