package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiConferenceCustomButton;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会议自定义按钮Mapper接口
 * 
 * @author lilinhai
 * @date 2024-07-05
 */
public interface BusiConferenceCustomButtonMapper 
{
    /**
     * 查询会议自定义按钮
     * 
     * @param id 会议自定义按钮ID
     * @return 会议自定义按钮
     */
    public BusiConferenceCustomButton selectBusiConferenceCustomButtonById(@Param("id") String id, @Param("mcuType") String mcuType);

    /**
     * 查询会议自定义按钮列表
     * 
     * @param busiConferenceCustomButton 会议自定义按钮
     * @return 会议自定义按钮集合
     */
    public List<BusiConferenceCustomButton> selectBusiConferenceCustomButtonList(BusiConferenceCustomButton busiConferenceCustomButton);

    /**
     * 新增会议自定义按钮
     * 
     * @param busiConferenceCustomButton 会议自定义按钮
     * @return 结果
     */
    public int insertBusiConferenceCustomButton(BusiConferenceCustomButton busiConferenceCustomButton);

    /**
     * 修改会议自定义按钮
     * 
     * @param busiConferenceCustomButton 会议自定义按钮
     * @return 结果
     */
    public int updateBusiConferenceCustomButton(BusiConferenceCustomButton busiConferenceCustomButton);

    /**
     * 删除会议自定义按钮
     * 
     * @param id 会议自定义按钮ID
     * @return 结果
     */
    public int deleteBusiConferenceCustomButtonById(@Param("id") String id, @Param("mcuType") String mcuType);
}
