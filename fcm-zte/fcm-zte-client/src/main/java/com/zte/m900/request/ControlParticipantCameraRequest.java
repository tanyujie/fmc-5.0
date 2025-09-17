/**
 * ControlParticipantCameraRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class ControlParticipantCameraRequest  extends com.zte.m900.request.TerCtrlRequest  implements java.io.Serializable {
    private int controlType;

    private int param1;

    private int param2;

    public ControlParticipantCameraRequest() {
    }

    public ControlParticipantCameraRequest(
           String conferenceIdentifier,
           String terminalIdentifier,
           int controlType,
           int param1,
           int param2) {
        super(
            conferenceIdentifier,
            terminalIdentifier);
        this.controlType = controlType;
        this.param1 = param1;
        this.param2 = param2;
    }


    /**
     * Gets the controlType value for this ControlParticipantCameraRequest.
     * 
     * @return controlType
     */
    public int getControlType() {
        return controlType;
    }


    /**
     * Sets the controlType value for this ControlParticipantCameraRequest.
     * 
     * @param controlType
     */
    public void setControlType(int controlType) {
        this.controlType = controlType;
    }


    /**
     * Gets the param1 value for this ControlParticipantCameraRequest.
     * 
     * @return param1
     */
    public int getParam1() {
        return param1;
    }


    /**
     * Sets the param1 value for this ControlParticipantCameraRequest.
     * 
     * @param param1
     */
    public void setParam1(int param1) {
        this.param1 = param1;
    }


    /**
     * Gets the param2 value for this ControlParticipantCameraRequest.
     * 
     * @return param2
     */
    public int getParam2() {
        return param2;
    }


    /**
     * Sets the param2 value for this ControlParticipantCameraRequest.
     * 
     * @param param2
     */
    public void setParam2(int param2) {
        this.param2 = param2;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ControlParticipantCameraRequest)) return false;
        ControlParticipantCameraRequest other = (ControlParticipantCameraRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.controlType == other.getControlType() &&
            this.param1 == other.getParam1() &&
            this.param2 == other.getParam2();
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
        _hashCode += getControlType();
        _hashCode += getParam1();
        _hashCode += getParam2();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ControlParticipantCameraRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "ControlParticipantCameraRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("controlType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "controlType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("param1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "param1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("param2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "param2"));
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
