/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : LayoutTemplateServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.attendee.impls
 * @author sinhy 
 * @since 2021-09-14 14:31
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.impls;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.paradisecloud.fcm.fme.attendee.interfaces.ILayoutTemplateService;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.LayoutTemplate;
import com.paradisecloud.fcm.fme.model.response.layout.ActiveLayoutTemplatesResponse;
import com.paradisecloud.fcm.fme.model.response.layout.LayoutTemplatesResponse;

@Service
public class LayoutTemplateServiceImpl implements ILayoutTemplateService
{
    
    @Override
    public void sync(FmeBridge fmeBridge, LayoutTemplateConsumptionProcessor layoutTemplateConsumptionProcessor)
    {
        try
        {
            int offset = 0;
            AtomicInteger totalCount = new AtomicInteger();
            while (true)
            {
                LayoutTemplatesResponse layoutTemplatesResponse = fmeBridge.getLayoutTemplateInvoker().getLayoutTemplates(offset);
                if (layoutTemplatesResponse != null)
                {
                    ActiveLayoutTemplatesResponse activeLayoutTemplatesResponse = layoutTemplatesResponse.getLayoutTemplates();
                    if (activeLayoutTemplatesResponse != null)
                    {
                        List<LayoutTemplate> layoutTemplates = activeLayoutTemplatesResponse.getLayoutTemplate();
                        if (layoutTemplates != null)
                        {
                            // 业务处理
                            for (LayoutTemplate layoutTemplate : layoutTemplates)
                            {
                                layoutTemplateConsumptionProcessor.process(fmeBridge, layoutTemplate);
                            }
                            Integer total = activeLayoutTemplatesResponse.getTotal();
                            totalCount.addAndGet(layoutTemplates.size());
                            if (totalCount.get() < total.intValue())
                            {
                                offset = totalCount.get();
                            }
                            else
                            {
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
                else
                {
                    break;
                }
            }
            
            fmeBridge.getFmeLogger().logWebsocketInfo("LayoutTemplate sync complete: " + totalCount.get(), true);
        }
        catch (Throwable e)
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("LayoutTemplate sync error", true, e);
        }
    }
}
