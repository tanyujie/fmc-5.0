/**
 * SendParticipantDualVideoRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class SendParticipantDualVideoRequest  extends com.zte.m900.request.Request  implements java.io.Serializable {
    private String terminalIdentifier;

    private int value;

    public SendParticipantDualVideoRequest() {
    }

    public SendParticipantDualVideoRequest(
           String conferenceIdentifier,
           String terminalIdentifier,
           int value) {
        super(
            conferenceIdentifier);
        this.terminalIdentifier = terminalIdentifier;
        this.value = value;
    }


    /**
     * Gets the terminalIdentifier value for this SendParticipantDualVideoRequest.
     * 
     * @return terminalIdentifier
     */
    public String getTerminalIdentifier() {
        return terminalIdentifier;
    }


    /**
     * Sets the terminalIdentifier value for this SendParticipantDualVideoRequest.
     * 
     * @param terminalIdentifier
     */
    public void setTerminalIdentifier(String terminalIdentifier) {
        this.terminalIdentifier = terminalIdentifier;
    }


    /**
     * Gets the value value for this SendParticipantDualVideoRequest.
     * 
     * @return value
     */
    public int getValue() {
        return value;
    }


    /**
     * Sets the value value for this SendParticipantDualVideoRequest.
     * 
     * @param value
     */
    public void setValue(int value) {
        this.value = value;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SendParticipantDualVideoRequest)) return false;
        SendParticipantDualVideoRequest other = (SendParticipantDualVideoRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.terminalIdentifier==null && other.getTerminalIdentifier()==null) || 
             (this.terminalIdentifier!=null &&
              this.terminalIdentifier.equals(other.getTerminalIdentifier()))) &&
            this.value == other.getValue();
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
        if (getTerminalIdentifier() != null) {
            _hashCode += getTerminalIdentifier().hashCode();
        }
        _hashCode += getValue();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SendParticipantDualVideoRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "SendParticipantDualVideoRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("", "value"));
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
