/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : JSONTypeHandler.java
 * Package : com.paradisecloud.fcm.dao.typehandler
 * 
 * @author sinhy
 * 
 * @since 2021-10-31 10:42
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.dao.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * <pre>Map格式的json处理器</pre>
 * 
 * @author sinhy
 * @since 2021-10-31 10:42
 * @version V1.0
 */
@MappedTypes({Map.class, JSONObject.class, List.class, JSONArray.class})
public class JSONTypeMapHandler extends BaseTypeHandler<Map<String, Object>>
{
    
    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * 
     * @author sinhy
     * @since 2021-10-31 10:45
     * @param ps
     * @param i
     * @param parameter
     * @param jdbcType
     * @throws SQLException
     * @see org.apache.ibatis.type.BaseTypeHandler#setNonNullParameter(java.sql.PreparedStatement, int, java.lang.Object, org.apache.ibatis.type.JdbcType)
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, Object> parameter, JdbcType jdbcType) throws SQLException
    {
        if (parameter != null)
        {
            if (jdbcType == null)
            {
                ps.setObject(i, JSON.toJSONString(parameter));
            }
            else
            {
                ps.setObject(i, JSON.toJSONString(parameter), jdbcType.TYPE_CODE);
            }
        }
    }
    
    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * 
     * @author sinhy
     * @since 2021-10-31 10:45
     * @param rs
     * @param columnName
     * @return
     * @throws SQLException
     * @see org.apache.ibatis.type.BaseTypeHandler#getNullableResult(java.sql.ResultSet, java.lang.String)
     */
    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException
    {
        String jsonStr = rs.getString(columnName);
        return !ObjectUtils.isEmpty(jsonStr) ? JSON.parseObject(jsonStr) : null;
    }
    
    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * 
     * @author sinhy
     * @since 2021-10-31 10:45
     * @param rs
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see org.apache.ibatis.type.BaseTypeHandler#getNullableResult(java.sql.ResultSet, int)
     */
    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException
    {
        String jsonStr = rs.getString(columnIndex);
        return !ObjectUtils.isEmpty(jsonStr) ? JSON.parseObject(jsonStr) : null;
    }
    
    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * 
     * @author sinhy
     * @since 2021-10-31 10:45
     * @param cs
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see org.apache.ibatis.type.BaseTypeHandler#getNullableResult(java.sql.CallableStatement, int)
     */
    @Override
    public Map<String, Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException
    {
        String jsonStr = cs.getString(columnIndex);
        return !ObjectUtils.isEmpty(jsonStr) ? JSON.parseObject(jsonStr) : null;
    }
    
}
