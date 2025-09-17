package com.paradisecloud.smc.service;

import com.paradisecloud.smc.dao.model.BusiSmcConferenceState;
import java.util.List;
/**
 * @author nj
 * @date 2022/10/25 9:50
 */
public interface IBusiSmcConferenceStateService {
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiSmcConferenceState selectBusiSmcConferenceStateById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param busiSmcConferenceState 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiSmcConferenceState> selectBusiSmcConferenceStateList(BusiSmcConferenceState busiSmcConferenceState);

    /**
     * 新增【请填写功能名称】
     *
     * @param busiSmcConferenceState 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiSmcConferenceState(BusiSmcConferenceState busiSmcConferenceState);

    /**
     * 修改【请填写功能名称】
     *
     * @param busiSmcConferenceState 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiSmcConferenceState(BusiSmcConferenceState busiSmcConferenceState);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcConferenceStateByIds(Integer[] ids);

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcConferenceStateById(Integer id);
}
