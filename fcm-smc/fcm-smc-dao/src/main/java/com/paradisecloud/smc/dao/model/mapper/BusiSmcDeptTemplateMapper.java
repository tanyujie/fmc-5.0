package com.paradisecloud.smc.dao.model.mapper;

import com.paradisecloud.smc.dao.model.BusiSmcDept;
import com.paradisecloud.smc.dao.model.BusiSmcDeptTemplate;

import java.util.List;

public interface BusiSmcDeptTemplateMapper {


    public BusiSmcDeptTemplate selectBusiSmcDeptTemplateById(Long id);

    public BusiSmcDeptTemplate selectBusiSmcDeptTemplateByTemplateId(String  templateId);

    public BusiSmcDeptTemplate selectBusiSmcDeptTemplate(BusiSmcDeptTemplate busiSmcDeptTemplate);

    public int insertBusiSmcDeptTemplate(BusiSmcDeptTemplate busiSmcDeptTemplate);


    public int updateBusiSmcDeptTemplate(BusiSmcDeptTemplate busiSmcDeptTemplate);


    public int deleteBusiSmcDeptTemplate(Long id);


    List<BusiSmcDeptTemplate> selectBusiSmcDeptTemplateList(BusiSmcDeptTemplate busiSmcDeptTemplate);
}
