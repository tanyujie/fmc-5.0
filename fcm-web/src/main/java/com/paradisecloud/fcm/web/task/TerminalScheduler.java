package com.paradisecloud.fcm.web.task;

import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmDeleteUsrRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmSearchUsrRequest;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmDeleteUsrResponse;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmSearchUsrResponse;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
public class TerminalScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(TerminalScheduler.class);

    @Resource
    private BusiTerminalMapper busiTerminalMapper;

    /**
     * 每天00:17处理过期终端
     */
    @Scheduled(cron = "0 17 0 * * ?")
    public void dealExpireTerminal() {
        List<BusiTerminal> busiTerminalList = busiTerminalMapper.selectBusiTerminalForExpire(new Date());
        if (busiTerminalList != null && busiTerminalList.size() > 0) {
            delExpireTerminal(busiTerminalList);
        }
    }

    public void delExpireTerminal(List<BusiTerminal> busiTerminalList) {
        if (busiTerminalList != null && busiTerminalList.size() > 0) {
            for (BusiTerminal busiTerminalTemp : busiTerminalList) {
                BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiTerminalTemp.getId());
                if (TerminalType.isZJ(busiTerminal.getType())) {
                    List<McuZjBridge> mcuZjBridgeList = McuZjBridgeCache.getInstance().getMcuZjBridgesByDept(busiTerminal.getDeptId());
                    McuZjBridge mcuZjBridge = null;
                    if (mcuZjBridgeList != null && mcuZjBridgeList.size() > 0) {
                        mcuZjBridge = mcuZjBridgeList.get(0);
                    }
                    CmSearchUsrRequest cmSearchUsrRequest = new CmSearchUsrRequest();
                    String[] filterType = new String[1];
                    filterType[0] = "login_id";
                    Object[] filterValue = new Object[1];
                    filterValue[0] = busiTerminal.getCredential();
                    cmSearchUsrRequest.setFilter_type(filterType);
                    cmSearchUsrRequest.setFilter_value(filterValue);
                    CmSearchUsrResponse cmSearchUsrResponse = mcuZjBridge.getConferenceManageApi().searchUsr(cmSearchUsrRequest);
                    if (cmSearchUsrResponse != null && cmSearchUsrResponse.getUsr_ids().length > 0) {
                        // 删除
                        CmDeleteUsrRequest cmDeleteUsrRequest = new CmDeleteUsrRequest();
                        cmDeleteUsrRequest.setUsr_ids(cmSearchUsrResponse.getUsr_ids());
                        cmDeleteUsrRequest.setOption("endpoint");
                        CmDeleteUsrResponse cmDeleteUsrResponse = mcuZjBridge.getConferenceManageApi().deleteUsr(cmDeleteUsrRequest);
                        if (cmDeleteUsrResponse != null) {
                            LOG.info("ZJ账号删除成功：" + busiTerminal.getCredential());
                            busiTerminal.setAvailable(2);
                            int i = busiTerminalMapper.updateBusiTerminal(busiTerminal);
                            if (i > 0) {
                                LOG.info("修改终端为不可用：" + busiTerminal.getCredential());
                            }
                        }
                        cmSearchUsrResponse = mcuZjBridge.getConferenceManageApi().searchUsr(cmSearchUsrRequest);
                        if (cmSearchUsrResponse != null && cmSearchUsrResponse.getUsr_ids().length > 0) {
                            LOG.error("ZJ账号删除失败：" + busiTerminal.getCredential());
                        }
                    }
                }
            }
        }
    }
}
