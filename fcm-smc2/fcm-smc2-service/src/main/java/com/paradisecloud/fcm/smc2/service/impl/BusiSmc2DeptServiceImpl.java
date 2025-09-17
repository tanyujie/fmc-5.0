package com.paradisecloud.fcm.smc2.service.impl;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.dao.mapper.BusiSmc2DeptMapper;
import com.paradisecloud.fcm.dao.model.BusiSmc2Dept;
import com.paradisecloud.fcm.smc2.service.IBusiSmc2DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author lilinhai
 * @date 2023-05-17
 */
@Service
public class BusiSmc2DeptServiceImpl implements IBusiSmc2DeptService
{
    @Autowired
    private BusiSmc2DeptMapper busiSmc2DeptMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiSmc2Dept selectBusiSmc2DeptById(Long id)
    {
        return busiSmc2DeptMapper.selectBusiSmc2DeptById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiSmc2Dept 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiSmc2Dept> selectBusiSmc2DeptList(BusiSmc2Dept busiSmc2Dept)
    {
        return busiSmc2DeptMapper.selectBusiSmc2DeptList(busiSmc2Dept);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiSmc2Dept 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusiSmc2Dept(BusiSmc2Dept busiSmc2Dept)
    {
        busiSmc2Dept.setCreateTime(new Date());
        int i = busiSmc2DeptMapper.insertBusiSmc2Dept(busiSmc2Dept);
//        if(i>0){
//            DeptSmc2MappingCache.getInstance().put(busiSmc2Dept.getDeptId(),busiSmc2Dept);
//        }
        return i;
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiSmc2Dept 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusiSmc2Dept(BusiSmc2Dept busiSmc2Dept)
    {
        busiSmc2Dept.setUpdateTime(new Date());
        return busiSmc2DeptMapper.updateBusiSmc2Dept(busiSmc2Dept);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmc2DeptByIds(Long[] ids)
    {
        return busiSmc2DeptMapper.deleteBusiSmc2DeptByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmc2DeptById(Long id)
    {
        return busiSmc2DeptMapper.deleteBusiSmc2DeptById(id);
    }
}
