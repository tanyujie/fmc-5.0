package com.paradisecloud.fcm.ding.core;

import com.paradisecloud.fcm.ding.cache.DeptDingMappingCache;
import com.paradisecloud.fcm.ding.cache.DingBridge;
import com.paradisecloud.fcm.ding.cache.DingBridgeCache;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiMcuDingHistoryConferenceService;
import com.paradisecloud.fcm.dao.mapper.BusiMcuDingDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuDingMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuDing;
import com.paradisecloud.fcm.dao.model.BusiMcuDingDept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 登录保活
 *
 * @author nj
 * @date 2023/4/21 17:18
 */
@Component
@Order(3)
public class DingDingModuleInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DingDingModuleInitializer.class);

    @Resource
    BusiMcuDingDeptMapper busiMcuDingDeptMapper;
    @Resource
    BusiMcuDingMapper busiMcuDingMapper;

    @Resource
    IBusiMcuDingHistoryConferenceService busiDingHistoryConferenceService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initDept();
        initBridge();
        syncConference();
    }

    private void endConference() throws Exception {
        DingBridge DingBridge = DingBridgeCache.getInstance().getAvailableBridgesByDept(1L);

    }


    private void initDept() {
        List<BusiMcuDingDept> busiDingDepts = busiMcuDingDeptMapper.selectBusiMcuDingDeptList(new BusiMcuDingDept());
        if (!CollectionUtils.isEmpty(busiDingDepts)) {
            for (BusiMcuDingDept busiDingDept : busiDingDepts) {
                DeptDingMappingCache.getInstance().put(busiDingDept.getDeptId(), busiDingDept);
            }
        }

    }

    private void initBridge() {
        List<BusiMcuDing> busiDings = busiMcuDingMapper.selectBusiMcuDingList(new BusiMcuDing());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(busiDings)) {
            for (BusiMcuDing busiDing : busiDings) {
                DingBridge DingBridge = new DingBridge(busiDing);
                if (DingBridge.isAvailable()) {
                    DingBridgeCache.getInstance().init(DingBridge);
                }
            }
        }

    }

    private void syncConference() {
        try {


        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Ding 会议同步错误：" + e.getMessage());
        }
    }




    public Date TimeStamp2Date(String timestampString) {
        String formats = "yyyy-MM-dd HH:mm:ss";
        Long timestamp = Long.parseLong(timestampString) * 1000;
        //日期格式字符串
        String dateStr = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
        Date date = null;
        SimpleDateFormat formater = new SimpleDateFormat();
        formater.applyPattern("yyyy-MM-dd HH:mm:ss");
        try {
            date = formater.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


}
