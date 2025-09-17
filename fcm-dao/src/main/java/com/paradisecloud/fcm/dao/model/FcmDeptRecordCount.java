/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DeptRecordCount.java
 * Package     : com.paradisecloud.fcm.dao.model
 * @author sinhy 
 * @since 2021-10-29 10:45
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.dao.model;

/**  
 * <pre>部门记录数实体</pre>
 * @author sinhy
 * @since 2021-10-29 10:45
 * @version V1.0  
 */
public class FcmDeptRecordCount
{
    
    private Long deptId;
    private Integer count;
    /**
     * <p>Get Method   :   deptId Long</p>
     * @return deptId
     */
    public Long getDeptId()
    {
        return deptId;
    }
    /**
     * <p>Set Method   :   deptId Long</p>
     * @param deptId
     */
    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }
    /**
     * <p>Get Method   :   count Integer</p>
     * @return count
     */
    public Integer getCount()
    {
        return count;
    }
    /**
     * <p>Set Method   :   count Integer</p>
     * @param count
     */
    public void setCount(Integer count)
    {
        this.count = count;
    }
}
