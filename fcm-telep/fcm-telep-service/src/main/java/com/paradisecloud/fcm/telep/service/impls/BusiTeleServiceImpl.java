package com.paradisecloud.fcm.telep.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.telep.dao.mapper.BusiTeleMapper;
import com.paradisecloud.fcm.telep.dao.model.BusiTele;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiTeleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * tele终端信息Service业务层处理
 * 
 * @author lilinhai
 * @date 2022-10-11
 */
@Service
public class BusiTeleServiceImpl implements IBusiTeleService
{
    @Resource
    private BusiTeleMapper busiTeleMapper;

    /**
     * 查询tele终端信息
     * 
     * @param id tele终端信息ID
     * @return tele终端信息
     */
    @Override
    public BusiTele selectBusiTeleById(Long id)
    {
        return busiTeleMapper.selectBusiTeleById(id);
    }

    /**
     * 查询tele终端信息列表
     * 
     * @param busiTele tele终端信息
     * @return tele终端信息
     */
    @Override
    public List<BusiTele> selectBusiTeleList(BusiTele busiTele)
    {
        return busiTeleMapper.selectBusiTeleList(busiTele);
    }

    /**
     * 新增tele终端信息
     * 
     * @param busiTele tele终端信息
     * @return 结果
     */
    @Override
    public int insertBusiTele(BusiTele busiTele)
    {
        busiTele.setCreateTime(new Date());
        return busiTeleMapper.insertBusiTele(busiTele);
    }

    /**
     * 修改tele终端信息
     * 
     * @param busiTele tele终端信息
     * @return 结果
     */
    @Override
    public int updateBusiTele(BusiTele busiTele)
    {
        busiTele.setUpdateTime(new Date());
        return busiTeleMapper.updateBusiTele(busiTele);
    }

    /**
     * 批量删除tele终端信息
     * 
     * @param ids 需要删除的tele终端信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiTeleByIds(Long[] ids)
    {
        return busiTeleMapper.deleteBusiTeleByIds(ids);
    }

    /**
     * 删除tele终端信息信息
     * 
     * @param id tele终端信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiTeleById(Long id)
    {
        return busiTeleMapper.deleteBusiTeleById(id);
    }
}
