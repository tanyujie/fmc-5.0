package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiConferenceUserSignIn;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
     * 对应 XML 中的 selectSignInList 方法（纯 MyBatis 实现）
     * @param userSignIn 入参（与原 XML 的 parameterType 一致）
     * @return 签到记录列表
     */
/*    @Select("<script>" +
            "select id, sign_in_id, conference_id, user_id, user_nickname, sign_in_time, sign_status " +
            "from busi_conference_user_sign_in " +
            "<where>" +
            // 关键修改：所有参数前加 "userSignIn." 前缀
            "   <if test='userSignIn.signInId > 0'> and sign_in_id = #{userSignIn.signInId}</if>" +
            "   <if test='userSignIn.conferenceId > 0'> and conference_id = #{userSignIn.conferenceId}</if>" +
            "   <if test='userSignIn.userId != null'> and user_id = #{userSignIn.userId}</if>" +
            "   <if test='userSignIn.userNickname != null and userSignIn.userNickname != \"\"'> and user_nickname like concat('%', #{userSignIn.userNickname}, '%')</if>" +
            "   <if test='userSignIn.signInTime > 0'> and sign_in_time = #{userSignIn.signInTime}</if>" +
            "   <if test='userSignIn.signStatus != null and userSignIn.signStatus > 0'> and sign_status = #{userSignIn.signStatus}</if>" +
            "</where>" +
            "</script>")*/
// 入参的 @Param("userSignIn") 保持不变
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
    /**
     * 根据会议ID、签到ID和用户昵称查询签到记录
     * @param conferenceId 会议ID
     * @param signInId 签到ID
     * @param userNickname 用户昵称
     * @return 签到记录，不存在则返回null
     */
    BusiConferenceUserSignIn selectByConferenceIdAndSignInIdAndNickname(
            @Param("conferenceId") Long conferenceId,
            @Param("signInId") Long signInId,
            @Param("userNickname") String userNickname
    );
}
