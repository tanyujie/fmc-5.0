package com.paradisecloud.smc.dao.model.mapper;

import com.paradisecloud.smc.dao.model.BusiSmcDeptConference;

public interface BusiSmcDeptConferenceMapper {

    public BusiSmcDeptConference selectBusiSmcDeptConferenceById(Long id);

    public BusiSmcDeptConference selectBusiSmcDeptConferenceByConferenceId(String conferenceId);

    public BusiSmcDeptConference selectBusiSmcDeptConference(BusiSmcDeptConference busiSmcDeptConference);

    public int insertBusiSmcDeptConference(BusiSmcDeptConference busiSmcDeptConference);


    public int updateBusiSmcDeptConference(BusiSmcDeptConference busiSmcDeptConference);


    public int deleteBusiSmcDeptConference(Long id);

}
