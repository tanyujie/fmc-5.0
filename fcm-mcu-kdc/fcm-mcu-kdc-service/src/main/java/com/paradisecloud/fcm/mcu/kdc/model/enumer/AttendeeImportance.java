/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2020-, All right reserved.
 * Description : <pre>(用一句话描述该文件做什么)</pre>
 * FileName :
 * Package :
 * 
 * @author
 * 
 * @since 2020/12/24 11:18
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.mcu.kdc.model.enumer;

import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcConferenceContextCache;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.McuAttendeeForMcuKdc;

/**
 * <pre>与会者权重</pre>
 * 点名:        [8000000, 8999999]
 * 主会场：      7500000
 * 选看：       [6000000, 6999999]
 * 轮询：       [5000000, 5999999]
 * 省FME权重：   7000000 （当地州终端是主会场时，省FME权重为4000000）
 * 普通会场：    20000
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum AttendeeImportance
{
    
    /**
     * 分会场
     */
    COMMON(null, null, "分会场")
    {
        public void processAttendeeWebsocketMessage(AttendeeForMcuKdc attendee)
        {
            attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
            attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
            attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
            attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
            attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
        }
    },
    
    /**
     * 主会场
     */
    MASTER(7500000, 7500000, "主会场")
    {
        public void processAttendeeWebsocketMessage(AttendeeForMcuKdc attendee)
        {
            // 主会场没有选看，广播和轮询三种状态
            attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
            attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
            attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
            attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
            attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
            
            if (!(attendee instanceof McuAttendeeForMcuKdc))
            {
                McuKdcConferenceContext mainConferenceContext = McuKdcConferenceContextCache.getInstance().getMainConferenceContext(attendee.getConferenceNumber());
                if (mainConferenceContext != null && mainConferenceContext.getMasterAttendee() != attendee)
                {
                    AttendeeForMcuKdc oldMasterAttendee = mainConferenceContext.getMasterAttendee();
                    
                    // 主会场的内存数据结构变更
                    mainConferenceContext.setMasterAttendee(attendee);
                }
            }
        }
    },
    
    /**
     * 广播式轮询
     */
    ROUND_BROADCAST(8600000, 8999999, "广播式轮询")
    {
        public void processAttendeeWebsocketMessage(AttendeeForMcuKdc attendee)
        {
            if (!(attendee instanceof McuAttendeeForMcuKdc))
            {
                attendee.setBroadcastStatus(BroadcastStatus.YES.getValue());
                attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                if (attendee.getUpdateMap().size() > 1)
                {
                    McuKdcConferenceContext mainConferenceContext = McuKdcConferenceContextCache.getInstance().getMainConferenceContext(attendee.getConferenceNumber());
                    if (mainConferenceContext != null)
                    {

                    }
                }
            }
        
        }
    },
    
    /**
     * 广播
     */
    BROADCAST(8200000, 8599999, "广播")
    {
        public void processAttendeeWebsocketMessage(AttendeeForMcuKdc attendee)
        {
            if (!(attendee instanceof McuAttendeeForMcuKdc))
            {
                attendee.setBroadcastStatus(BroadcastStatus.YES.getValue());
                attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                if (attendee.getUpdateMap().size() > 1)
                {
                    McuKdcConferenceContext mainConferenceContext = McuKdcConferenceContextCache.getInstance().getMainConferenceContext(attendee.getConferenceNumber());
                    if (mainConferenceContext != null)
                    {
                    }
                }
            }
        }
    },
    
    /**
     * 对话
     */
    TALK(8160000, 81999999, "对话")
    {
        public void processAttendeeWebsocketMessage(AttendeeForMcuKdc attendee)
        {
            if (!(attendee instanceof McuAttendeeForMcuKdc))
            {
                attendee.setTalkStatus(AttendeeTalkStatus.YES.getValue());
                attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
                attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                if (attendee.getUpdateMap().size() > 1)
                {
                    McuKdcConferenceContext mainConferenceContext = McuKdcConferenceContextCache.getInstance().getMainConferenceContext(attendee.getConferenceNumber());
                    if (mainConferenceContext != null && mainConferenceContext.getMasterAttendee() != null && attendee != mainConferenceContext.getMasterAttendee())
                    {
                    }
                }
            }
        }
    },
    
    /**
     * 点名
     */
    POINT(8000000, 81599999, "点名")
    {
        public void processAttendeeWebsocketMessage(AttendeeForMcuKdc attendee)
        {
            if (!(attendee instanceof McuAttendeeForMcuKdc))
            {
                attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.YES.getValue());
                attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
                attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                if (attendee.getUpdateMap().size() > 1)
                {
                    McuKdcConferenceContext mainConferenceContext = McuKdcConferenceContextCache.getInstance().getMainConferenceContext(attendee.getConferenceNumber());
                    if (mainConferenceContext != null)
                    {
                    }
                }
            }
        }
    },
    
    /**
     * 选看
     */
    CHOOSE_SEE(6000000, 6999999, "选看")
    {
        public void processAttendeeWebsocketMessage(AttendeeForMcuKdc attendee)
        {
            if (!(attendee instanceof McuAttendeeForMcuKdc))
            {
                attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());
                attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
                attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                if (attendee.getUpdateMap().size() > 1)
                {
                    McuKdcConferenceContext mainConferenceContext = McuKdcConferenceContextCache.getInstance().getMainConferenceContext(attendee.getConferenceNumber());
                    if (mainConferenceContext != null && mainConferenceContext.getMasterAttendee() != null)
                    {
                    }
                }
            }
        }
    },
    
    /**
     * 轮询
     */
    ROUND(5000000, 5999999, "轮询")
    {
        public void processAttendeeWebsocketMessage(AttendeeForMcuKdc attendee)
        {
            if (!(attendee instanceof McuAttendeeForMcuKdc))
            {
                attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.YES.getValue());
                attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
                attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                if (attendee.getUpdateMap().size() > 1)
                {
                    McuKdcConferenceContext mainConferenceContext = McuKdcConferenceContextCache.getInstance().getMainConferenceContext(attendee.getConferenceNumber());
                    if (mainConferenceContext != null && mainConferenceContext.getMasterAttendee() != null)
                    {
                    }
                }
            }
        }
    },
    
    /**
     * 主FME在子的权重
     */
    UP_FME(7000000, 7000000, "上级FME终端在下级会议中的权重") 
    {
        public void processAttendeeWebsocketMessage(AttendeeForMcuKdc attendee)
        {
            
        }
    },
    
    /**
     * 当地州终端是主会场时，省FME权重为4000000
     */
    UP_FME_WHILE_MASTER_IS_SUB(4000000, 4000000, "当地州终端是主会场时，省FME权重为4000000") 
    {
        public void processAttendeeWebsocketMessage(AttendeeForMcuKdc attendee)
        {
            
        }
    };
    
    /**
     * 起始值
     */
    private Integer startValue;
    
    /**
     * 结束值
     */
    private Integer endValue;
    
    /**
     * 名
     */
    private String name;
    
    AttendeeImportance(Integer startValue, Integer endValue, String name)
    {
        this.startValue = startValue;
        this.endValue = endValue;
        this.name = name;
    }
    
    private static String getViewName(String deptName, String name)
    {
        if (deptName.equals(name))
        {
            return deptName;
        }
        return name + " / " + deptName;
    }
    
    /**
     * <p>
     * Get Method : value int
     * </p>
     * 
     * @return value
     */
    public Integer getStartValue()
    {
        return startValue;
    }
    
    /**
     * <p>Get Method   :   endValue int</p>
     * @return endValue
     */
    public Integer getEndValue()
    {
        return endValue;
    }

    /**
     * <p>
     * Get Method : name String
     * </p>
     * 
     * @return name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * 处理参会者消息
     * @author lilinhai
     * @since 2021-03-09 16:25 
     * @param attendee void
     */
    public abstract void processAttendeeWebsocketMessage(AttendeeForMcuKdc attendee);
    
    public boolean is(Integer value)
    {
        if (value == null || startValue == null || endValue == null)
        {
            return startValue == value && endValue == value;
        }
        return startValue.intValue() <= value.intValue() && value.intValue() <= endValue.intValue();
    }
    
    public static AttendeeImportance convert(Integer value)
    {
        for (AttendeeImportance attendeeImportance : values())
        {
            if (attendeeImportance.is(value))
            {
                return attendeeImportance;
            }
        }
        return null;
    }
  
}
