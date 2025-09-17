package com.paradisecloud.smc.service.impl;

import com.paradisecloud.smc.dao.model.BusiSmcDeptTemplate;
import com.paradisecloud.smc.dao.model.mapper.BusiSmcDeptTemplateMapper;
import com.paradisecloud.smc.service.BusiSmcDeptTemplateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class BusiSmcDeptTemplateServiceImpl implements BusiSmcDeptTemplateService {


    @Resource
    private BusiSmcDeptTemplateMapper busiSmcDeptTemplateMapper;

    @Override
    public List<BusiSmcDeptTemplate> queryTemplateListByDeptId(Long deptId) {
        BusiSmcDeptTemplate busiSmcDeptTemplate=new BusiSmcDeptTemplate();
        busiSmcDeptTemplate.setDeptId(deptId);
        return busiSmcDeptTemplateMapper.selectBusiSmcDeptTemplateList(busiSmcDeptTemplate);
    }

    @Override
    public BusiSmcDeptTemplate queryTemplateByDeptId(Long deptId) {
        BusiSmcDeptTemplate busiSmcDeptTemplate=new BusiSmcDeptTemplate();
        busiSmcDeptTemplate.setDeptId(deptId);
        return busiSmcDeptTemplateMapper.selectBusiSmcDeptTemplate(busiSmcDeptTemplate);
    }

    @Override
    public BusiSmcDeptTemplate queryTemplateById(Long id) {
        return busiSmcDeptTemplateMapper.selectBusiSmcDeptTemplateById(id);
    }

    @Override
    public BusiSmcDeptTemplate queryTemplate(String templateId) {
        return busiSmcDeptTemplateMapper.selectBusiSmcDeptTemplateByTemplateId(templateId);
    }

    @Override
    public void add(BusiSmcDeptTemplate busiSmcDeptTemplate) {
        busiSmcDeptTemplateMapper.insertBusiSmcDeptTemplate(busiSmcDeptTemplate);
    }

    @Override
    public void update(BusiSmcDeptTemplate busiSmcDeptTemplate) {
        busiSmcDeptTemplateMapper.updateBusiSmcDeptTemplate(busiSmcDeptTemplate);
    }

    @Override
    public int delete(Long id) {
        return busiSmcDeptTemplateMapper.deleteBusiSmcDeptTemplate(id);
    }


}
