/**
 * JoinConferenceRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class JoinConferenceRequest  extends com.zte.m900.request.CreateConferenceRequest  implements java.io.Serializable {
    private com.zte.m900.bean.Participant participant;

    public JoinConferenceRequest() {
    }

    public JoinConferenceRequest(
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
           com.zte.m900.bean.Participant participant) {
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
        this.participant = participant;
    }


    /**
     * Gets the participant value for this JoinConferenceRequest.
     * 
     * @return participant
     */
    public com.zte.m900.bean.Participant getParticipant() {
        return participant;
    }


    /**
     * Sets the participant value for this JoinConferenceRequest.
     * 
     * @param participant
     */
    public void setParticipant(com.zte.m900.bean.Participant participant) {
        this.participant = participant;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof JoinConferenceRequest)) return false;
        JoinConferenceRequest other = (JoinConferenceRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
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
        int _hashCode = super.hashCode();
        if (getParticipant() != null) {
            _hashCode += getParticipant().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(JoinConferenceRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "JoinConferenceRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
