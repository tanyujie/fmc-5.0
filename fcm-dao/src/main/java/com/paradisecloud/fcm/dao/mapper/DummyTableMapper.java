package com.paradisecloud.fcm.dao.mapper;

/**
 * 执行DB升级等sql用
 */
public interface DummyTableMapper {

    int executeSql(String sql) throws Exception;
}
