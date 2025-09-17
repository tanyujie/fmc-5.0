package com.paradisecloud.fcm.web.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiLiveInformation;

import java.util.List;

/**
 * 直播资料Service接口
 *
 * @author lilinhai
 * @date 2024-05-07
 */
public interface IBusiLiveInformationService
{
    /**
     * 查询直播资料
     *
     * @param id 直播资料ID
     * @return 直播资料
     */
    public BusiLiveInformation selectBusiLiveInformationById(Long id);

    /**
     * 查询直播资料列表
     *
     * @param busiLiveInformation 直播资料
     * @return 直播资料集合
     */
    public List<BusiLiveInformation> selectBusiLiveInformationList(BusiLiveInformation busiLiveInformation);

    /**
     * 新增直播资料
     *
     * @param busiLiveInformation 直播资料
     * @return 结果
     */
    public int insertBusiLiveInformation(BusiLiveInformation busiLiveInformation);

    /**
     * 修改直播资料
     *
     * @param busiLiveInformation 直播资料
     * @return 结果
     */
    public int updateBusiLiveInformation(BusiLiveInformation busiLiveInformation);

    /**
     * 批量删除直播资料
     *
     * @param ids 需要删除的直播资料ID
     * @return 结果
     */
    public int deleteBusiLiveInformationByIds(Long[] ids);

    /**
     * 删除直播资料信息
     *
     * @param id 直播资料ID
     * @return 结果
     */
    public int deleteBusiLiveInformationById(Long id);
}
