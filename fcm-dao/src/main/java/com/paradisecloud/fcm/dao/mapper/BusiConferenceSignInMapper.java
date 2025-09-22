package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiConferenceSignIn;
import com.paradisecloud.fcm.dao.model.BusiEduClass;

import java.util.List;


/**
 * 会议模板的签到主Mapper接口
 *
 * @author lilinhai
 * @date 2025-09-18
 */
public interface BusiConferenceSignInMapper
{
    /**
     * 查询会议模板的签到主
     *
     * @param signInId 会议模板的签到主ID
     * @return 会议模板的签到主
     */
    public BusiConferenceSignIn selectBusiConferenceSignInById(Long signInId);
    public BusiConferenceSignIn selectBusiConferenceSignInByConferenceId(Long conferenceId);

    /**
     * 查询会议模板的签到主列表
     *
     * @param busiConferenceSignIn 会议模板的签到主
     * @return 会议模板的签到主集合
     */
    public List<BusiConferenceSignIn> selectBusiConferenceSignInList(BusiConferenceSignIn busiConferenceSignIn);

    /**
     * 新增会议模板的签到主
     *
     * @param busiConferenceSignIn 会议模板的签到主
     * @return 结果
     */
    public int insertBusiConferenceSignIn(BusiConferenceSignIn busiConferenceSignIn);

    /**
     * 修改会议模板的签到主
     *
     * @param busiConferenceSignIn 会议模板的签到主
     * @return 结果
     */
    public int updateBusiConferenceSignIn(BusiConferenceSignIn busiConferenceSignIn);

    /**
     * 删除会议模板的签到主
     *
     * @param signInId 会议模板的签到主ID
     * @return 结果
     */
    public int deleteBusiConferenceSignInById(Long signInId);

    /**
     * 批量删除会议模板的签到主
     *
     * @param signInIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConferenceSignInByIds(Long[] signInIds);
}

