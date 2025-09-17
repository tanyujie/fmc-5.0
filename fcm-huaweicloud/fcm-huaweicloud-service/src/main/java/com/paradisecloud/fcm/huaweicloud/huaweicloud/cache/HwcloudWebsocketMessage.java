package com.paradisecloud.fcm.huaweicloud.huaweicloud.cache;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author nj
 * @date 2023/3/20 14:08
 */
@JsonIgnoreProperties({ "destination"})
public class HwcloudWebsocketMessage {
    /**
     * 消息类型
     */
    private String type;

    /**
     * 消息内容
     */
    private Object data;

    /**
     * 当前时间戳
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * 目的地
     */
    private String destination;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-02-04 16:38
     */
    public HwcloudWebsocketMessage()
    {
    }

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-02-04 16:38
     * @param type
     * @param data
     */
    public HwcloudWebsocketMessage(String type, Object data)
    {
        this.type = type;
        this.data = data;
    }

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-02-04 17:17
     * @param type
     * @param data
     * @param destination
     */
    public HwcloudWebsocketMessage(String type, Object data, String destination)
    {
        super();
        this.type = type;
        this.data = data;
        this.destination = destination;
    }

    /**
     * <p>Get Method   :   type String</p>
     * @return type
     */
    public String getType()
    {
        return type;
    }

    /**
     * <p>Set Method   :   type String</p>
     * @param type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * <p>Get Method   :   timestamp long</p>
     * @return timestamp
     */
    public long getTimestamp()
    {
        return timestamp;
    }

    /**
     * <p>Set Method   :   timestamp long</p>
     * @param timestamp
     */
    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    /**
     * <p>Get Method   :   data Object</p>
     * @return data
     */
    public Object getData()
    {
        return data;
    }

    /**
     * <p>Set Method   :   data Object</p>
     * @param data
     */
    public void setData(Object data)
    {
        this.data = data;
    }

    /**
     * <p>Get Method   :   destination String</p>
     * @return destination
     */
    public String getDestination()
    {
        return destination;
    }

    /**
     * <p>Set Method   :   destination String</p>
     * @param destination
     */
    public void setDestination(String destination)
    {
        this.destination = destination;
    }

    @Override
    public String toString()
    {
        return "WebSocketMessage [type=" + type + ", data=" + data + ", timestamp=" + timestamp + ", destination=" + destination + "]";
    }
}
