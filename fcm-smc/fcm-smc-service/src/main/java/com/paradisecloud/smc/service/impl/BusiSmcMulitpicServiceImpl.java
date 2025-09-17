package com.paradisecloud.smc.service.impl;

import com.paradisecloud.smc.dao.model.BusiSmcMulitpic;
import com.paradisecloud.smc.dao.model.mapper.BusiSmcMulitpicMapper;
import com.paradisecloud.smc.service.IBusiSmcMulitpicService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author nj
 * @date 2022/10/20 14:34
 */
@Service
public class BusiSmcMulitpicServiceImpl implements IBusiSmcMulitpicService
{
    @Resource
    private BusiSmcMulitpicMapper busiSmcMulitpicMapper;

    @Override
    public BusiSmcMulitpic selectBusiSmcMulitpicByConferenceId(String conferenceId) {
        return busiSmcMulitpicMapper.selectBusiSmcMulitpicByConferenceId(conferenceId);
    }

    @Override
    public int deleteBusiSmcMulitpicByConferenceId(String conferenceId) {
        return busiSmcMulitpicMapper.deleteBusiSmcMulitpicByConferenceId(conferenceId);
    }

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiSmcMulitpic selectBusiSmcMulitpicById(Integer id)
    {
        return busiSmcMulitpicMapper.selectBusiSmcMulitpicById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param busiSmcMulitpic 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiSmcMulitpic> selectBusiSmcMulitpicList(BusiSmcMulitpic busiSmcMulitpic)
    {
        return busiSmcMulitpicMapper.selectBusiSmcMulitpicList(busiSmcMulitpic);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param busiSmcMulitpic 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusiSmcMulitpic(BusiSmcMulitpic busiSmcMulitpic)
    {
        return busiSmcMulitpicMapper.insertBusiSmcMulitpic(busiSmcMulitpic);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param busiSmcMulitpic 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusiSmcMulitpic(BusiSmcMulitpic busiSmcMulitpic)
    {
        return busiSmcMulitpicMapper.updateBusiSmcMulitpic(busiSmcMulitpic);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmcMulitpicByIds(Integer[] ids)
    {
        return busiSmcMulitpicMapper.deleteBusiSmcMulitpicByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmcMulitpicById(Integer id)
    {
        return busiSmcMulitpicMapper.deleteBusiSmcMulitpicById(id);
    }
}
