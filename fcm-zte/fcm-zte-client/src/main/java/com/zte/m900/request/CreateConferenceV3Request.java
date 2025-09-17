/**
 * CreateConferenceV3Request.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class CreateConferenceV3Request  extends com.zte.m900.request.ConferenceRequest  implements java.io.Serializable {
    private String ext;

    public CreateConferenceV3Request() {
    }

    public CreateConferenceV3Request(
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
           String ext) {
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
        this.ext = ext;
    }


    /**
     * Gets the ext value for this CreateConferenceV3Request.
     * 
     * @return ext
     */
    public String getExt() {
        return ext;
    }


    /**
     * Sets the ext value for this CreateConferenceV3Request.
     * 
     * @param ext
     */
    public void setExt(String ext) {
        this.ext = ext;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof CreateConferenceV3Request)) return false;
        CreateConferenceV3Request other = (CreateConferenceV3Request) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.ext==null && other.getExt()==null) || 
             (this.ext!=null &&
              this.ext.equals(other.getExt())));
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
        if (getExt() != null) {
            _hashCode += getExt().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CreateConferenceV3Request.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "CreateConferenceV3Request"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ext");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ext"));
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
