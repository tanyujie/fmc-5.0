package com.paradisecloud.smc.service;


import com.paradisecloud.smc.dao.model.BusiSmcMulitpic;

import java.util.List;

/**
 * @author nj
 * @date 2022/10/20 14:34
 */
public interface IBusiSmcMulitpicService
{
    /**
     * 查询【请填写功能名称】
     *
     * @param conferenceId 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
     BusiSmcMulitpic selectBusiSmcMulitpicByConferenceId(String conferenceId);


    /**
     * 删除【请填写功能名称】
     *
     * @param conferenceId 【请填写功能名称】ID
     * @return 结果
     */
     int deleteBusiSmcMulitpicByConferenceId(String conferenceId);
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiSmcMulitpic selectBusiSmcMulitpicById(Integer id);

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
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcMulitpicByIds(Integer[] ids);

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcMulitpicById(Integer id);
}