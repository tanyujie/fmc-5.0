/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DeptAttendees.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.polling
 * @author lilinhai 
 * @since 2021-02-25 15:44
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.polling;

import java.util.ArrayList;
import java.util.List;

import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;

/**  
 * <pre>部门参会者</pre>
 * @author lilinhai
 * @since 2021-02-25 15:44
 * @version V1.0  
 */
public class DeptPollingAttendees implements Comparable<DeptPollingAttendees>
{
    
    private long deptId;
    
    /**
     * 权重顺序
     */
    private int weight;
    
    /**
     * 轮询参会者列表，已排序
     */
    private List<PollingAttendee> pollingAttendees = new ArrayList<>();

    /**
     * <p>Get Method   :   pollingAttendee List<PollingAttendee></p>
     * @return pollingAttendee
     */
    public List<PollingAttendee> getPollingAttendees()
    {
        return pollingAttendees;
    }

    /**
     * <p>Set Method   :   pollingAttendee List<PollingAttendee></p>
     * @param pollingAttendee
     */
    public void addPollingAttendee(PollingAttendee pollingAttendee)
    {
        this.pollingAttendees.add(pollingAttendee);
    }

    /**
     * <p>Get Method   :   deptId long</p>
     * @return deptId
     */
    public long getDeptId()
    {
        return deptId;
    }

    /**
     * <p>Set Method   :   deptId long</p>
     * @param deptId
     */
    public void setDeptId(long deptId)
    {
        this.deptId = deptId;
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
    
    public void removeInvalidAttendee(ConferenceContext cc)
    {
        for (PollingAttendee pollingAttendee : new ArrayList<>(pollingAttendees))
        {
            if (pollingAttendee.getAttendee() != null) {
                if (cc.getAttendeeById(pollingAttendee.getAttendee().getId()) == null) {
                    pollingAttendees.remove(pollingAttendee);
                }
            }
        }
    }

    @Override
    public int compareTo(DeptPollingAttendees o)
    {
        return o.weight - weight;
    }
}
