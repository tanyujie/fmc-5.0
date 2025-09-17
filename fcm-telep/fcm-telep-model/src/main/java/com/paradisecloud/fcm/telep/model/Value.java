package com.paradisecloud.fcm.telep.model;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * @author nj
 * @date 2022/10/10 17:50
 */
public class Value {

    private Integer intValue;

    private Long longValue;

    private String stringValue;

    private String dateTimeValue;

    private Boolean BooleanValue;

    private List<DataValue> dataValueList;


    @XmlElement(name = "array")
    public List<DataValue> getDataValueList() {
        return dataValueList;
    }

    public void setDataValueList(List<DataValue> dataValueList) {
        this.dataValueList = dataValueList;
    }

    @XmlElement(name = "boolean")
    public Boolean getBooleanValue() {
        return BooleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        BooleanValue = booleanValue;
    }

    @XmlElement(name = "int")
    public Integer getIntValue() {
        return intValue;
    }

    @XmlElement(name = "long")
    public Long getLongValue() {
        return longValue;
    }

    @XmlElement(name = "string")
    public String getStringValue() {
        return stringValue;
    }

    @XmlElement(name = "dateTime.iso8601")
    public String getDateTimeValue() {
        return dateTimeValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public void setDateTimeValue(String dateTimeValue) {
        this.dateTimeValue = dateTimeValue;
    }
}
