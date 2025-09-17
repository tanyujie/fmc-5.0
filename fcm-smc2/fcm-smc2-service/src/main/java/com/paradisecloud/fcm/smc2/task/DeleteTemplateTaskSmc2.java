package com.paradisecloud.fcm.smc2.task;

import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.DelConfTemplateEx;
import com.suntek.smc.esdk.pojo.local.TPSDKResponseEx;
import com.suntek.smc.esdk.service.client.TemplateServiceEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nj
 * @date 2023/5/15 10:03
 */
public class DeleteTemplateTaskSmc2 extends Smc2DelayTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteTemplateTaskSmc2.class);

    private Long templateId;

    public DeleteTemplateTaskSmc2(String id, long delayInMilliseconds, Long templateId) {
        super(id, delayInMilliseconds);
        this.templateId = templateId;
    }

    @Override
    public void run() {
        DelConfTemplateEx delConfTemplateEx = new DelConfTemplateEx();
        delConfTemplateEx.setConfTemplateId(templateId);
        TemplateServiceEx service = ServiceFactoryEx.getService(TemplateServiceEx.class);
        TPSDKResponseEx<Integer> integerTPSDKResponseEx =
                service.delConfTemplateEx(delConfTemplateEx);
        LOGGER.info("SMC2模板删除结果："+integerTPSDKResponseEx.getResultCode()+" 模板id："+templateId);
    }


}
