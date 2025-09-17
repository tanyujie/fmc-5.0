/**
 * InviteParticipantRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class InviteParticipantRequest  implements java.io.Serializable {
    private String conferenceIdOption;

    private String conferenceIdentifier;

    private String conferencePassword;

    private com.zte.m900.bean.Participant participant;

    public InviteParticipantRequest() {
    }

    public InviteParticipantRequest(
           String conferenceIdOption,
           String conferenceIdentifier,
           String conferencePassword,
           com.zte.m900.bean.Participant participant) {
           this.conferenceIdOption = conferenceIdOption;
           this.conferenceIdentifier = conferenceIdentifier;
           this.conferencePassword = conferencePassword;
           this.participant = participant;
    }


    /**
     * Gets the conferenceIdOption value for this InviteParticipantRequest.
     * 
     * @return conferenceIdOption
     */
    public String getConferenceIdOption() {
        return conferenceIdOption;
    }


    /**
     * Sets the conferenceIdOption value for this InviteParticipantRequest.
     * 
     * @param conferenceIdOption
     */
    public void setConferenceIdOption(String conferenceIdOption) {
        this.conferenceIdOption = conferenceIdOption;
    }


    /**
     * Gets the conferenceIdentifier value for this InviteParticipantRequest.
     * 
     * @return conferenceIdentifier
     */
    public String getConferenceIdentifier() {
        return conferenceIdentifier;
    }


    /**
     * Sets the conferenceIdentifier value for this InviteParticipantRequest.
     * 
     * @param conferenceIdentifier
     */
    public void setConferenceIdentifier(String conferenceIdentifier) {
        this.conferenceIdentifier = conferenceIdentifier;
    }


    /**
     * Gets the conferencePassword value for this InviteParticipantRequest.
     * 
     * @return conferencePassword
     */
    public String getConferencePassword() {
        return conferencePassword;
    }


    /**
     * Sets the conferencePassword value for this InviteParticipantRequest.
     * 
     * @param conferencePassword
     */
    public void setConferencePassword(String conferencePassword) {
        this.conferencePassword = conferencePassword;
    }


    /**
     * Gets the participant value for this InviteParticipantRequest.
     * 
     * @return participant
     */
    public com.zte.m900.bean.Participant getParticipant() {
        return participant;
    }


    /**
     * Sets the participant value for this InviteParticipantRequest.
     * 
     * @param participant
     */
    public void setParticipant(com.zte.m900.bean.Participant participant) {
        this.participant = participant;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof InviteParticipantRequest)) return false;
        InviteParticipantRequest other = (InviteParticipantRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.conferenceIdOption==null && other.getConferenceIdOption()==null) || 
             (this.conferenceIdOption!=null &&
              this.conferenceIdOption.equals(other.getConferenceIdOption()))) &&
            ((this.conferenceIdentifier==null && other.getConferenceIdentifier()==null) || 
             (this.conferenceIdentifier!=null &&
              this.conferenceIdentifier.equals(other.getConferenceIdentifier()))) &&
            ((this.conferencePassword==null && other.getConferencePassword()==null) || 
             (this.conferencePassword!=null &&
              this.conferencePassword.equals(other.getConferencePassword()))) &&
            ((this.participant==null && other.getParticipant()==null) || 
             (this.participant!=null &&
              this.participant.equals(other.getParticipant())));
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
        if (getConferenceIdOption() != null) {
            _hashCode += getConferenceIdOption().hashCode();
        }
        if (getConferenceIdentifier() != null) {
            _hashCode += getConferenceIdentifier().hashCode();
        }
        if (getConferencePassword() != null) {
            _hashCode += getConferencePassword().hashCode();
        }
        if (getParticipant() != null) {
            _hashCode += getParticipant().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InviteParticipantRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "InviteParticipantRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceIdOption");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceIdOption"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferencePassword");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferencePassword"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("participant");
        elemField.setXmlName(new javax.xml.namespace.QName("", "participant"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "Participant"));
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
