/**
 * VideoWallState.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class VideoWallState  implements java.io.Serializable {
    private String MS90Address;

    private int audioState;

    private int picNo;

    private int timeInterval;

    private int videoState;

    private int videoWallNo;

    public VideoWallState() {
    }

    public VideoWallState(
           String MS90Address,
           int audioState,
           int picNo,
           int timeInterval,
           int videoState,
           int videoWallNo) {
           this.MS90Address = MS90Address;
           this.audioState = audioState;
           this.picNo = picNo;
           this.timeInterval = timeInterval;
           this.videoState = videoState;
           this.videoWallNo = videoWallNo;
    }


    /**
     * Gets the MS90Address value for this VideoWallState.
     * 
     * @return MS90Address
     */
    public String getMS90Address() {
        return MS90Address;
    }


    /**
     * Sets the MS90Address value for this VideoWallState.
     * 
     * @param MS90Address
     */
    public void setMS90Address(String MS90Address) {
        this.MS90Address = MS90Address;
    }


    /**
     * Gets the audioState value for this VideoWallState.
     * 
     * @return audioState
     */
    public int getAudioState() {
        return audioState;
    }


    /**
     * Sets the audioState value for this VideoWallState.
     * 
     * @param audioState
     */
    public void setAudioState(int audioState) {
        this.audioState = audioState;
    }


    /**
     * Gets the picNo value for this VideoWallState.
     * 
     * @return picNo
     */
    public int getPicNo() {
        return picNo;
    }


    /**
     * Sets the picNo value for this VideoWallState.
     * 
     * @param picNo
     */
    public void setPicNo(int picNo) {
        this.picNo = picNo;
    }


    /**
     * Gets the timeInterval value for this VideoWallState.
     * 
     * @return timeInterval
     */
    public int getTimeInterval() {
        return timeInterval;
    }


    /**
     * Sets the timeInterval value for this VideoWallState.
     * 
     * @param timeInterval
     */
    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }


    /**
     * Gets the videoState value for this VideoWallState.
     * 
     * @return videoState
     */
    public int getVideoState() {
        return videoState;
    }


    /**
     * Sets the videoState value for this VideoWallState.
     * 
     * @param videoState
     */
    public void setVideoState(int videoState) {
        this.videoState = videoState;
    }


    /**
     * Gets the videoWallNo value for this VideoWallState.
     * 
     * @return videoWallNo
     */
    public int getVideoWallNo() {
        return videoWallNo;
    }


    /**
     * Sets the videoWallNo value for this VideoWallState.
     * 
     * @param videoWallNo
     */
    public void setVideoWallNo(int videoWallNo) {
        this.videoWallNo = videoWallNo;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof VideoWallState)) return false;
        VideoWallState other = (VideoWallState) obj;
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
            this.audioState == other.getAudioState() &&
            this.picNo == other.getPicNo() &&
            this.timeInterval == other.getTimeInterval() &&
            this.videoState == other.getVideoState() &&
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
        _hashCode += getAudioState();
        _hashCode += getPicNo();
        _hashCode += getTimeInterval();
        _hashCode += getVideoState();
        _hashCode += getVideoWallNo();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VideoWallState.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoWallState"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MS90Address");
        elemField.setXmlName(new javax.xml.namespace.QName("", "MS90Address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("audioState");
        elemField.setXmlName(new javax.xml.namespace.QName("", "audioState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("picNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "picNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeInterval");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timeInterval"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("videoState");
        elemField.setXmlName(new javax.xml.namespace.QName("", "videoState"));
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
