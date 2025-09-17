package com.paradisecloud.fcm.cdr.service.core.cache;

import com.paradisecloud.common.cache.JavaCache;

import java.time.LocalDateTime;

/**
 * @author johnson liu
 * @date 2021/7/3 11:21
 */
public class NoSaveCallCache extends JavaCache<String, LocalDateTime>
{
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-12-16 14:53 
     */
    private static final long serialVersionUID = 1L;
    private static final NoSaveCallCache INSTANCE = new NoSaveCallCache();
    
    public static NoSaveCallCache getInstance()
    {
        return INSTANCE;
    }
    
    @Override
    public LocalDateTime put(String key, LocalDateTime value)
    {
        synchronized (INSTANCE)
        {
            LocalDateTime localDateTime = INSTANCE.get(key);
            if (localDateTime == null)
            {
                return super.put(key, value);
            }
        }
        return null;
    }
}
