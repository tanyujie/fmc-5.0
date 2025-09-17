/**
 * GetMcuConfCountsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetMcuConfCountsResponse  implements java.io.Serializable {
    private com.zte.m900.bean.McuConfCounts[] mcuConfCountList;

    private int mcuCount;

    private String result;

    private int totalFailedCount;

    private int totalSucceedCount;

    public GetMcuConfCountsResponse() {
    }

    public GetMcuConfCountsResponse(
           com.zte.m900.bean.McuConfCounts[] mcuConfCountList,
           int mcuCount,
           String result,
           int totalFailedCount,
           int totalSucceedCount) {
           this.mcuConfCountList = mcuConfCountList;
           this.mcuCount = mcuCount;
           this.result = result;
           this.totalFailedCount = totalFailedCount;
           this.totalSucceedCount = totalSucceedCount;
    }


    /**
     * Gets the mcuConfCountList value for this GetMcuConfCountsResponse.
     * 
     * @return mcuConfCountList
     */
    public com.zte.m900.bean.McuConfCounts[] getMcuConfCountList() {
        return mcuConfCountList;
    }


    /**
     * Sets the mcuConfCountList value for this GetMcuConfCountsResponse.
     * 
     * @param mcuConfCountList
     */
    public void setMcuConfCountList(com.zte.m900.bean.McuConfCounts[] mcuConfCountList) {
        this.mcuConfCountList = mcuConfCountList;
    }


    /**
     * Gets the mcuCount value for this GetMcuConfCountsResponse.
     * 
     * @return mcuCount
     */
    public int getMcuCount() {
        return mcuCount;
    }


    /**
     * Sets the mcuCount value for this GetMcuConfCountsResponse.
     * 
     * @param mcuCount
     */
    public void setMcuCount(int mcuCount) {
        this.mcuCount = mcuCount;
    }


    /**
     * Gets the result value for this GetMcuConfCountsResponse.
     * 
     * @return result
     */
    public String getResult() {
        return result;
    }


    /**
     * Sets the result value for this GetMcuConfCountsResponse.
     * 
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }


    /**
     * Gets the totalFailedCount value for this GetMcuConfCountsResponse.
     * 
     * @return totalFailedCount
     */
    public int getTotalFailedCount() {
        return totalFailedCount;
    }


    /**
     * Sets the totalFailedCount value for this GetMcuConfCountsResponse.
     * 
     * @param totalFailedCount
     */
    public void setTotalFailedCount(int totalFailedCount) {
        this.totalFailedCount = totalFailedCount;
    }


    /**
     * Gets the totalSucceedCount value for this GetMcuConfCountsResponse.
     * 
     * @return totalSucceedCount
     */
    public int getTotalSucceedCount() {
        return totalSucceedCount;
    }


    /**
     * Sets the totalSucceedCount value for this GetMcuConfCountsResponse.
     * 
     * @param totalSucceedCount
     */
    public void setTotalSucceedCount(int totalSucceedCount) {
        this.totalSucceedCount = totalSucceedCount;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetMcuConfCountsResponse)) return false;
        GetMcuConfCountsResponse other = (GetMcuConfCountsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.mcuConfCountList==null && other.getMcuConfCountList()==null) || 
             (this.mcuConfCountList!=null &&
              java.util.Arrays.equals(this.mcuConfCountList, other.getMcuConfCountList()))) &&
            this.mcuCount == other.getMcuCount() &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
            this.totalFailedCount == other.getTotalFailedCount() &&
            this.totalSucceedCount == other.getTotalSucceedCount();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getMcuConfCountList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMcuConfCountList());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getMcuConfCountList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getMcuCount();
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        _hashCode += getTotalFailedCount();
        _hashCode += getTotalSucceedCount();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetMcuConfCountsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "getMcuConfCountsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mcuConfCountList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mcuConfCountList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "McuConfCounts"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mcuCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mcuCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalFailedCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totalFailedCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalSucceedCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totalSucceedCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
