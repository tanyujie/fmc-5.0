/**
 * ParticipantScheduleInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class ParticipantScheduleInfo  implements java.io.Serializable {
    private String confCreator;

    private String confName;

    private int duration;

    private String startTime;

    public ParticipantScheduleInfo() {
    }

    public ParticipantScheduleInfo(
           String confCreator,
           String confName,
           int duration,
           String startTime) {
           this.confCreator = confCreator;
           this.confName = confName;
           this.duration = duration;
           this.startTime = startTime;
    }


    /**
     * Gets the confCreator value for this ParticipantScheduleInfo.
     * 
     * @return confCreator
     */
    public String getConfCreator() {
        return confCreator;
    }


    /**
     * Sets the confCreator value for this ParticipantScheduleInfo.
     * 
     * @param confCreator
     */
    public void setConfCreator(String confCreator) {
        this.confCreator = confCreator;
    }


    /**
     * Gets the confName value for this ParticipantScheduleInfo.
     * 
     * @return confName
     */
    public String getConfName() {
        return confName;
    }


    /**
     * Sets the confName value for this ParticipantScheduleInfo.
     * 
     * @param confName
     */
    public void setConfName(String confName) {
        this.confName = confName;
    }


    /**
     * Gets the duration value for this ParticipantScheduleInfo.
     * 
     * @return duration
     */
    public int getDuration() {
        return duration;
    }


    /**
     * Sets the duration value for this ParticipantScheduleInfo.
     * 
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }


    /**
     * Gets the startTime value for this ParticipantScheduleInfo.
     * 
     * @return startTime
     */
    public String getStartTime() {
        return startTime;
    }


    /**
     * Sets the startTime value for this ParticipantScheduleInfo.
     * 
     * @param startTime
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ParticipantScheduleInfo)) return false;
        ParticipantScheduleInfo other = (ParticipantScheduleInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.confCreator==null && other.getConfCreator()==null) || 
             (this.confCreator!=null &&
              this.confCreator.equals(other.getConfCreator()))) &&
            ((this.confName==null && other.getConfName()==null) || 
             (this.confName!=null &&
              this.confName.equals(other.getConfName()))) &&
            this.duration == other.getDuration() &&
            ((this.startTime==null && other.getStartTime()==null) || 
             (this.startTime!=null &&
              this.startTime.equals(other.getStartTime())));
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
        if (getConfCreator() != null) {
            _hashCode += getConfCreator().hashCode();
        }
        if (getConfName() != null) {
            _hashCode += getConfName().hashCode();
        }
        _hashCode += getDuration();
        if (getStartTime() != null) {
            _hashCode += getStartTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ParticipantScheduleInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantScheduleInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confCreator");
        elemField.setXmlName(new javax.xml.namespace.QName("", "confCreator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "confName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("duration");
        elemField.setXmlName(new javax.xml.namespace.QName("", "duration"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "startTime"));
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
