package com.paradisecloud.smc.service.impl;

import java.util.List;
import java.util.Date;
import java.util.Map;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.smc.SMCWebsocketClient;
import com.paradisecloud.smc.SmcWebsocketContext;
import com.paradisecloud.smc.SmcWebsocketReconnecter;
import com.paradisecloud.smc.dao.model.BusiSmcDept;
import com.paradisecloud.smc.dao.model.mapper.BusiSmcMapper;
import com.paradisecloud.smc.service.IBusiSmcDeptService;
import com.paradisecloud.smc.service.IBusiSmcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.paradisecloud.smc.dao.model.BusiSmc;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

/**
 * smc信息Service业务层处理
 * 
 * @author liuxilong
 * @date 2022-08-25
 */
@Service
public class BusiSmcServiceImpl implements IBusiSmcService
{
    @Resource
    private BusiSmcMapper busiSmcMapper;
    @Resource
    private IBusiSmcDeptService busiSmcDeptService;
    /**
     * 查询smc信息
     * 
     * @param id smc信息ID
     * @return smc信息
     */
    @Override
    public BusiSmc selectBusiSmcById(Long id)
    {
        return busiSmcMapper.selectBusiSmcById(id);
    }

    /**
     * 查询smc信息列表
     * 
     * @param busiSmc smc信息
     * @return smc信息
     */
    @Override
    public List<BusiSmc> selectBusiSmcList(BusiSmc busiSmc)
    {
        return busiSmcMapper.selectBusiSmcList(busiSmc);
    }

    @Override
    public List<BusiSmc> selectBusiSmcListNoP(BusiSmc busiSmc) {
        return busiSmcMapper.selectBusiSmcListNoP(busiSmc);
    }

    /**
     * 新增smc信息
     * 
     * @param busiSmc smc信息
     * @return 结果
     */
    @Override
    public int insertBusiSmc(BusiSmc busiSmc)
    {
        busiSmc.setCreateTime(new Date());
        SmcBridge smcBridge = new SmcBridge(busiSmc);

        int i=  busiSmcMapper.insertBusiSmc(busiSmc);

        List<BusiSmc> busiSmcs = busiSmcMapper.selectBusiSmcList(busiSmc);

        busiSmc.setId(busiSmcs.get(0).getId());
        SmcBridgeCache.getInstance().update(smcBridge);
        SmcWebsocketReconnecter.getInstance().add(smcBridge);
        return i;
    }

    /**
     * 修改smc信息
     * 
     * @param busiSmc smc信息
     * @return 结果
     */
    @Override
    public int updateBusiSmc(BusiSmc busiSmc)
    {
        busiSmc.setUpdateTime(new Date());
        SmcBridge smcBridge = new SmcBridge(busiSmc);
        SmcBridgeCache.getInstance().update(smcBridge);
        return busiSmcMapper.updateBusiSmc(busiSmc);
    }

    /**
     * 删除smc信息信息
     * 
     * @param id smc信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmcById(Long id)
    {
        BusiSmcDept busiSmcDept = new BusiSmcDept();
        busiSmcDept.setSmcId(id);
        List<BusiSmcDept> busiSmcDepts = busiSmcDeptService.selectBusiSmcDeptList(busiSmcDept);
        if(!CollectionUtils.isEmpty(busiSmcDepts)){
            throw new CustomException("SMC被租戶使用，不能刪除");
        }
        SmcBridgeCache.getInstance().removeSmcById(id);
        Map<String, SMCWebsocketClient> smcWebsocketClientMap = SmcWebsocketContext.getSmcWebsocketClientMap();
        BusiSmc busiSmc = busiSmcMapper.selectBusiSmcById(id);
        SMCWebsocketClient websocketClient = smcWebsocketClientMap.get(busiSmc.getIp());
        if(websocketClient!=null){
            websocketClient.close();
        }
        return busiSmcMapper.deleteBusiSmcById(id);
    }
}
