package com.paradisecloud.fcm.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.dao.mapper.BusiTransServerMapper;
import com.paradisecloud.fcm.dao.model.BusiTransServer;
import com.paradisecloud.fcm.service.interfaces.IBusiTransServerService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 转流服务器Service业务层处理
 *
 * @author lilinhai
 * @date 2024-03-29
 */
@Service
public class BusiTransServerServiceImpl implements IBusiTransServerService
{
    @Resource
    private BusiTransServerMapper busiTransServerMapper;

    /**
     * 查询转流服务器
     *
     * @param id 转流服务器ID
     * @return 转流服务器
     */
    @Override
    public BusiTransServer selectBusiTransServerById(Long id)
    {
        BusiTransServer busiTransServer = busiTransServerMapper.selectBusiTransServerById(id);
        if(busiTransServer != null){
            busiTransServer.setPassword("******");
            busiTransServer.setUserName("******");
        }
        return busiTransServer;
    }

    /**
     * 查询转流服务器列表
     *
     * @param busiTransServer 转流服务器
     * @return 转流服务器
     */
    @Override
    public List<BusiTransServer> selectBusiTransServerList(BusiTransServer busiTransServer)
    {
        List<BusiTransServer> busiTransServers = busiTransServerMapper.selectBusiTransServerList(busiTransServer);

        return busiTransServers;
    }

    /**
     * 新增转流服务器
     *
     * @param busiTransServer 转流服务器
     * @return 结果
     */
    @Override
    public int insertBusiTransServer(BusiTransServer busiTransServer)
    {
        busiTransServer.setCreateTime(new Date());
        if(Strings.isBlank(busiTransServer.getUserName())){
            busiTransServer.setUserName("root");
        }
        if(Strings.isBlank(busiTransServer.getPassword())){
            busiTransServer.setPassword("P@rad1se");
        }
        return busiTransServerMapper.insertBusiTransServer(busiTransServer);
    }

    /**
     * 修改转流服务器
     *
     * @param busiTransServer 转流服务器
     * @return 结果
     */
    @Override
    public int updateBusiTransServer(BusiTransServer busiTransServer)
    {
        busiTransServer.setUpdateTime(new Date());
        return busiTransServerMapper.updateBusiTransServer(busiTransServer);
    }

    /**
     * 批量删除转流服务器
     *
     * @param ids 需要删除的转流服务器ID
     * @return 结果
     */
    @Override
    public int deleteBusiTransServerByIds(Long[] ids)
    {
        return busiTransServerMapper.deleteBusiTransServerByIds(ids);
    }

    /**
     * 删除转流服务器信息
     *
     * @param id 转流服务器ID
     * @return 结果
     */
    @Override
    public int deleteBusiTransServerById(Long id)
    {
        return busiTransServerMapper.deleteBusiTransServerById(id);
    }
}
