package com.paradisecloud.fcm.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiConferenceCustomButton;

import java.util.List;

/**
 * 会议自定义按钮Service接口
 * 
 * @author lilinhai
 * @date 2024-07-05
 */
public interface IBusiConferenceCustomButtonService 
{
    /**
     * 查询会议自定义按钮
     * 
     * @param id 会议自定义按钮ID
     * @return 会议自定义按钮
     */
    public BusiConferenceCustomButton selectBusiConferenceCustomButtonById(String id, String mcuType);

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
     * 修改会议自定义按钮
     *
     * @param busiConferenceCustomButtonList 会议自定义按钮
     * @return 结果
     */
    public int updateBusiConferenceCustomButton(List<BusiConferenceCustomButton> busiConferenceCustomButtonList, String mcuType);

    /**
     * 删除会议自定义按钮信息
     * 
     * @param id 会议自定义按钮ID
     * @return 结果
     */
    public int deleteBusiConferenceCustomButtonById(String id, String mcuType);

    /**
     * 删除会议自定义按钮信息
     *
     * @param ids 会议自定义按钮ID
     * @return 结果
     */
    public int deleteBusiConferenceCustomButtonByIds(String[] ids, String mcuType);
}
