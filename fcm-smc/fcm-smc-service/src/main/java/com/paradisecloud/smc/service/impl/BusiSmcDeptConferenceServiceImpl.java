package com.paradisecloud.smc.service.impl;

import com.paradisecloud.smc.dao.model.BusiSmcDeptConference;
import com.paradisecloud.smc.dao.model.mapper.BusiSmcDeptConferenceMapper;
import com.paradisecloud.smc.service.BusiSmcDeptConferenceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BusiSmcDeptConferenceServiceImpl implements BusiSmcDeptConferenceService {

    @Resource
    private BusiSmcDeptConferenceMapper busiSmcDeptConferenceMapper;

    @Override
    public void add(BusiSmcDeptConference busiSmcDeptConference) {
        busiSmcDeptConferenceMapper.insertBusiSmcDeptConference(busiSmcDeptConference);
    }
}
