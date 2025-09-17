package com.paradisecloud.smc.service.impl;

import com.paradisecloud.com.fcm.smc.modle.request.BusiSmcAppointmentConferenceQuery;
import com.paradisecloud.smc.dao.model.BusiSmcHistoryConference;
import com.paradisecloud.smc.dao.model.mapper.BusiSmcHistoryConferenceMapper;
import com.paradisecloud.smc.service.IBusiSmcHistoryConferenceService;
import com.paradisecloud.system.dao.model.SysDept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author nj
 * @date 2023/3/14 14:37
 */
@Service
public class BusiSmcHistoryConferenceServiceImpl implements IBusiSmcHistoryConferenceService {

    @Autowired
    private BusiSmcHistoryConferenceMapper busiSmcHistoryConferenceMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiSmcHistoryConference selectBusiSmcHistoryConferenceById(Integer id)
    {
        return busiSmcHistoryConferenceMapper.selectBusiSmcHistoryConferenceById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param busiSmcHistoryConference 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiSmcHistoryConference> selectBusiSmcHistoryConferenceList(BusiSmcHistoryConference busiSmcHistoryConference)
    {
        return busiSmcHistoryConferenceMapper.selectBusiSmcHistoryConferenceList(busiSmcHistoryConference);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param busiSmcHistoryConference 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusiSmcHistoryConference(BusiSmcHistoryConference busiSmcHistoryConference)
    {
        busiSmcHistoryConference.setCreateTime(new Date());
        return busiSmcHistoryConferenceMapper.insertBusiSmcHistoryConference(busiSmcHistoryConference);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param busiSmcHistoryConference 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusiSmcHistoryConference(BusiSmcHistoryConference busiSmcHistoryConference)
    {
        return busiSmcHistoryConferenceMapper.updateBusiSmcHistoryConference(busiSmcHistoryConference);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmcHistoryConferenceByIds(Integer[] ids)
    {
        return busiSmcHistoryConferenceMapper.deleteBusiSmcHistoryConferenceByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmcHistoryConferenceById(Integer id)
    {
        return busiSmcHistoryConferenceMapper.deleteBusiSmcHistoryConferenceById(id);
    }

    @Override
    public BusiSmcHistoryConference selectBusiSmcHistoryConferenceByConferenceId(String conferenceId) {
        return busiSmcHistoryConferenceMapper.selectBusiSmcHistoryConferenceByConferenceId(conferenceId);
    }

    @Override
    public List<BusiSmcHistoryConference> selectBusiSmcHistoryConferenceListByDate(BusiSmcHistoryConference historyConference) {
        return busiSmcHistoryConferenceMapper.selectBusiSmcHistoryConferenceListByDate(historyConference);
    }

    @Override
    public List<BusiSmcHistoryConference> selectBusiSmcHistoryConferenceNotTemplate(String searchKey,List<SysDept> sds) {
        return busiSmcHistoryConferenceMapper.selectBusiSmcHistoryConferenceNotTemplate(searchKey,sds);
    }

    @Override
    public List<BusiSmcHistoryConference> selectBusiSmcHistoryConferenceListBySearchKey(BusiSmcAppointmentConferenceQuery query) {
        Long deptId = query.getDeptId();
        return busiSmcHistoryConferenceMapper.selectBusiSmcHistoryConferenceListBySearchKey(deptId, query.getSearchKey(),query.getStartTime(),query.getEndTime(),1);
    }
}
