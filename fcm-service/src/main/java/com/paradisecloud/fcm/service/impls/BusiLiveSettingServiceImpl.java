package com.paradisecloud.fcm.service.impls;

import java.util.ArrayList;
import java.util.List;

import com.paradisecloud.fcm.dao.mapper.BusiMcuZjTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuZjTemplateConference;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.dao.model.ViewTemplateConference;
import com.paradisecloud.fcm.terminal.fs.cache.LiveSettingCache;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.paradisecloud.fcm.dao.mapper.BusiLiveSettingMapper;
import com.paradisecloud.fcm.dao.model.BusiLiveSetting;
import com.paradisecloud.fcm.service.interfaces.IBusiLiveSettingService;

import javax.annotation.Resource;

/**
 * 直播地址配置管理Service业务层处理
 *
 * @author lilinhai
 * @date 2021-04-29
 */
@Service
public class BusiLiveSettingServiceImpl implements IBusiLiveSettingService {
    @Resource
    private BusiLiveSettingMapper busiLiveSettingMapper;
    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;

    /**
     * 查询直播地址配置管理
     *
     * @param id 直播地址配置管理ID
     * @return 直播地址配置管理
     */
    @Override
    public BusiLiveSetting selectBusiLiveSettingById(Long id) {
        BusiLiveSetting busiLiveSetting = LiveSettingCache.getInstance().get(id);
        if (busiLiveSetting != null) {
            return busiLiveSetting;
        }
        return busiLiveSettingMapper.selectBusiLiveSettingById(id);
    }

    /**
     * 查询直播地址配置管理列表
     *
     * @param busiLiveSetting 直播地址配置管理
     * @return 直播地址配置管理
     */
    @Override
    public List<BusiLiveSetting> selectBusiLiveSettingList(BusiLiveSetting busiLiveSetting) {
        Assert.notNull(busiLiveSetting.getDeptId(), "部门ID不能为空");
        return busiLiveSettingMapper.selectBusiLiveSettingList(busiLiveSetting);
    }

    /**
     * 新增直播地址配置管理
     *
     * @param busiLiveSetting 直播地址配置管理
     * @return 结果
     */
    @Override
    public int insertBusiLiveSetting(BusiLiveSetting busiLiveSetting) {
        Assert.notNull(busiLiveSetting.getDeptId(), "部门ID不能为空");
        Assert.isTrue(busiLiveSetting.getUrl() != null, "URL不能为空");
        int i = busiLiveSettingMapper.insertBusiLiveSetting(busiLiveSetting);
        if (i > 0) {
            LiveSettingCache.getInstance().put(busiLiveSetting.getId(), busiLiveSetting);
        }
        return i;
    }

    /**
     * 修改直播地址配置管理
     *
     * @param busiLiveSetting 直播地址配置管理
     * @return 结果
     */
    @Override
    public int updateBusiLiveSetting(BusiLiveSetting busiLiveSetting) {
        Assert.notNull(busiLiveSetting.getDeptId(), "部门ID不能为空");
        Assert.isTrue(busiLiveSetting.getUrl() != null, "URL不能为空");
        int i = busiLiveSettingMapper.updateBusiLiveSetting(busiLiveSetting);
        if (i > 0) {
            LiveSettingCache.getInstance().put(busiLiveSetting.getId(), busiLiveSetting);
        }
        return i;
    }

    /**
     * 批量删除直播地址配置管理
     *
     * @param ids 需要删除的直播地址配置管理ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveSettingByIds(Long[] ids) {
        for (Long id : ids) {
            BusiLiveSetting busiLiveSetting = busiLiveSettingMapper.selectBusiLiveSettingById(id);
            ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
            viewTemplateConferenceCon.setStreamUrl(busiLiveSetting.getUrl());
            List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCon);
            if (viewTemplateConferenceList != null && viewTemplateConferenceList.size() > 0) {
                Assert.isTrue(false, "该直播频道已被使用，不能直接删除！");
            }
        }
        int i = busiLiveSettingMapper.deleteBusiLiveSettingByIds(ids);

        if (i > 0 ) {
            for (Long id : ids) {
                LiveSettingCache.getInstance().remove(id);
            }
        }
        return i;
    }

    /**
     * 删除直播地址配置管理信息
     *
     * @param id 直播地址配置管理ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveSettingById(Long id) {
        BusiLiveSetting busiLiveSetting = busiLiveSettingMapper.selectBusiLiveSettingById(id);
        ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
        viewTemplateConferenceCon.setStreamUrl(busiLiveSetting.getUrl());
        List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCon);
        if (viewTemplateConferenceList != null && viewTemplateConferenceList.size() > 0) {
            Assert.isTrue(false, "该直播频道已被使用，不能直接删除！");
        }
        int i = busiLiveSettingMapper.deleteBusiLiveSettingById(id);
        if (i > 0) {
            LiveSettingCache.getInstance().remove(id);
        }
        return i;
    }

    @Override
    public List<BusiLiveSetting> getBusiLiveSettingByDeptId(Long deptId) {
        BusiLiveSetting busiLiveSettingCon = new BusiLiveSetting();
        busiLiveSettingCon.setStatus(1);
        busiLiveSettingCon.setDeptId(deptId);
        List<BusiLiveSetting> busiLiveSettingListNew = new ArrayList<>();
        List<BusiLiveSetting> busiLiveSettingList = busiLiveSettingMapper.selectBusiLiveSettingList(busiLiveSettingCon);
        if (busiLiveSettingList != null && busiLiveSettingList.size() > 0) {
            for (BusiLiveSetting busiLiveSetting : busiLiveSettingList) {
                // 新直播方式:已经被配置到会议的地址不能再被配置
                if (StringUtils.isNotEmpty(busiLiveSetting.getRemoteParty())) {
                    ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                    viewTemplateConferenceCon.setStreamUrl(busiLiveSetting.getUrl());
                    List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCon);
                    if (viewTemplateConferenceList == null || viewTemplateConferenceList.size() == 0) {
                        busiLiveSettingListNew.add(busiLiveSetting);
                    } else {
                        if (deptId.longValue() == busiLiveSetting.getDeptId().longValue()) {
                            busiLiveSetting.setName("[使用中]" + busiLiveSetting.getName());
                            busiLiveSettingListNew.add(busiLiveSetting);
                        }
                    }
                } else {
                    busiLiveSettingListNew.add(busiLiveSetting);
                }
            }
            return busiLiveSettingListNew;
        } else {
            SysDept sysDept = SysDeptCache.getInstance().get(deptId);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0) {
                return getBusiLiveSettingByDeptId(sysDept.getParentId());
            } else {
                return null;
            }
        }
    }
}
