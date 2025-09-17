/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : ConferenceDebuggerController.java
 * Package : com.paradisecloud.fcm.web.controller.business
 * 
 * @author lilinhai
 * 
 * @since 2021-03-05 10:37
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.fme.conference.model.pm.debug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.fme.conference.interfaces.IDebugService;

@RestController
@RequestMapping("/confere" + "nce-de" + "bu" + "gger-tgmrLZjnLu8gcxRgHrgg02FmdeSR0ZmEf5Gg8")
public class ConferenceDebugger extends BaseController
{
    
    @Autowired
    private IDebugService debugService;
    
    /**
     * 查询所有会议
     */
    @GetMapping("/allConf" + "erence")
    public RestResponse viewAllConferenceInMemery()
    {
        return RestResponse.success(debugService.allConference());
    }
    
    /**
     * 查询轮询方案列表
     */
    @GetMapping("/att" + "endee" + "Impo" + "rtance/{confer" + "enceNum" + "ber}")
    public RestResponse attendeeImportance(@PathVariable("conferenceNumber") String conferenceNumber)
    {
        return success(debugService.logJson(conferenceNumber));
    }
    
}
