package com.paradisecloud.fcm.smartroom.service.impls;

import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;

import com.paradisecloud.fcm.common.enumer.LotModel;
import com.paradisecloud.fcm.common.enumer.LotType;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomLotMapper;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDevice;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomLot;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomDeviceVo;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomLotVo;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomLotCache;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeviceService;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomLotService;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import io.jsonwebtoken.lang.Assert;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 智慧办公物联网关Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-03-04
 */
@Service
public class BusiSmartRoomLotServiceImpl implements IBusiSmartRoomLotService
{
    @Resource
    private BusiSmartRoomLotMapper busiSmartRoomLotMapper;
    @Resource
    private IBusiSmartRoomDeviceService busiSmartRoomDeviceService;

    /**
     * 查询智慧办公物联网关
     * 
     * @param id 智慧办公物联网关ID
     * @return 智慧办公物联网关
     */
    @Override
    public BusiSmartRoomLot selectBusiSmartRoomLotById(Long id)
    {
        return busiSmartRoomLotMapper.selectBusiSmartRoomLotById(id);
    }

    /**
     * 查询智慧办公物联网关列表
     * 
     * @param busiSmartRoomLot 智慧办公物联网关
     * @return 智慧办公物联网关
     */
    @Override
    public List<BusiSmartRoomLot> selectBusiSmartRoomLotList(BusiSmartRoomLotVo busiSmartRoomLot)
    {
        return busiSmartRoomLotMapper.selectBusiSmartRoomLotList(busiSmartRoomLot);
    }

    /**
     * 新增智慧办公物联网关
     * 
     * @param busiSmartRoomLot 智慧办公物联网关
     * @return 结果
     */
    @Override
    public int insertBusiSmartRoomLot(BusiSmartRoomLot busiSmartRoomLot)
    {
        Assert.notNull(busiSmartRoomLot.getLotName(), "网关名不能为空！");
        Assert.notNull(busiSmartRoomLot.getLotType(), "网关类型不能为空！");
        Assert.notNull(busiSmartRoomLot.getLotModel(), "网关型号不能为空！");
        if (busiSmartRoomLot.getLotType() == LotType.SERIAL_DEVICE.getCode()) {
            LotModel lotModel = LotModel.convert(busiSmartRoomLot.getLotModel());
            if (lotModel != null) {
                busiSmartRoomLot.setBrand(lotModel.getBrand());
                Map<String, Object> details = new HashMap<>();
                details.put("channels", lotModel.getChannels());
                details.put("brandAlias", lotModel.getBrandAlias());
                busiSmartRoomLot.setDetails(details);
            }
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        busiSmartRoomLot.setCreateTime(new Date());
        busiSmartRoomLot.setClientId("lot");
        busiSmartRoomLot.setPubTopic("platform/lot/lot");
        busiSmartRoomLot.setSubTopic("lot/lot");
        busiSmartRoomLot.setCreateBy(loginUser.getUsername());

        int i = busiSmartRoomLotMapper.insertBusiSmartRoomLot(busiSmartRoomLot);
        if (i > 0) {
            updateBusiSmartRoomLot(busiSmartRoomLot);
        }
        return i;
    }

    /**
     * 修改智慧办公物联网关
     * 
     * @param busiSmartRoomLot 智慧办公物联网关
     * @return 结果
     */
    @Override
    public int updateBusiSmartRoomLot(BusiSmartRoomLot busiSmartRoomLot)
    {
        Assert.notNull(busiSmartRoomLot.getLotName(), "网关名不能为空！");
        BusiSmartRoomLot busiSmartRoomLotExist = busiSmartRoomLotMapper.selectBusiSmartRoomLotById(busiSmartRoomLot.getId());
        Assert.notNull(busiSmartRoomLotExist, "该网关不存在！");
        BusiSmartRoomLot busiSmartRoomLotUpdate = new BusiSmartRoomLot();
        busiSmartRoomLotUpdate.setId(busiSmartRoomLot.getId());
        busiSmartRoomLotUpdate.setLotName(busiSmartRoomLot.getLotName());
        busiSmartRoomLotUpdate.setPubTopic("platform/lot/lot" + busiSmartRoomLot.getId());
        busiSmartRoomLotUpdate.setSubTopic("lot/lot" + busiSmartRoomLot.getId());
        busiSmartRoomLotUpdate.setClientId("lot" + busiSmartRoomLot.getId());
        if (busiSmartRoomLotExist.getLotType() == LotType.SERIAL_DEVICE.getCode()) {
            LotModel lotModel = LotModel.convert(busiSmartRoomLotExist.getLotModel());
            if (lotModel != null) {
                busiSmartRoomLotUpdate.setBrand(lotModel.getBrand());
                Map<String, Object> details = new HashMap<>();
                details.put("channels", lotModel.getChannels());
                details.put("brandAlias", lotModel.getBrandAlias());
                busiSmartRoomLotUpdate.setDetails(details);
            }
        }
        busiSmartRoomLotUpdate.setUpdateTime(new Date());
        int i = busiSmartRoomLotMapper.updateBusiSmartRoomLot(busiSmartRoomLotUpdate);
        if (i > 0) {
            SmartRoomLotCache.getInstance().add(busiSmartRoomLotUpdate);
        }
        return i;
    }

    /**
     * 批量删除智慧办公物联网关
     * 
     * @param ids 需要删除的智慧办公物联网关ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomLotByIds(Long[] ids)
    {
        int i = busiSmartRoomLotMapper.deleteBusiSmartRoomLotByIds(ids);
        if (i > 0) {
            for (Long id : ids) {
                SmartRoomLotCache.getInstance().remove(id);
                BusiSmartRoomDeviceVo busiSmartRoomDeviceVo = new BusiSmartRoomDeviceVo();
                busiSmartRoomDeviceVo.setLotId(id);
                List<BusiSmartRoomDevice> busiSmartRoomDeviceList = busiSmartRoomDeviceService.selectBusiSmartRoomDeviceList(busiSmartRoomDeviceVo);
                for (BusiSmartRoomDevice busiSmartRoomDevice : busiSmartRoomDeviceList) {
                    if (busiSmartRoomDevice != null) {
                        busiSmartRoomDeviceService.deleteBusiSmartRoomDeviceById(busiSmartRoomDevice.getId());
                    }
                }
            }
        }
        return i;
    }

    /**
     * 删除智慧办公物联网关信息
     * 
     * @param id 智慧办公物联网关ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomLotById(Long id)
    {
        int i = busiSmartRoomLotMapper.deleteBusiSmartRoomLotById(id);
        if (i > 0) {
            SmartRoomLotCache.getInstance().remove(id);
        }
        return i;
    }

    /**
     * 查询未绑定的物联网关列表
     *
     * @return
     */
    @Override
    public List<BusiSmartRoomLot> selectBusiSmartRoomLotListForUnbind() {
        return busiSmartRoomLotMapper.selectBusiSmartRoomLotListForUnbind();
    }
}
