package com.paradisecloud.fcm.mcu.zj.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.dao.mapper.BusiMcuZjResourceTemplateMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuZjResourceTemplate;
import com.paradisecloud.fcm.dao.model.BusiMcuZjTemplateConference;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.api.ConferenceManageApi;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.model.SourceTemplate;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmAddResourceRequest;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmAddResourceTmplResponse;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjResourceTemplateService;
import com.sinhy.exception.SystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 紫荆MCU资源模板Service业务层处理
 * 
 * @author lilinhai
 * @date 2023-03-17
 */
@Transactional
@Service
public class BusiMcuZjResourceTemplateServiceImpl implements IBusiMcuZjResourceTemplateService
{
    @Resource
    private BusiMcuZjResourceTemplateMapper busiMcuZjResourceTemplateMapper;
    @Resource
    private BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper;

    /**
     * 查询紫荆MCU资源模板
     * 
     * @param id 紫荆MCU资源模板ID
     * @return 紫荆MCU资源模板
     */
    @Override
    public BusiMcuZjResourceTemplate selectBusiMcuZjResourceTemplateById(Long id)
    {
        return busiMcuZjResourceTemplateMapper.selectBusiMcuZjResourceTemplateById(id);
    }

    /**
     * 查询紫荆MCU资源模板列表
     * 
     * @param busiMcuZjResourceTemplate 紫荆MCU资源模板
     * @return 紫荆MCU资源模板
     */
    @Override
    public List<BusiMcuZjResourceTemplate> selectBusiMcuZjResourceTemplateList(BusiMcuZjResourceTemplate busiMcuZjResourceTemplate)
    {
        return busiMcuZjResourceTemplateMapper.selectBusiMcuZjResourceTemplateList(busiMcuZjResourceTemplate);
    }

    /**
     * 新增紫荆MCU资源模板
     * 
     * @param busiMcuZjResourceTemplate 紫荆MCU资源模板
     * @return 结果
     */
    @Override
    public int insertBusiMcuZjResourceTemplate(BusiMcuZjResourceTemplate busiMcuZjResourceTemplate)
    {
        busiMcuZjResourceTemplate.setCreateTime(new Date());
        setBusiMcuZjResourceTemplate(busiMcuZjResourceTemplate);
        BusiMcuZjResourceTemplate busiMcuZjResourceTemplateCon = new BusiMcuZjResourceTemplate();
        busiMcuZjResourceTemplateCon.setName(busiMcuZjResourceTemplate.getName());
        busiMcuZjResourceTemplateCon.setMcuZjServerId(busiMcuZjResourceTemplate.getMcuZjServerId());
        List<BusiMcuZjResourceTemplate> busiMcuZjResourceTemplateList = busiMcuZjResourceTemplateMapper.selectBusiMcuZjResourceTemplateList(busiMcuZjResourceTemplateCon);
        if (busiMcuZjResourceTemplateList.size() > 0) {
            throw new SystemException("资源模板已存在，请勿重复添加!");
        }
        int rows = busiMcuZjResourceTemplateMapper.insertBusiMcuZjResourceTemplate(busiMcuZjResourceTemplate);
        if (rows > 0) {
            McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(busiMcuZjResourceTemplate.getMcuZjServerId());
            if (mcuZjBridge != null) {
                CmAddResourceRequest cmAddResourceRequest = CmAddResourceRequest.buildDefaultRequest();
                cmAddResourceRequest.setName(busiMcuZjResourceTemplate.getName());
                cmAddResourceRequest.setHas_mosic(busiMcuZjResourceTemplate.getHasMosic());
                cmAddResourceRequest.setHas_record(busiMcuZjResourceTemplate.getHasRecord());
                cmAddResourceRequest.setMax_mosic(busiMcuZjResourceTemplate.getMaxMosic());
                cmAddResourceRequest.setMax_spk_mosic(busiMcuZjResourceTemplate.getMaxSpkMosic());
                cmAddResourceRequest.setMax_guest_mosic(busiMcuZjResourceTemplate.getMaxGuestMosic());
                cmAddResourceRequest.setMax_chair_mosic(busiMcuZjResourceTemplate.getMaxChairMosic());
                cmAddResourceRequest.setChair_copy(busiMcuZjResourceTemplate.getChairCopy());
                cmAddResourceRequest.setRes_bw(busiMcuZjResourceTemplate.getResBw());
                cmAddResourceRequest.setSingle_view(busiMcuZjResourceTemplate.getSingleView());
                CmAddResourceTmplResponse cmAddResourceTmplResponse = mcuZjBridge.getConferenceManageApi().addResourceTmpl(cmAddResourceRequest);
                if (cmAddResourceTmplResponse != null && cmAddResourceTmplResponse.getResult().contains("success")) {
                }
                mcuZjBridge.setSourceTemplateChanged(true);
            }
        }
        return rows;
    }

    /**
     * 修改紫荆MCU资源模板
     * 
     * @param busiMcuZjResourceTemplate 紫荆MCU资源模板
     * @return 结果
     */
    @Override
    public int updateBusiMcuZjResourceTemplate(BusiMcuZjResourceTemplate busiMcuZjResourceTemplate)
    {
        setBusiMcuZjResourceTemplate(busiMcuZjResourceTemplate);
        return busiMcuZjResourceTemplateMapper.updateBusiMcuZjResourceTemplate(busiMcuZjResourceTemplate);
    }

    /**
     * 批量删除紫荆MCU资源模板
     * 
     * @param ids 需要删除的紫荆MCU资源模板ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuZjResourceTemplateByIds(Long[] ids)
    {
        return busiMcuZjResourceTemplateMapper.deleteBusiMcuZjResourceTemplateByIds(ids);
    }

    /**
     * 删除紫荆MCU资源模板信息
     * 
     * @param id 紫荆MCU资源模板ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuZjResourceTemplateById(Long id)
    {
        BusiMcuZjResourceTemplate busiMcuZjResourceTemplate = busiMcuZjResourceTemplateMapper.selectBusiMcuZjResourceTemplateById(id);
        if (busiMcuZjResourceTemplate != null) {
            McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(busiMcuZjResourceTemplate.getMcuZjServerId());
            if (mcuZjBridge != null) {
                SourceTemplate sourceTemplate = mcuZjBridge.getSourceTemplate(busiMcuZjResourceTemplate.getName());
                if (sourceTemplate != null) {
                    BusiMcuZjTemplateConference busiMcuZjTemplateConferenceCon = new BusiMcuZjTemplateConference();
                    busiMcuZjTemplateConferenceCon.setResourceTemplateId(sourceTemplate.getId());
                    List<BusiMcuZjTemplateConference> busiMcuZjTemplateConferenceList = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceList(busiMcuZjTemplateConferenceCon);
                    if (busiMcuZjTemplateConferenceList.size() > 0) {
                        throw new SystemException("该资源模板正被会议模板使用，无法删除！");
                    }
                    int rows = busiMcuZjResourceTemplateMapper.deleteBusiMcuZjResourceTemplateById(id);
                    if (rows > 0) {
                        try {
                            mcuZjBridge.removeSourceTemplate(busiMcuZjResourceTemplate.getName());
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
        return 1;
    }

    /**
     * 设置默认会议资源模板
     *
     * @param id 紫荆MCU资源模板id
     * @return 结果
     */
    public int setDefaultBusiMcuZjResourceTemplate(Long id) {
        BusiMcuZjResourceTemplate busiMcuZjResourceTemplate = busiMcuZjResourceTemplateMapper.selectBusiMcuZjResourceTemplateById(id);
        if (busiMcuZjResourceTemplate != null) {
            BusiMcuZjResourceTemplate busiMcuZjResourceTemplateCon = new BusiMcuZjResourceTemplate();
            busiMcuZjResourceTemplateCon.setMcuZjServerId(busiMcuZjResourceTemplate.getMcuZjServerId());
            List<BusiMcuZjResourceTemplate> busiMcuZjResourceTemplateList = busiMcuZjResourceTemplateMapper.selectBusiMcuZjResourceTemplateList(busiMcuZjResourceTemplateCon);
            for (BusiMcuZjResourceTemplate busiMcuZjResourceTemplateTemp : busiMcuZjResourceTemplateList) {
                BusiMcuZjResourceTemplate busiMcuZjResourceTemplateUpdate = new BusiMcuZjResourceTemplate();
                busiMcuZjResourceTemplateUpdate.setId(busiMcuZjResourceTemplateTemp.getId());
                if (busiMcuZjResourceTemplateTemp.getId().longValue() == id) {
                    busiMcuZjResourceTemplateUpdate.setIsDefault(1);
                } else {
                    busiMcuZjResourceTemplateUpdate.setIsDefault(0);
                }
                busiMcuZjResourceTemplateMapper.updateBusiMcuZjResourceTemplate(busiMcuZjResourceTemplateUpdate);
            }
        } else {
            Assert.isTrue(false, "找不到给资源模板！");
        }
        McuZjBridgeCache.getInstance().get(busiMcuZjResourceTemplate.getMcuZjServerId()).setSourceTemplateChanged(true);
        return 1;
    }

    private void setBusiMcuZjResourceTemplate(BusiMcuZjResourceTemplate busiMcuZjResourceTemplate) {
        if (busiMcuZjResourceTemplate.getHasMosic() == null) {
            busiMcuZjResourceTemplate.setHasMosic(0);
        }
        if (busiMcuZjResourceTemplate.getHasRecord() == null) {
            busiMcuZjResourceTemplate.setHasRecord(1);
        }
        if (busiMcuZjResourceTemplate.getMaxSpkMosic() == null) {
            busiMcuZjResourceTemplate.setMaxSpkMosic(1);
        }
        if (busiMcuZjResourceTemplate.getMaxChairMosic() == null) {
            busiMcuZjResourceTemplate.setMaxChairMosic(1);
        }
        if (busiMcuZjResourceTemplate.getMaxGuestMosic() == null) {
            busiMcuZjResourceTemplate.setMaxGuestMosic(1);
        }
        if (busiMcuZjResourceTemplate.getChairCopy() == null) {
            busiMcuZjResourceTemplate.setChairCopy("");
        }
        if (busiMcuZjResourceTemplate.getMaxSpkMosic() < 1 || busiMcuZjResourceTemplate.getMaxSpkMosic() > 25) {
            busiMcuZjResourceTemplate.setMaxSpkMosic(1);
        }
        if (busiMcuZjResourceTemplate.getMaxGuestMosic() < 1 || busiMcuZjResourceTemplate.getMaxGuestMosic() > 25) {
            busiMcuZjResourceTemplate.setMaxGuestMosic(1);
        }
        if (busiMcuZjResourceTemplate.getIsDefault() == null) {
            busiMcuZjResourceTemplate.setIsDefault(0);
        }
        if (ConferenceManageApi.RESOURCE_RES_BW_720P30_1M.equals(busiMcuZjResourceTemplate.getResBw())) {
        } else if (ConferenceManageApi.RESOURCE_RES_BW_1080P30_2M.equals(busiMcuZjResourceTemplate.getResBw())) {
        } else if (ConferenceManageApi.RESOURCE_RES_BW_1080P60_4M.equals(busiMcuZjResourceTemplate.getResBw())) {
        } else if (ConferenceManageApi.RESOURCE_RES_BW_4KP30_4M.equals(busiMcuZjResourceTemplate.getResBw())) {
        } else if (ConferenceManageApi.RESOURCE_RES_BW_4KP30_8M.equals(busiMcuZjResourceTemplate.getResBw())) {
        } else {
            busiMcuZjResourceTemplate.setResBw(ConferenceManageApi.RESOURCE_RES_BW_720P30_1M);
        }
        boolean hasMosic = false;
        int maxMosic = 0;
        int maxMosicView = 0;
        String totalMosicView = "";
        String name = "";
        if (busiMcuZjResourceTemplate.getHasMosic() != null && busiMcuZjResourceTemplate.getHasMosic() == 1) {
            name += "可设置分屏";
            hasMosic = true;
        } else {
            name += "不可设分屏";
        }
        if (busiMcuZjResourceTemplate.getSingleView() != null && busiMcuZjResourceTemplate.getSingleView() == 1) {
            if (StringUtils.hasText(name)) {
                name += ",";
            }
            name += "单视角";
            busiMcuZjResourceTemplate.setMaxSpkMosic(1);
            busiMcuZjResourceTemplate.setMaxChairMosic(1);
            maxMosic = busiMcuZjResourceTemplate.getMaxGuestMosic();
            maxMosicView = busiMcuZjResourceTemplate.getMaxGuestMosic();
            totalMosicView += maxMosicView;
        } else {
            if (StringUtils.hasText(name)) {
                name += ",";
            }
            name += "多视角";
            if (StringUtils.hasText(name)) {
                name += ",";
            }
            name += "支持点名";
            busiMcuZjResourceTemplate.setChairCopy("");
            busiMcuZjResourceTemplate.setMaxChairMosic(1);
            maxMosicView = busiMcuZjResourceTemplate.getMaxSpkMosic();
            if (busiMcuZjResourceTemplate.getMaxGuestMosic() > maxMosicView) {
                maxMosicView = busiMcuZjResourceTemplate.getMaxGuestMosic();
            }
            totalMosicView = busiMcuZjResourceTemplate.getMaxSpkMosic() + "+" + busiMcuZjResourceTemplate.getMaxGuestMosic() + "(" + busiMcuZjResourceTemplate.getMaxChairMosic() + ")";
            if (busiMcuZjResourceTemplate.getMaxGuestMosic() <= 1) {
                busiMcuZjResourceTemplate.setMaxGuestMosic(2);
            }
            maxMosic = busiMcuZjResourceTemplate.getMaxSpkMosic() + busiMcuZjResourceTemplate.getMaxGuestMosic() + busiMcuZjResourceTemplate.getMaxChairMosic();
            if (busiMcuZjResourceTemplate.getMaxSpkMosic() <= busiMcuZjResourceTemplate.getMaxGuestMosic()) {
                if (StringUtils.hasText(name)) {
                    name += ",";
                }
                name += "支持广播";
            }
        }
        busiMcuZjResourceTemplate.setMaxMosic(maxMosic);
        if (StringUtils.hasText(name)) {
            name += ",";
        }
        name += "最大" + maxMosicView + "分屏,共" + totalMosicView + "分屏";
        busiMcuZjResourceTemplate.setHasRecord(1);
        if (busiMcuZjResourceTemplate.getHasRecord() != null && busiMcuZjResourceTemplate.getHasRecord() == 1) {
            if (StringUtils.hasText(name)) {
                name += ",";
            }
            name += "带录制";
        }
        if (StringUtils.hasText(busiMcuZjResourceTemplate.getResBw())) {
            if (StringUtils.hasText(name)) {
                name += ",";
            }
            name += busiMcuZjResourceTemplate.getResBw();
        }
        busiMcuZjResourceTemplate.setName(name);
    }
}
