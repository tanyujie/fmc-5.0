package com.paradisecloud.fcm.telep.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.telep.dao.mapper.BusiTeleDeptMapper;
import com.paradisecloud.fcm.telep.dao.model.BusiTeleDept;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiTeleDeptService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author lilinhai
 * @date 2022-10-11
 */
@Service
public class BusiTeleDeptServiceImpl implements IBusiTeleDeptService
{
    @Resource
    private BusiTeleDeptMapper busiTeleDeptMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiTeleDept selectBusiTeleDeptById(Long id)
    {
        return busiTeleDeptMapper.selectBusiTeleDeptById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiTeleDept 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiTeleDept> selectBusiTeleDeptList(BusiTeleDept busiTeleDept)
    {
        return busiTeleDeptMapper.selectBusiTeleDeptList(busiTeleDept);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiTeleDept 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusiTeleDept(BusiTeleDept busiTeleDept)
    {
        busiTeleDept.setCreateTime(new Date());
        return busiTeleDeptMapper.insertBusiTeleDept(busiTeleDept);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiTeleDept 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusiTeleDept(BusiTeleDept busiTeleDept)
    {
        busiTeleDept.setUpdateTime(new Date());
        return busiTeleDeptMapper.updateBusiTeleDept(busiTeleDept);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiTeleDeptByIds(Long[] ids)
    {
        return busiTeleDeptMapper.deleteBusiTeleDeptByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiTeleDeptById(Long id)
    {
        return busiTeleDeptMapper.deleteBusiTeleDeptById(id);
    }
}
