package com.paradisecloud.fcm.smc2.service.impl;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.smc2.cache.Smc2BridgeCache;
import com.paradisecloud.fcm.dao.mapper.BusiSmc2Mapper;
import com.paradisecloud.fcm.dao.model.BusiSmc2;
import com.paradisecloud.fcm.smc2.cache.Smc2Bridge;
import com.paradisecloud.fcm.smc2.service.IBusiSmc2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * smc信息Service业务层处理
 * 
 * @author lilinhai
 * @date 2023-05-17
 */
@Service
public class BusiSmc2ServiceImpl implements IBusiSmc2Service
{
    @Autowired
    private BusiSmc2Mapper busiSmc2Mapper;

    /**
     * 查询smc信息
     * 
     * @param id smc信息ID
     * @return smc信息
     */
    @Override
    public BusiSmc2 selectBusiSmc2ById(Long id)
    {
        return busiSmc2Mapper.selectBusiSmc2ById(id);
    }

    /**
     * 查询smc信息列表
     * 
     * @param busiSmc2 smc信息
     * @return smc信息
     */
    @Override
    public List<BusiSmc2> selectBusiSmc2List(BusiSmc2 busiSmc2)
    {
        return busiSmc2Mapper.selectBusiSmc2List(busiSmc2);
    }

    /**
     * 新增smc信息
     * 
     * @param busiSmc2 smc信息
     * @return 结果
     */
    @Override
    public int insertBusiSmc2(BusiSmc2 busiSmc2)
    {
        busiSmc2.setCreateTime(new Date());
        int i = busiSmc2Mapper.insertBusiSmc2(busiSmc2);
//        if(i>0){
//            Smc2Bridge smc2Bridge = new Smc2Bridge(busiSmc2);
//            if(smc2Bridge.isAvailable()){
//                busiSmc2.setStatus(1);
//                Smc2BridgeCache.getInstance().init(smc2Bridge);
//                Smc2SubscribleTask smc2SubscribleTask = new Smc2SubscribleTask(smc2Bridge.getSubscribeServiceEx(),smc2Bridge);
//                Thread thread = new Thread(smc2SubscribleTask);
//                thread.setDaemon(true);
//                thread.start();
//            }else {
//                busiSmc2.setStatus(2);
//            }
//            busiSmc2Mapper.updateBusiSmc2(busiSmc2);
//        }
        return i;
    }

    /**
     * 修改smc信息
     * 
     * @param busiSmc2 smc信息
     * @return 结果
     */
    @Override
    public int updateBusiSmc2(BusiSmc2 busiSmc2)
    {
        busiSmc2.setUpdateTime(new Date());
        return busiSmc2Mapper.updateBusiSmc2(busiSmc2);
    }

    /**
     * 批量删除smc信息
     * 
     * @param ids 需要删除的smc信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmc2ByIds(Long[] ids)
    {
        return busiSmc2Mapper.deleteBusiSmc2ByIds(ids);
    }

    /**
     * 删除smc信息信息
     * 
     * @param id smc信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmc2ById(Long id)
    {

        if(id==null){
            return 0;
        }
        Smc2Bridge smc2Bridge = Smc2BridgeCache.getInstance().getSmc2BridgeMap().get(id);
        if(smc2Bridge!=null){
            Smc2BridgeCache.getInstance().removeSmc2(smc2Bridge);
        }
        return busiSmc2Mapper.deleteBusiSmc2ById(id);
    }
}
