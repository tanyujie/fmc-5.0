package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiConferenceUserSignIn;

import java.util.List;

/**
 * 成员签到关联Mapper接口
 *
 * @author lilinhai
 * @date 2025-09-18
 */
public interface BusiConferenceUserSignInMapper
{
    /**
     * 查询成员签到关联
     *
     * @param id 成员签到关联ID
     * @return 成员签到关联
     */
    public BusiConferenceUserSignIn selectBusiConferenceUserSignInById(Long id);

    /**
     * 查询成员签到关联列表
     *
     * @param busiConferenceUserSignIn 成员签到关联
     * @return 成员签到关联集合
     */
    public List<BusiConferenceUserSignIn> selectBusiConferenceUserSignInList(BusiConferenceUserSignIn busiConferenceUserSignIn);

    /**
     * 新增成员签到关联
     *
     * @param busiConferenceUserSignIn 成员签到关联
     * @return 结果
     */
    public int insertBusiConferenceUserSignIn(BusiConferenceUserSignIn busiConferenceUserSignIn);

    /**
     * 修改成员签到关联
     *
     * @param busiConferenceUserSignIn 成员签到关联
     * @return 结果
     */
    public int updateBusiConferenceUserSignIn(BusiConferenceUserSignIn busiConferenceUserSignIn);

    /**
     * 删除成员签到关联
     *
     * @param id 成员签到关联ID
     * @return 结果
     */
    public int deleteBusiConferenceUserSignInById(Long id);

    /**
     * 批量删除成员签到关联
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConferenceUserSignInByIds(Long[] ids);
}
