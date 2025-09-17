package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiLiveComments;

import java.util.List;

/**
 * 直播回放评论Mapper接口
 * 
 * @author lilinhai
 * @date 2024-05-21
 */
public interface BusiLiveCommentsMapper 
{
    /**
     * 查询直播回放评论
     * 
     * @param id 直播回放评论ID
     * @return 直播回放评论
     */
    public BusiLiveComments selectBusiLiveCommentsById(Long id);

    /**
     * 查询直播回放评论列表
     * 
     * @param busiLiveComments 直播回放评论
     * @return 直播回放评论集合
     */
    public List<BusiLiveComments> selectBusiLiveCommentsList(BusiLiveComments busiLiveComments);

    /**
     * 新增直播回放评论
     * 
     * @param busiLiveComments 直播回放评论
     * @return 结果
     */
    public int insertBusiLiveComments(BusiLiveComments busiLiveComments);

    /**
     * 修改直播回放评论
     * 
     * @param busiLiveComments 直播回放评论
     * @return 结果
     */
    public int updateBusiLiveComments(BusiLiveComments busiLiveComments);

    /**
     * 删除直播回放评论
     * 
     * @param id 直播回放评论ID
     * @return 结果
     */
    public int deleteBusiLiveCommentsById(Long id);

    /**
     * 批量删除直播回放评论
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiLiveCommentsByIds(Long[] ids);
}
