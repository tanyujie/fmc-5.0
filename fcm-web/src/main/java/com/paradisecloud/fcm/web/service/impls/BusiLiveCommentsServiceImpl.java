package com.paradisecloud.fcm.web.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.dao.mapper.BusiLiveCommentsMapper;
import com.paradisecloud.fcm.dao.model.BusiLiveComments;
import com.paradisecloud.fcm.web.service.interfaces.IBusiLiveCommentsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 直播回放评论Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-05-21
 */
@Service
public class BusiLiveCommentsServiceImpl implements IBusiLiveCommentsService
{
    @Resource
    private BusiLiveCommentsMapper busiLiveCommentsMapper;

    /**
     * 查询直播回放评论
     * 
     * @param id 直播回放评论ID
     * @return 直播回放评论
     */
    @Override
    public BusiLiveComments selectBusiLiveCommentsById(Long id)
    {
        return busiLiveCommentsMapper.selectBusiLiveCommentsById(id);
    }

    /**
     * 查询直播回放评论列表
     * 
     * @param busiLiveComments 直播回放评论
     * @return 直播回放评论
     */
    @Override
    public List<BusiLiveComments> selectBusiLiveCommentsList(BusiLiveComments busiLiveComments)
    {
        return busiLiveCommentsMapper.selectBusiLiveCommentsList(busiLiveComments);
    }

    /**
     * 新增直播回放评论
     * 
     * @param busiLiveComments 直播回放评论
     * @return 结果
     */
    @Override
    public int insertBusiLiveComments(BusiLiveComments busiLiveComments)
    {
        busiLiveComments.setCreateTime(new Date());
        return busiLiveCommentsMapper.insertBusiLiveComments(busiLiveComments);
    }

    /**
     * 修改直播回放评论
     * 
     * @param busiLiveComments 直播回放评论
     * @return 结果
     */
    @Override
    public int updateBusiLiveComments(BusiLiveComments busiLiveComments)
    {
        return busiLiveCommentsMapper.updateBusiLiveComments(busiLiveComments);
    }

    /**
     * 批量删除直播回放评论
     * 
     * @param ids 需要删除的直播回放评论ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveCommentsByIds(Long[] ids)
    {
        return busiLiveCommentsMapper.deleteBusiLiveCommentsByIds(ids);
    }

    /**
     * 删除直播回放评论信息
     * 
     * @param id 直播回放评论ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveCommentsById(Long id)
    {
        return busiLiveCommentsMapper.deleteBusiLiveCommentsById(id);
    }
}
