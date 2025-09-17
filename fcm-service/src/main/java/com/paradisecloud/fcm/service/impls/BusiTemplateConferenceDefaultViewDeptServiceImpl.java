package com.paradisecloud.fcm.service.impls;

import java.util.List;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceDefaultViewDeptMapper;
import com.paradisecloud.fcm.dao.model.BusiTemplateConferenceDefaultViewDept;
import com.paradisecloud.fcm.service.interfaces.IBusiTemplateConferenceDefaultViewDeptService;

/**
 * 默认视图的部门显示顺序Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-04-08
 */
@Service
public class BusiTemplateConferenceDefaultViewDeptServiceImpl implements IBusiTemplateConferenceDefaultViewDeptService 
{
    @Autowired
    private BusiTemplateConferenceDefaultViewDeptMapper busiTemplateConferenceDefaultViewDeptMapper;

    /**
     * 查询默认视图的部门显示顺序
     * 
     * @param id 默认视图的部门显示顺序ID
     * @return 默认视图的部门显示顺序
     */
    @Override
    public BusiTemplateConferenceDefaultViewDept selectBusiTemplateConferenceDefaultViewDeptById(Long id)
    {
        return busiTemplateConferenceDefaultViewDeptMapper.selectBusiTemplateConferenceDefaultViewDeptById(id);
    }

    /**
     * 查询默认视图的部门显示顺序列表
     * 
     * @param busiTemplateConferenceDefaultViewDept 默认视图的部门显示顺序
     * @return 默认视图的部门显示顺序
     */
    @Override
    public List<BusiTemplateConferenceDefaultViewDept> selectBusiTemplateConferenceDefaultViewDeptList(BusiTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept)
    {
        return busiTemplateConferenceDefaultViewDeptMapper.selectBusiTemplateConferenceDefaultViewDeptList(busiTemplateConferenceDefaultViewDept);
    }

    /**
     * 新增默认视图的部门显示顺序
     * 
     * @param busiTemplateConferenceDefaultViewDept 默认视图的部门显示顺序
     * @return 结果
     */
    @Override
    public int insertBusiTemplateConferenceDefaultViewDept(BusiTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept)
    {
        busiTemplateConferenceDefaultViewDept.setCreateTime(new Date());
        return busiTemplateConferenceDefaultViewDeptMapper.insertBusiTemplateConferenceDefaultViewDept(busiTemplateConferenceDefaultViewDept);
    }

    /**
     * 修改默认视图的部门显示顺序
     * 
     * @param busiTemplateConferenceDefaultViewDept 默认视图的部门显示顺序
     * @return 结果
     */
    @Override
    public int updateBusiTemplateConferenceDefaultViewDept(BusiTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept)
    {
        busiTemplateConferenceDefaultViewDept.setUpdateTime(new Date());
        return busiTemplateConferenceDefaultViewDeptMapper.updateBusiTemplateConferenceDefaultViewDept(busiTemplateConferenceDefaultViewDept);
    }

    /**
     * 批量删除默认视图的部门显示顺序
     * 
     * @param ids 需要删除的默认视图的部门显示顺序ID
     * @return 结果
     */
    @Override
    public int deleteBusiTemplateConferenceDefaultViewDeptByIds(Long[] ids)
    {
        return busiTemplateConferenceDefaultViewDeptMapper.deleteBusiTemplateConferenceDefaultViewDeptByIds(ids);
    }

    /**
     * 删除默认视图的部门显示顺序信息
     * 
     * @param id 默认视图的部门显示顺序ID
     * @return 结果
     */
    @Override
    public int deleteBusiTemplateConferenceDefaultViewDeptById(Long id)
    {
        return busiTemplateConferenceDefaultViewDeptMapper.deleteBusiTemplateConferenceDefaultViewDeptById(id);
    }
}
