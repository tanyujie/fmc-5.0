/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CellScreenAttendeePollingThread.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author sinhy 
 * @since 2021-08-20 14:47
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.operation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.fme.attendee.exception.PollingCancelException;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.layout.CellScreen;

/**  
 * <pre>窗格参会者轮询线程</pre>
 * @author sinhy
 * @since 2021-08-20 14:47
 * @version V1.0  
 */
class CellScreenAttendeePollingThread extends Thread
{
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    private int pollingInterval;
    
    private List<CellScreen> css = new ArrayList<CellScreen>();
    
    private volatile ConferenceContext conferenceContext;
    
    private DefaultAttendeeOperation defaultAttendeeOperation;
    
    /**
     * 标记是否取消
     */
    private volatile boolean isCancel;
    
    /**
     * 是否处理本轮
     */
    private volatile boolean isProcessingThisRound;
    
    private boolean isStart;
    
    private List<Attendee> operatedAttendees = new ArrayList<>();

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-08-20 14:48 
     * @param pollingInterval
     * @param css 
     */
    public CellScreenAttendeePollingThread(DefaultAttendeeOperation defaultAttendeeOperation, ConferenceContext conferenceContext, int pollingInterval)
    {
        this.defaultAttendeeOperation = defaultAttendeeOperation;
        this.conferenceContext = conferenceContext;
        this.pollingInterval = pollingInterval;
    }
    
    public void addCellScreen(CellScreen cs)
    {
        css.add(cs);
    }
    
    public void start()
    {
        if (!ObjectUtils.isEmpty(css))
        {
            if (!isStart)
            {
                super.start();
                isStart = true;
                logger.info("已设置【" + css.size() + "】个窗格轮询方案，“窗格参会者轮询线程”已启动!");
            }
        }
        else
        {
            logger.info("没有设置窗格轮询方案，“窗格参会者轮询线程”未启动!");
        }
    }
    
    /**
     * <p>Set Method   :   isCancel boolean</p>
     * @param isCancel
     */
    public void setCancel(boolean isCancel)
    {
        this.isCancel = isCancel;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                if (ObjectUtils.isEmpty(css))
                {
                    break;
                }
                
                if (isCancel || conferenceContext.isEnd())
                {
                    throw new PollingCancelException();
                }
                
                operatedAttendees.clear();
                for (CellScreen cellScreen : css)
                {
                    Attendee attendee = cellScreen.getPollingAttendee();
                    
                    // 若是补位，则离线终端显示空白
                    if (YesOrNo.convert(defaultAttendeeOperation.getDefaultViewIsFill()) == YesOrNo.YES)
                    {
                        while (!attendee.isMeetingJoined())
                        {
                            if (isCancel || conferenceContext.isEnd())
                            {
                                throw new PollingCancelException();
                            }
                            attendee = cellScreen.getPollingAttendee();
                        }
                    }
                    
                    defaultAttendeeOperation.updateImportance(cellScreen.getImportance(), attendee);
                    operatedAttendees.add(attendee);
                    
                    // 还原上一个参会者的权重为普通权重
                    if (cellScreen.getLastOperationAttendee() != null && cellScreen.getLastOperationAttendee() != attendee)
                    {
                        defaultAttendeeOperation.updateImportance(cellScreen.getLastOperationAttendee() == conferenceContext.getMasterAttendee() 
                                ? AttendeeImportance.MASTER.getStartValue() : AttendeeImportance.COMMON.getStartValue(), cellScreen.getLastOperationAttendee());
                    }
                    
                    cellScreen.setLastOperationAttendee(attendee);
                }
                
                isProcessingThisRound = true;
                Thread.sleep(pollingInterval * 1000);
                isProcessingThisRound = false;
            }
            catch (Throwable e)
            {
                logger.error("已终止“窗格参会者轮询线程”" , e);
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "已终止【窗格参会者轮询线程】");
                break;
            }
        }
    }

    /**
     * <p>Get Method   :   operatedAttendees List<Attendee></p>
     * @return operatedAttendees
     */
    List<Attendee> getOperatedAttendees()
    {
        return operatedAttendees;
    }

    /**
     * <p>Get Method   :   isProcessingThisRound boolean</p>
     * @return isProcessingThisRound
     */
    boolean isProcessingThisRound()
    {
        return isProcessingThisRound;
    }

    /**
     * <p>Get Method   :   isStart boolean</p>
     * @return isStart
     */
    boolean isStart()
    {
        return isStart;
    }
}
