/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DeptWeight.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.core
 * @author sinhy 
 * @since 2021-09-06 19:54
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.plc.cache.model;

/**  
 * <pre>部门权重</pre>
 * @author sinhy
 * @since 2021-09-06 19:54
 * @version V1.0  
 */
public class DeptWeight implements Comparable<DeptWeight>
{
    
    private long deptId;
    private Integer weight;
    
    /**
     * <p>Get Method   :   deptId long</p>
     * @return deptId
     */
    public long getDeptId()
    {
        return deptId;
    }
    /**
     * <p>Set Method   :   deptId long</p>
     * @param deptId
     */
    public void setDeptId(long deptId)
    {
        this.deptId = deptId;
    }
    /**
     * <p>Get Method   :   weight int</p>
     * @return weight
     */
    public Integer getWeight()
    {
        return weight;
    }
    /**
     * <p>Set Method   :   weight int</p>
     * @param weight
     */
    public void setWeight(Integer weight)
    {
        this.weight = weight;
    }

    @Override
    public int compareTo(DeptWeight o)
    {
        return o.weight.compareTo(this.weight);
    }
}
