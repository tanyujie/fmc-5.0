package com.paradisecloud.fcm.fme.service.impls;

import java.util.List;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.paradisecloud.fcm.dao.mapper.BusiTemplatePollingDeptMapper;
import com.paradisecloud.fcm.dao.model.BusiTemplatePollingDept;
import com.paradisecloud.fcm.fme.service.interfaces.IBusiTemplatePollingDeptService;

/**
 * 轮询方案的部门Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
@Service
public class BusiTemplatePollingDeptServiceImpl implements IBusiTemplatePollingDeptService 
{
    @Autowired
    private BusiTemplatePollingDeptMapper busiTemplatePollingDeptMapper;

    /**
     * 查询轮询方案的部门
     * 
     * @param id 轮询方案的部门ID
     * @return 轮询方案的部门
     */
    @Override
    public BusiTemplatePollingDept selectBusiTemplatePollingDeptById(Long id)
    {
        return busiTemplatePollingDeptMapper.selectBusiTemplatePollingDeptById(id);
    }

    /**
     * 查询轮询方案的部门列表
     * 
     * @param busiTemplatePollingDept 轮询方案的部门
     * @return 轮询方案的部门
     */
    @Override
    public List<BusiTemplatePollingDept> selectBusiTemplatePollingDeptList(BusiTemplatePollingDept busiTemplatePollingDept)
    {
        return busiTemplatePollingDeptMapper.selectBusiTemplatePollingDeptList(busiTemplatePollingDept);
    }

    /**
     * 新增轮询方案的部门
     * 
     * @param busiTemplatePollingDept 轮询方案的部门
     * @return 结果
     */
    @Override
    public int insertBusiTemplatePollingDept(BusiTemplatePollingDept busiTemplatePollingDept)
    {
        busiTemplatePollingDept.setCreateTime(new Date());
        return busiTemplatePollingDeptMapper.insertBusiTemplatePollingDept(busiTemplatePollingDept);
    }

    /**
     * 修改轮询方案的部门
     * 
     * @param busiTemplatePollingDept 轮询方案的部门
     * @return 结果
     */
    @Override
    public int updateBusiTemplatePollingDept(BusiTemplatePollingDept busiTemplatePollingDept)
    {
        busiTemplatePollingDept.setUpdateTime(new Date());
        return busiTemplatePollingDeptMapper.updateBusiTemplatePollingDept(busiTemplatePollingDept);
    }

    /**
     * 批量删除轮询方案的部门
     * 
     * @param ids 需要删除的轮询方案的部门ID
     * @return 结果
     */
    @Override
    public int deleteBusiTemplatePollingDeptByIds(Long[] ids)
    {
        return busiTemplatePollingDeptMapper.deleteBusiTemplatePollingDeptByIds(ids);
    }

    /**
     * 删除轮询方案的部门信息
     * 
     * @param id 轮询方案的部门ID
     * @return 结果
     */
    @Override
    public int deleteBusiTemplatePollingDeptById(Long id)
    {
        return busiTemplatePollingDeptMapper.deleteBusiTemplatePollingDeptById(id);
    }
}
