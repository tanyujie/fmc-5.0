/**
 * ParticipantExtInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class ParticipantExtInfo  implements java.io.Serializable {
    private String fieldName;

    private String filedValue;

    public ParticipantExtInfo() {
    }

    public ParticipantExtInfo(
           String fieldName,
           String filedValue) {
           this.fieldName = fieldName;
           this.filedValue = filedValue;
    }


    /**
     * Gets the fieldName value for this ParticipantExtInfo.
     * 
     * @return fieldName
     */
    public String getFieldName() {
        return fieldName;
    }


    /**
     * Sets the fieldName value for this ParticipantExtInfo.
     * 
     * @param fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }


    /**
     * Gets the filedValue value for this ParticipantExtInfo.
     * 
     * @return filedValue
     */
    public String getFiledValue() {
        return filedValue;
    }


    /**
     * Sets the filedValue value for this ParticipantExtInfo.
     * 
     * @param filedValue
     */
    public void setFiledValue(String filedValue) {
        this.filedValue = filedValue;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ParticipantExtInfo)) return false;
        ParticipantExtInfo other = (ParticipantExtInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.fieldName==null && other.getFieldName()==null) || 
             (this.fieldName!=null &&
              this.fieldName.equals(other.getFieldName()))) &&
            ((this.filedValue==null && other.getFiledValue()==null) || 
             (this.filedValue!=null &&
              this.filedValue.equals(other.getFiledValue())));
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
        if (getFieldName() != null) {
            _hashCode += getFieldName().hashCode();
        }
        if (getFiledValue() != null) {
            _hashCode += getFiledValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ParticipantExtInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantExtInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fieldName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fieldName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("filedValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "filedValue"));
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
