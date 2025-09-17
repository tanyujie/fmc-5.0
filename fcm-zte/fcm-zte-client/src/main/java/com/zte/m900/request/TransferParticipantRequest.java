/**
 * TransferParticipantRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class TransferParticipantRequest  extends com.zte.m900.request.Request  implements java.io.Serializable {
    private String newConferenceIdentifier;

    private String terminalIdentifier;

    public TransferParticipantRequest() {
    }

    public TransferParticipantRequest(
           String conferenceIdentifier,
           String newConferenceIdentifier,
           String terminalIdentifier) {
        super(
            conferenceIdentifier);
        this.newConferenceIdentifier = newConferenceIdentifier;
        this.terminalIdentifier = terminalIdentifier;
    }


    /**
     * Gets the newConferenceIdentifier value for this TransferParticipantRequest.
     * 
     * @return newConferenceIdentifier
     */
    public String getNewConferenceIdentifier() {
        return newConferenceIdentifier;
    }


    /**
     * Sets the newConferenceIdentifier value for this TransferParticipantRequest.
     * 
     * @param newConferenceIdentifier
     */
    public void setNewConferenceIdentifier(String newConferenceIdentifier) {
        this.newConferenceIdentifier = newConferenceIdentifier;
    }


    /**
     * Gets the terminalIdentifier value for this TransferParticipantRequest.
     * 
     * @return terminalIdentifier
     */
    public String getTerminalIdentifier() {
        return terminalIdentifier;
    }


    /**
     * Sets the terminalIdentifier value for this TransferParticipantRequest.
     * 
     * @param terminalIdentifier
     */
    public void setTerminalIdentifier(String terminalIdentifier) {
        this.terminalIdentifier = terminalIdentifier;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof TransferParticipantRequest)) return false;
        TransferParticipantRequest other = (TransferParticipantRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.newConferenceIdentifier==null && other.getNewConferenceIdentifier()==null) || 
             (this.newConferenceIdentifier!=null &&
              this.newConferenceIdentifier.equals(other.getNewConferenceIdentifier()))) &&
            ((this.terminalIdentifier==null && other.getTerminalIdentifier()==null) || 
             (this.terminalIdentifier!=null &&
              this.terminalIdentifier.equals(other.getTerminalIdentifier())));
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
        if (getNewConferenceIdentifier() != null) {
            _hashCode += getNewConferenceIdentifier().hashCode();
        }
        if (getTerminalIdentifier() != null) {
            _hashCode += getTerminalIdentifier().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TransferParticipantRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "TransferParticipantRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newConferenceIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "newConferenceIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalIdentifier"));
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
