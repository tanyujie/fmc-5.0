/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : EduCacheInitializer.java
 * Package     : com.paradisecloud.fcm.edu.cache
 * @author sinhy 
 * @since 2021-10-23 16:44
 * @version  V1.0
 */
package com.paradisecloud.fcm.ops.cloud.core;

import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.dao.mapper.BusiClientMapper;
import com.paradisecloud.fcm.dao.mapper.BusiOpsMapper;
import com.paradisecloud.fcm.dao.model.BusiClient;
import com.paradisecloud.fcm.dao.model.BusiOps;
import com.paradisecloud.fcm.ops.cloud.cache.ClientCache;
import com.paradisecloud.fcm.ops.cloud.cache.OpsCache;
import com.paradisecloud.fcm.service.ops.OpsDataCache;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**  
 * <pre>智慧办公缓存初始化器</pre>
 * @author sinhy
 * @since 2021-10-23 16:44
 * @version V1.0  
 */
@Order(4)
@Component
public class OpsCacheInitializer implements ApplicationRunner
{

    @Resource
    private BusiOpsMapper busiOpsMapper;
    @Resource
    private BusiClientMapper busiClientMapper;
    
    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        List<BusiOps> busiOpsList = busiOpsMapper.selectBusiOpsList(new BusiOps());
        for (BusiOps busiOps : busiOpsList) {
            OpsCache.getInstance().add(busiOps);
        }
        OpsCache.getInstance().setLoadFinished();
        if (busiOpsList.size() > 0) {
            OpsCache.getInstance().setNeedUpdateMqStatus(true);
        }
        //客户端
        List<BusiClient> busiClientList = busiClientMapper.selectBusiClientList(new BusiClient());
        for (BusiClient busiClient : busiClientList) {
            ClientCache.getInstance().add(busiClient);
        }
        ClientCache.getInstance().setLoadFinished();
        if (busiClientList.size() > 0) {
            ClientCache.getInstance().setNeedUpdateMqStatus(true);
        }
        if (ExternalConfigCache.getInstance().isEnableIm()) {
            OpsDataCache.getInstance().setImTime(-1);
        }
    }
    
}
