package com.paradisecloud.smc.dao.model.mapper;


import com.paradisecloud.smc.dao.model.BusiSmcMulitpic;

import java.util.List;


/**
 * 【请填写功能名称】Mapper接口
 *
 * @author lilinhai
 * @date 2022-10-20
 */
public interface BusiSmcMulitpicMapper
{
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiSmcMulitpic selectBusiSmcMulitpicById(Integer id);
    /**
     * 查询【请填写功能名称】
     *
     * @param conferenceId 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiSmcMulitpic selectBusiSmcMulitpicByConferenceId(String conferenceId);


    /**
     * 查询【请填写功能名称】列表
     *
     * @param busiSmcMulitpic 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiSmcMulitpic> selectBusiSmcMulitpicList(BusiSmcMulitpic busiSmcMulitpic);

    /**
     * 新增【请填写功能名称】
     *
     * @param busiSmcMulitpic 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiSmcMulitpic(BusiSmcMulitpic busiSmcMulitpic);

    /**
     * 修改【请填写功能名称】
     *
     * @param busiSmcMulitpic 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiSmcMulitpic(BusiSmcMulitpic busiSmcMulitpic);

    /**
     * 删除【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcMulitpicById(Integer id);


    /**
     * 删除【请填写功能名称】
     *
     * @param conferenceId 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcMulitpicByConferenceId(String conferenceId);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmcMulitpicByIds(Integer[] ids);
}