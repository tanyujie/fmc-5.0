/**
 * RecordParam.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class RecordParam  implements java.io.Serializable {
    private boolean ifAutoRecord;

    private boolean ifLive;

    private boolean ifRecord;

    private String password;

    public RecordParam() {
    }

    public RecordParam(
           boolean ifAutoRecord,
           boolean ifLive,
           boolean ifRecord,
           String password) {
           this.ifAutoRecord = ifAutoRecord;
           this.ifLive = ifLive;
           this.ifRecord = ifRecord;
           this.password = password;
    }


    /**
     * Gets the ifAutoRecord value for this RecordParam.
     * 
     * @return ifAutoRecord
     */
    public boolean isIfAutoRecord() {
        return ifAutoRecord;
    }


    /**
     * Sets the ifAutoRecord value for this RecordParam.
     * 
     * @param ifAutoRecord
     */
    public void setIfAutoRecord(boolean ifAutoRecord) {
        this.ifAutoRecord = ifAutoRecord;
    }


    /**
     * Gets the ifLive value for this RecordParam.
     * 
     * @return ifLive
     */
    public boolean isIfLive() {
        return ifLive;
    }


    /**
     * Sets the ifLive value for this RecordParam.
     * 
     * @param ifLive
     */
    public void setIfLive(boolean ifLive) {
        this.ifLive = ifLive;
    }


    /**
     * Gets the ifRecord value for this RecordParam.
     * 
     * @return ifRecord
     */
    public boolean isIfRecord() {
        return ifRecord;
    }


    /**
     * Sets the ifRecord value for this RecordParam.
     * 
     * @param ifRecord
     */
    public void setIfRecord(boolean ifRecord) {
        this.ifRecord = ifRecord;
    }


    /**
     * Gets the password value for this RecordParam.
     * 
     * @return password
     */
    public String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this RecordParam.
     * 
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof RecordParam)) return false;
        RecordParam other = (RecordParam) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.ifAutoRecord == other.isIfAutoRecord() &&
            this.ifLive == other.isIfLive() &&
            this.ifRecord == other.isIfRecord() &&
            ((this.password==null && other.getPassword()==null) || 
             (this.password!=null &&
              this.password.equals(other.getPassword())));
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
        _hashCode += (isIfAutoRecord() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isIfLive() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isIfRecord() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RecordParam.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "RecordParam"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ifAutoRecord");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ifAutoRecord"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ifLive");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ifLive"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ifRecord");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ifRecord"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
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
