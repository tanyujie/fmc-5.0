/**
 * DataCapability.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class DataCapability  implements java.io.Serializable {
    private String encodeType;

    private int rate;

    public DataCapability() {
    }

    public DataCapability(
           String encodeType,
           int rate) {
           this.encodeType = encodeType;
           this.rate = rate;
    }


    /**
     * Gets the encodeType value for this DataCapability.
     * 
     * @return encodeType
     */
    public String getEncodeType() {
        return encodeType;
    }


    /**
     * Sets the encodeType value for this DataCapability.
     * 
     * @param encodeType
     */
    public void setEncodeType(String encodeType) {
        this.encodeType = encodeType;
    }


    /**
     * Gets the rate value for this DataCapability.
     * 
     * @return rate
     */
    public int getRate() {
        return rate;
    }


    /**
     * Sets the rate value for this DataCapability.
     * 
     * @param rate
     */
    public void setRate(int rate) {
        this.rate = rate;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof DataCapability)) return false;
        DataCapability other = (DataCapability) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.encodeType==null && other.getEncodeType()==null) || 
             (this.encodeType!=null &&
              this.encodeType.equals(other.getEncodeType()))) &&
            this.rate == other.getRate();
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
        if (getEncodeType() != null) {
            _hashCode += getEncodeType().hashCode();
        }
        _hashCode += getRate();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DataCapability.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "DataCapability"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("encodeType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "encodeType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rate"));
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
