/**
 * VideoWallPollConfigRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class VideoWallPollConfigRequest  implements java.io.Serializable {
    private String MS90Address;

    private int picNo;

    private com.zte.m900.bean.PollTer[] pollTers;

    private int terCount;

    private int videoWallNo;

    public VideoWallPollConfigRequest() {
    }

    public VideoWallPollConfigRequest(
           String MS90Address,
           int picNo,
           com.zte.m900.bean.PollTer[] pollTers,
           int terCount,
           int videoWallNo) {
           this.MS90Address = MS90Address;
           this.picNo = picNo;
           this.pollTers = pollTers;
           this.terCount = terCount;
           this.videoWallNo = videoWallNo;
    }


    /**
     * Gets the MS90Address value for this VideoWallPollConfigRequest.
     * 
     * @return MS90Address
     */
    public String getMS90Address() {
        return MS90Address;
    }


    /**
     * Sets the MS90Address value for this VideoWallPollConfigRequest.
     * 
     * @param MS90Address
     */
    public void setMS90Address(String MS90Address) {
        this.MS90Address = MS90Address;
    }


    /**
     * Gets the picNo value for this VideoWallPollConfigRequest.
     * 
     * @return picNo
     */
    public int getPicNo() {
        return picNo;
    }


    /**
     * Sets the picNo value for this VideoWallPollConfigRequest.
     * 
     * @param picNo
     */
    public void setPicNo(int picNo) {
        this.picNo = picNo;
    }


    /**
     * Gets the pollTers value for this VideoWallPollConfigRequest.
     * 
     * @return pollTers
     */
    public com.zte.m900.bean.PollTer[] getPollTers() {
        return pollTers;
    }


    /**
     * Sets the pollTers value for this VideoWallPollConfigRequest.
     * 
     * @param pollTers
     */
    public void setPollTers(com.zte.m900.bean.PollTer[] pollTers) {
        this.pollTers = pollTers;
    }


    /**
     * Gets the terCount value for this VideoWallPollConfigRequest.
     * 
     * @return terCount
     */
    public int getTerCount() {
        return terCount;
    }


    /**
     * Sets the terCount value for this VideoWallPollConfigRequest.
     * 
     * @param terCount
     */
    public void setTerCount(int terCount) {
        this.terCount = terCount;
    }


    /**
     * Gets the videoWallNo value for this VideoWallPollConfigRequest.
     * 
     * @return videoWallNo
     */
    public int getVideoWallNo() {
        return videoWallNo;
    }


    /**
     * Sets the videoWallNo value for this VideoWallPollConfigRequest.
     * 
     * @param videoWallNo
     */
    public void setVideoWallNo(int videoWallNo) {
        this.videoWallNo = videoWallNo;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof VideoWallPollConfigRequest)) return false;
        VideoWallPollConfigRequest other = (VideoWallPollConfigRequest) obj;
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
            this.picNo == other.getPicNo() &&
            ((this.pollTers==null && other.getPollTers()==null) || 
             (this.pollTers!=null &&
              java.util.Arrays.equals(this.pollTers, other.getPollTers()))) &&
            this.terCount == other.getTerCount() &&
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
        _hashCode += getPicNo();
        if (getPollTers() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPollTers());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getPollTers(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getTerCount();
        _hashCode += getVideoWallNo();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VideoWallPollConfigRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "VideoWallPollConfigRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MS90Address");
        elemField.setXmlName(new javax.xml.namespace.QName("", "MS90Address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("picNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "picNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pollTers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pollTers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "PollTer"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terCount"));
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
