/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CellScreen.java
 * Package     : com.paradisecloud.fcm.fme.service.model.layout
 * @author lilinhai 
 * @since 2021-02-09 14:23
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.busi.layout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paradisecloud.fcm.common.enumer.CellScreenAttendeeOperation;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;

/**  
 * <pre>分屏单元</pre>
 * @author lilinhai
 * @since 2021-02-09 14:23
 * @version V1.0  
 */
public class CellScreen implements Serializable
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-22 13:05 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 权重
     */
    @JsonIgnore
    private int importance;
    
    /**
     * 分屏单元格需要渲染的参会者
     */
    private List<Attendee> attendees = new ArrayList<>();
    
    /**
     * 轮询索引下标
     */
    private int pollingIndex;
    
    /**
     * 上一个操作的参会者（用于单元格轮询的时候取消）
     */
    @JsonIgnore
    private Attendee lastOperationAttendee;
    
    /**
     * 单元格参会者操作枚举选项
     */
    private CellScreenAttendeeOperation cellScreenAttendeeOperation;
    
    /**
     * 单元格是否固定画面
     */
    private YesOrNo isFixed = YesOrNo.NO;
    
    /**
     * 单元格序号，权重根据此序号进行相应设置
     */
    private int serialNumber;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 14:19 
     * @param importance 
     */
    CellScreen(int serialNumber, int importance)
    {
        this.serialNumber = serialNumber;
        this.importance = importance;
    }

    /**
     * <p>Get Method   :   importance int</p>
     * @return importance
     */
    public int getImportance()
    {
        return importance;
    }
    
    /**
     * <p>Set Method   :   importance int</p>
     * @param importance
     */
    public void setImportance(int importance)
    {
        this.importance = importance;
    }

    /**
     * <p>Get Method   :   isFixed boolean</p>
     * @return isFixed
     */
    public boolean isFixed()
    {
        return isFixed == YesOrNo.YES;
    }
    
    /**
     * <p>Get Method   :   isFixed boolean</p>
     * @return isFixed
     */
    public int getIsFixedValue()
    {
        return isFixed != null ? isFixed.getValue() : YesOrNo.NO.getValue();
    }

    /**
     * <p>Set Method   :   isFixed boolean</p>
     * @param isFixed
     */
    public void setFixed(YesOrNo isFixed)
    {
        this.isFixed = isFixed;
    }

    /**
     * <p>Get Method   :   serialNumber int</p>
     * @return serialNumber
     */
    public int getSerialNumber()
    {
        return serialNumber;
    }

    /**
     * <p>Set Method   :   serialNumber int</p>
     * @param serialNumber
     */
    public void setSerialNumber(int serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    /**
     * <p>Get Method   :   cellScreenAttendeeOperation CellScreenAttendeeOperation</p>
     * @return cellScreenAttendeeOperation
     */
    public CellScreenAttendeeOperation getCellScreenAttendeeOperation()
    {
        return cellScreenAttendeeOperation;
    }
    
    /**
     * <p>Get Method   :   cellScreenAttendeeOperation CellScreenAttendeeOperation</p>
     * @return cellScreenAttendeeOperation
     */
    public int getCellScreenAttendeeOperationId()
    {
        return cellScreenAttendeeOperation != null ? cellScreenAttendeeOperation.getValue() : CellScreenAttendeeOperation.CHOOSE_SEE.getValue();
    }

    /**
     * <p>Set Method   :   cellScreenAttendeeOperation CellScreenAttendeeOperation</p>
     * @param cellScreenAttendeeOperation
     */
    public void setCellScreenAttendeeOperation(CellScreenAttendeeOperation cellScreenAttendeeOperation)
    {
        this.cellScreenAttendeeOperation = cellScreenAttendeeOperation;
    }

    /**
     * <p>Get Method   :   attendees List<Attendee></p>
     * @return attendees
     */
    public List<Attendee> getAttendees()
    {
        return attendees;
    }
    
    public void addAttendee(Attendee a)
    {
        attendees.add(a);
    }

    /**
     * <p>Get Method   :   lastOperationAttendee Attendee</p>
     * @return lastOperationAttendee
     */
    public Attendee getLastOperationAttendee()
    {
        return lastOperationAttendee;
    }

    /**
     * <p>Set Method   :   lastOperationAttendee Attendee</p>
     * @param lastOperationAttendee
     */
    public void setLastOperationAttendee(Attendee lastOperationAttendee)
    {
        this.lastOperationAttendee = lastOperationAttendee;
    }

    /**
     * <p>Get Method   :   pollingIndex int</p>
     * @return pollingIndex
     */
    @JsonIgnore
    public Attendee getPollingAttendee()
    {
        if (ObjectUtils.isEmpty(attendees))
        {
            return null;
        }
        
        if (pollingIndex >= attendees.size())
        {
            pollingIndex = 0;
        }
        return attendees.get(pollingIndex++);
    }

    @Override
    public String toString()
    {
        return "CellScreen [importance=" + importance + ", cellScreenAttendeeOperation=" + cellScreenAttendeeOperation + ", isFixed=" + isFixed + ", serialNumber=" + serialNumber + "]";
    }
    
}
