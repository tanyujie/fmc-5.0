/**
 * VideoWallLayoutConfig.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class VideoWallLayoutConfig  implements java.io.Serializable {
    private String MS90Address;

    private int audioPort;

    private String decoderAddress;

    private int mastPicNo;

    private int maxPicNo;

    private String name;

    private int videoPort;

    private int videoWallNo;

    public VideoWallLayoutConfig() {
    }

    public VideoWallLayoutConfig(
           String MS90Address,
           int audioPort,
           String decoderAddress,
           int mastPicNo,
           int maxPicNo,
           String name,
           int videoPort,
           int videoWallNo) {
           this.MS90Address = MS90Address;
           this.audioPort = audioPort;
           this.decoderAddress = decoderAddress;
           this.mastPicNo = mastPicNo;
           this.maxPicNo = maxPicNo;
           this.name = name;
           this.videoPort = videoPort;
           this.videoWallNo = videoWallNo;
    }


    /**
     * Gets the MS90Address value for this VideoWallLayoutConfig.
     * 
     * @return MS90Address
     */
    public String getMS90Address() {
        return MS90Address;
    }


    /**
     * Sets the MS90Address value for this VideoWallLayoutConfig.
     * 
     * @param MS90Address
     */
    public void setMS90Address(String MS90Address) {
        this.MS90Address = MS90Address;
    }


    /**
     * Gets the audioPort value for this VideoWallLayoutConfig.
     * 
     * @return audioPort
     */
    public int getAudioPort() {
        return audioPort;
    }


    /**
     * Sets the audioPort value for this VideoWallLayoutConfig.
     * 
     * @param audioPort
     */
    public void setAudioPort(int audioPort) {
        this.audioPort = audioPort;
    }


    /**
     * Gets the decoderAddress value for this VideoWallLayoutConfig.
     * 
     * @return decoderAddress
     */
    public String getDecoderAddress() {
        return decoderAddress;
    }


    /**
     * Sets the decoderAddress value for this VideoWallLayoutConfig.
     * 
     * @param decoderAddress
     */
    public void setDecoderAddress(String decoderAddress) {
        this.decoderAddress = decoderAddress;
    }


    /**
     * Gets the mastPicNo value for this VideoWallLayoutConfig.
     * 
     * @return mastPicNo
     */
    public int getMastPicNo() {
        return mastPicNo;
    }


    /**
     * Sets the mastPicNo value for this VideoWallLayoutConfig.
     * 
     * @param mastPicNo
     */
    public void setMastPicNo(int mastPicNo) {
        this.mastPicNo = mastPicNo;
    }


    /**
     * Gets the maxPicNo value for this VideoWallLayoutConfig.
     * 
     * @return maxPicNo
     */
    public int getMaxPicNo() {
        return maxPicNo;
    }


    /**
     * Sets the maxPicNo value for this VideoWallLayoutConfig.
     * 
     * @param maxPicNo
     */
    public void setMaxPicNo(int maxPicNo) {
        this.maxPicNo = maxPicNo;
    }


    /**
     * Gets the name value for this VideoWallLayoutConfig.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }


    /**
     * Sets the name value for this VideoWallLayoutConfig.
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Gets the videoPort value for this VideoWallLayoutConfig.
     * 
     * @return videoPort
     */
    public int getVideoPort() {
        return videoPort;
    }


    /**
     * Sets the videoPort value for this VideoWallLayoutConfig.
     * 
     * @param videoPort
     */
    public void setVideoPort(int videoPort) {
        this.videoPort = videoPort;
    }


    /**
     * Gets the videoWallNo value for this VideoWallLayoutConfig.
     * 
     * @return videoWallNo
     */
    public int getVideoWallNo() {
        return videoWallNo;
    }


    /**
     * Sets the videoWallNo value for this VideoWallLayoutConfig.
     * 
     * @param videoWallNo
     */
    public void setVideoWallNo(int videoWallNo) {
        this.videoWallNo = videoWallNo;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof VideoWallLayoutConfig)) return false;
        VideoWallLayoutConfig other = (VideoWallLayoutConfig) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.MS90Address==null && other.getMS90Address()==null) || 
             (this.MS90Address!=null &&
              this.MS90Address.equals(other.getMS90Address()))) &&
            this.audioPort == other.getAudioPort() &&
            ((this.decoderAddress==null && other.getDecoderAddress()==null) || 
             (this.decoderAddress!=null &&
              this.decoderAddress.equals(other.getDecoderAddress()))) &&
            this.mastPicNo == other.getMastPicNo() &&
            this.maxPicNo == other.getMaxPicNo() &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            this.videoPort == other.getVideoPort() &&
            this.videoWallNo == other.getVideoWallNo();
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
        if (getMS90Address() != null) {
            _hashCode += getMS90Address().hashCode();
        }
        _hashCode += getAudioPort();
        if (getDecoderAddress() != null) {
            _hashCode += getDecoderAddress().hashCode();
        }
        _hashCode += getMastPicNo();
        _hashCode += getMaxPicNo();
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        _hashCode += getVideoPort();
        _hashCode += getVideoWallNo();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VideoWallLayoutConfig.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoWallLayoutConfig"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MS90Address");
        elemField.setXmlName(new javax.xml.namespace.QName("", "MS90Address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("audioPort");
        elemField.setXmlName(new javax.xml.namespace.QName("", "audioPort"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("decoderAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "decoderAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mastPicNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mastPicNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxPicNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "maxPicNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("videoPort");
        elemField.setXmlName(new javax.xml.namespace.QName("", "videoPort"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("videoWallNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "videoWallNo"));
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
