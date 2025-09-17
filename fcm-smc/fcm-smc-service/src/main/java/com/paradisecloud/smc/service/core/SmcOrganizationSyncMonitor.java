package com.paradisecloud.smc.service.core;

import com.paradisecloud.com.fcm.smc.modle.response.SmcOrganization;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.service.ISysDeptService;
import com.sinhy.utils.ThreadUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * @author nj
 * @date 2022/8/30 15:31
 */
public class SmcOrganizationSyncMonitor extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmcOrganizationSyncMonitor.class);
    private final static long WAIT_TIME = 120 * 1000;
    private ISysDeptService iSysDeptService;


    private static final SmcOrganizationSyncMonitor INSTANCE = new SmcOrganizationSyncMonitor();


    public void init(ISysDeptService iSysDeptService) {
        this.iSysDeptService = iSysDeptService;
        this.start();
    }

    private SmcOrganizationSyncMonitor() {
        super("SmcOnlineStatusMonitor");
    }

    public static SmcOrganizationSyncMonitor getInstance() {
        return INSTANCE;
    }

    @Override
    public void run() {

        LOGGER.info("SMC组织监视器启动并初始化成功");
        long startTime = 0;
        long endTime = 0;
        while (true) {
            try {
                startTime = System.currentTimeMillis();
                checkTask();

            } catch (Throwable e) {
                LOGGER.error("主线程wait异常：", e);
            } finally {
                endTime = System.currentTimeMillis();
                long spendTime = endTime - startTime;
                if (spendTime < WAIT_TIME) {
                    ThreadUtils.sleep(WAIT_TIME - spendTime);
                }
                LOGGER.info("SMC组织ORG本轮检测，共耗时：" + spendTime);
            }
        }

    }


    private void checkTask() {

        List<SysDept> sysDepts = iSysDeptService.selectDeptList(new SysDept());
        if (CollectionUtils.isEmpty(sysDepts)) {
            return;
        }

        for (SysDept sysDept : sysDepts) {

            SmcBridge smcBridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(sysDept.getDeptId());
            List<SmcOrganization> smcOrganizations = smcBridge.getSmcOrganizationsInvoker().getOrganizationsList(smcBridge.getSmcportalTokenInvoker().getSystemHeaders());


        }
        List<SysDept> sysDepts1 = iSysDeptService.buildDeptTree(sysDepts);


        createOrg(sysDepts1, null);

    }

    public void createOrg(List<SysDept> sysDepts, String parentId) {

        if (CollectionUtils.isEmpty(sysDepts)) {
            return;
        }
        for (SysDept p : sysDepts) {
            Long deptId = p.getDeptId();
            SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(deptId);
            String orgId = SmcBridgeCache.getInstance().getDeptIdOrgMap().get(p.getDeptId());
            if (StringUtils.isBlank(orgId)) {
                if (Objects.isNull(bridge)) {
                    break;
                }
                SmcOrganization smcOrganization = new SmcOrganization();
                smcOrganization.setName(p.getDeptName());
                if (StringUtils.isNotBlank(parentId)) {
                    SmcOrganization parent = new SmcOrganization();
                    parent.setId(parentId);
                    smcOrganization.setParent(parent);
                } else {
                    if (p.getParentId() == 0 || p.getParentId() == null) {
                        String rootId = bridge.getOrgId();
                        if (StringUtils.isBlank(orgId)) {
                            break;
                        } else {
                            SmcOrganization parent = new SmcOrganization();
                            parent.setId(rootId);
                            smcOrganization.setParent(parent);
                        }
                    }
                }
                smcOrganization.setSeqInParent(Double.valueOf(p.getOrderNum()));
                SmcOrganization smcOrganization1 = bridge.getSmcOrganizationsInvoker().create(smcOrganization, bridge.getSmcportalTokenInvoker().getSystemHeaders());
                if (Objects.isNull(smcOrganization1)) {
                    throw new CustomException("数据同步组织失败" + p.toString());
                }
                SmcBridgeCache.getInstance().updateDeptIdOrgMap(deptId, smcOrganization1.getId());
                if (CollectionUtils.isNotEmpty(p.getChildren())) {
                    createOrg(p.getChildren(), smcOrganization1.getId());
                }
            }


        }

    }


}
