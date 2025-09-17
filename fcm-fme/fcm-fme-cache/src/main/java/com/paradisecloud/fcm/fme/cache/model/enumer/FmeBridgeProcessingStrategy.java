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
package com.paradisecloud.fcm.fme.cache.model.enumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.sinhy.exception.SystemException;

/**
 * <pre>FME桥业务处理策略</pre>
 * 
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum FmeBridgeProcessingStrategy
{
    
    /**
     * 随机
     */
    RANDOM(1, "随机") {
        public void process(List<FmeBridge> fbs, FmeBridgeProcessor fmeBridgeProcessor)
        {
            fmeBridgeProcessor.process(fbs.get(RANDOM0.nextInt(fbs.size())));
        }
    },
    
    /**
     * 中断
     */
    BREAK(2, "中断") {
        public void process(List<FmeBridge> fbs, FmeBridgeProcessor fmeBridgeProcessor)
        {
            for (FmeBridge fmeBridge : fbs)
            {
                if (fmeBridgeProcessor.isBreak())
                {
                    break;
                }
                fmeBridgeProcessor.process(fmeBridge);
            }
        }
    },
    
    /**
     * 遍历
     */
    TRAVERSE(3, "遍历") {
        public void process(List<FmeBridge> fbs, FmeBridgeProcessor fmeBridgeProcessor)
        {
            for (FmeBridge fmeBridge : fbs)
            {
                fmeBridgeProcessor.process(fmeBridge);
            }
        }
    };
    
    /**
     * 值
     */
    private int value;
    
    /**
     * 名
     */
    private String name;
    
    private static final Random RANDOM0 = new Random();
    private static final Map<Integer, FmeBridgeProcessingStrategy> MAP = new HashMap<>();
    static
    {
        for (FmeBridgeProcessingStrategy recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    FmeBridgeProcessingStrategy(int value, String name)
    {
        this.value = value;
        this.name = name;
    }
    
    /**
     * <p>
     * Get Method : value int
     * </p>
     * 
     * @return value
     */
    public int getValue()
    {
        return value;
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
    
    public abstract void process(List<FmeBridge> fbs, FmeBridgeProcessor fmeBridgeProcessor);
    
    public static FmeBridgeProcessingStrategy convert(int value)
    {
        FmeBridgeProcessingStrategy t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + FmeBridgeProcessingStrategy.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
