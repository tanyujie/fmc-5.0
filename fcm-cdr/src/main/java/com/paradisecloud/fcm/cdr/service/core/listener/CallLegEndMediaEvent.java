package com.paradisecloud.fcm.cdr.service.core.listener;

import com.paradisecloud.fcm.dao.model.CdrCallLegEndMediaInfo;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 丢包率报表统计事件
 *
 * @author johnson liu
 * @date 2021/5/28 17:51
 */
public class CallLegEndMediaEvent extends ApplicationEvent
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-12-16 14:52 
     */
    private static final long serialVersionUID = 1L;

    private Long deptId;
    
    private String callId;
    
    private List<CdrCallLegEndMediaInfo> callLegEndMediaInfoList;
    
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *                   which the event is associated (never {@code null})
     */
    public CallLegEndMediaEvent(Object source)
    {
        super(source);
    }
    
    public CallLegEndMediaEvent(Object source, Long deptId, String callId, List<CdrCallLegEndMediaInfo> callLegEndMediaInfoList)
    {
        super(source);
        this.deptId = deptId;
        this.callId = callId;
        this.callLegEndMediaInfoList = callLegEndMediaInfoList;
    }
    
    public List<CdrCallLegEndMediaInfo> getCallLegEndMediaInfoList()
    {
        return callLegEndMediaInfoList;
    }
    
    public void setCallLegEndMediaInfoList(List<CdrCallLegEndMediaInfo> callLegEndMediaInfoList)
    {
        this.callLegEndMediaInfoList = callLegEndMediaInfoList;
    }
    
    public Long getDeptId()
    {
        return deptId;
    }
    
    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }
    
    public String getCallId()
    {
        return callId;
    }
    
    public void setCallId(String callId)
    {
        this.callId = callId;
    }
    
    @Override
    public String toString()
    {
        return "CallLegEndMediaEvent{" + "deptId=" + deptId + ", callLegEndMediaInfoList=" + callLegEndMediaInfoList + '}';
    }
}
