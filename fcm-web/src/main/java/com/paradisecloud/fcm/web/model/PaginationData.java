/* 
 * <p>Copyright   : LinHai Technologies Co., Ltd. Copyright 2018, All right reserved.</p>
 * <p>Description : <pre>TODO(用一句话描述该文件做什么)</pre></p>
 * <p>FileName    : PageData.java</p>
 * <p>Package     : com.meme.crm.service.core.model</p>
 * @Creator lilinhai 2018年2月7日 下午5:49:05
 * @Version  V1.0  
 */

package com.paradisecloud.fcm.web.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页结果数据
 * <p>ClassName      : PageData</p>
 * @author lilinhai 2018年2月7日 下午5:49:05
 * @version 1.0
 */
public class PaginationData<T>
{
    
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 第几页的数据
     */
    private int page;
    
    /**
     * 当前页的大小
     */
    private int size;
    
    /**
     * 返回实体集合
     */
    private List<T> records = new ArrayList<T>();
    
    /**
     * <p>Get Method   :   total long</p>
     * @return total
     */
    public long getTotal()
    {
        return total;
    }
    
    /**
     * <p>Set Method   :   total long</p>
     * @param total
     */
    public void setTotal(long total)
    {
        this.total = total;
    }
    
    /**
     * <p>Get Method   :   page int</p>
     * @return page
     */
    public int getPage()
    {
        return page;
    }
    
    /**
     * <p>Set Method   :   page int</p>
     * @param page
     */
    public void setPage(int page)
    {
        this.page = page;
    }
    
    /**
     * <p>Get Method   :   size int</p>
     * @return size
     */
    public int getSize()
    {
        return size;
    }
    
    /**
     * <p>Set Method   :   size int</p>
     * @param size
     */
    public void setSize(int size)
    {
        this.size = size;
    }
    
    /**
     * <p>Get Method   :   dataList List<Entity></p>
     * @return dataList
     */
    public List<T> getRecords()
    {
        return records;
    }
    
    /**
     * <p>Set Method   :   dataList List<Entity></p>
     * @param records
     */
    public void addRecord(T entityBean)
    {
        this.records.add(entityBean);
    }
    
    /**
     * <p>Set Method   :   records List<T></p>
     * @param records
     */
    public void setRecords(List<T> records)
    {
        this.records = records;
    }
    
}
