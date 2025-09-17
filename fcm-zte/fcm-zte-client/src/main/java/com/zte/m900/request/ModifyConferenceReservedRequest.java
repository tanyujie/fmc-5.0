/**
 * ModifyConferenceReservedRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class ModifyConferenceReservedRequest  extends com.zte.m900.request.ConferenceRequest  implements java.io.Serializable {
    private String conferenceIdentifier;

    public ModifyConferenceReservedRequest() {
    }

    public ModifyConferenceReservedRequest(
           int UPConf,
           String account,
           int confCascadeMode,
           String conferenceName,
           String conferenceNumber,
           String conferencePassword,
           String conferenceTemplet,
           String defaultBroadcastTerminalNumber,
           String defaultChairmanTerminalNumber,
           int duration,
           String dynamicRes,
           int enableAutoVoiceRecord,
           int enableMcuBanner,
           int enableMcuTitle,
           int enableVoiceRecord,
           int inviteWithSDP,
           int maxParticipants,
           String multiPicControl,
           int multiViewNumber,
           com.zte.m900.bean.Participant[] participants,
           com.zte.m900.bean.RecordParam record,
           com.zte.m900.bean.MailInfo sendMail,
           java.util.Calendar startTime,
           String subject,
           String conferenceIdentifier) {
        super(
            UPConf,
            account,
            confCascadeMode,
            conferenceName,
            conferenceNumber,
            conferencePassword,
            conferenceTemplet,
            defaultBroadcastTerminalNumber,
            defaultChairmanTerminalNumber,
            duration,
            dynamicRes,
            enableAutoVoiceRecord,
            enableMcuBanner,
            enableMcuTitle,
            enableVoiceRecord,
            inviteWithSDP,
            maxParticipants,
            multiPicControl,
            multiViewNumber,
            participants,
            record,
            sendMail,
            startTime,
            subject);
        this.conferenceIdentifier = conferenceIdentifier;
    }


    /**
     * Gets the conferenceIdentifier value for this ModifyConferenceReservedRequest.
     * 
     * @return conferenceIdentifier
     */
    public String getConferenceIdentifier() {
        return conferenceIdentifier;
    }


    /**
     * Sets the conferenceIdentifier value for this ModifyConferenceReservedRequest.
     * 
     * @param conferenceIdentifier
     */
    public void setConferenceIdentifier(String conferenceIdentifier) {
        this.conferenceIdentifier = conferenceIdentifier;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ModifyConferenceReservedRequest)) return false;
        ModifyConferenceReservedRequest other = (ModifyConferenceReservedRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.conferenceIdentifier==null && other.getConferenceIdentifier()==null) || 
             (this.conferenceIdentifier!=null &&
              this.conferenceIdentifier.equals(other.getConferenceIdentifier())));
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
        if (getConferenceIdentifier() != null) {
            _hashCode += getConferenceIdentifier().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ModifyConferenceReservedRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "ModifyConferenceReservedRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceIdentifier"));
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
