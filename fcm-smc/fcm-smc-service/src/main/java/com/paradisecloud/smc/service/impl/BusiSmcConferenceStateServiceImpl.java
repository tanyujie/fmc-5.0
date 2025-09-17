package com.paradisecloud.smc.service.impl;

import com.paradisecloud.smc.dao.model.BusiSmcConferenceState;
import com.paradisecloud.smc.dao.model.mapper.BusiSmcConferenceStateMapper;
import com.paradisecloud.smc.service.IBusiSmcConferenceStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author nj
 * @date 2022/10/25 9:51
 */
@Service
public class BusiSmcConferenceStateServiceImpl implements IBusiSmcConferenceStateService
{
    @Autowired
    private BusiSmcConferenceStateMapper busiSmcConferenceStateMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiSmcConferenceState selectBusiSmcConferenceStateById(Integer id)
    {
        return busiSmcConferenceStateMapper.selectBusiSmcConferenceStateById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param busiSmcConferenceState 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiSmcConferenceState> selectBusiSmcConferenceStateList(BusiSmcConferenceState busiSmcConferenceState)
    {
        return busiSmcConferenceStateMapper.selectBusiSmcConferenceStateList(busiSmcConferenceState);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param busiSmcConferenceState 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusiSmcConferenceState(BusiSmcConferenceState busiSmcConferenceState)
    {
        busiSmcConferenceState.setCreateTime(new Date());
        return busiSmcConferenceStateMapper.insertBusiSmcConferenceState(busiSmcConferenceState);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param busiSmcConferenceState 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusiSmcConferenceState(BusiSmcConferenceState busiSmcConferenceState)
    {
        return busiSmcConferenceStateMapper.updateBusiSmcConferenceState(busiSmcConferenceState);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmcConferenceStateByIds(Integer[] ids)
    {
        return busiSmcConferenceStateMapper.deleteBusiSmcConferenceStateByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmcConferenceStateById(Integer id)
    {
        return busiSmcConferenceStateMapper.deleteBusiSmcConferenceStateById(id);
    }
}
