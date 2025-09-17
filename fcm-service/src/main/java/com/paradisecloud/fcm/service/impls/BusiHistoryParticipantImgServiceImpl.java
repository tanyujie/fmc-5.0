package com.paradisecloud.fcm.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantImgMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipantImg;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantImgService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-09-23
 */
@Service
public class BusiHistoryParticipantImgServiceImpl implements IBusiHistoryParticipantImgService
{
    @Resource
    private BusiHistoryParticipantImgMapper busiHistoryParticipantImgMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiHistoryParticipantImg selectBusiHistoryParticipantImgById(Integer id)
    {
        return busiHistoryParticipantImgMapper.selectBusiHistoryParticipantImgById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiHistoryParticipantImg 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiHistoryParticipantImg> selectBusiHistoryParticipantImgList(BusiHistoryParticipantImg busiHistoryParticipantImg)
    {
        return busiHistoryParticipantImgMapper.selectBusiHistoryParticipantImgList(busiHistoryParticipantImg);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiHistoryParticipantImg 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusiHistoryParticipantImg(BusiHistoryParticipantImg busiHistoryParticipantImg)
    {
        busiHistoryParticipantImg.setCreateTime(new Date());
        return busiHistoryParticipantImgMapper.insertBusiHistoryParticipantImg(busiHistoryParticipantImg);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiHistoryParticipantImg 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusiHistoryParticipantImg(BusiHistoryParticipantImg busiHistoryParticipantImg)
    {
        return busiHistoryParticipantImgMapper.updateBusiHistoryParticipantImg(busiHistoryParticipantImg);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiHistoryParticipantImgByIds(Integer[] ids)
    {
        return busiHistoryParticipantImgMapper.deleteBusiHistoryParticipantImgByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiHistoryParticipantImgById(Integer id)
    {
        return busiHistoryParticipantImgMapper.deleteBusiHistoryParticipantImgById(id);
    }
}
