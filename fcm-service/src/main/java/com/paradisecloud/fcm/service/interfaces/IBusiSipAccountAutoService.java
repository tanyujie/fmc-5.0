package com.paradisecloud.fcm.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiSipAccountAuto;

import java.util.List;

/**
 * SIP账号自动生成Service接口
 * 
 * @author lilinhai
 * @date 2022-06-24
 */
public interface IBusiSipAccountAutoService 
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
     * 批量删除SIP账号自动生成
     * 
     * @param ids 需要删除的SIP账号自动生成ID
     * @return 结果
     */
    int deleteBusiSipAccountAutoByIds(Integer[] ids);

    /**
     * 删除SIP账号自动生成信息
     * 
     * @param id SIP账号自动生成ID
     * @return 结果
     */
    int deleteBusiSipAccountAutoById(Integer id);
}
