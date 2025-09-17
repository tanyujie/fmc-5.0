package com.paradisecloud.fcm.web.controller.cascade;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.ConferenceType;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceNumberMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.sinhy.spring.BeanFactory;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2022/9/17 15:50
 */
@RestController
@RequestMapping("/cascade/fme")
@Tag(name = "fme级联会议")
public class FmeCascadeConferencesController extends BaseController {

    @GetMapping("/getConferenceByDeptId")
    public RestResponse buildTemplateConferenceContext(long deptId) {

        List<ConferenceContext> conferenceContextList = new ArrayList<>();

        BusiConferenceNumber busiConferenceNumberCondition = new BusiConferenceNumber();
        busiConferenceNumberCondition.setDeptId(deptId);
        busiConferenceNumberCondition.setType(ConferenceType.CASCADE.getValue());
        List<BusiConferenceNumber> cns = BeanFactory.getBean(BusiConferenceNumberMapper.class).selectBusiConferenceNumberList(busiConferenceNumberCondition);
        if (ObjectUtils.isEmpty(cns)) {
            return null;
        }
        for (BusiConferenceNumber cn : cns) {
            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(cn.getId().toString());
            conferenceContextList.add(conferenceContext);
        }

        return RestResponse.success(conferenceContextList);
    }

}
