package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiSipAccountAuto;

import java.util.Date;
import java.util.List;

/**
 * SIP账号自动生成Mapper接口
 * 
 * @author lilinhai
 * @date 2022-06-24
 */
public interface BusiSipAccountAutoMapper 
{
    /**
     * 查询SIP账号自动生成
     * 
     * @param id SIP账号自动生成ID
     * @return SIP账号自动生成
     */
    BusiSipAccountAuto selectBusiSipAccountAutoById(Integer id);

    /**
     * 查询SIP账号自动生成列表
     * 
     * @param busiSipAccountAuto SIP账号自动生成
     * @return SIP账号自动生成集合
     */
    List<BusiSipAccountAuto> selectBusiSipAccountAutoList(BusiSipAccountAuto busiSipAccountAuto);

    /**
     * 新增SIP账号自动生成
     * 
     * @param busiSipAccountAuto SIP账号自动生成
     * @return 结果
     */
    int insertBusiSipAccountAuto(BusiSipAccountAuto busiSipAccountAuto);

    /**
     * 修改SIP账号自动生成
     * 
     * @param busiSipAccountAuto SIP账号自动生成
     * @return 结果
     */
    int updateBusiSipAccountAuto(BusiSipAccountAuto busiSipAccountAuto);

    /**
     * 删除SIP账号自动生成
     * 
     * @param id SIP账号自动生成ID
     * @return 结果
     */
    int deleteBusiSipAccountAutoById(Integer id);

    /**
     * 批量删除SIP账号自动生成
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteBusiSipAccountAutoByIds(Integer[] ids);

    /**
     * 批量删除SIP账号自动生成
     *
     * @param dateTime 需要删除的数据ID
     * @return 结果
     */
    int deleteBusiSipAccountAutoOfOld(Date dateTime);
}
