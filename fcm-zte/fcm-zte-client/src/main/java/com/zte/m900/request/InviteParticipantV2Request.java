/**
 * InviteParticipantV2Request.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class InviteParticipantV2Request  implements java.io.Serializable {
    private String conferenceIdOption;

    private String conferenceIdentifier;

    private String conferencePassword;

    private com.zte.m900.bean.ParticipantV2 participantV2;

    public InviteParticipantV2Request() {
    }

    public InviteParticipantV2Request(
           String conferenceIdOption,
           String conferenceIdentifier,
           String conferencePassword,
           com.zte.m900.bean.ParticipantV2 participantV2) {
           this.conferenceIdOption = conferenceIdOption;
           this.conferenceIdentifier = conferenceIdentifier;
           this.conferencePassword = conferencePassword;
           this.participantV2 = participantV2;
    }


    /**
     * Gets the conferenceIdOption value for this InviteParticipantV2Request.
     * 
     * @return conferenceIdOption
     */
    public String getConferenceIdOption() {
        return conferenceIdOption;
    }


    /**
     * Sets the conferenceIdOption value for this InviteParticipantV2Request.
     * 
     * @param conferenceIdOption
     */
    public void setConferenceIdOption(String conferenceIdOption) {
        this.conferenceIdOption = conferenceIdOption;
    }


    /**
     * Gets the conferenceIdentifier value for this InviteParticipantV2Request.
     * 
     * @return conferenceIdentifier
     */
    public String getConferenceIdentifier() {
        return conferenceIdentifier;
    }


    /**
     * Sets the conferenceIdentifier value for this InviteParticipantV2Request.
     * 
     * @param conferenceIdentifier
     */
    public void setConferenceIdentifier(String conferenceIdentifier) {
        this.conferenceIdentifier = conferenceIdentifier;
    }


    /**
     * Gets the conferencePassword value for this InviteParticipantV2Request.
     * 
     * @return conferencePassword
     */
    public String getConferencePassword() {
        return conferencePassword;
    }


    /**
     * Sets the conferencePassword value for this InviteParticipantV2Request.
     * 
     * @param conferencePassword
     */
    public void setConferencePassword(String conferencePassword) {
        this.conferencePassword = conferencePassword;
    }


    /**
     * Gets the participantV2 value for this InviteParticipantV2Request.
     * 
     * @return participantV2
     */
    public com.zte.m900.bean.ParticipantV2 getParticipantV2() {
        return participantV2;
    }


    /**
     * Sets the participantV2 value for this InviteParticipantV2Request.
     * 
     * @param participantV2
     */
    public void setParticipantV2(com.zte.m900.bean.ParticipantV2 participantV2) {
        this.participantV2 = participantV2;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof InviteParticipantV2Request)) return false;
        InviteParticipantV2Request other = (InviteParticipantV2Request) obj;
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
            ((this.participantV2==null && other.getParticipantV2()==null) || 
             (this.participantV2!=null &&
              this.participantV2.equals(other.getParticipantV2())));
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
        if (getParticipantV2() != null) {
            _hashCode += getParticipantV2().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InviteParticipantV2Request.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "InviteParticipantV2Request"));
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
        elemField.setFieldName("participantV2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "participantV2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantV2"));
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
