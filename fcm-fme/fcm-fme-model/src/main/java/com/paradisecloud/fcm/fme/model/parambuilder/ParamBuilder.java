/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ParticipantParamBuilder.java
 * Package     : com.paradisecloud.fcm.fme.model.param
 * @author lilinhai 
 * @since 2021-02-19 11:46
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.parambuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**  
 * <pre>抽象参数构建器</pre>
 * @author lilinhai
 * @since 2021-02-19 11:46
 * @version V1.0  
 */
@SuppressWarnings("unchecked")
public abstract class ParamBuilder<T extends ParamBuilder<?>>
{
    
    protected List<NameValuePair> nameValuePairs = new ArrayList<>();
    
    private T t;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-19 11:55  
     */
    protected ParamBuilder()
    {
        this.t = (T)this;
    }

    /**
     * 参数构建
     * @author lilinhai
     * @since 2021-02-19 11:50 
     * @param name
     * @param value
     * @return ParticipantParamBuilder
     */
    public T param(String name, String value)
    {
        if(value != null)
        {
            nameValuePairs.add(new BasicNameValuePair(name, value));
        }
        return t;
    }
    
    public <A extends Serializable> T param(String name, A[] as)
    {
        if (as != null)
        {
            for (A val : as)
            {
                if (val != null)
                {
                    if (val instanceof String)
                    {
                        return param(name, (String) val);
                    }
                    else if (val instanceof Integer)
                    {
                        return param(name, (Integer) val);
                    }
                    else if (val instanceof Long)
                    {
                        return param(name, (Long) val);
                    }
                    else if (val instanceof Boolean)
                    {
                        return param(name, (Boolean) val);
                    }
                    else if (val instanceof Float)
                    {
                        return param(name, (Float) val);
                    }
                    else if (val instanceof Double)
                    {
                        return param(name, (Double) val);
                    }
                    else
                    {
                        return param(name, val.toString());
                    }
                }
            }
        }
        return t;
    }
    
    /**
     * 参数构建
     * @author lilinhai
     * @since 2021-02-19 11:50 
     * @param name
     * @param value
     * @return ParticipantParamBuilder
     */
    public T param(String name, int value)
    {
        return param(name, String.valueOf(value));
    }
    
    /**
     * 参数构建
     * @author lilinhai
     * @since 2021-02-19 11:50 
     * @param name
     * @param value
     * @return ParticipantParamBuilder
     */
    public T param(String name, long value)
    {
        return param(name, String.valueOf(value));
    }
    
    /**
     * 参数构建
     * @author lilinhai
     * @since 2021-02-19 11:50 
     * @param name
     * @param value
     * @return ParticipantParamBuilder
     */
    public T param(String name, boolean value)
    {
        return param(name, String.valueOf(value));
    }
    
    /**
     * 参数构建
     * @author lilinhai
     * @since 2021-02-19 11:50 
     * @param name
     * @param value
     * @return ParticipantParamBuilder
     */
    public T param(String name, float value)
    {
        return param(name, String.valueOf(value));
    }
    
    /**
     * 参数构建
     * @author lilinhai
     * @since 2021-02-19 11:50 
     * @param name
     * @param value
     * @return ParticipantParamBuilder
     */
    public T param(String name, double value)
    {
        return param(name, String.valueOf(value));
    }
    
    /**
     * 开始构建
     * @author lilinhai
     * @since 2021-02-19 11:58 
     * @return List<NameValuePair>
     */
    public List<NameValuePair> build()
    {
        return nameValuePairs;
    }
}
