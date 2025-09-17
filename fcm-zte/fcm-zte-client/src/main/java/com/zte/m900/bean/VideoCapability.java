/**
 * VideoCapability.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class VideoCapability  implements java.io.Serializable {
    private String encodeType;

    private String format;

    private int frameHz;

    private int rate;

    public VideoCapability() {
    }

    public VideoCapability(
           String encodeType,
           String format,
           int frameHz,
           int rate) {
           this.encodeType = encodeType;
           this.format = format;
           this.frameHz = frameHz;
           this.rate = rate;
    }


    /**
     * Gets the encodeType value for this VideoCapability.
     * 
     * @return encodeType
     */
    public String getEncodeType() {
        return encodeType;
    }


    /**
     * Sets the encodeType value for this VideoCapability.
     * 
     * @param encodeType
     */
    public void setEncodeType(String encodeType) {
        this.encodeType = encodeType;
    }


    /**
     * Gets the format value for this VideoCapability.
     * 
     * @return format
     */
    public String getFormat() {
        return format;
    }


    /**
     * Sets the format value for this VideoCapability.
     * 
     * @param format
     */
    public void setFormat(String format) {
        this.format = format;
    }


    /**
     * Gets the frameHz value for this VideoCapability.
     * 
     * @return frameHz
     */
    public int getFrameHz() {
        return frameHz;
    }


    /**
     * Sets the frameHz value for this VideoCapability.
     * 
     * @param frameHz
     */
    public void setFrameHz(int frameHz) {
        this.frameHz = frameHz;
    }


    /**
     * Gets the rate value for this VideoCapability.
     * 
     * @return rate
     */
    public int getRate() {
        return rate;
    }


    /**
     * Sets the rate value for this VideoCapability.
     * 
     * @param rate
     */
    public void setRate(int rate) {
        this.rate = rate;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof VideoCapability)) return false;
        VideoCapability other = (VideoCapability) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.encodeType==null && other.getEncodeType()==null) || 
             (this.encodeType!=null &&
              this.encodeType.equals(other.getEncodeType()))) &&
            ((this.format==null && other.getFormat()==null) || 
             (this.format!=null &&
              this.format.equals(other.getFormat()))) &&
            this.frameHz == other.getFrameHz() &&
            this.rate == other.getRate();
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
        if (getEncodeType() != null) {
            _hashCode += getEncodeType().hashCode();
        }
        if (getFormat() != null) {
            _hashCode += getFormat().hashCode();
        }
        _hashCode += getFrameHz();
        _hashCode += getRate();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VideoCapability.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoCapability"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("encodeType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "encodeType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("format");
        elemField.setXmlName(new javax.xml.namespace.QName("", "format"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("frameHz");
        elemField.setXmlName(new javax.xml.namespace.QName("", "frameHz"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rate"));
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
