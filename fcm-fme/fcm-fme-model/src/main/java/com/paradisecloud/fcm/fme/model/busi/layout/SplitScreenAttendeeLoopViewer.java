/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SplitScreenLoopViewer.java
 * Package     : com.paradisecloud.fcm.fme.service.model.layout
 * @author lilinhai 
 * @since 2021-02-09 15:16
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.busi.layout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.sinhy.exception.SystemException;
import com.sinhy.utils.ThreadUtils;

/**  
 * <pre>分屏参会者轮询查看器</pre>
 * @author lilinhai
 * @since 2021-02-09 15:16
 * @version V1.0  
 */
public class SplitScreenAttendeeLoopViewer implements Serializable
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-22 13:05 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 参会者
     */
    private List<Attendee> attendees;
    
    /**
     * 权重处理器
     */
    private ImportanceProcessor importanceProcessor;
    
    /**
     * 分屏
     */
    private SplitScreen splitScreen;
    
    /**
     * 间隔毫秒数
     */
    private int intervalMillisecond;
    
    /**
     * 是否填充空白
     */
    private boolean isFillBlank;
    
    /**
     * 是否停止轮询
     */
    private boolean isStopLoopView;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 15:20 
     * @param importanceProcessor 
     */
    public SplitScreenAttendeeLoopViewer(ImportanceProcessor importanceProcessor)
    {
        this.importanceProcessor = importanceProcessor;
    }

    /**
     * <p>Set Method   :   attendees List<Attendee></p>
     * @param attendees
     */
    public void setAttendees(List<Attendee> attendees)
    {
        if (ObjectUtils.isEmpty(attendees))
        {
            throw new SystemException(10074714, "参会人数不能为空");
        }
        this.attendees = attendees;
    }
    
    /**
     * <p>Set Method   :   splitScreen SplitScreen</p>
     * @param splitScreen
     */
    public void setSplitScreen(SplitScreen splitScreen)
    {
        this.splitScreen = splitScreen;
    }
    
    /**
     * <p>Set Method   :   isFillBlank boolean</p>
     * @param isFillBlank
     */
    public void setFillBlank(boolean isFillBlank)
    {
        this.isFillBlank = isFillBlank;
    }
    
    /**
     * <p>Get Method   :   isStopLoopView boolean</p>
     * @return isStopLoopView
     */
    public boolean isStopLoopView()
    {
        return isStopLoopView;
    }

    /**
     * <p>Set Method   :   isStopLoopView boolean</p>
     * @param isStopLoopView
     */
    public void setStopLoopView(boolean isStopLoopView)
    {
        this.isStopLoopView = isStopLoopView;
    }
    
    /**
     * <p>Set Method   :   intervalMillisecond int</p>
     * @param intervalMillisecond
     */
    public void setIntervalMillisecond(int intervalMillisecond)
    {
        this.intervalMillisecond = intervalMillisecond;
    }

    public void loopView()
    {
        List<Attendee> attendeeViewGroup = new ArrayList<>();
        for (int i = 0; i < attendees.size(); i++)
        {
            attendeeViewGroup.add(attendees.get(i));
            if (attendeeViewGroup.size() == splitScreen.getCellScreens().size() || (i == attendees.size() - 1 && isFillBlank))
            {
                // 轮询
                splitScreen.processImportance(attendeeViewGroup, importanceProcessor);
                attendeeViewGroup.clear();
                if (attendees.size() > splitScreen.getCellScreens().size())
                {
                    ThreadUtils.sleep(intervalMillisecond);
                    if (i == attendees.size() - 1)
                    {
                        i = 0;
                    }
                }
            }
        }
        
        // 轮询
        if (!attendeeViewGroup.isEmpty())
        {
            splitScreen.processImportance(attendeeViewGroup, importanceProcessor);
        }
    }
}
