package com.paradisecloud.fcm.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.dao.mapper.BusiSipAccountAutoMapper;
import com.paradisecloud.fcm.dao.model.BusiSipAccountAuto;
import com.paradisecloud.fcm.service.interfaces.IBusiSipAccountAutoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * SIP账号自动生成Service业务层处理
 * 
 * @author lilinhai
 * @date 2022-06-24
 */
@Service
public class BusiSipAccountAutoServiceImpl implements IBusiSipAccountAutoService
{
    @Resource
    private BusiSipAccountAutoMapper busiSipAccountAutoMapper;

    /**
     * 查询SIP账号自动生成
     * 
     * @param id SIP账号自动生成ID
     * @return SIP账号自动生成
     */
    @Override
    public BusiSipAccountAuto selectBusiSipAccountAutoById(Integer id)
    {
        return busiSipAccountAutoMapper.selectBusiSipAccountAutoById(id);
    }

    /**
     * 查询SIP账号自动生成列表
     * 
     * @param busiSipAccountAuto SIP账号自动生成
     * @return SIP账号自动生成
     */
    @Override
    public List<BusiSipAccountAuto> selectBusiSipAccountAutoList(BusiSipAccountAuto busiSipAccountAuto)
    {
        return busiSipAccountAutoMapper.selectBusiSipAccountAutoList(busiSipAccountAuto);
    }

    /**
     * 新增SIP账号自动生成
     * 
     * @param busiSipAccountAuto SIP账号自动生成
     * @return 结果
     */
    @Override
    public int insertBusiSipAccountAuto(BusiSipAccountAuto busiSipAccountAuto)
    {
        busiSipAccountAuto.setCreateTime(new Date());
        return busiSipAccountAutoMapper.insertBusiSipAccountAuto(busiSipAccountAuto);
    }

    /**
     * 修改SIP账号自动生成
     * 
     * @param busiSipAccountAuto SIP账号自动生成
     * @return 结果
     */
    @Override
    public int updateBusiSipAccountAuto(BusiSipAccountAuto busiSipAccountAuto)
    {
        return busiSipAccountAutoMapper.updateBusiSipAccountAuto(busiSipAccountAuto);
    }

    /**
     * 批量删除SIP账号自动生成
     * 
     * @param ids 需要删除的SIP账号自动生成ID
     * @return 结果
     */
    @Override
    public int deleteBusiSipAccountAutoByIds(Integer[] ids)
    {
        return busiSipAccountAutoMapper.deleteBusiSipAccountAutoByIds(ids);
    }

    /**
     * 删除SIP账号自动生成信息
     * 
     * @param id SIP账号自动生成ID
     * @return 结果
     */
    @Override
    public int deleteBusiSipAccountAutoById(Integer id)
    {
        return busiSipAccountAutoMapper.deleteBusiSipAccountAutoById(id);
    }
}
