package com.paradisecloud.fcm.web.controller.im;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.im.service.IBusiConferenceVoteQuestionService;
import com.paradisecloud.im.service.IBusiConferenceVoteRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/im/conference/vote/record")
@Tag(name = "会议投票记录")
public class BusiConferenceVoteRecordController extends BaseController {
    @Resource
    private IBusiConferenceVoteRecordService voteRecordService;
}
