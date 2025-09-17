/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : BridgeHostDiagnosisUtils.java
 * Package     : com.paradisecloud.fcm.fme.service.utils
 * @author lilinhai 
 * @since 2020-12-29 16:02
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.utils;

import com.paradisecloud.fcm.dao.model.BusiFme;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import com.sinhy.model.GenericValue;
import com.sinhy.utils.HostUtils;
import com.sinhy.utils.ThreadUtils;

/**  
 * <pre>会议桥工具类</pre>
 * @author lilinhai
 * @since 2020-12-29 16:02
 * @version V1.0  
 */
public class BridgeUtils
{
    
    /**
     * 检测会议桥是否可用的超时时间：1秒
     */
    private static final int TIMEOUT = 1000;
    
    /**
     * <pre>根据FMEid获取FME桥</pre>
     * @author lilinhai
     * @since 2021-02-05 14:25 
     * @param fmeId
     * @return FmeBridge
     */
    public static FmeBridge getFmeBridge(Long fmeId)
    {
        return FmeBridgeCache.getInstance().get(fmeId);
    }
    
    /**
     * <pre>根据部门ID获取FME桥，为空会抛异常</pre>
     * @author lilinhai
     * @since 2021-02-05 14:25 
     * @param deptId
     * @return FmeBridge
     */
    public static FmeBridge getAvailableFmeBridge(Long deptId)
    {
        return getAvailableFmeBridge(deptId, true);
    }
    
    /**
     * 根据部门和会议号获取fme桥，若是不存在，则降级处理：只根据部门获取FME桥，若也不存在，则表明该部门不存在可用的FME桥
     * @author Administrator
     * @since 2021-03-07 00:16 
     * @param deptId
     * @param conferenceNumber
     * @param isThrowError
     * @return FmeBridge
     */
    public static FmeBridge getFmeBridgeByDeptIdAndConferenceNumber(Long deptId, String conferenceNumber, boolean isThrowError)
    {
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceNumber(deptId, conferenceNumber, false);
        if (fmeBridge != null)
        {
            return fmeBridge;
        }
        return getAvailableFmeBridge(deptId, isThrowError);
    }
    
    /**
     * <pre>获取最大优先级的</pre>
     * @author lilinhai
     * @since 2021-02-02 14:07 
     * @param deptId
     * @param isThrowError
     * @return FmeBridge
     */
    public static FmeBridge getAvailableFmeBridge(Long deptId, boolean isThrowError)
    {
        if (deptId == null)
        {
            deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
        }
        if (deptId == null)
        {
            throw new SystemException(1003232, "获取FME桥失败，部门ID不能为空！");
        }
        
        GenericValue<FmeBridge> genericValue = new GenericValue<>();
        FmeBridgeCache.getInstance().doBreakFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
        {
            public void process(FmeBridge fmeBridge)
            {
                genericValue.setValue(fmeBridge);
                setBreak(true);
            }
        }, isThrowError);
        
        return genericValue.getValue();
    }
    
    /**
     * <pre>判断会议桥是否可用(如果出现一次无法连接，则再做两次连续检测，让三次检测结果都指向无法连接，则证明该会议桥确实不可用)</pre>
     * @author lilinhai
     * @since 2020-12-28 10:43 
     * @param busiFme
     * @return BridgeHostDiagnosisResult
     */
    public static boolean isConnectable(BusiFme busiFme)
    {
        return HostUtils.isHostReachable(busiFme.getIp(), TIMEOUT);
    }
    
    /**
     * <pre>判断会议桥是否可用(如果出现一次无法连接，则再做两次连续检测，让三次检测结果都指向无法连接，则证明该会议桥确实不可用)</pre>
     * @author lilinhai
     * @since 2020-12-28 10:43 
     * @param busiFme
     * @return BridgeHostDiagnosisResult
     */
    public static BridgeDiagnosisResult diagnosis(BusiFme busiFme)
    {
        int diagnosisCount = 3;
        int successCount = 0;
        for (int i = 0; i < diagnosisCount; i++)
        {
            if (HostUtils.isHostConnectable(busiFme.getIp(), busiFme.getPort(), TIMEOUT))
            {
                successCount++;
                if (i < diagnosisCount)
                {
                    ThreadUtils.sleep(TIMEOUT);
                }
            }
            else
            {
                ThreadUtils.sleep(TIMEOUT);
            }
        }
        return new BridgeDiagnosisResult(diagnosisCount, successCount);
    }
    
    /**
     * <pre>会议桥诊断结果</pre>
     * @author lilinhai
     * @since 2020-12-29 16:24
     * @version V1.0
     */
    public static class BridgeDiagnosisResult
    {
        /**
         * 诊断次数
         */
        private int diagnosisCount;
        
        /**
         * 成功次数
         */
        private int successCount;
        
        /**
         * 成功率
         */
        private double successRate;
        
        /**
         * <pre>构造方法</pre>
         * @author lilinhai 
         * @since 2020-12-29 16:23 
         * @param diagnosisCount
         * @param successCount 
         */
        public BridgeDiagnosisResult(int diagnosisCount, int successCount)
        {
            super();
            this.diagnosisCount = diagnosisCount;
            this.successCount = successCount;
            this.successRate = (successCount + 0.0) / diagnosisCount;
        }

        /**
         * <p>Get Method   :   diagnosisCount int</p>
         * @return diagnosisCount
         */
        public int getDiagnosisCount()
        {
            return diagnosisCount;
        }

        /**
         * <p>Get Method   :   successCount int</p>
         * @return successCount
         */
        public int getSuccessCount()
        {
            return successCount;
        }

        /**
         * <p>Get Method   :   successRate double</p>
         * @return successRate
         */
        public double getSuccessRate()
        {
            return successRate;
        }

        @Override
        public String toString()
        {
            return "diagnosisCount=" + diagnosisCount + ", successCount=" + successCount + ", successRate=" + successRate + "";
        }
    }
}
