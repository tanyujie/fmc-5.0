package com.paradisecloud.fcm.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.dao.mapper.BusiConferenceMotionCaptureMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceMotionCapture;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceMotionCaptureService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 会议动作捕捉Service业务层处理
 * 
 * @author lilinhai
 * @date 2025-06-16
 */
@Service
public class BusiConferenceMotionCaptureServiceImpl implements IBusiConferenceMotionCaptureService
{
    @Resource
    private BusiConferenceMotionCaptureMapper busiConferenceMotionCaptureMapper;

    /**
     * 查询会议动作捕捉
     * 
     * @param id 会议动作捕捉ID
     * @return 会议动作捕捉
     */
    @Override
    public BusiConferenceMotionCapture selectBusiConferenceMotionCaptureById(Long id)
    {
        return busiConferenceMotionCaptureMapper.selectBusiConferenceMotionCaptureById(id);
    }

    /**
     * 查询会议动作捕捉列表
     * 
     * @param busiConferenceMotionCapture 会议动作捕捉
     * @return 会议动作捕捉
     */
    @Override
    public List<BusiConferenceMotionCapture> selectBusiConferenceMotionCaptureList(BusiConferenceMotionCapture busiConferenceMotionCapture)
    {
        return busiConferenceMotionCaptureMapper.selectBusiConferenceMotionCaptureList(busiConferenceMotionCapture);
    }

    /**
     * 新增会议动作捕捉
     * 
     * @param busiConferenceMotionCapture 会议动作捕捉
     * @return 结果
     */
    @Override
    public int insertBusiConferenceMotionCapture(BusiConferenceMotionCapture busiConferenceMotionCapture)
    {
        busiConferenceMotionCapture.setCreateTime(new Date());
        return busiConferenceMotionCaptureMapper.insertBusiConferenceMotionCapture(busiConferenceMotionCapture);
    }

    /**
     * 修改会议动作捕捉
     * 
     * @param busiConferenceMotionCapture 会议动作捕捉
     * @return 结果
     */
    @Override
    public int updateBusiConferenceMotionCapture(BusiConferenceMotionCapture busiConferenceMotionCapture)
    {
        return busiConferenceMotionCaptureMapper.updateBusiConferenceMotionCapture(busiConferenceMotionCapture);
    }

    /**
     * 批量删除会议动作捕捉
     * 
     * @param ids 需要删除的会议动作捕捉ID
     * @return 结果
     */
    @Override
    public int deleteBusiConferenceMotionCaptureByIds(Long[] ids)
    {
        return busiConferenceMotionCaptureMapper.deleteBusiConferenceMotionCaptureByIds(ids);
    }

    /**
     * 删除会议动作捕捉信息
     * 
     * @param id 会议动作捕捉ID
     * @return 结果
     */
    @Override
    public int deleteBusiConferenceMotionCaptureById(Long id)
    {
        return busiConferenceMotionCaptureMapper.deleteBusiConferenceMotionCaptureById(id);
    }
}
