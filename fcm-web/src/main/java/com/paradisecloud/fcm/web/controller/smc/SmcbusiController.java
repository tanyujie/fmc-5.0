package com.paradisecloud.fcm.web.controller.smc;


import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.smc.dao.model.BusiSmc;
import com.paradisecloud.smc.dao.model.mapper.BusiSmcMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/smc/admin")
public class SmcbusiController {


    @Resource
    BusiSmcMapper busiSmcMapper;

    /**
     * 终端号码
     * @return
     */
    @PostMapping("/add")
    public RestResponse addBusiSmc(@RequestBody BusiSmc  busiSmc){
        return RestResponse.success(busiSmcMapper.insertBusiSmc(busiSmc));
    }
}
