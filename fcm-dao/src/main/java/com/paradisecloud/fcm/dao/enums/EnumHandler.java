package com.paradisecloud.fcm.dao.enums;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description 定义枚举处理器
 * @Author johnson liu
 * @Date 2021/6/6 14:20
 **/
@MappedTypes(BaseEnum.class)
public final class EnumHandler<E extends BaseEnum> extends BaseTypeHandler<E> {

    private Class<E> type;
    private E[] enums;

    public EnumHandler(Class<E> type) {
        if (type == null)
            throw new IllegalArgumentException("type参数不能为空");
        this.type = type;
        this.enums = type.getEnumConstants();
        if (this.enums == null)
            throw new IllegalArgumentException(type.getSimpleName() + "不是枚举类型.");
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, E e, JdbcType jdbcType) throws SQLException {
        if(jdbcType==null){
            preparedStatement.setObject(i, e.getEnumCode());
        }else {
            preparedStatement.setObject(i, e.getEnumCode(), jdbcType.TYPE_CODE);
        }
    }

    @Override
    public E getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String code = resultSet.getString(columnName);
        if (resultSet.wasNull()) {
            return null;
        } else {
            return convert(code);
        }
    }

    @Override
    public E getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String code = resultSet.getString(columnIndex);
        if (resultSet.wasNull()) {
            return null;
        } else {
            // 根据数据库中的value值，定位BaseEnum子类
            return convert(code);
        }
    }

    @Override
    public E getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        String code = callableStatement.getString(columnIndex);
        if (callableStatement.wasNull()) {
            return null;
        } else {
            // 根据数据库中的value值，定位BaseEnum子类
            return convert(code);
        }
    }

    /**
     * 通过code转为枚举对象
     *
     * @param code
     * @return
     */
    private E convert(String code) {
        //final E[] enums = type.getEnumConstants();
        for (E e : enums) {
            if (e.getEnumCode().toString().equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("未知的枚举类型：" + code + ",请核对" + type.getSimpleName());
    }
}
