package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiHistoryParticipantImg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author lilinhai
 * @date 2024-09-23
 */
public interface BusiHistoryParticipantImgMapper 
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
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiHistoryParticipantImgById(Integer id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiHistoryParticipantImgByIds(Integer[] ids);


    /**
     * 批量插入 BusiHistoryParticipantImg 列表
     *
     * @param imgList 插入的数据列表
     * @return 影响的行数
     */
    int batchInsertBusiHistoryParticipantImg(@Param("imgList") List<BusiHistoryParticipantImg> imgList);

    /**
     * 查询与会者图片
     * @param coSpace
     * @return
     */
    List<BusiHistoryParticipantImg> selectMeetingVenues(@Param("coSpace") String coSpace);

    List<String>  selectImgs(@Param("historyId") Long historyId,@Param("remoteParty") String remoteParty);
}
