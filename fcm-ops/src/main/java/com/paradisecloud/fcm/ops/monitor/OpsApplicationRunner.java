package com.paradisecloud.fcm.ops.monitor;

import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.common.utils.PropertiesUtil;
import com.paradisecloud.fcm.dao.mapper.BusiOpsInfoMapper;
import com.paradisecloud.fcm.dao.model.BusiOpsInfo;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.ops.FmeOutboundDialPlanRulesConfigModifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author nj
 * @date 2024/5/29 11:12
 */
@Slf4j
@Order(2)
@Component
public class OpsApplicationRunner implements ApplicationRunner {


    @Resource
    private BusiOpsInfoMapper busiOpsInfoMapper;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        changeCongfig();

    }

    private void changeCongfig() {
        String filePath = PathUtil.getRootPath() + "/external_config.properties";
        Properties properties = PropertiesUtil.readProperties(filePath);
        String region = properties.getProperty("region");
        if(!Objects.equals(region,"ops")){
            return;
        }
        long startTime = System.currentTimeMillis();
        List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(new BusiOpsInfo());
        if (CollectionUtils.isEmpty(busiOpsInfos)) {
            return;
        } else {
            new Thread(() -> {
                Boolean flag=false;
                log.info("FmeOutboundDialPlanRulesConfigModifierMonitor start====================");
                while (true) {
                    long endTime = System.currentTimeMillis();
                    if(endTime-startTime>1000*1000*20){
                        log.info("FmeOutboundDialPlanRules 运行时间："+(endTime-startTime)+"ms");
                        log.info("FmeOutboundDialPlanRules 是否执行修改："+flag);
                        break;
                    }
                    try {
                        List<FmeBridge> fmeBridges = FmeBridgeCache.getInstance().getFmeBridges();
                        if (CollectionUtils.isNotEmpty(fmeBridges)) {
                            for (FmeBridge fmeBridge : fmeBridges) {
                                if (fmeBridge.isAvailable()) {
                                    // 执行规则配置修改操作
                                    FmeOutboundDialPlanRulesConfigModifier.modify(fmeBridge);
                                    flag=true;
                                    log.info("FmeOutboundDialPlanRules规则配置修改完成====================");
                                    return;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        Threads.sleep(1000);
                    }
                }

            }).start();
        }
    }
}
