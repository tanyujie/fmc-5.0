/**
 * McuConfCounts.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class McuConfCounts  implements java.io.Serializable {
    private int failedCount;

    private String mcuName;

    private String mcuNo;

    private int succeedCount;

    public McuConfCounts() {
    }

    public McuConfCounts(
           int failedCount,
           String mcuName,
           String mcuNo,
           int succeedCount) {
           this.failedCount = failedCount;
           this.mcuName = mcuName;
           this.mcuNo = mcuNo;
           this.succeedCount = succeedCount;
    }


    /**
     * Gets the failedCount value for this McuConfCounts.
     * 
     * @return failedCount
     */
    public int getFailedCount() {
        return failedCount;
    }


    /**
     * Sets the failedCount value for this McuConfCounts.
     * 
     * @param failedCount
     */
    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }


    /**
     * Gets the mcuName value for this McuConfCounts.
     * 
     * @return mcuName
     */
    public String getMcuName() {
        return mcuName;
    }


    /**
     * Sets the mcuName value for this McuConfCounts.
     * 
     * @param mcuName
     */
    public void setMcuName(String mcuName) {
        this.mcuName = mcuName;
    }


    /**
     * Gets the mcuNo value for this McuConfCounts.
     * 
     * @return mcuNo
     */
    public String getMcuNo() {
        return mcuNo;
    }


    /**
     * Sets the mcuNo value for this McuConfCounts.
     * 
     * @param mcuNo
     */
    public void setMcuNo(String mcuNo) {
        this.mcuNo = mcuNo;
    }


    /**
     * Gets the succeedCount value for this McuConfCounts.
     * 
     * @return succeedCount
     */
    public int getSucceedCount() {
        return succeedCount;
    }


    /**
     * Sets the succeedCount value for this McuConfCounts.
     * 
     * @param succeedCount
     */
    public void setSucceedCount(int succeedCount) {
        this.succeedCount = succeedCount;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof McuConfCounts)) return false;
        McuConfCounts other = (McuConfCounts) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.failedCount == other.getFailedCount() &&
            ((this.mcuName==null && other.getMcuName()==null) || 
             (this.mcuName!=null &&
              this.mcuName.equals(other.getMcuName()))) &&
            ((this.mcuNo==null && other.getMcuNo()==null) || 
             (this.mcuNo!=null &&
              this.mcuNo.equals(other.getMcuNo()))) &&
            this.succeedCount == other.getSucceedCount();
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
        _hashCode += getFailedCount();
        if (getMcuName() != null) {
            _hashCode += getMcuName().hashCode();
        }
        if (getMcuNo() != null) {
            _hashCode += getMcuNo().hashCode();
        }
        _hashCode += getSucceedCount();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(McuConfCounts.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "McuConfCounts"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("failedCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "failedCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mcuName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mcuName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mcuNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mcuNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("succeedCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "succeedCount"));
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
