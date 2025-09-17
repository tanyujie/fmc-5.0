/**
 * ConferenceExtInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class ConferenceExtInfo  implements java.io.Serializable {
    private String fieldName;

    private String fieldValue;

    private String filedValue;

    public ConferenceExtInfo() {
    }

    public ConferenceExtInfo(
           String fieldName,
           String fieldValue,
           String filedValue) {
           this.fieldName = fieldName;
           this.fieldValue = fieldValue;
           this.filedValue = filedValue;
    }


    /**
     * Gets the fieldName value for this ConferenceExtInfo.
     * 
     * @return fieldName
     */
    public String getFieldName() {
        return fieldName;
    }


    /**
     * Sets the fieldName value for this ConferenceExtInfo.
     * 
     * @param fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }


    /**
     * Gets the fieldValue value for this ConferenceExtInfo.
     * 
     * @return fieldValue
     */
    public String getFieldValue() {
        return fieldValue;
    }


    /**
     * Sets the fieldValue value for this ConferenceExtInfo.
     * 
     * @param fieldValue
     */
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }


    /**
     * Gets the filedValue value for this ConferenceExtInfo.
     * 
     * @return filedValue
     */
    public String getFiledValue() {
        return filedValue;
    }


    /**
     * Sets the filedValue value for this ConferenceExtInfo.
     * 
     * @param filedValue
     */
    public void setFiledValue(String filedValue) {
        this.filedValue = filedValue;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ConferenceExtInfo)) return false;
        ConferenceExtInfo other = (ConferenceExtInfo) obj;
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
            ((this.fieldValue==null && other.getFieldValue()==null) || 
             (this.fieldValue!=null &&
              this.fieldValue.equals(other.getFieldValue()))) &&
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
        if (getFieldValue() != null) {
            _hashCode += getFieldValue().hashCode();
        }
        if (getFiledValue() != null) {
            _hashCode += getFiledValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConferenceExtInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceExtInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fieldName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fieldName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fieldValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fieldValue"));
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
