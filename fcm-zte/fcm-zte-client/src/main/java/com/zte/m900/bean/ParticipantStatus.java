/**
 * ParticipantStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class ParticipantStatus  implements java.io.Serializable {
    private String audioType;

    private String dualVideoFormat;

    private boolean mute;

    private int rate;

    private String status;

    private String terminalIdentifier;

    private String terminalName;

    private String terminalNumber;

    private String videoFormat;

    public ParticipantStatus() {
    }

    public ParticipantStatus(
           String audioType,
           String dualVideoFormat,
           boolean mute,
           int rate,
           String status,
           String terminalIdentifier,
           String terminalName,
           String terminalNumber,
           String videoFormat) {
           this.audioType = audioType;
           this.dualVideoFormat = dualVideoFormat;
           this.mute = mute;
           this.rate = rate;
           this.status = status;
           this.terminalIdentifier = terminalIdentifier;
           this.terminalName = terminalName;
           this.terminalNumber = terminalNumber;
           this.videoFormat = videoFormat;
    }


    /**
     * Gets the audioType value for this ParticipantStatus.
     * 
     * @return audioType
     */
    public String getAudioType() {
        return audioType;
    }


    /**
     * Sets the audioType value for this ParticipantStatus.
     * 
     * @param audioType
     */
    public void setAudioType(String audioType) {
        this.audioType = audioType;
    }


    /**
     * Gets the dualVideoFormat value for this ParticipantStatus.
     * 
     * @return dualVideoFormat
     */
    public String getDualVideoFormat() {
        return dualVideoFormat;
    }


    /**
     * Sets the dualVideoFormat value for this ParticipantStatus.
     * 
     * @param dualVideoFormat
     */
    public void setDualVideoFormat(String dualVideoFormat) {
        this.dualVideoFormat = dualVideoFormat;
    }


    /**
     * Gets the mute value for this ParticipantStatus.
     * 
     * @return mute
     */
    public boolean isMute() {
        return mute;
    }


    /**
     * Sets the mute value for this ParticipantStatus.
     * 
     * @param mute
     */
    public void setMute(boolean mute) {
        this.mute = mute;
    }


    /**
     * Gets the rate value for this ParticipantStatus.
     * 
     * @return rate
     */
    public int getRate() {
        return rate;
    }


    /**
     * Sets the rate value for this ParticipantStatus.
     * 
     * @param rate
     */
    public void setRate(int rate) {
        this.rate = rate;
    }


    /**
     * Gets the status value for this ParticipantStatus.
     * 
     * @return status
     */
    public String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this ParticipantStatus.
     * 
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }


    /**
     * Gets the terminalIdentifier value for this ParticipantStatus.
     * 
     * @return terminalIdentifier
     */
    public String getTerminalIdentifier() {
        return terminalIdentifier;
    }


    /**
     * Sets the terminalIdentifier value for this ParticipantStatus.
     * 
     * @param terminalIdentifier
     */
    public void setTerminalIdentifier(String terminalIdentifier) {
        this.terminalIdentifier = terminalIdentifier;
    }


    /**
     * Gets the terminalName value for this ParticipantStatus.
     * 
     * @return terminalName
     */
    public String getTerminalName() {
        return terminalName;
    }


    /**
     * Sets the terminalName value for this ParticipantStatus.
     * 
     * @param terminalName
     */
    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }


    /**
     * Gets the terminalNumber value for this ParticipantStatus.
     * 
     * @return terminalNumber
     */
    public String getTerminalNumber() {
        return terminalNumber;
    }


    /**
     * Sets the terminalNumber value for this ParticipantStatus.
     * 
     * @param terminalNumber
     */
    public void setTerminalNumber(String terminalNumber) {
        this.terminalNumber = terminalNumber;
    }


    /**
     * Gets the videoFormat value for this ParticipantStatus.
     * 
     * @return videoFormat
     */
    public String getVideoFormat() {
        return videoFormat;
    }


    /**
     * Sets the videoFormat value for this ParticipantStatus.
     * 
     * @param videoFormat
     */
    public void setVideoFormat(String videoFormat) {
        this.videoFormat = videoFormat;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ParticipantStatus)) return false;
        ParticipantStatus other = (ParticipantStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.audioType==null && other.getAudioType()==null) || 
             (this.audioType!=null &&
              this.audioType.equals(other.getAudioType()))) &&
            ((this.dualVideoFormat==null && other.getDualVideoFormat()==null) || 
             (this.dualVideoFormat!=null &&
              this.dualVideoFormat.equals(other.getDualVideoFormat()))) &&
            this.mute == other.isMute() &&
            this.rate == other.getRate() &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.terminalIdentifier==null && other.getTerminalIdentifier()==null) || 
             (this.terminalIdentifier!=null &&
              this.terminalIdentifier.equals(other.getTerminalIdentifier()))) &&
            ((this.terminalName==null && other.getTerminalName()==null) || 
             (this.terminalName!=null &&
              this.terminalName.equals(other.getTerminalName()))) &&
            ((this.terminalNumber==null && other.getTerminalNumber()==null) || 
             (this.terminalNumber!=null &&
              this.terminalNumber.equals(other.getTerminalNumber()))) &&
            ((this.videoFormat==null && other.getVideoFormat()==null) || 
             (this.videoFormat!=null &&
              this.videoFormat.equals(other.getVideoFormat())));
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
        if (getAudioType() != null) {
            _hashCode += getAudioType().hashCode();
        }
        if (getDualVideoFormat() != null) {
            _hashCode += getDualVideoFormat().hashCode();
        }
        _hashCode += (isMute() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += getRate();
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getTerminalIdentifier() != null) {
            _hashCode += getTerminalIdentifier().hashCode();
        }
        if (getTerminalName() != null) {
            _hashCode += getTerminalName().hashCode();
        }
        if (getTerminalNumber() != null) {
            _hashCode += getTerminalNumber().hashCode();
        }
        if (getVideoFormat() != null) {
            _hashCode += getVideoFormat().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ParticipantStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("audioType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "audioType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dualVideoFormat");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dualVideoFormat"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mute");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mute"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("videoFormat");
        elemField.setXmlName(new javax.xml.namespace.QName("", "videoFormat"));
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
