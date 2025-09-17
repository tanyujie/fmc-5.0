package com.paradisecloud.smc.service;

import com.paradisecloud.com.fcm.smc.modle.request.BusiSmcAppointmentConferenceQuery;
import com.paradisecloud.smc.dao.model.BusiSmcHistoryConference;
import com.paradisecloud.system.dao.model.SysDept;

import java.util.List;

/**
 * @author nj
 * @date 2023/3/14 14:37
 */
public interface IBusiSmcHistoryConferenceService {

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiSmcHistoryConference selectBusiSmcHistoryConferenceById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param busiSmcHistoryConference 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiSmcHistoryConference> selectBusiSmcHistoryConferenceList(BusiSmcHistoryConference busiSmcHistoryConference);

    /**
     * 新增【请填写功能名称】
     *
     * @param busiSmcHistoryConference 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiSmcHistoryConference(BusiSmcHistoryConference busiSmcHistoryConference);

    /**
     * 修改【请填写功能名称】
     *
     * @param busiSmcHistoryConference 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiSmcHistoryConference(BusiSmcHistoryConference busiSmcHistoryConference);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcHistoryConferenceByIds(Integer[] ids);

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcHistoryConferenceById(Integer id);


    public BusiSmcHistoryConference selectBusiSmcHistoryConferenceByConferenceId(String conferenceId);

    List<BusiSmcHistoryConference> selectBusiSmcHistoryConferenceListByDate(BusiSmcHistoryConference historyConference);

    List<BusiSmcHistoryConference> selectBusiSmcHistoryConferenceNotTemplate(String searchKey,List<SysDept> sds);

    List<BusiSmcHistoryConference> selectBusiSmcHistoryConferenceListBySearchKey(BusiSmcAppointmentConferenceQuery query);
}
