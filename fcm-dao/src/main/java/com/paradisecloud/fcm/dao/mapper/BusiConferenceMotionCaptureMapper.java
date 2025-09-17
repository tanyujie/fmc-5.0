package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiConferenceMotionCapture;

import java.util.List;

/**
 * 会议动作捕捉Mapper接口
 * 
 * @author lilinhai
 * @date 2025-06-16
 */
public interface BusiConferenceMotionCaptureMapper 
{
    /**
     * 查询会议动作捕捉
     * 
     * @param id 会议动作捕捉ID
     * @return 会议动作捕捉
     */
    public BusiConferenceMotionCapture selectBusiConferenceMotionCaptureById(Long id);

    /**
     * 查询会议动作捕捉列表
     * 
     * @param busiConferenceMotionCapture 会议动作捕捉
     * @return 会议动作捕捉集合
     */
    public List<BusiConferenceMotionCapture> selectBusiConferenceMotionCaptureList(BusiConferenceMotionCapture busiConferenceMotionCapture);

    /**
     * 新增会议动作捕捉
     * 
     * @param busiConferenceMotionCapture 会议动作捕捉
     * @return 结果
     */
    public int insertBusiConferenceMotionCapture(BusiConferenceMotionCapture busiConferenceMotionCapture);

    /**
     * 修改会议动作捕捉
     * 
     * @param busiConferenceMotionCapture 会议动作捕捉
     * @return 结果
     */
    public int updateBusiConferenceMotionCapture(BusiConferenceMotionCapture busiConferenceMotionCapture);

    /**
     * 删除会议动作捕捉
     * 
     * @param id 会议动作捕捉ID
     * @return 结果
     */
    public int deleteBusiConferenceMotionCaptureById(Long id);

    /**
     * 批量删除会议动作捕捉
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConferenceMotionCaptureByIds(Long[] ids);
}
