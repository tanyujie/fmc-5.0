package com.paradisecloud.fcm.web.controller.im;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.im.service.IBusiConferenceOptionService;
import com.paradisecloud.im.service.IBusiConferenceQuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/im/conference/question")
@Tag(name = "会议问卷问题")
public class BusiConferenceQuestionController extends BaseController {
    @Resource
    private IBusiConferenceQuestionService questionService;
}
