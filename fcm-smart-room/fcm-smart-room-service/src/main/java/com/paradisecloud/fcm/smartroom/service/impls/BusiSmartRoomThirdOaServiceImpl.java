package com.paradisecloud.fcm.smartroom.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.common.enumer.OaType;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomThirdOaMapper;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomThirdOa;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomThirdOaService;
import com.sinhy.exception.SystemException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 智慧办公第三方OAService业务层处理
 * 
 * @author lilinhai
 * @date 2024-03-04
 */
@Service
public class BusiSmartRoomThirdOaServiceImpl implements IBusiSmartRoomThirdOaService
{
    @Resource
    private BusiSmartRoomThirdOaMapper busiSmartRoomThirdOaMapper;

    /**
     * 查询智慧办公第三方OA
     * 
     * @param id 智慧办公第三方OAID
     * @return 智慧办公第三方OA
     */
    @Override
    public BusiSmartRoomThirdOa selectBusiSmartRoomThirdOaById(Long id)
    {
        BusiSmartRoomThirdOa busiSmartRoomThirdOa = busiSmartRoomThirdOaMapper.selectBusiSmartRoomThirdOaById(id);
        String oaTypeName = "";
        OaType oaType = OaType.convert(busiSmartRoomThirdOa.getOaType());
        if (oaType != null) {
            oaTypeName = oaType.getName();
        }
        busiSmartRoomThirdOa.getParams().put("oaTypeName", oaTypeName);
        return busiSmartRoomThirdOa;
    }

    /**
     * 查询智慧办公第三方OA列表
     * 
     * @param busiSmartRoomThirdOa 智慧办公第三方OA
     * @return 智慧办公第三方OA
     */
    @Override
    public List<BusiSmartRoomThirdOa> selectBusiSmartRoomThirdOaList(BusiSmartRoomThirdOa busiSmartRoomThirdOa)
    {
        List<BusiSmartRoomThirdOa> busiSmartRoomThirdOaList = busiSmartRoomThirdOaMapper.selectBusiSmartRoomThirdOaList(busiSmartRoomThirdOa);
        for (BusiSmartRoomThirdOa busiSmartRoomThirdOaTemp : busiSmartRoomThirdOaList) {
            String oaTypeName = "";
            OaType oaType = OaType.convert(busiSmartRoomThirdOaTemp.getOaType());
            if (oaType != null) {
                oaTypeName = oaType.getName();
            }
            busiSmartRoomThirdOaTemp.getParams().put("oaTypeName", oaTypeName);
        }
        return busiSmartRoomThirdOaList;
    }

    /**
     * 新增智慧办公第三方OA
     * 
     * @param busiSmartRoomThirdOa 智慧办公第三方OA
     * @return 结果
     */
    @Override
    public int insertBusiSmartRoomThirdOa(BusiSmartRoomThirdOa busiSmartRoomThirdOa)
    {
        busiSmartRoomThirdOa.setCreateTime(new Date());
        List<BusiSmartRoomThirdOa> busiSmartRoomThirdOas = busiSmartRoomThirdOaMapper.selectBusiSmartRoomThirdOaList(new BusiSmartRoomThirdOa());
        if (busiSmartRoomThirdOas.size() > 0) {
            throw new SystemException("当前系统仅支持绑定单个OA！");
        }
        return busiSmartRoomThirdOaMapper.insertBusiSmartRoomThirdOa(busiSmartRoomThirdOa);
    }

    /**
     * 修改智慧办公第三方OA
     * 
     * @param busiSmartRoomThirdOa 智慧办公第三方OA
     * @return 结果
     */
    @Override
    public int updateBusiSmartRoomThirdOa(BusiSmartRoomThirdOa busiSmartRoomThirdOa)
    {
        busiSmartRoomThirdOa.setUpdateTime(new Date());
        return busiSmartRoomThirdOaMapper.updateBusiSmartRoomThirdOa(busiSmartRoomThirdOa);
    }

    /**
     * 批量删除智慧办公第三方OA
     * 
     * @param ids 需要删除的智慧办公第三方OAID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomThirdOaByIds(Long[] ids)
    {
        return busiSmartRoomThirdOaMapper.deleteBusiSmartRoomThirdOaByIds(ids);
    }

    /**
     * 删除智慧办公第三方OA信息
     * 
     * @param id 智慧办公第三方OAID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomThirdOaById(Long id)
    {
        return busiSmartRoomThirdOaMapper.deleteBusiSmartRoomThirdOaById(id);
    }
}
