package com.paradisecloud.fcm.cdr.service.core.listener;

import com.paradisecloud.fcm.dao.model.CdrCall;
import org.springframework.context.ApplicationEvent;

/**
 * 定义根据会议结束CDR记录生成话单及报表的事件
 *
 * @author johnson liu
 * @date 2021/5/27 13:43
 */
public class AllRecordEvent extends ApplicationEvent {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-12-16 14:52
     */
    private static final long serialVersionUID = 1L;

    private CdrCall cdrCall;
    private String fmeIp;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public AllRecordEvent(Object source) {
        super(source);
    }

    public AllRecordEvent(Object source, CdrCall cdrCall, String fmeIp) {
        super(source);
        this.cdrCall = cdrCall;
        this.fmeIp = fmeIp;
    }

    public CdrCall getCdrCall() {
        return cdrCall;
    }

    public void setCdrCall(CdrCall cdrCall) {
        this.cdrCall = cdrCall;
    }

    public String getFmeIp() {
        return fmeIp;
    }

    public void setFmeIp(String fmeIp) {
        this.fmeIp = fmeIp;
    }

    @Override
    public String toString() {
        return "AllRecordEvent{" +
                "cdrCall=" + cdrCall +
                ", fmeIp='" + fmeIp + '\'' +
                '}';
    }
}
