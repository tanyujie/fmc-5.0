/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2020-, All right reserved.
 * Description : <pre>(用一句话描述该文件做什么)</pre>
 * FileName    :
 * Package     :
 * @author
 * @since 2020/12/24 11:18
 * @version  V1.0
 */
package com.paradisecloud.fcm.common.enumer;

import java.util.HashMap;
import java.util.Map;

/**
 * 会议记录枚举类
 * @author ws
 * @version 1.0
 * @createTime 2020/12/24 11:18
 */
public enum RecordType {

    /**
     * 会议进行中
     */
    CONFERENCE_LIVING(1,"会议进行中"),

    /**
     * 会议结束
     */
    CONFERENCE_ENDED(2,"会议结束"),

    /**
     * 会议时间格式
     */
    CONFERENCE_DATE_FORMAT(3,"yyyy年MM月dd日HH时mm分ss秒"),

    /**
     * 会议记录
     */
    CONFERENCE_RECORD(4,"会议记录"),

    /**
     * 会议密码
     */
    CONFERENCE_NO_PASSWORD(5,"无会议密码");

    /**
     * 信息码
     */
    private int code;

    /**
     * 信息描述
     */
    private String message;
    
    private static final Map<Integer, RecordType> MAP = new HashMap<>();
    static
    {
        for (RecordType recordType : values())
        {
            MAP.put(recordType.code, recordType);
        }
    }
    
    RecordType(int code, String message){
        this.code=code;
        this.message=message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
    
    public static RecordType convert(int code) {
        RecordType t = MAP.get(code);
        if (t == null)
        {
            return RecordType.CONFERENCE_LIVING;
        }
        return t;
    }
}
