/**
 * DisconnectParticipantRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class DisconnectParticipantRequest  extends com.zte.m900.request.ParticipantRequest  implements java.io.Serializable {
    private String conferenceIdOption;

    private String terminalIdOption;

    public DisconnectParticipantRequest() {
    }

    public DisconnectParticipantRequest(
           String conferenceIdentifier,
           String[] terminalIdentifier,
           String conferenceIdOption,
           String terminalIdOption) {
        super(
            conferenceIdentifier,
            terminalIdentifier);
        this.conferenceIdOption = conferenceIdOption;
        this.terminalIdOption = terminalIdOption;
    }


    /**
     * Gets the conferenceIdOption value for this DisconnectParticipantRequest.
     * 
     * @return conferenceIdOption
     */
    public String getConferenceIdOption() {
        return conferenceIdOption;
    }


    /**
     * Sets the conferenceIdOption value for this DisconnectParticipantRequest.
     * 
     * @param conferenceIdOption
     */
    public void setConferenceIdOption(String conferenceIdOption) {
        this.conferenceIdOption = conferenceIdOption;
    }


    /**
     * Gets the terminalIdOption value for this DisconnectParticipantRequest.
     * 
     * @return terminalIdOption
     */
    public String getTerminalIdOption() {
        return terminalIdOption;
    }


    /**
     * Sets the terminalIdOption value for this DisconnectParticipantRequest.
     * 
     * @param terminalIdOption
     */
    public void setTerminalIdOption(String terminalIdOption) {
        this.terminalIdOption = terminalIdOption;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof DisconnectParticipantRequest)) return false;
        DisconnectParticipantRequest other = (DisconnectParticipantRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.conferenceIdOption==null && other.getConferenceIdOption()==null) || 
             (this.conferenceIdOption!=null &&
              this.conferenceIdOption.equals(other.getConferenceIdOption()))) &&
            ((this.terminalIdOption==null && other.getTerminalIdOption()==null) || 
             (this.terminalIdOption!=null &&
              this.terminalIdOption.equals(other.getTerminalIdOption())));
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
        if (getConferenceIdOption() != null) {
            _hashCode += getConferenceIdOption().hashCode();
        }
        if (getTerminalIdOption() != null) {
            _hashCode += getTerminalIdOption().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DisconnectParticipantRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "DisconnectParticipantRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceIdOption");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceIdOption"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalIdOption");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalIdOption"));
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
