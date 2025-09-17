package com.paradisecloud.fcm.web.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.dao.mapper.BusiLiveBroadcastMapper;
import com.paradisecloud.fcm.dao.mapper.BusiLiveInformationMapper;
import com.paradisecloud.fcm.dao.model.BusiLiveBroadcast;
import com.paradisecloud.fcm.dao.model.BusiLiveInformation;
import com.paradisecloud.fcm.web.service.interfaces.IBusiLiveInformationService;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 直播资料Service业务层处理
 *
 * @author lilinhai
 * @date 2024-05-07
 */
@Service
public class BusiLiveInformationServiceImpl implements IBusiLiveInformationService
{
    @Resource
    private BusiLiveInformationMapper busiLiveInformationMapper;
    @Resource
    private BusiLiveBroadcastMapper busiLiveBroadcastMapper;

    /**
     * 查询直播资料
     *
     * @param id 直播资料ID
     * @return 直播资料
     */
    @Override
    public BusiLiveInformation selectBusiLiveInformationById(Long id)
    {
        return busiLiveInformationMapper.selectBusiLiveInformationById(id);
    }

    /**
     * 查询直播资料列表
     *
     * @param busiLiveInformation 直播资料
     * @return 直播资料
     */
    @Override
    public List<BusiLiveInformation> selectBusiLiveInformationList(BusiLiveInformation busiLiveInformation)
    {
        return busiLiveInformationMapper.selectBusiLiveInformationList(busiLiveInformation);
    }

    /**
     * 新增直播资料
     *
     * @param busiLiveInformation 直播资料
     * @return 结果
     */
    @Override
    public int insertBusiLiveInformation(BusiLiveInformation busiLiveInformation)
    {
        int i = 0;
        busiLiveInformation.setCreateTime(new Date());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiLiveInformation.setCreateBy(loginUser.getUser().getUserName());
        }

        Long liveBroadcastId = busiLiveInformation.getLiveBroadcastId();
        if (liveBroadcastId != null) {
            BusiLiveBroadcast busiLiveBroadcast = busiLiveBroadcastMapper.selectBusiLiveBroadcastById(liveBroadcastId);
            if (busiLiveBroadcast != null) {
                i = busiLiveInformationMapper.insertBusiLiveInformation(busiLiveInformation);
            }
        }
        return i;
    }

    /**
     * 修改直播资料
     *
     * @param busiLiveInformation 直播资料
     * @return 结果
     */
    @Override
    public int updateBusiLiveInformation(BusiLiveInformation busiLiveInformation)
    {
        busiLiveInformation.setUpdateTime(new Date());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiLiveInformation.setUpdateBy(loginUser.getUser().getUserName());
        }
        return busiLiveInformationMapper.updateBusiLiveInformation(busiLiveInformation);
    }

    /**
     * 批量删除直播资料
     *
     * @param ids 需要删除的直播资料ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveInformationByIds(Long[] ids)
    {
        int i = 0;
        for (Long id : ids) {
            i += deleteBusiLiveInformationById(id);
        }
        return i;
    }

    /**
     * 删除直播资料信息
     *
     * @param id 直播资料ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveInformationById(Long id)
    {
        return busiLiveInformationMapper.deleteBusiLiveInformationById(id);
    }
}
