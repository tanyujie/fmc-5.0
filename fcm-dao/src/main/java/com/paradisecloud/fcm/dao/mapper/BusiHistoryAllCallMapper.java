package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiHistoryAllCall;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 历史全call；busi_history_all_conference与该是一对多的关系Mapper接口
 *
 * @author lilinhai
 * @date 2021-06-04
 */
public interface BusiHistoryAllCallMapper {
    /**
     * 查询历史call；busi_history_all_conference与该是一对多的关系
     *
     * @param id 历史call；busi_history_all_conference与该是一对多的关系ID
     * @return 历史call；busi_history_all_conference与该是一对多的关系
     */
    BusiHistoryAllCall selectBusiHistoryAllCallById(Long id);

    /**
     * 根据callId查询历史call；busi_history_all_conference与该是一对多的关系
     *
     * @param callId
     * @return
     */
    BusiHistoryAllCall selectBusiHistoryAllCallByCallId(String callId);

    /**
     * 查询历史call；busi_history_all_conference与该是一对多的关系列表
     *
     * @param busiHistoryAllCall 历史call；busi_history_all_conference与该是一对多的关系
     * @return 历史call；busi_history_all_conference与该是一对多的关系集合
     */
    List<BusiHistoryAllCall> selectBusiHistoryAllCallList(BusiHistoryAllCall busiHistoryAllCall);

    /**
     * 新增历史call；busi_history_all_conference与该是一对多的关系
     *
     * @param busiHistoryAllCall 历史call；busi_history_all_conference与该是一对多的关系
     * @return 结果
     */
    int insertBusiHistoryAllCall(BusiHistoryAllCall busiHistoryAllCall);

    /**
     * 修改历史call；busi_history_all_conference与该是一对多的关系
     *
     * @param busiHistoryAllCall 历史call；busi_history_all_conference与该是一对多的关系
     * @return 结果
     */
    int updateBusiHistoryAllCall(BusiHistoryAllCall busiHistoryAllCall);

    /**
     * 删除历史call；busi_history_all_conference与该是一对多的关系
     *
     * @param id 历史call；busi_history_all_conference与该是一对多的关系ID
     * @return 结果
     */
    int deleteBusiHistoryAllCallById(Long id);

    /**
     * 批量删除历史call；busi_history_all_conference与该是一对多的关系
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteBusiHistoryAllCallByIds(Long[] ids);

    /**
     * 批量添加记录
     *
     * @param callList
     * @return
     */
    int batchAddHistoryCall(@Param("callList") List<BusiHistoryAllCall> callList);

    /**
     * 根据coSpace和callId查询历史call
     *
     * @param callId
     * @param coSpaceId
     * @return
     */
    List<BusiHistoryAllCall> findHistoryCallByCoSpaceAndCall(@Param("callId") String callId, @Param("coSpaceId") String coSpaceId);
}