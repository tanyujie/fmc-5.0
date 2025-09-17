/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : PollingScheme.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.polling
 * @author lilinhai 
 * @since 2021-02-25 15:37
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.zte.attendee.model.polling;

import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.zte.cache.enumer.PollingStrategy;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**  
 * <pre>轮询方案</pre>
 * @author lilinhai
 * @since 2021-02-25 15:37
 * @version V1.0  
 */
public class PollingScheme
{
    
    /**
     * 轮询方案ID
     */
    private long id;
    
    /**
     * 轮询时间间隔
     */
    private int interval;
    
    /**
     * 轮询布局
     */
    private String layout;
    
    /**
     * 是否广播
     */
    private YesOrNo isBroadcast;
    
    /**
     * 是否显示自己(1是，2否)
     */
    private PanePlacementSelfPaneMode panePlacementSelfPaneMode;
    
    /**
     * 是否补位(1是，2否)
     */
    private YesOrNo isFill;
    
    /**
     * 轮询策略
     */
    private PollingStrategy pollingStrategy;

    /**
     * 是否固定主会场
     */
    private YesOrNo isFixSelf;

    public YesOrNo getIsFixSelf() {
        return isFixSelf;
    }

    public void setIsFixSelf(YesOrNo isFixSelf) {
        this.isFixSelf = isFixSelf;
    }
    
    /**
     * 部门轮询参会者集合
     */
    private List<DeptPollingAttendees> deptPollingAttendeesList = new ArrayList<>();

    /**
     * <p>Get Method   :   interval int</p>
     * @return interval
     */
    public int getInterval()
    {
        return interval;
    }

    /**
     * <p>Set Method   :   interval int</p>
     * @param interval
     */
    public void setInterval(int interval)
    {
        this.interval = interval;
    }

    /**
     * <p>Get Method   :   pollingStrategy PollingStrategy</p>
     * @return pollingStrategy
     */
    public PollingStrategy getPollingStrategy()
    {
        return pollingStrategy;
    }

    /**
     * <p>Set Method   :   pollingStrategy PollingStrategy</p>
     * @param pollingStrategy
     */
    public void setPollingStrategy(PollingStrategy pollingStrategy)
    {
        this.pollingStrategy = pollingStrategy;
    }

    /**
     * <p>Get Method   :   deptPollingAttendeesList List<DeptPollingAttendees></p>
     * @return deptPollingAttendeesList
     */
    public List<DeptPollingAttendees> getDeptPollingAttendeesList()
    {
        return deptPollingAttendeesList;
    }

    /**
     * <p>Set Method   :   deptPollingAttendees List<DeptPollingAttendees></p>
     * @param deptPollingAttendees
     */
    public void addDeptPollingAttendees(DeptPollingAttendees deptPollingAttendees)
    {
        this.deptPollingAttendeesList.add(deptPollingAttendees);
    }
    
    /**
     * <p>Get Method   :   layout String</p>
     * @return layout
     */
    public String getLayout()
    {
        return layout;
    }

    /**
     * <p>Set Method   :   layout String</p>
     * @param layout
     */
    public void setLayout(String layout)
    {
        this.layout = layout;
    }
    
    /**
     * <p>Get Method   :   isBroadcast YesOrNo</p>
     * @return isBroadcast
     */
    public YesOrNo getIsBroadcast()
    {
        return isBroadcast;
    }

    /**
     * <p>Set Method   :   isBroadcast YesOrNo</p>
     * @param isBroadcast
     */
    public void setIsBroadcast(YesOrNo isBroadcast)
    {
        this.isBroadcast = isBroadcast;
    }

    /**
     * <p>Get Method   :   panePlacementSelfPaneMode PanePlacementSelfPaneMode</p>
     * @return panePlacementSelfPaneMode
     */
    public PanePlacementSelfPaneMode getPanePlacementSelfPaneMode()
    {
        return panePlacementSelfPaneMode;
    }

    /**
     * <p>Set Method   :   panePlacementSelfPaneMode PanePlacementSelfPaneMode</p>
     * @param panePlacementSelfPaneMode
     */
    public void setPanePlacementSelfPaneMode(PanePlacementSelfPaneMode panePlacementSelfPaneMode)
    {
        this.panePlacementSelfPaneMode = panePlacementSelfPaneMode;
    }

    /**
     * <p>Get Method   :   isFill YesOrNo</p>
     * @return isFill
     */
    public YesOrNo getIsFill()
    {
        return isFill;
    }

    /**
     * <p>Set Method   :   isFill YesOrNo</p>
     * @param isFill
     */
    public void setIsFill(YesOrNo isFill)
    {
        this.isFill = isFill;
    }
    
    /**
     * <p>Get Method   :   id long</p>
     * @return id
     */
    public long getId()
    {
        return id;
    }

    /**
     * <p>Set Method   :   id long</p>
     * @param id
     */
    public void setId(long id)
    {
        this.id = id;
    }

    public void addPollingAttendee(PollingAttendee pollingAttendee)
    {
        for (DeptPollingAttendees deptPollingAttendees : deptPollingAttendeesList)
        {
            if (pollingAttendee.getAttendee() != null) {
                if (deptPollingAttendees.getDeptId() == pollingAttendee.getAttendee().getDeptId()) {
                    deptPollingAttendees.addPollingAttendee(pollingAttendee);
                    break;
                }
            } else if (pollingAttendee.getDownCascadeAttendee() != null) {
                if (deptPollingAttendees.getDeptId() == pollingAttendee.getDownCascadeAttendee().getDeptId()) {
                    deptPollingAttendees.addPollingAttendee(pollingAttendee);
                    break;
                }
            }
        }
    }
    
    public void removeInvalidAttendee(McuZteConferenceContext cc)
    {
        for (DeptPollingAttendees deptPollingAttendees : deptPollingAttendeesList)
        {
            deptPollingAttendees.removeInvalidAttendee(cc);
        }
    }
    
    public void sort()
    {
        Collections.sort(deptPollingAttendeesList);
        for (DeptPollingAttendees deptPollingAttendees : deptPollingAttendeesList)
        {
            Collections.sort(deptPollingAttendees.getPollingAttendees());
        }
    }
    
}
