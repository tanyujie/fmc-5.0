/**
 * SelectMultiOrSingleViewRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class SelectMultiOrSingleViewRequest  extends com.zte.m900.request.Request  implements java.io.Serializable {
    private String cmdType;

    public SelectMultiOrSingleViewRequest() {
    }

    public SelectMultiOrSingleViewRequest(
           String conferenceIdentifier,
           String cmdType) {
        super(
            conferenceIdentifier);
        this.cmdType = cmdType;
    }


    /**
     * Gets the cmdType value for this SelectMultiOrSingleViewRequest.
     * 
     * @return cmdType
     */
    public String getCmdType() {
        return cmdType;
    }


    /**
     * Sets the cmdType value for this SelectMultiOrSingleViewRequest.
     * 
     * @param cmdType
     */
    public void setCmdType(String cmdType) {
        this.cmdType = cmdType;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SelectMultiOrSingleViewRequest)) return false;
        SelectMultiOrSingleViewRequest other = (SelectMultiOrSingleViewRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.cmdType==null && other.getCmdType()==null) || 
             (this.cmdType!=null &&
              this.cmdType.equals(other.getCmdType())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getCmdType() != null) {
            _hashCode += getCmdType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SelectMultiOrSingleViewRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "SelectMultiOrSingleViewRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cmdType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cmdType"));
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
