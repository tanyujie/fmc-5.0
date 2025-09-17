package com.paradisecloud.smc.service;

import com.paradisecloud.smc.dao.model.BusiSmcDeptTemplate;

import java.util.List;

public interface BusiSmcDeptTemplateService {


    List<BusiSmcDeptTemplate> queryTemplateListByDeptId(Long DeptId);

    BusiSmcDeptTemplate queryTemplateByDeptId(Long DeptId);

    BusiSmcDeptTemplate queryTemplateById(Long id);

    BusiSmcDeptTemplate queryTemplate(String templateId);

    void add(BusiSmcDeptTemplate busiSmcDeptTemplate);


    void  update(BusiSmcDeptTemplate busiSmcDeptTemplate);

    int delete(Long id);

}
