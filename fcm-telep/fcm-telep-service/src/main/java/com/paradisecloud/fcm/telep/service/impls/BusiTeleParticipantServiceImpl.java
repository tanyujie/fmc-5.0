package com.paradisecloud.fcm.telep.service.impls;

import com.paradisecloud.fcm.telep.dao.mapper.BusiTeleParticipantMapper;
import com.paradisecloud.fcm.telep.dao.model.BusiTeleParticipant;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiTeleParticipantService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author nj
 * @date 2022/10/21 11:03
 */
@Service
public class BusiTeleParticipantServiceImpl implements IBusiTeleParticipantService
{
    @Resource
    private BusiTeleParticipantMapper busiTeleParticipantMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiTeleParticipant selectBusiTeleParticipantById(Integer id)
    {
        return busiTeleParticipantMapper.selectBusiTeleParticipantById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param busiTeleParticipant 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiTeleParticipant> selectBusiTeleParticipantList(BusiTeleParticipant busiTeleParticipant)
    {
        return busiTeleParticipantMapper.selectBusiTeleParticipantList(busiTeleParticipant);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param busiTeleParticipant 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusiTeleParticipant(BusiTeleParticipant busiTeleParticipant)
    {
        busiTeleParticipant.setCreateTime(new Date());
        return busiTeleParticipantMapper.insertBusiTeleParticipant(busiTeleParticipant);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param busiTeleParticipant 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusiTeleParticipant(BusiTeleParticipant busiTeleParticipant)
    {
        return busiTeleParticipantMapper.updateBusiTeleParticipant(busiTeleParticipant);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiTeleParticipantByIds(Integer[] ids)
    {
        return busiTeleParticipantMapper.deleteBusiTeleParticipantByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiTeleParticipantById(Integer id)
    {
        return busiTeleParticipantMapper.deleteBusiTeleParticipantById(id);
    }

    @Override
    public int deleteBusiTeleParticipantByConferenceNumber(String number) {
        return busiTeleParticipantMapper.deleteBusiTeleParticipantByConferenceNumber(number);
    }
}
