/**
 * AudioAndVideoCapability.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class AudioAndVideoCapability  implements java.io.Serializable {
    private com.zte.m900.bean.AudioCapability audioCapability;

    private int rate;

    private com.zte.m900.bean.VideoCapability videoCapability;

    public AudioAndVideoCapability() {
    }

    public AudioAndVideoCapability(
           com.zte.m900.bean.AudioCapability audioCapability,
           int rate,
           com.zte.m900.bean.VideoCapability videoCapability) {
           this.audioCapability = audioCapability;
           this.rate = rate;
           this.videoCapability = videoCapability;
    }


    /**
     * Gets the audioCapability value for this AudioAndVideoCapability.
     * 
     * @return audioCapability
     */
    public com.zte.m900.bean.AudioCapability getAudioCapability() {
        return audioCapability;
    }


    /**
     * Sets the audioCapability value for this AudioAndVideoCapability.
     * 
     * @param audioCapability
     */
    public void setAudioCapability(com.zte.m900.bean.AudioCapability audioCapability) {
        this.audioCapability = audioCapability;
    }


    /**
     * Gets the rate value for this AudioAndVideoCapability.
     * 
     * @return rate
     */
    public int getRate() {
        return rate;
    }


    /**
     * Sets the rate value for this AudioAndVideoCapability.
     * 
     * @param rate
     */
    public void setRate(int rate) {
        this.rate = rate;
    }


    /**
     * Gets the videoCapability value for this AudioAndVideoCapability.
     * 
     * @return videoCapability
     */
    public com.zte.m900.bean.VideoCapability getVideoCapability() {
        return videoCapability;
    }


    /**
     * Sets the videoCapability value for this AudioAndVideoCapability.
     * 
     * @param videoCapability
     */
    public void setVideoCapability(com.zte.m900.bean.VideoCapability videoCapability) {
        this.videoCapability = videoCapability;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof AudioAndVideoCapability)) return false;
        AudioAndVideoCapability other = (AudioAndVideoCapability) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.audioCapability==null && other.getAudioCapability()==null) || 
             (this.audioCapability!=null &&
              this.audioCapability.equals(other.getAudioCapability()))) &&
            this.rate == other.getRate() &&
            ((this.videoCapability==null && other.getVideoCapability()==null) || 
             (this.videoCapability!=null &&
              this.videoCapability.equals(other.getVideoCapability())));
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
        if (getAudioCapability() != null) {
            _hashCode += getAudioCapability().hashCode();
        }
        _hashCode += getRate();
        if (getVideoCapability() != null) {
            _hashCode += getVideoCapability().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AudioAndVideoCapability.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "AudioAndVideoCapability"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("audioCapability");
        elemField.setXmlName(new javax.xml.namespace.QName("", "audioCapability"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "AudioCapability"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("videoCapability");
        elemField.setXmlName(new javax.xml.namespace.QName("", "videoCapability"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoCapability"));
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
