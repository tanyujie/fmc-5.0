package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiSipAccount;

/**
 * sip账号信息Mapper接口
 * 
 * @author zyz
 * @date 2021-09-24
 */
public interface BusiSipAccountMapper 
{
    /**
     * 查询sip账号信息
     * 
     * @param id sip账号信息ID
     * @return sip账号信息
     */
    public BusiSipAccount selectBusiSipAccountById(Long id);

    /**
     * 查询sip账号信息列表
     * 
     * @param busiSipAccount sip账号信息
     * @return sip账号信息集合
     */
    public List<BusiSipAccount> selectBusiSipAccountList(BusiSipAccount busiSipAccount);

    /**
     * 新增sip账号信息
     * 
     * @param busiSipAccount sip账号信息
     * @return 结果
     */
    public int insertBusiSipAccount(BusiSipAccount busiSipAccount);

    /**
     * 修改sip账号信息
     * 
     * @param busiSipAccount sip账号信息
     * @return 结果
     */
    public int updateBusiSipAccount(BusiSipAccount busiSipAccount);

    /**
     * 删除sip账号信息
     * 
     * @param id sip账号信息ID
     * @return 结果
     */
    public int deleteBusiSipAccountById(Long id);

    /**
     * 批量删除sip账号信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSipAccountByIds(Long[] ids);
}
