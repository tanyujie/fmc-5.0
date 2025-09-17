package com.paradisecloud.fcm.service.impls;

import java.util.List;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateDeptMapper;
import com.paradisecloud.fcm.dao.model.BusiTemplateDept;
import com.paradisecloud.fcm.service.interfaces.IBusiTemplateDeptService;

/**
 * 会议模板的级联部门Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-01-29
 */
@Service
public class BusiTemplateDeptServiceImpl implements IBusiTemplateDeptService 
{
    @Autowired
    private BusiTemplateDeptMapper busiTemplateDeptMapper;

    /**
     * 查询会议模板的级联部门
     * 
     * @param id 会议模板的级联部门ID
     * @return 会议模板的级联部门
     */
    @Override
    public BusiTemplateDept selectBusiTemplateDeptById(Long id)
    {
        return busiTemplateDeptMapper.selectBusiTemplateDeptById(id);
    }

    /**
     * 查询会议模板的级联部门列表
     * 
     * @param busiTemplateDept 会议模板的级联部门
     * @return 会议模板的级联部门
     */
    @Override
    public List<BusiTemplateDept> selectBusiTemplateDeptList(BusiTemplateDept busiTemplateDept)
    {
        return busiTemplateDeptMapper.selectBusiTemplateDeptList(busiTemplateDept);
    }

    /**
     * 新增会议模板的级联部门
     * 
     * @param busiTemplateDept 会议模板的级联部门
     * @return 结果
     */
    @Override
    public int insertBusiTemplateDept(BusiTemplateDept busiTemplateDept)
    {
        busiTemplateDept.setCreateTime(new Date());
        return busiTemplateDeptMapper.insertBusiTemplateDept(busiTemplateDept);
    }

    /**
     * 修改会议模板的级联部门
     * 
     * @param busiTemplateDept 会议模板的级联部门
     * @return 结果
     */
    @Override
    public int updateBusiTemplateDept(BusiTemplateDept busiTemplateDept)
    {
        busiTemplateDept.setUpdateTime(new Date());
        return busiTemplateDeptMapper.updateBusiTemplateDept(busiTemplateDept);
    }

    /**
     * 批量删除会议模板的级联部门
     * 
     * @param ids 需要删除的会议模板的级联部门ID
     * @return 结果
     */
    @Override
    public int deleteBusiTemplateDeptByIds(Long[] ids)
    {
        return busiTemplateDeptMapper.deleteBusiTemplateDeptByIds(ids);
    }

    /**
     * 删除会议模板的级联部门信息
     * 
     * @param id 会议模板的级联部门ID
     * @return 结果
     */
    @Override
    public int deleteBusiTemplateDeptById(Long id)
    {
        return busiTemplateDeptMapper.deleteBusiTemplateDeptById(id);
    }
}
