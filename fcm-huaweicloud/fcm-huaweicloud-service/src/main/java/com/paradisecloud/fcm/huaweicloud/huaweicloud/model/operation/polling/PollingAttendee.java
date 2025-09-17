/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : PollingAttendee.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.polling
 * @author lilinhai 
 * @since 2021-02-25 15:48
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.polling;


import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.AttendeeHwcloud;

/**
 * <pre>轮询参会者</pre>
 * @author lilinhai
 * @since 2021-02-25 15:48
 * @version V1.0  
 */
public class PollingAttendee implements Comparable<PollingAttendee>
{
    
    /**
     * 轮询参会者自身的时间间隔
     */
    private Integer interval;
    
    /**
     * 权重顺序
     */
    private int weight;
    
    /**
     * 参会者
     */
    private AttendeeHwcloud attendee;
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-09 21:41  
     */
    public PollingAttendee()
    {
        
    }

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-09 21:41 
     * @param attendee 
     */
    public PollingAttendee(AttendeeHwcloud attendee)
    {
        super();
        this.attendee = attendee;
    }

    /**
     * <p>Get Method   :   interval int</p>
     * @return interval
     */
    public Integer getInterval()
    {
        return interval;
    }

    /**
     * <p>Set Method   :   interval int</p>
     * @param interval
     */
    public void setInterval(Integer interval)
    {
        this.interval = interval;
    }

    /**
     * <p>Get Method   :   weight int</p>
     * @return weight
     */
    public int getWeight()
    {
        return weight;
    }

    /**
     * <p>Set Method   :   weight int</p>
     * @param weight
     */
    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    /**
     * <p>Get Method   :   attendee Attendee</p>
     * @return attendee
     */
    public AttendeeHwcloud getAttendee()
    {
        return attendee;
    }

    /**
     * <p>Set Method   :   attendee Attendee</p>
     * @param attendee
     */
    public void setAttendee(AttendeeHwcloud attendee)
    {
        this.attendee = attendee;
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author lilinhai
     * @since 2021-02-25 15:50 
     * @param o
     * @return
     * @see Comparable#compareTo(Object)
     */
    @Override
    public int compareTo(PollingAttendee o)
    {
        return o.weight - this.weight;
    }

    /**
     * @author lilinhai
     * @since 2021-02-25 15:51
     * @return
     * @see Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attendee == null) ? 0 : attendee.hashCode());
        return result;
    }

    /**
     * @author lilinhai
     * @since 2021-02-25 15:51
     * @param obj
     * @return
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PollingAttendee other = (PollingAttendee) obj;
        if (attendee == null)
        {
            if (other.attendee != null) return false;
        }
        else if (!attendee.equals(other.attendee)) return false;
        return true;
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author lilinhai
     * @since 2021-02-25 15:51
     * @return
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return "PollingAttendee [interval=" + interval + ", weight=" + weight + ", attendee=" + attendee + "]";
    }
    
}
