package com.paradisecloud.fcm.cdr.service.core.listener;

import com.paradisecloud.fcm.dao.model.CdrCall;
import org.springframework.context.ApplicationEvent;

/**
 * 定义根据会议结束CDR记录生成话单及报表的事件
 *
 * @author johnson liu
 * @date 2021/5/27 13:43
 */
public class RecordEvent extends ApplicationEvent
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-12-16 14:52 
     */
    private static final long serialVersionUID = 1L;

    private Long deptId;
    
    private CdrCall cdrCall;
    
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *                   which the event is associated (never {@code null})
     */
    public RecordEvent(Object source)
    {
        super(source);
    }
    
    public RecordEvent(Object source, Long deptId, CdrCall cdrCall)
    {
        super(source);
        this.cdrCall = cdrCall;
        this.deptId = deptId;
    }
    
    public CdrCall getCdrCall()
    {
        return cdrCall;
    }
    
    public void setCdrCall(CdrCall cdrCall)
    {
        this.cdrCall = cdrCall;
    }
    
    public Long getDeptId()
    {
        return deptId;
    }
    
    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }
    
    @Override
    public String toString()
    {
        return "RecordEvent{" + "cdrCall=" + cdrCall + ", deptId=" + deptId + '}';
    }
}
