/**
 * MonitorParticipantRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class MonitorParticipantRequest  extends com.zte.m900.request.Request  implements java.io.Serializable {
    private int cmdType;

    private int encodeFormat;

    private int mediaType;

    private int rate;

    private String receiveIpAddress;

    private int receiveIpPort;

    private String terminalIdentifier;

    private String uniqueCode;

    private int videoFormat;

    private int videoFrame;

    public MonitorParticipantRequest() {
    }

    public MonitorParticipantRequest(
           String conferenceIdentifier,
           int cmdType,
           int encodeFormat,
           int mediaType,
           int rate,
           String receiveIpAddress,
           int receiveIpPort,
           String terminalIdentifier,
           String uniqueCode,
           int videoFormat,
           int videoFrame) {
        super(
            conferenceIdentifier);
        this.cmdType = cmdType;
        this.encodeFormat = encodeFormat;
        this.mediaType = mediaType;
        this.rate = rate;
        this.receiveIpAddress = receiveIpAddress;
        this.receiveIpPort = receiveIpPort;
        this.terminalIdentifier = terminalIdentifier;
        this.uniqueCode = uniqueCode;
        this.videoFormat = videoFormat;
        this.videoFrame = videoFrame;
    }


    /**
     * Gets the cmdType value for this MonitorParticipantRequest.
     * 
     * @return cmdType
     */
    public int getCmdType() {
        return cmdType;
    }


    /**
     * Sets the cmdType value for this MonitorParticipantRequest.
     * 
     * @param cmdType
     */
    public void setCmdType(int cmdType) {
        this.cmdType = cmdType;
    }


    /**
     * Gets the encodeFormat value for this MonitorParticipantRequest.
     * 
     * @return encodeFormat
     */
    public int getEncodeFormat() {
        return encodeFormat;
    }


    /**
     * Sets the encodeFormat value for this MonitorParticipantRequest.
     * 
     * @param encodeFormat
     */
    public void setEncodeFormat(int encodeFormat) {
        this.encodeFormat = encodeFormat;
    }


    /**
     * Gets the mediaType value for this MonitorParticipantRequest.
     * 
     * @return mediaType
     */
    public int getMediaType() {
        return mediaType;
    }


    /**
     * Sets the mediaType value for this MonitorParticipantRequest.
     * 
     * @param mediaType
     */
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }


    /**
     * Gets the rate value for this MonitorParticipantRequest.
     * 
     * @return rate
     */
    public int getRate() {
        return rate;
    }


    /**
     * Sets the rate value for this MonitorParticipantRequest.
     * 
     * @param rate
     */
    public void setRate(int rate) {
        this.rate = rate;
    }


    /**
     * Gets the receiveIpAddress value for this MonitorParticipantRequest.
     * 
     * @return receiveIpAddress
     */
    public String getReceiveIpAddress() {
        return receiveIpAddress;
    }


    /**
     * Sets the receiveIpAddress value for this MonitorParticipantRequest.
     * 
     * @param receiveIpAddress
     */
    public void setReceiveIpAddress(String receiveIpAddress) {
        this.receiveIpAddress = receiveIpAddress;
    }


    /**
     * Gets the receiveIpPort value for this MonitorParticipantRequest.
     * 
     * @return receiveIpPort
     */
    public int getReceiveIpPort() {
        return receiveIpPort;
    }


    /**
     * Sets the receiveIpPort value for this MonitorParticipantRequest.
     * 
     * @param receiveIpPort
     */
    public void setReceiveIpPort(int receiveIpPort) {
        this.receiveIpPort = receiveIpPort;
    }


    /**
     * Gets the terminalIdentifier value for this MonitorParticipantRequest.
     * 
     * @return terminalIdentifier
     */
    public String getTerminalIdentifier() {
        return terminalIdentifier;
    }


    /**
     * Sets the terminalIdentifier value for this MonitorParticipantRequest.
     * 
     * @param terminalIdentifier
     */
    public void setTerminalIdentifier(String terminalIdentifier) {
        this.terminalIdentifier = terminalIdentifier;
    }


    /**
     * Gets the uniqueCode value for this MonitorParticipantRequest.
     * 
     * @return uniqueCode
     */
    public String getUniqueCode() {
        return uniqueCode;
    }


    /**
     * Sets the uniqueCode value for this MonitorParticipantRequest.
     * 
     * @param uniqueCode
     */
    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }


    /**
     * Gets the videoFormat value for this MonitorParticipantRequest.
     * 
     * @return videoFormat
     */
    public int getVideoFormat() {
        return videoFormat;
    }


    /**
     * Sets the videoFormat value for this MonitorParticipantRequest.
     * 
     * @param videoFormat
     */
    public void setVideoFormat(int videoFormat) {
        this.videoFormat = videoFormat;
    }


    /**
     * Gets the videoFrame value for this MonitorParticipantRequest.
     * 
     * @return videoFrame
     */
    public int getVideoFrame() {
        return videoFrame;
    }


    /**
     * Sets the videoFrame value for this MonitorParticipantRequest.
     * 
     * @param videoFrame
     */
    public void setVideoFrame(int videoFrame) {
        this.videoFrame = videoFrame;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof MonitorParticipantRequest)) return false;
        MonitorParticipantRequest other = (MonitorParticipantRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.cmdType == other.getCmdType() &&
            this.encodeFormat == other.getEncodeFormat() &&
            this.mediaType == other.getMediaType() &&
            this.rate == other.getRate() &&
            ((this.receiveIpAddress==null && other.getReceiveIpAddress()==null) || 
             (this.receiveIpAddress!=null &&
              this.receiveIpAddress.equals(other.getReceiveIpAddress()))) &&
            this.receiveIpPort == other.getReceiveIpPort() &&
            ((this.terminalIdentifier==null && other.getTerminalIdentifier()==null) || 
             (this.terminalIdentifier!=null &&
              this.terminalIdentifier.equals(other.getTerminalIdentifier()))) &&
            ((this.uniqueCode==null && other.getUniqueCode()==null) || 
             (this.uniqueCode!=null &&
              this.uniqueCode.equals(other.getUniqueCode()))) &&
            this.videoFormat == other.getVideoFormat() &&
            this.videoFrame == other.getVideoFrame();
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
        _hashCode += getCmdType();
        _hashCode += getEncodeFormat();
        _hashCode += getMediaType();
        _hashCode += getRate();
        if (getReceiveIpAddress() != null) {
            _hashCode += getReceiveIpAddress().hashCode();
        }
        _hashCode += getReceiveIpPort();
        if (getTerminalIdentifier() != null) {
            _hashCode += getTerminalIdentifier().hashCode();
        }
        if (getUniqueCode() != null) {
            _hashCode += getUniqueCode().hashCode();
        }
        _hashCode += getVideoFormat();
        _hashCode += getVideoFrame();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MonitorParticipantRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "MonitorParticipantRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cmdType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cmdType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("encodeFormat");
        elemField.setXmlName(new javax.xml.namespace.QName("", "encodeFormat"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mediaType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mediaType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receiveIpAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "receiveIpAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receiveIpPort");
        elemField.setXmlName(new javax.xml.namespace.QName("", "receiveIpPort"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("uniqueCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "uniqueCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("videoFormat");
        elemField.setXmlName(new javax.xml.namespace.QName("", "videoFormat"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("videoFrame");
        elemField.setXmlName(new javax.xml.namespace.QName("", "videoFrame"));
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
