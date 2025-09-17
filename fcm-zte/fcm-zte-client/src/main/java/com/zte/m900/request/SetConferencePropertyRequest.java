/**
 * SetConferencePropertyRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class SetConferencePropertyRequest  extends com.zte.m900.request.Request  implements java.io.Serializable {
    private int autoRecall;

    public SetConferencePropertyRequest() {
    }

    public SetConferencePropertyRequest(
           String conferenceIdentifier,
           int autoRecall) {
        super(
            conferenceIdentifier);
        this.autoRecall = autoRecall;
    }


    /**
     * Gets the autoRecall value for this SetConferencePropertyRequest.
     * 
     * @return autoRecall
     */
    public int getAutoRecall() {
        return autoRecall;
    }


    /**
     * Sets the autoRecall value for this SetConferencePropertyRequest.
     * 
     * @param autoRecall
     */
    public void setAutoRecall(int autoRecall) {
        this.autoRecall = autoRecall;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SetConferencePropertyRequest)) return false;
        SetConferencePropertyRequest other = (SetConferencePropertyRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.autoRecall == other.getAutoRecall();
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
        _hashCode += getAutoRecall();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SetConferencePropertyRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "SetConferencePropertyRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("autoRecall");
        elemField.setXmlName(new javax.xml.namespace.QName("", "autoRecall"));
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
