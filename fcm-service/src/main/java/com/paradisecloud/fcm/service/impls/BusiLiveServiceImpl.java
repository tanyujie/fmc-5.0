package com.paradisecloud.fcm.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.mapper.BusiLiveClusterMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiLiveDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiLiveMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.interfaces.IBusiLiveService;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.LiveBridge;
import com.sinhy.exception.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

/**
 * 直播服务器信息Service业务层处理
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
@Service
public class BusiLiveServiceImpl implements IBusiLiveService
{
    @Resource
    private BusiLiveMapper busiLiveMapper;
    @Resource
    private BusiLiveDeptMapper busiLiveDeptMapper;
    @Resource
    private BusiLiveClusterMapMapper busiLiveClusterMapMapper;

    /**
     * 查询直播服务器信息
     * 
     * @param id 直播服务器信息ID
     * @return 直播服务器信息
     */
    @Override
    public BusiLive selectBusiLiveById(Long id)
    {
        return busiLiveMapper.selectBusiLiveById(id);
    }

    /**
     * 查询直播服务器信息列表
     * 
     * @param busiLive 直播服务器信息
     * @return 直播服务器信息
     */
    @Override
    public List<BusiLive> selectBusiLiveList(BusiLive busiLive)
    {
        return busiLiveMapper.selectBusiLiveList(busiLive);
    }

    /**
     * 新增直播服务器信息
     * 
     * @param busiLive 直播服务器信息
     * @return 结果
     */
    @Override
    public int insertBusiLive(BusiLive busiLive)
    {
        busiLive.setCreateTime(new Date());
        if (busiLive.getUriPath() == null) {
            busiLive.setUriPath("live");
        }
        if (busiLive.getProtocolType() == null) {
            busiLive.setProtocolType("rtmp");
        }
        int i = busiLiveMapper.insertBusiLive(busiLive);
        BusiLive selectBusiLiveById = busiLiveMapper.selectBusiLiveById(busiLive.getId());
        if (i > 0) {
//            busiLiveMapper.se
            LiveBridgeCache.getInstance().update(new LiveBridge(selectBusiLiveById));
        }
        return i;
    }

    /**
     * 修改直播服务器信息
     * 
     * @param busiLive 直播服务器信息
     * @return 结果
     */
    @Override
    public int updateBusiLive(BusiLive busiLive)
    {
        busiLive.setUpdateTime(new Date());
        if (busiLive.getUriPath() == null) {
            busiLive.setUriPath("live");
        }
        if (busiLive.getProtocolType() == null) {
            busiLive.setProtocolType("rtmp");
        }
        LiveBridgeCache.getInstance().update(new LiveBridge(busiLive));
        return busiLiveMapper.updateBusiLive(busiLive);
    }

    /**
     * 批量删除直播服务器信息
     * 
     * @param ids 需要删除的直播服务器信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveByIds(Long[] ids)
    {
        for (Long id : ids) {
            BusiLiveDept busiLiveDept = new BusiLiveDept();
            busiLiveDept.setLiveId(id);
            busiLiveDept.setLiveType(1);
            List<BusiLiveDept> busiLives = busiLiveDeptMapper.selectBusiLiveDeptList(busiLiveDept);
            if (!ObjectUtils.isEmpty(busiLives))
            {
                throw new SystemException(1000016, "该直播服务器正在被租户使用，不能删除！");
            }

            BusiLiveClusterMap busiLiveClusterMap = new BusiLiveClusterMap();
            busiLiveClusterMap.setLiveId(id);
            List<BusiLiveClusterMap> busiLiveClusterMaps = busiLiveClusterMapMapper.selectBusiLiveClusterMapList(busiLiveClusterMap);
            if (!ObjectUtils.isEmpty(busiLiveClusterMaps))
            {
                throw new SystemException(1000016, "该直播服务器正在集群中，无法删除，请先从集群中剔除，再删除！");
            }
            LiveBridgeCache.getInstance().remove(id);
        }

        return busiLiveMapper.deleteBusiLiveByIds(ids);
    }

    /**
     * 删除直播服务器信息信息
     * 
     * @param id 直播服务器信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveById(Long id)
    {
        return busiLiveMapper.deleteBusiLiveById(id);
    }
}
