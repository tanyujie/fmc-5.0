package com.paradisecloud.fcm.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiHistoryParticipantImg;

import java.util.List;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author lilinhai
 * @date 2024-09-23
 */
public interface IBusiHistoryParticipantImgService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiHistoryParticipantImg selectBusiHistoryParticipantImgById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiHistoryParticipantImg 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiHistoryParticipantImg> selectBusiHistoryParticipantImgList(BusiHistoryParticipantImg busiHistoryParticipantImg);

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiHistoryParticipantImg 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiHistoryParticipantImg(BusiHistoryParticipantImg busiHistoryParticipantImg);

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiHistoryParticipantImg 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiHistoryParticipantImg(BusiHistoryParticipantImg busiHistoryParticipantImg);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiHistoryParticipantImgByIds(Integer[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiHistoryParticipantImgById(Integer id);
}
