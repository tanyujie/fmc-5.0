package com.paradisecloud.smc.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Date;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.dao.mapper.BusiFmeDeptMapper;
import com.paradisecloud.fcm.dao.model.BusiFmeDept;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.smc.dao.model.BusiSmcDept;
import com.paradisecloud.smc.dao.model.mapper.BusiSmcDeptMapper;
import com.paradisecloud.smc.service.IBusiSmcDeptService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author liuxilong
 * @date 2022-08-25
 */
@Service
public class BusiSmcDeptServiceImpl implements IBusiSmcDeptService
{
    @Resource
    private BusiSmcDeptMapper busiSmcDeptMapper;

    @Resource
    private BusiFmeDeptMapper busiFmeDeptMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiSmcDept selectBusiSmcDeptById(Long id)
    {
        return busiSmcDeptMapper.selectBusiSmcDeptById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param busiSmcDept 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiSmcDept> selectBusiSmcDeptList(BusiSmcDept busiSmcDept)
    {
        return busiSmcDeptMapper.selectBusiSmcDeptList(busiSmcDept);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param busiSmcDept 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusiSmcDept(BusiSmcDept busiSmcDept) {
        BusiFmeDept busiFmeDept = busiFmeDeptMapper.selectBusiFmeDeptByDeptId(busiSmcDept.getDeptId());
        List<BusiSmcDept> busiSmcDepts = busiSmcDeptMapper.selectBusiSmcDeptList(busiSmcDept);
        if (!CollectionUtils.isEmpty(busiSmcDepts) && busiFmeDept != null) {
            throw new CustomException("当前部门已绑定，不能重复绑定！");
        }
        busiSmcDept.setCreateTime(new Date());
        SmcBridgeCache.getInstance().update(busiSmcDept);
        return busiSmcDeptMapper.insertBusiSmcDept(busiSmcDept);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param busiSmcDept 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusiSmcDept(BusiSmcDept busiSmcDept)
    {
        busiSmcDept.setUpdateTime(new Date());
        SmcBridgeCache.getInstance().update(busiSmcDept);
        return busiSmcDeptMapper.updateBusiSmcDept(busiSmcDept);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmcDeptByIds(Long[] ids)
    {

        Arrays.stream(ids).forEach(p->{
            SmcBridgeCache.getInstance().removeDeptMap(p);
        });
        return busiSmcDeptMapper.deleteBusiSmcDeptByIds(ids);

    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmcDeptById(Long id)
    {

        SmcBridgeCache.getInstance().removeDeptMap(id);
        return busiSmcDeptMapper.deleteBusiSmcDeptById(id);
    }
}
